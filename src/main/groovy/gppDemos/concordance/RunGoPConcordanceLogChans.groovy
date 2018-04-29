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


def title = "bible"
int N = 6
int minSeqLen = 2
//int groups = Integer.parseInt(args[0])

// eclipse versions
def fileName = "src\\demos\\concordance\\${title}.txt"
def outFileName = "src\\demos\\concordance\\${title}GoP"
int groups = 2

// launcher version
//def drive = args[1]
//def fileName = drive + ":\\orgJCSPgpp\\Concordance\\sources\\${title}.txt"
//def outFileName = drive + ":\\orgJCSPgpp\\Concordance\\outputs\\${title}GoP"

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
            eDetails: dDetails,
            logPhaseName: "emit",
            logPropertyName: "strLen")

def fanOut = new OneFanAny(input: toFanOut.in(),
              outputAny: toGoP.out(),
              destinations: groups)

def poG = new GroupOfPipelineCollects( inputAny: toGoP.in(),
                     stages: 3,
                     rDetails: rDetails,
                     stageOp: [cd.valueList, cd.indicesMap, cd.wordsMap],
                     groups: groups,
                     logPhaseNames: ["value", "indeces", "words"],
                     logPropertyName: "strLen",
                     logFileName: "D:\\GPPLog\\concordance")

def startime = System.currentTimeMillis()
new PAR([emitter, fanOut, poG]).run()
def endtime = System.currentTimeMillis()
println "Time taken = ${endtime - startime} milliseconds"
println "Refresh the project F5 to view output in concordance package"


