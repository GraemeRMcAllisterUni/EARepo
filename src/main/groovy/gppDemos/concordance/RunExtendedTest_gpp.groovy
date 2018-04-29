package gppDemos.concordance

import gppLibrary.DataDetails
import gppLibrary.LocalDetails
import gppLibrary.ResultDetails
import gppLibrary.connectors.reducers.ListFanOne
import gppLibrary.connectors.spreaders.OneFanList
import gppLibrary.functionals.groups.ListGroupList
import gppLibrary.functionals.transformers.CombineNto1
import gppLibrary.terminals.Collect
import gppLibrary.terminals.Emit
import gppLibrary.terminals.EmitFromInput
import gppDemos.concordance.ConcordanceWords as cw
import gppDemos.concordance.ConcordanceCombine as cc
import gppDemos.concordance.ConcordanceData as cd


//usage runDemo concordance/RunExtendedTest resultFile title N blockWorkers blockSize

int blockWorkers = 2
String title = "bible"
int N = 8
int minSeqLen = 2
int blockSize = 16000
boolean doFileOutput = false

if (args.size() == 0){
    blockWorkers = 3
    title = "bible"
	N = 8
    blockSize = 64000
}
else {
    title = args[0]
	N = Integer.parseInt(args[1])
    blockWorkers = Integer.parseInt(args[2])
    blockSize = Integer.parseInt(args[3])
}

// eclipse versions
def fileName = "src\\demos\\concordance\\${title}.txt"
def outFileName = "src\\demos\\concordance\\${title}PoG"

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

def rDetails = new ResultDetails( rName: cd.getName(),
    rInitMethod: cd.init,
   rCollectMethod: cd.collector,
    rFinaliseMethod: cd.finaliseCollect,
    rFinaliseData: [null])


System.gc()
print "ExtendedTest, $title, $blockWorkers, $blockSize, "

def startime = System.currentTimeMillis()

def emit = new Emit (eDetails: dDetails)

def fos = new OneFanList()

def group = new ListGroupList(function: cw.processBuffer,
            workers: blockWorkers)

def fis = new ListFanOne()


def combine = new CombineNto1(localDetails: localData,
                outDetails: outData,
                combineMethod: cc.appendBuff )

def emitInstances = new EmitFromInput( eDetails: outData)


def collector = new Collect(rDetails: rDetails)

def endtime = System.currentTimeMillis()
println " ${endtime - startime}"
