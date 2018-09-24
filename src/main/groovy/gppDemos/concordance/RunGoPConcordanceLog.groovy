package gppDemos.concordance

import jcsp.lang.*
import groovyJCSP.*
 
import gppLibrary.DataDetails
import gppLibrary.ResultDetails
import gppLibrary.connectors.spreaders.OneFanAny
import gppLibrary.functionals.composites.GroupOfPipelineCollects
import gppLibrary.terminals.Emit
 
import gppDemos.concordance.ConcordanceData as cd
import gppDemos.concordance.ConcordanceResults as cr
 
 

//usage runDemo concordance/RunGoPConcordanceLog resultFile groups title N runNo
 
int groups = 2
String title = "bible"
int N = 8
int minSeqLen = 2
boolean doFileOutput = false
int runNo = 2
 
if (args.size() > 0){
groups = Integer.parseInt(args[0])
title = args[1]
N = Integer.parseInt(args[2])
runNo = Integer.parseInt(args[3])
}
 
 
def fileName = "./${title}.txt"
def outFileName = "./${title}GoPLog"
 
def dDetails = new DataDetails( dName: cd.getName(),
dInitMethod: cd.init,
dInitData: [N, fileName, outFileName],
dCreateMethod: cd.create,
dCreateData: [null])
 
def rDetails = new ResultDetails( rName: cr.getName(),
rInitMethod: cr.init,
rInitData: [minSeqLen, doFileOutput],
rCollectMethod: cr.collector,
rFinaliseMethod: cr.finalise,
rFinaliseData: [null])
 
List <ResultDetails>  resultDetails = []
 
for ( g in 0..< groups) resultDetails << rDetails
 
System.gc()
print "RunGoPConcordanceLog $doFileOutput,  $groups, "
 
def startime = System.currentTimeMillis()
 

//NETWORK

def chan1 = Channel.one2one()
def chan2 = Channel.one2any()

def emitter = new Emit(
    // input channel not required
    output: chan1.out(),
    eDetails: dDetails,
    logPhaseName: "0-emit",
    logPropertyName: "strLen")
 
def fanOut = new OneFanAny(
    input: chan1.in(),
    outputAny: chan2.out(),
    destinations: groups)
 
def poG = new GroupOfPipelineCollects(
    inputAny: chan2.in(),
    // no output channel required
    stages: 3,
    rDetails: resultDetails,
    stageOp: [cd.valueList, cd.indicesMap, cd.wordsMap],
    groups: groups,
    logPhaseNames: ["1-value", "2-indeces", "3-words"],
    logPropertyName: "strLen",
    logFileName: "./GPPLogs/LogFile-$runNo-")

PAR network = new PAR()
 network = new PAR([emitter , fanOut , poG ])
 network.run()
 network.removeAllProcesses()
//END

 
def endtime = System.currentTimeMillis()
println " ${endtime - startime}"
 
 
