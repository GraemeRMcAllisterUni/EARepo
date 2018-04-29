package gppDemos.mapReduceV0

import gppLibrary.DataDetails
import gppLibrary.LocalDetails
import gppLibrary.ResultDetails
import gppLibrary.connectors.spreaders.OneFanList
import gppLibrary.functionals.groups.ListOneMapOneList
import gppLibrary.functionals.groups.ListReduceList
import gppLibrary.functionals.groups.ListThreePhaseWorkerList
import gppLibrary.mapReduce.ReducerPrevious
import gppLibrary.terminals.Collect
import gppLibrary.terminals.EmitWithLocal
import gppDemos.mapReduceV0.WCLine as mrl
import gppDemos.mapReduceV0.WCWorker as mrw
import gppDemos.mapReduceV0.WCReadFile as mrrf
import gppDemos.mapReduceV0.WCResult as mrr
import gppDemos.mapReduceV0.WCWordCount as mrwc

def title = "ACMmr"
def fileName = ".\\src\\demos\\mapReduceV0\\${title}.txt"
def logFile = ".\\src\\demos\\mapReduceV0\\${title}"

int mappers = 12
int reducers = 4

def eDetails = new DataDetails(lName: mrrf.getName(),
                 lInitMethod: mrrf.initMRReadFile,
                 lInitData: [fileName],
                 dName: mrl.getName(),
                 dCreateMethod: mrl.createMRLine,
                 dInitMethod: mrl.initMRLine)

def rDetails = new ResultDetails(rName: mrr.getName(),
  rInitMethod: mrr.init,
  rInitData: ["The Result is:"],
  rCollectMethod: mrr.collector,
  rFinaliseMethod: mrr.finalise,
  rFinaliseData: ["Words Collected ="])

def tpwDetails = new LocalDetails(lName: mrw.getName(),
                  lInitMethod: mrw.init)

System.gc()
def ewl = new EmitWithLocal(eDetails: eDetails,
              logPhaseName: "ewl",
              logPropertyName: "logInstance")

def ofl = new OneFanList( )

def lml = new ListOneMapOneList(mappers: mappers,
               outClassName: mrwc.getName(),
               createClass: mrwc.createWCClass,
               mapFunction: mrl.MRMapFunction,
              logPhaseName: "map",
              logPropertyName: "logInstance")


def ltpwl = new ListThreePhaseWorkerList(workers: mappers,
                    inputMethod: mrw.inFunction,
                    workMethod: mrw.workFunction,
                    outFunction: mrw.outFunction,
                    lDetails: tpwDetails,
              logPhaseName: "tpw",
              logPropertyName: "logInstance")

def lrl = new ListReduceList(reducers: reducers,
                reduceFunction: mrwc.reduceFunction,
                outClassName: mrwc.getName(),
              logPhaseName: "lrl",
              logPropertyName: "logInstance")

def red =  new ReducerPrevious(reduceFunction: mrwc.reduceFunction,
             outClassName: mrwc.getName(),
              logPhaseName: "red",
              logPropertyName: "logInstance")

def collector =  new Collect (rDetails: rDetails,
                logFileName: logFile)


