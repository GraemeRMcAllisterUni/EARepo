package gppDemos.concordance

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


def emitter = new Emit( eDetails: dDetails)

def pipe = new OnePipelineOne(stages : 3,
                stageOp: [cd.valueList, cd.indicesMap, cd.wordsMap])

def collector = new Collect(rDetails: rDetails)

def endtime = System.currentTimeMillis()
println " ${endtime - startime} "
