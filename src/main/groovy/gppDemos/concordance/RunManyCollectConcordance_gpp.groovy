package gppDemos.concordance

import gppLibrary.DataDetails
import gppLibrary.ResultDetails
import gppLibrary.connectors.spreaders.OneFanAny
import gppLibrary.functionals.groups.AnyGroupCollect
import gppLibrary.functionals.pipelines.OnePipelineOne
import gppLibrary.terminals.Emit

import gppDemos.concordance.ConcordanceData as cd
import gppDemos.concordance.ConcordanceResults as cr


//usage runDemo concordance/RunManyCollectConcordance resultsFile collectors

int collectors = 0
if (args.size() == 0){
    collectors = 2
}
else {
    collectors = Integer.parseInt(args[0])
}

def title = "bible"
def fileName = "src\\demos\\concordance\\${title}.txt"
def outFileName = "src\\demos\\concordance\\${title}Collect"

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
print "Many Collect $doFileOutput, $collectors, "

def startime = System.currentTimeMillis()


def emitter = new Emit( eDetails: dDetails)

def pipe = new OnePipelineOne(stages : 3,
                stageOp: [cd.valueList, cd.indicesMap, cd.wordsMap])

def ofa = new OneFanAny(destinations: collectors)

def collectGroup  = new AnyGroupCollect(rDetails: rDetails,
                        collectors: collectors)

def endtime = System.currentTimeMillis()
println " ${endtime - startime} "
