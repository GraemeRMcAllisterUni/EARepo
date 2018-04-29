package gppDemos.concordance

import gppLibrary.DataDetails
import gppLibrary.LocalDetails
import gppLibrary.ResultDetails
import gppLibrary.connectors.reducers.ListFanOne
import gppLibrary.connectors.spreaders.OneFanAny
import gppLibrary.connectors.spreaders.OneFanList
import gppLibrary.functionals.composites.GroupOfPipelineCollects
import gppLibrary.functionals.groups.ListGroupList
import gppLibrary.terminals.*
import gppLibrary.functionals.transformers.CombineNto1
import groovyJCSP.*
import jcsp.lang.*
import gppDemos.concordance.ConcordanceWords as cw
import gppDemos.concordance.ConcordanceCombine as cc
import gppDemos.concordance.ConcordanceResults as cr
import gppDemos.concordance.ConcordanceData as cd

def title = "bible"
int N = 6
int minSeqLen = 2

// eclipse versions
def blockWorkers = 2
def pogWorkers = 2
def blockSize = 4000
def fileName = "src\\demos\\concordance\\${title}.txt"
def outFileName = "src\\demos\\concordance\\${title}par_Ex"

//int workers = Integer.parseInt(args[0])
//def pogWorkers = Integer.parseInt(args[1])
//def blockSize = Integer.parseInt(args[2])
//def drive = args[3]
//def fileName = drive + ":\\orgJCSPgpp\\Concordance\\sources\\${title}.txt"
//def outFileName = drive + ":\\orgJCSPgpp\\Concordance\\outputs\\${title}X_PoG"

println "X PoG - $title : N: $N, minSeqLen: $minSeqLen (workers: $blockWorkers, PoGworkers: $pogWorkers, blockSize: $blockSize)"
System.gc()
def startime = System.currentTimeMillis()

def EmitToFoS = Channel.one2one()
def FoSToGroup = Channel.one2oneArray(blockWorkers)
def GroupToFiS = Channel.one2oneArray(blockWorkers)
def FiSToCombine = Channel.one2one()
def CombineToEmitFI = Channel.one2one()
def EmitToOut = Channel.one2one()
def toFanOut = Channel.one2one()
def toPoG = Channel.one2any()


def F2Gout = new ChannelOutputList(FoSToGroup)
def F2Gin = new ChannelInputList(FoSToGroup)

def G2Fout = new ChannelOutputList(GroupToFiS)
def G2Fin = new ChannelInputList(GroupToFiS)

def dDetails = new DataDetails( dName: cw.getName(),
                dInitMethod: cw.init,
                dInitData: [fileName, blockSize],
                dCreateMethod: cw.create,
                dCreateData: [null])

def emit = new Emit (output: EmitToFoS.out(),
           eDetails: dDetails)

def fos = new OneFanList(input: EmitToFoS.in(),
            outputList: F2Gout)

def group = new ListGroupList(inputList: F2Gin,
            outputList: G2Fout,
            function: cw.processBuffer,
            workers: blockWorkers)

def fis = new ListFanOne(inputList: G2Fin,
             output: FiSToCombine.out())

def localData = new LocalDetails(lName: cc.getName(),
                 lInitData: [N, outFileName],
                 lInitMethod: cc.init,
                 lFinaliseMethod: cc.finalise)

def outData = new LocalDetails( lName:cd.getName(),
                lInitMethod: cd.init,
                lInitData: null,
                lCreateMethod: cd.create,
                lFinaliseMethod: cd.finalise)

def combine = new CombineNto1(input: FiSToCombine.in(),
                output: CombineToEmitFI.out(),
                localDetails: localData,
                outDetails: outData,
                combineMethod: cc.appendBuff )

def emitInstances = new EmitFromInput(input: CombineToEmitFI.in(),
                    output: toFanOut.out(),
                    eDetails: outData)

def fanOut = new OneFanAny( input: toFanOut.in(),
              outputAny: toPoG.out(),
              destinations: pogWorkers)

def rDetails = new ResultDetails( rName: cr.getName(),
    rInitMethod: cr.init,
    rInitData: [minSeqLen],
    rCollectMethod: cr.collector,
    rFinaliseMethod: cr.finalise,
    rFinaliseData: [null])

def poG = new GroupOfPipelineCollects( inputAny: toPoG.in(),
                stages: 3,
                rDetails: rDetails,
                stageOp: [cd.valueList, cd.indicesMap, cd.wordsMap],
                groups: pogWorkers)

def network = [emit, fos, group, fis, combine, emitInstances, fanOut, poG]
new PAR(network).run()
def endtime = System.currentTimeMillis()
println "Time taken = ${endtime - startime} milliseconds"
println "Refresh the project F5 to view output in concordance package"

