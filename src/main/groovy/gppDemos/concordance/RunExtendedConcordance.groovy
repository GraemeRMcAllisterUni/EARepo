package gppDemos.concordance

import jcsp.lang.*
import groovyJCSP.*
 
import gppLibrary.DataDetails
import gppLibrary.LocalDetails
import gppLibrary.ResultDetails
import gppLibrary.connectors.reducers.ListFanOne
import gppLibrary.connectors.reducers.ListMergeOne
import gppLibrary.connectors.spreaders.OneFanAny
import gppLibrary.connectors.spreaders.OneFanList
import gppLibrary.functionals.composites.GroupOfPipelineCollects
import gppLibrary.functionals.groups.ListGroupList
import gppLibrary.terminals.*
import gppLibrary.functionals.transformers.CombineNto1
 
import gppDemos.concordance.ConcordanceWords as cw
import gppDemos.concordance.ConcordanceCombine as cc
import gppDemos.concordance.ConcordanceResults as cr
import gppDemos.concordance.ConcordanceData as cd
 

//usage runDemo concordance/RunExtendedConcordance resultsFile title blockWorkers blocksize pogWorkers
 
int blockWorkers
int pogWorkers
int blockSize
String title
 
 
if (args.size() == 0){
blockWorkers = 4
pogWorkers = 2
blockSize = 64000
title = "bible"
}
else {
title = args[0]
blockWorkers = Integer.parseInt(args[1])
blockSize = Integer.parseInt(args[2])
pogWorkers = Integer.parseInt(args[3])
}
 
 
String fileName = "./${title}.txt"
String outFileName = "./${title}Ext"
 
int N = 8
int minSeqLen = 2
boolean doFileOutput = false
 
 
def dDetails = new DataDetails( dName: cw.getName(),
dInitMethod: cw.init,
dInitData: [fileName, blockSize],
dCreateMethod: cw.create,
dCreateData: [null])
 
def localData = new LocalDetails(lName: cc.getName(),
lInitData: [N, outFileName],
lInitMethod: cc.init,
lFinaliseMethod: cc.finalise)
 
def outData = new LocalDetails( lName:cd.getName(),
lInitMethod: cd.initLocal,
lInitData: null,
lCreateMethod: cd.create,
lFinaliseMethod: cd.finalise)
 
def rDetails = new ResultDetails( rName: cr.getName(),
rInitMethod: cr.init,
rInitData: [minSeqLen, doFileOutput],
rCollectMethod: cr.collector,
rFinaliseMethod: cr.finalise,
rFinaliseData: [null])
 
List <ResultDetails>  resultDetails = []
 
for ( g in 0..< pogWorkers) resultDetails << rDetails
 
print "RunExtendedConcordance $doFileOutput, $blockWorkers, $pogWorkers, $blockSize, "
System.gc()
def startime = System.currentTimeMillis()
 

//NETWORK

def chan1 = Channel.one2one()
def chan2 = Channel.one2oneArray(blockWorkers)
def chan2OutList = new ChannelOutputList(chan2)
def chan2InList = new ChannelInputList(chan2)
def chan3 = Channel.one2oneArray(blockWorkers)
def chan3OutList = new ChannelOutputList(chan3)
def chan3InList = new ChannelInputList(chan3)
def chan4 = Channel.one2one()
def chan5 = Channel.one2one()
def chan6 = Channel.one2one()
def chan7 = Channel.one2any()

def emit = new Emit (
    // input channel not required
    output: chan1.out(),
    eDetails: dDetails)
 
def fos = new OneFanList(
    input: chan1.in(),
    outputList: chan2OutList )
 
def group = new ListGroupList(
    inputList: chan2InList,
    outputList: chan3OutList,
    function: cw.processBuffer,
    workers: blockWorkers)
 
def fis = new ListMergeOne(
    inputList: chan3InList,
    output: chan4.out(),
    )
 
 
def combine = new CombineNto1(
    input: chan4.in(),
    output: chan5.out(),
    localDetails: localData,
    outDetails: outData,
    combineMethod: cc.appendBuff )
 
def emitInstances = new EmitFromInput(
    input: chan5.in(),
    output: chan6.out(),
    eDetails: outData)
 
def fanOut = new OneFanAny(
    input: chan6.in(),
    outputAny: chan7.out(),
    destinations: pogWorkers)
 
def poG = new GroupOfPipelineCollects(
    inputAny: chan7.in(),
    // no output channel required
    stages: 3,
    rDetails: resultDetails,
    stageOp: [cd.valueList, cd.indicesMap, cd.wordsMap],
    groups: pogWorkers)

PAR network = new PAR()
 network = new PAR([emit , fos , group , fis , combine , emitInstances , fanOut , poG ])
 network.run()
 network.removeAllProcesses()
//END

 
def endtime = System.currentTimeMillis()
println " ${endtime - startime} "
 
