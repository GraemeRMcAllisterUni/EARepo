package gppDemos.concordance

import gppLibrary.DataDetails
import gppLibrary.ResultDetails
import gppLibrary.functionals.pipelines.OnePipelineOne
import gppLibrary.terminals.Collect
import gppLibrary.terminals.Emit

import gppDemos.concordance.ConcordanceData as cd
import gppDemos.concordance.ConcordanceResults as cr


//usage runDemo concordance RunCollectConcordance resultsFile title N

String title
int N
int minSeqLen = 2
boolean doFileOutput = false
String workingDirectory = System.getProperty('user.dir')
String fileName
String outFileName

if (args.size() == 0){
    // assumed to be running form within Intellij
    title = "bible"
    N = 8
    fileName = "./${title}.txt"
    outFileName = "./${title}PoG"
}
else {
    // assumed to be running via runDemo
    String folder = args[0]
    title = args[1]
    fileName = workingDirectory + "/src/main/groovy/gppDemos/${folder}/${title}.txt"
    outFileName = workingDirectory + "/src/main/groovy/gppDemos/${folder}/${title}PoG"
    N = Integer.parseInt(args[2])
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

System.gc()
print "Collect, $doFileOutput, "

def startime = System.currentTimeMillis()


def emitter = new Emit( eDetails: dDetails)

def pipe = new OnePipelineOne(stages : 3,
                stageOp: [cd.valueList, cd.indicesMap, cd.wordsMap])

def collector = new Collect(rDetails: rDetails)

def endtime = System.currentTimeMillis()
println " ${endtime - startime} "
