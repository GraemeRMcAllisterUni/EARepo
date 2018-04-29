package gppDemos.MCpi

import gppLibrary.DataDetails
import gppLibrary.FeedbackDetails
import gppLibrary.ResultDetails
import gppLibrary.connectors.reducers.AnyFanOne
import gppLibrary.connectors.spreaders.OneFanAny
import gppLibrary.functionals.groups.AnyGroupAny
import gppLibrary.terminals.*
import gppLibrary.functionals.transformers.FeedbackBool
import jcsp.lang.*
import groovyJCSP.*
import gppDemos.MCpi.MCpiData as piData
import gppDemos.MCpi.MCpiResults as piResults
import gppDemos.MCpi.MCpiFeedback as piFBack

def toFanOut = Channel.one2one()
def toFarm = Channel.one2any()
def fromFarm =Channel.any2one()
def toFeedback = Channel.one2one()
def feedbackChan = Channel.one2one()
def collectChan = Channel.one2one()

String dataClassName = MCpiData.getName()
String resultsClassName = MCpiResults.getName()
String fbClassname = MCpiFeedback.getName()

float errorMargin = 0.0000001

int workers = 4
int instances =1024
int iterations = 100000

println "run feedback pi workers = $workers, instances = $instances, iterations per instance = $iterations"
System.gc()
def emitData = new DataDetails( dName: piData.getName(),
                dInitMethod: piData.init,
                dInitData: [instances],
                dCreateMethod: piData.create,
                dCreateData: [iterations])


def resultDetails = new ResultDetails( rName: piResults.getName(),
                     rInitMethod: piResults.init,
                     rCollectMethod: piResults.collector,
                     rFinaliseMethod: piResults.finalise)

def feedbackDetails = new FeedbackDetails( fName: piFBack.getName(),
                       fInitMethod:  piFBack.initClass ,
                       fInitData: [errorMargin],
                       fMethod: piFBack.feedbackBool)

def startime = System.currentTimeMillis()
  def emitter = new EmitWithFeedback( output: toFanOut.out(),
                    feedback: feedbackChan.in(),
                    eDetails: emitData)

  def fanOut = new OneFanAny(input: toFanOut.in(),
              outputAny: toFarm.out(),
              destinations: workers)

  def farmer = new AnyGroupAny ( inputAny: toFarm.in(),
                outputAny: fromFarm.out(),
                workers: workers,
                function: piData.getWithinOp())

  def fanIn = new AnyFanOne(inputAny: fromFarm.in(),
             output: toFeedback.out(),
             sources: workers)

  def feedBack = new FeedbackBool(input: toFeedback.in(),
                output: collectChan.out(),
                feedback: feedbackChan.out(),
                fDetails: feedbackDetails )

  def collector = new Collect( input: collectChan.in(),
                  rDetails: resultDetails )

  new PAR([emitter, fanOut, farmer, fanIn, feedBack, collector]).run()
def endtime = System.currentTimeMillis()
def elapsedTime = endtime - startime
println "Time taken = ${elapsedTime} milliseconds"
