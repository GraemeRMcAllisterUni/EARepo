package gppDemos.mapReduceV0

import jcsp.lang.Channel

import gppLibrary.DataDetails
import gppLibrary.LocalDetails
import gppLibrary.ResultDetails
import gppLibrary.connectors.spreaders.OneFanList
import gppLibrary.functionals.groups.ListOneMapManyList
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
import groovyJCSP.ChannelInputList
import groovyJCSP.ChannelOutputList
import groovyJCSP.PAR

def title = "ACMmr"
def fileName = "src\\demos\\mapReduceV0\\${title}.txt"

int mappers = 4
int reducers = 4
int mapOuts = 3
int tpws = 12

def chan1 = Channel.one2one()
def chan2 = Channel.one2oneArray(mappers)
def chan2InList = new ChannelInputList(chan2)
def chan2OutList = new ChannelOutputList(chan2)
def chan3 = Channel.one2oneArray(tpws)
def chan3InList = new ChannelInputList(chan3)
def chan3OutList = new ChannelOutputList(chan3)
def chan4 = Channel.one2oneArray(tpws)
def chan4InList = new ChannelInputList(chan4)
def chan4OutList = new ChannelOutputList(chan4)
def chan5 = Channel.one2oneArray(reducers)
def chan5InList = new ChannelInputList(chan5)
def chan5OutList = new ChannelOutputList(chan5)
def chan6 = Channel.one2one()

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

def network = []

network << new EmitWithLocal(output: chan1.out(),
                 eDetails: eDetails)

network << new OneFanList(input: chan1.in(),
              outputList: chan2OutList)

network << new ListOneMapManyList(inputList: chan2InList,
               outputList: chan3OutList,
               mappers: mappers,
               outsPerMap: mapOuts,
               outClassName: mrwc.getName(),
               createClass: mrwc.createWCClass,
               indexingFunction: mrwc.indexFunction,
               mapFunction: mrl.MRMapFunction)


network << new ListThreePhaseWorkerList(inputList: chan3InList,
                    outputList: chan4OutList,
                    workers: tpws,
                    inputMethod: mrw.inFunction,
                    workMethod: mrw.workFunction,
                    outFunction: mrw.outFunction,
                    lDetails: tpwDetails)

network << new ListReduceList(inputList: chan4InList,
                outputList: chan5OutList,
                reducers: reducers,
                reduceFunction: mrwc.reduceFunction,
                outClassName: mrwc.getName())

network << new ReducerPrevious(inputList: chan5InList,
             output: chan6.out(),
             reduceFunction: mrwc.reduceFunction,
             outClassName: mrwc.getName())

network << new Collect (input: chan6.in(),
            rDetails: rDetails)
long start = System.currentTimeMillis()
new PAR( network ).run()
long end = System.currentTimeMillis()
println " took ${end - start} millisecs"
