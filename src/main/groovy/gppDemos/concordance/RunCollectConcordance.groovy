package gppDemos.concordance

import jcsp.lang.*
import groovyJCSP.*
 
import gppLibrary.DataDetails
import gppLibrary.ResultDetails
import gppLibrary.functionals.pipelines.OnePipelineOne
import gppLibrary.terminals.Collect
import gppLibrary.terminals.Emit
 
import gppDemos.concordance.ConcordanceData as cd
import gppDemos.concordance.ConcordanceResults as cr
 
 

//usage runDemo concordance/RunCollectConcordance resultsFile
 
def title = "bible"
def fileName = "./${title}.txt"
def outFileName = "./${title}Collect"
 
int N = 8
int minSeqLen = 2
boolean doFileOutput = false
 
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
 
System.gc()
print "Collect, $doFileOutput, "
 
def startime = System.currentTimeMillis()
 
 

//NETWORK

def chan1 = Channel.one2one()
def chan2 = Channel.one2one()

def emitter = new Emit(
    // input channel not required
    output: chan1.out(),
    eDetails: dDetails)
 
def pipe = new OnePipelineOne(
    input: chan1.in(),
    output: chan2.out(),
    stages : 3,
    stageOp: [cd.valueList, cd.indicesMap, cd.wordsMap])
 
def collector = new Collect(
    input: chan2.in(),
    // no output channel required
    rDetails: rDetails)

PAR network = new PAR()
 network = new PAR([emitter , pipe , collector ])
 network.run()
 network.removeAllProcesses()
//END

 
def endtime = System.currentTimeMillis()
println " ${endtime - startime} "
