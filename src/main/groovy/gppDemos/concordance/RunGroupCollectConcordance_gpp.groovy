package gppDemos.concordance

import gppLibrary.DataDetails
import gppLibrary.ResultDetails
import gppLibrary.connectors.spreaders.OneIndexedList
import gppLibrary.functionals.groups.ListGroupCollect
import gppLibrary.functionals.pipelines.OnePipelineOne
import gppLibrary.terminals.Emit

import gppDemos.concordance.ConcordanceData as cd
import gppDemos.concordance.ConcordanceResults as cr


//usage runDemo concordance/RunGroupCollectConcordance resultsFile

String title = "bible"
String fileName = "src\\demos\\concordance\\${title}.txt"
String outFileName = "src\\demos\\concordance\\${title}GC"

int N = 8
int minSeqLen = 2
int collectors = N
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

List <ResultDetails>  resultDetails = []

for ( g in 0..< collectors) resultDetails << rDetails

System.gc()
print "GroupCollect $doFileOutput, $collectors, "
def startime = System.currentTimeMillis()


def emitter = new Emit( eDetails: dDetails)

def pipe = new OnePipelineOne(stages : 3,
                stageOp: [cd.valueList, cd.indicesMap, cd.wordsMap])

def oil = new OneIndexedList(indexFunction : cd.indexer)

def lgc = new ListGroupCollect(workers: collectors,
                 rDetails: resultDetails)


def endtime = System.currentTimeMillis()
println " ${endtime - startime} "

