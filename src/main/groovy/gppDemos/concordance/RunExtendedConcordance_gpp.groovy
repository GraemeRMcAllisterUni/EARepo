package gppDemos.concordance

import gppLibrary.DataDetails
import gppLibrary.LocalDetails
import gppLibrary.ResultDetails
import gppLibrary.connectors.reducers.ListFanOne
import gppLibrary.connectors.reducers.ListMergeOne
import gppLibrary.connectors.spreaders.OneFanAny
import gppLibrary.connectors.spreaders.OneFanList
import gppLibrary.functionals.composites.GroupOfPipelineCollects
import gppLibrary.functionals.groups.ListGroupList
import gppLibrary.terminals.*
import gppLibrary.functionals.transformers.CombineNto1

import gppDemos.concordance.ConcordanceWords as cw
import gppDemos.concordance.ConcordanceCombine as cc
import gppDemos.concordance.ConcordanceResults as cr
import gppDemos.concordance.ConcordanceData as cd

//usage runDemo concordance RunExtendedConcordance resultsFile title blockWorkers blocksize pogWorkers N

int blockWorkers
int pogWorkers
int blockSize
String title
int N
int minSeqLen = 2
boolean doFileOutput = false
String workingDirectory = System.getProperty('user.dir')
String fileName
String outFileName

if (args.size() == 0){
    // assumed to be running form within Intellij
    blockWorkers = 4
    pogWorkers = 2
    blockSize = 64000
    title = "bible"
    N = 8
    fileName = "./${title}.txt"
    outFileName = "./${title}Ext"
}
else {
    // assumed to be running via runDemo
    String folder = args[0]
    title = args[1]
    fileName = workingDirectory + "/src/main/groovy/gppDemos/${folder}/${title}.txt"
    outFileName = workingDirectory + "/src/main/groovy/gppDemos/${folder}/${title}Ext"
    blockWorkers = Integer.parseInt(args[2])
    blockSize = Integer.parseInt(args[3])
    pogWorkers = Integer.parseInt(args[4])
    N = Integer.parseInt(args[5])
}

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

def rDetails = new ResultDetails( rName: cr.getName(),
    rInitMethod: cr.init,
    rInitData: [minSeqLen, doFileOutput],
    rCollectMethod: cr.collector,
    rFinaliseMethod: cr.finalise,
    rFinaliseData: [null])

List <ResultDetails>  resultDetails = []

for ( g in 0..< pogWorkers) resultDetails << rDetails

print "RunExtendedConcordance, $doFileOutput, $title, $N, $blockWorkers, $pogWorkers, $blockSize, "
System.gc()
def startime = System.currentTimeMillis()

def emit = new Emit (eDetails: dDetails)

def fos = new OneFanList()

def group = new ListGroupList(function: cw.processBuffer,
            workers: blockWorkers)

def fis = new ListMergeOne()


def combine = new CombineNto1(localDetails: localData,
                outDetails: outData,
                combineMethod: cc.appendBuff )

def emitInstances = new EmitFromInput( eDetails: outData)

def fanOut = new OneFanAny( destinations: pogWorkers)

def poG = new GroupOfPipelineCollects( stages: 3,
                rDetails: resultDetails,
                stageOp: [cd.valueList, cd.indicesMap, cd.wordsMap],
                groups: pogWorkers)

def endtime = System.currentTimeMillis()
println " ${endtime - startime} "

