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

// runDemo mapReduceV0/RunMapReduce dataFileName mappers reducers


String title = "ACMmr"
//String title = args[0]
String fileName = "src\\demos\\mapReduceV0\\${title}.txt"

//int mappers = Integer.parseInt(args[1])
//int reducers = Integer.parseInt(args[2])
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
print "WordCount $mappers, $reducers -> "
def startime = System.currentTimeMillis()

def ewl = new EmitWithLocal(eDetails: eDetails)

def ofl = new OneFanList( )

def lml = new ListOneMapOneList(mappers: mappers,
               outClassName: mrwc.getName(),
               createClass: mrwc.createWCClass,
               mapFunction: mrl.MRMapFunction)


def ltpwl = new ListThreePhaseWorkerList(workers: mappers,
                    inputMethod: mrw.inFunction,
                    workMethod: mrw.workFunction,
                    outFunction: mrw.outFunction,
                    lDetails: tpwDetails)

def lrl = new ListReduceList(reducers: reducers,
                reduceFunction: mrwc.reduceFunction,
                outClassName: mrwc.getName())

def red =  new ReducerPrevious(reduceFunction: mrwc.reduceFunction,
             outClassName: mrwc.getName())

def collector =  new Collect (rDetails: rDetails)

def endtime = System.currentTimeMillis()

println " ${endtime - startime}"

