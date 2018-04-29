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

import groovyJCSP.*

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

//NETWORK

def chan1 = Channel.one2one()
def chan2 = Channel.one2oneArray(mappers)
def chan2OutList = new ChannelOutputList(chan2)
def chan2InList = new ChannelInputList(chan2)
def chan3 = Channel.one2oneArray(mappers)
def chan3OutList = new ChannelOutputList(chan3)
def chan3InList = new ChannelInputList(chan3)
def chan4 = Channel.one2oneArray(mappers)
def chan4OutList = new ChannelOutputList(chan4)
def chan4InList = new ChannelInputList(chan4)
def chan5 = Channel.one2oneArray(reducers)
def chan5OutList = new ChannelOutputList(chan5)
def chan5InList = new ChannelInputList(chan5)
def chan6 = Channel.one2one()

def ewl = new EmitWithLocal(
    // input channel not required
    output: chan1.out(),
    eDetails: eDetails,
    logPhaseName: "ewl",
    logPropertyName: "logInstance")

def ofl = new OneFanList(
    input: chan1.in(),
    outputList: chan2OutList )

def lml = new ListOneMapOneList(
    inputList: chan2InList,
    outputList: chan3OutList,
    mappers: mappers,
    outClassName: mrwc.getName(),
    createClass: mrwc.createWCClass,
    mapFunction: mrl.MRMapFunction,
    logPhaseName: "map",
    logPropertyName: "logInstance")


def ltpwl = new ListThreePhaseWorkerList(
    inputList: chan3InList,
    outputList: chan4OutList,
    workers: mappers,
    inputMethod: mrw.inFunction,
    workMethod: mrw.workFunction,
    outFunction: mrw.outFunction,
    lDetails: tpwDetails,
    logPhaseName: "tpw",
    logPropertyName: "logInstance")

def lrl = new ListReduceList(
    inputList: chan4InList,
    outputList: chan5OutList,
    reducers: reducers,
    reduceFunction: mrwc.reduceFunction,
    outClassName: mrwc.getName(),
    logPhaseName: "lrl",
    logPropertyName: "logInstance")

def red =  new ReducerPrevious(
    inputList: chan5InList,
    output: chan6.out(),
    reduceFunction: mrwc.reduceFunction,
    outClassName: mrwc.getName(),
    logPhaseName: "red",
    logPropertyName: "logInstance")

def collector =  new Collect (
    input: chan6.in(),
    // no output channel required
    rDetails: rDetails,
    logFileName: logFile)

new PAR([ewl , ofl , lml , ltpwl , lrl , red , collector ]).run()

//END



