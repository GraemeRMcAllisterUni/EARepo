package gppDemos.MCpi

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

def emitter = new EmitWithFeedback(
        eDetails: emitData,
        logPhaseName: "emit",
        logPropertyName: "instance")

def fanOut = new OneFanAny(
        destinations: workers)

def farmer = new AnyGroupAny(
        workers: workers,
        function: piData.getWithinOp(),
        logPhaseName: "work",
        logPropertyName: "instance")

def fanIn = new AnyFanOne(
        sources: workers)

def feedBack = new FeedbackBool(
        fDetails: feedbackDetails,
        logPhaseName: "fback",
        logPropertyName: "instance")

def collector = new Collect(
        rDetails: resultDetails,
        logPhaseName: "collect",
        logPropertyName: "instance")

def endtime = System.currentTimeMillis()
def elapsedTime = endtime - startime
println "Time taken = ${elapsedTime} milliseconds"
