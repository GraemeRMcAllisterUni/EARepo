package gppDemos.concordance

import gppLibrary.DataDetails
import gppLibrary.ResultDetails
import gppLibrary.patterns.TaskParallelOfGroupCollects
import gppDemos.concordance.ConcordanceData as cd
import gppDemos.concordance.ConcordanceResults as cr


//usage runDemo concordance/RunConcordancePoG resultFile workers title N

int workers = 2
String title = "bible"
int N = 8
int minSeqLen = 2
boolean doFileOutput = false

if (args.size() > 0){
    workers = Integer.parseInt(args[0])
    title = args[1]
    N = Integer.parseInt(args[2])
}


// eclipse versions
def fileName = "./${title}.txt"
def outFileName = "./${title}PoG"

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

for ( g in 0..< workers) resultDetails << rDetails

System.gc()
print "PoG, $doFileOutput, $workers, "
def startime = System.currentTimeMillis()

def concordancePoG = new TaskParallelOfGroupCollects(
    eDetails: dDetails,
    rDetails: resultDetails,
    stages: 3,
    stageOp: [cd.valueList, cd.indicesMap, cd.wordsMap],
    workers: workers,
    stageModifier: null
    )


def endtime = System.currentTimeMillis()
println " ${endtime - startime} "


