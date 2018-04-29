package gppDemos.concordance

import jcsp.lang.*
import groovyJCSP.*
 
import gppLibrary.DataDetails
import gppLibrary.ResultDetails
import gppLibrary.connectors.spreaders.OneIndexedList
import gppLibrary.functionals.groups.ListGroupCollect
import gppLibrary.functionals.pipelines.OnePipelineOne
import gppLibrary.terminals.Emit
 
import gppDemos.concordance.ConcordanceData as cd
import gppDemos.concordance.ConcordanceResults as cr
 

//usage runDemo concordance/RunSkeletonConcordance resultsFile collectors
 
String title = "bible"
String fileName = "src\\demos\\concordance\\${title}.txt"
String outFileName = "src\\demos\\concordance\\${title}Skel"
int N = 8
int minSeqLen = 2
boolean doFileOutput = false
 
int collectors = 0
if (args.size() == 0){
collectors = 2
}
else {
collectors = Integer.parseInt(args[0])
}
 
 
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
 
for ( g in 0..< collectors) resultDetails << rDetails
 
System.gc()
print "Skeleton $doFileOutput, $collectors, "
 
def startime = System.currentTimeMillis()
 

//NETWORK

def chan1 = Channel.one2one()
def chan2 = Channel.one2one()
def chan3 = Channel.one2oneArray(collectors)
def chan3OutList = new ChannelOutputList(chan3)
def chan3InList = new ChannelInputList(chan3)

def emitter = new Emit(
    // input channel not required
    output: chan1.out(),
    eDetails: dDetails)
 
def pipe = new OnePipelineOne(
    input: chan1.in(),
    output: chan2.out(),
    stages : 3,
    stageOp: [cd.valueList, cd.indicesMap, cd.wordsMap])
 
def oil = new OneIndexedList(
    input: chan2.in(),
    outputList: chan3OutList,
    indexFunction : cd.indexer,
    indexBounds: [0, collectors])
 
def lgc = new ListGroupCollect(
    inputList: chan3InList,
    // no output channel required
    rDetails: resultDetails,
    workers: collectors)

PAR network = new PAR()
 network = new PAR([emitter , pipe , oil , lgc ])
 network.run()
 network.removeAllProcesses()
//END

 
 
def endtime = System.currentTimeMillis()
println " ${endtime - startime}"
 
