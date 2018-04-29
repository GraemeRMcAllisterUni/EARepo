package gppDemos.concordance

import gppLibrary.DataDetails
import gppLibrary.ResultDetails
import gppLibrary.connectors.spreaders.OneFanAny
import gppLibrary.functionals.composites.GroupOfPipelineCollects
import gppLibrary.terminals.Emit
import groovyJCSP.PAR
import jcsp.lang.*

import gppDemos.concordance.ConcordanceData as cd
import gppDemos.concordance.ConcordanceResults as cr

String title = "bible"
String fileName = "src\\demos\\concordance\\${title}.txt"
String outFileName = "src\\demos\\concordance\\${title}GoP"

int N = 6
int minSeqLen = 2
//int groups = Integer.parseInt(args[0])  // for bat version only!!
int groups = 4


//// only when running by bat file
//String path = this.class.getClassLoader().getResource("")
//path = path.substring(6, path.size())	// remove file:/ from absolute path
////println "mod1 path = $path"
//fileName = path + fileName
//outFileName = path + outFileName
//println "inFile = $fileName"
//println "outFile = $outFileName"
//



def dDetails = new DataDetails( dName: cd.getName(),
  dInitMethod: cd.init,
  dInitData: [N, fileName, outFileName],
  dCreateMethod: cd.create,
  dCreateData: [null])

def rDetails = new ResultDetails( rName: cr.getName(),
    rInitMethod: cr.init,
    rInitData: [minSeqLen],
    rCollectMethod: cr.collector,
    rFinaliseMethod: cr.finalise,
    rFinaliseData: [null])

System.gc()
println "GoP - $title : N: $N, minSeqLen: $minSeqLen (groups: $groups)"

def toFanOut = Channel.one2one()
def toGoP = Channel.one2any()

def emitter = new Emit( output: toFanOut.out(),
            eDetails: dDetails)

def fanOut = new OneFanAny(input: toFanOut.in(),
              outputAny: toGoP.out(),
              destinations: groups)

def poG = new GroupOfPipelineCollects( inputAny: toGoP.in(),
                     stages: 3,
                     rDetails: rDetails,
                     stageOp: [cd.valueList, cd.indicesMap, cd.wordsMap],
                     groups: groups)

def startime = System.currentTimeMillis()
new PAR([emitter, fanOut, poG]).run()
def endtime = System.currentTimeMillis()
println "Time taken = ${endtime - startime} milliseconds\n"


