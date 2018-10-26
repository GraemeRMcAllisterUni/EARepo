package gppDemos.MCpi

import jcsp.lang.*
import groovyJCSP.*
 
import gppLibrary.DataDetails
import gppLibrary.FeedbackDetails
import gppLibrary.ResultDetails
import gppLibrary.connectors.reducers.AnyFanOne
import gppLibrary.connectors.spreaders.OneFanAny
import gppLibrary.functionals.groups.AnyGroupAny
import gppLibrary.terminals.*
import gppLibrary.functionals.transformers.FeedbackBool
 
import gppDemos.MCpi.MCpiData as piData
import gppDemos.MCpi.MCpiResults as piResults
import gppDemos.MCpi.MCpiFeedback as piFBack
 
 

//double errorMargin = 0.000001   // will end with less than 2048 instances
double errorMargin = 0.000000001   // will end after 2048 instances
 
int workers = 4
int instances = 2048
int iterations = 100000
 
println "run feedback pi workers = $workers, max instances = $instances, iterations = $iterations, error = $errorMargin"
System.gc()
def emitData = new DataDetails(dName: piData.getName(),
dInitMethod: piData.init,
dInitData: [instances],
dCreateMethod: piData.create,
dCreateData: [iterations])
 
 
def resultDetails = new ResultDetails(rName: piResults.getName(),
rInitMethod: piResults.init,
rCollectMethod: piResults.collector,
rFinaliseMethod: piResults.finalise)
 
def feedbackDetails = new FeedbackDetails(fName: piFBack.getName(),
fInitMethod: piFBack.initClass,
fInitData: [errorMargin],
fMethod: piFBack.feedbackBool)
 
def startime = System.currentTimeMillis()
 
//@log 1 "./PiLog-"

import gppLibrary.Logger
import gppLibrary.LoggingVisualiser

def logChan = Channel.any2one()
Logger.initLogChannel(logChan.out())
def logVis = new LoggingVisualiser ( logInput: logChan.in(), 
                     collectors: 1,
                     logFileName: "./PiLog-" )

 

//NETWORK

def feedbackChan = Channel.one2one()
def chan1 = Channel.one2one()
def chan2 = Channel.one2any()
def chan3 = Channel.any2any()
def chan4 = Channel.one2one()
def chan5 = Channel.one2one()

def emitter = new EmitWithFeedback(
    feedback: feedbackChan.in(),
    output: chan1.out(),
    eDetails: emitData,
    logPhaseName: "emit",
    logPropertyName: "instance")
 
def fanOut = new OneFanAny(
    input: chan1.in(),
    outputAny: chan2.out(),
    destinations: workers)
 
def farmer = new AnyGroupAny(
    inputAny: chan2.in(),
    outputAny: chan3.out(),
    workers: workers,
    function: piData.getWithinOp(),
    logPhaseName: "work",
    logPropertyName: "instance")
 
def fanIn = new AnyFanOne(
    inputAny: chan3.in(),
    output: chan4.out(),
    sources: workers)
 
def feedBack = new FeedbackBool(
    input: chan4.in(),
    output: chan5.out(),
    feedback: feedbackChan.out(),
    fDetails: feedbackDetails,
    logPhaseName: "fback",
    logPropertyName: "instance")
 
def collector = new Collect(
    input: chan5.in(),
    visLogChan : logChan.out(),
    // no output channel required
    rDetails: resultDetails,
    logPhaseName: "collect",
    logPropertyName: "instance")

PAR network = new PAR()
 network = new PAR([logVis, emitter , fanOut , farmer , fanIn , feedBack , collector ])
 network.run()
 network.removeAllProcesses()
//END

 
def endtime = System.currentTimeMillis()
def elapsedTime = endtime - startime
println "Time taken = ${elapsedTime} milliseconds"
