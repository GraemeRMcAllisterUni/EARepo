package gppDemos.mapReduceV0

import jcsp.lang.*
import groovyJCSP.*

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

String title = "biblemr"
//String title = args[0]
String fileName = "src\\demos\\mapReduceV0\\${title}.txt"

//int mappers = Integer.parseInt(args[1])
//int reducers = Integer.parseInt(args[2])
int mappers = 2
int reducers = 2
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


//NETWORK

def chan1 = Channel.one2one()
def chan2 = Channel.one2oneArray(mappers)
def chan2OutList = new ChannelOutputList(chan2)
def chan2InList = new ChannelInputList(chan2)
def chan3 = Channel.one2oneArray(tpws)
def chan3OutList = new ChannelOutputList(chan3)
def chan3InList = new ChannelInputList(chan3)
def chan4 = Channel.one2oneArray(tpws)
def chan4OutList = new ChannelOutputList(chan4)
def chan4InList = new ChannelInputList(chan4)
def chan5 = Channel.one2oneArray(reducers)
def chan5OutList = new ChannelOutputList(chan5)
def chan5InList = new ChannelInputList(chan5)
def chan6 = Channel.one2one()

def ewl=new EmitWithLocal(
    // input channel not required
    output: chan1.out(),
    eDetails: eDetails)

def ofl=new OneFanList(
    input: chan1.in(),
    outputList: chan2OutList )

def lomml = new ListOneMapManyList(
    inputList: chan2InList,
    outputList: chan3OutList,
    outsPerMap: mapOuts,
    outClassName: mrwc.getName(),
    createClass: mrwc.createWCClass,
    indexingFunction: mrwc.indexFunction,
    mapFunction: mrl.MRMapFunction,
    mappers: mappers)

def ltpwl =  new ListThreePhaseWorkerList(
    inputList: chan3InList,
    outputList: chan4OutList,
    inputMethod: mrw.inFunction,
    workMethod: mrw.workFunction,
    outFunction: mrw.outFunction,
    workers: tpws,
    lDetails: tpwDetails)

def lrl = new ListReduceList(
    inputList: chan4InList,
    outputList: chan5OutList,
    reducers: reducers,
    reduceFunction: mrwc.reduceFunction,
    outClassName: mrwc.getName())

def red = new ReducerPrevious(
    inputList: chan5InList,
    output: chan6.out(),
    reduceFunction: mrwc.reduceFunction,
    outClassName: mrwc.getName())

def col = new Collect (
    input: chan6.in(),
    // no output channel required
    rDetails: rDetails)

new PAR([ewl, ofl, lomml , ltpwl , lrl , red , col ]).run()

//END


long end = System.currentTimeMillis()
println " took ${end - start} millisecsfor $mappers mappers and $reducers reducers and $tpws tpws"
