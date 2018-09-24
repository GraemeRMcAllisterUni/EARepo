package gppDemos.concordance

import jcsp.lang.*
import groovyJCSP.*
 
import gppLibrary.DataDetails
import gppLibrary.LocalDetails
import gppLibrary.ResultDetails
import gppLibrary.connectors.reducers.ListFanOne
import gppLibrary.connectors.spreaders.OneFanList
import gppLibrary.functionals.groups.ListGroupList
import gppLibrary.functionals.transformers.CombineNto1
import gppLibrary.terminals.Collect
import gppLibrary.terminals.Emit
import gppLibrary.terminals.EmitFromInput
import gppDemos.concordance.ConcordanceWords as cw
import gppDemos.concordance.ConcordanceCombine as cc
import gppDemos.concordance.ConcordanceData as cd
 
 

//usage runDemo concordance/RunExtendedTest resultFile title N blockWorkers blockSize
 
int blockWorkers = 2
String title = "bible"
int N = 8
int minSeqLen = 2
int blockSize = 16000
boolean doFileOutput = false
 
if (args.size() == 0){
blockWorkers = 3
title = "bible"
N = 8
blockSize = 64000
}
else {
title = args[0]
N = Integer.parseInt(args[1])
blockWorkers = Integer.parseInt(args[2])
blockSize = Integer.parseInt(args[3])
}
 
// eclipse versions
def fileName = "./${title}.txt"
def outFileName = "./${title}PoG"
 
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
 
def rDetails = new ResultDetails( rName: cd.getName(),
rInitMethod: cd.init,
rCollectMethod: cd.collector,
rFinaliseMethod: cd.finaliseCollect,
rFinaliseData: [null])
 
 
System.gc()
print "ExtendedTest, $title, $blockWorkers, $blockSize, "
 
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
 
def fis = new ListFanOne(
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
 
 
def collector = new Collect(
    input: chan6.in(),
    // no output channel required
    rDetails: rDetails)

PAR network = new PAR()
 network = new PAR([emit , fos , group , fis , combine , emitInstances , collector ])
 network.run()
 network.removeAllProcesses()
//END

 
def endtime = System.currentTimeMillis()
println " ${endtime - startime}"
