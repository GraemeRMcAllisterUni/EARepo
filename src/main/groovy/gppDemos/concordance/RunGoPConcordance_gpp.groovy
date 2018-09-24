package gppDemos.concordance

import gppLibrary.DataDetails
import gppLibrary.ResultDetails
import gppLibrary.connectors.spreaders.OneFanAny
import gppLibrary.functionals.composites.GroupOfPipelineCollects
import gppLibrary.terminals.Emit

import gppDemos.concordance.ConcordanceData as cd
import gppDemos.concordance.ConcordanceResults as cr


//usage runDemo concordance/RunGoPConcordance resultFile workers title N

int groups = 2
String title = "bible"
int N = 8
int minSeqLen = 2
boolean doFileOutput = false

if (args.size() > 0){
    groups = Integer.parseInt(args[0])
    title = args[1]
    N = Integer.parseInt(args[2])
}

String fileName = "./${title}.txt"
String outFileName = "./${title}GoP"

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
print "GoP, $doFileOutput, $groups, "

def startime = System.currentTimeMillis()


def emitter = new Emit( eDetails: dDetails)

def fanOut = new OneFanAny(destinations: groups)

def GoPconcordance = new GroupOfPipelineCollects( stages: 3,
                     rDetails: resultDetails,
                     stageOp: [cd.valueList, cd.indicesMap, cd.wordsMap],
                     groups: groups)


def endtime = System.currentTimeMillis()
println " ${endtime - startime}"

