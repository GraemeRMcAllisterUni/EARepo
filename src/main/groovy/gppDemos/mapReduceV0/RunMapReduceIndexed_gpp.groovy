package gppDemos.mapReduceV0

import gppLibrary.*

import gppLibrary.connectors.spreaders.OneFanList
import gppLibrary.functionals.groups.ListOneMapManyList
import gppLibrary.functionals.groups.ListReduceList
import gppLibrary.functionals.groups.ListThreePhaseWorkerList
import gppDemos.mapReduceV0.WCLine as mrl
import gppDemos.mapReduceV0.WCWorker as mrw
import gppDemos.mapReduceV0.WCReadFile as mrrf
import gppDemos.mapReduceV0.WCResult as mrr
import gppDemos.mapReduceV0.WCWordCount as mrwc

import gppLibrary.mapReduce.ReducerPrevious
import gppLibrary.terminals.Collect
import gppLibrary.terminals.EmitWithLocal

// runDemo mapReduceV0/RunMapReduce dataFileName mappers reducers

String title = "ACMmr"
//String title = args[0]
String fileName = "src\\demos\\mapReduceV0\\${title}.txt"

//int mappers = Integer.parseInt(args[1])
//int reducers = Integer.parseInt(args[2])
int mappers = 4
int reducers = 4
int mapOuts = 3
int tpws = mappers * mapOuts


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
long start = System.currentTimeMillis()

def ewl=new EmitWithLocal(eDetails: eDetails)

def ofl=new OneFanList()

def lomml = new ListOneMapManyList( outsPerMap: mapOuts,
                outClassName: mrwc.getName(),
                createClass: mrwc.createWCClass,
                indexingFunction: mrwc.indexFunction,
                mapFunction: mrl.MRMapFunction,
				mappers: mappers)

def ltpwl =  new ListThreePhaseWorkerList(inputMethod: mrw.inFunction,
                 workMethod: mrw.workFunction,
                 outFunction: mrw.outFunction,
                 workers: tpws,
                 lDetails: tpwDetails)

def lrl = new ListReduceList( reducers: reducers,
              reduceFunction: mrwc.reduceFunction,
              outClassName: mrwc.getName())

def red = new ReducerPrevious(reduceFunction: mrwc.reduceFunction,
              outClassName: mrwc.getName())

def col = new Collect (rDetails: rDetails)

long end = System.currentTimeMillis()
println " took ${end - start} millisecsfor $mappers mappers and $reducers reducers and $tpws tpws"
