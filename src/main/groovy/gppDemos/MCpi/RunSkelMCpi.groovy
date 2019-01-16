package gppDemos.MCpi

import jcsp.lang.*
import groovyJCSP.*
 
import gppLibrary.*
import gppLibrary.connectors.reducers.*
import gppLibrary.connectors.spreaders.*
import gppLibrary.terminals.*
import gppLibrary.functionals.groups.*
import gppDemos.MCpi.MCpiData as piData
import gppDemos.MCpi.MCpiResults as piResults
 

//usage runDemo MCpi/RunSkelMCpi resultsFile workers instances iterations
 
int workers = 0
int instances = 0
int iterations = 0
 
if (args.size() == 0 ) {
workers = 4
instances =1024
iterations = 100000
}
else {
workers = Integer.parseInt(args[0])
instances = Integer.parseInt(args[1])
iterations = Integer.parseInt(args[2])
}
 
DataDetails emitData = new DataDetails( dName: piData.getName(),
dInitMethod: piData.init,
dInitData: [instances],
dCreateMethod: piData.create,
dCreateData: [iterations])
 
 
ResultDetails resultDetails = new ResultDetails(rName: piResults.getName(),
rInitMethod: piResults.init,
rCollectMethod: piResults.collector,
rFinaliseMethod: piResults.finalise)
 
System.gc()
 
print """SkelMCpi $workers, $instances, $iterations, """
def startime = System.currentTimeMillis()
 

//NETWORK

def chan1 = Channel.one2one()
def chan2 = Channel.one2any()
def chan3 = Channel.any2any()
def chan4 = Channel.one2one()

def emit = new Emit (
    // input channel not required
    output: chan1.out(),
    eDetails: emitData)
 
def ofa = new OneFanAny (
    input: chan1.in(),
    outputAny: chan2.out(),
    destinations: workers)
 
def group = new AnyGroupAny (
    inputAny: chan2.in(),
    outputAny: chan3.out(),
    workers: workers, function: piData.withinOp)
 
def afo = new AnyFanOne (
    inputAny: chan3.in(),
    output: chan4.out(),
    sources: workers)
 
def collector = new Collect (
    input: chan4.in(),
    // no output channel required
    rDetails: resultDetails)

PAR network = new PAR()
 network = new PAR([emit , ofa , group , afo , collector ])
 network.run()
 network.removeAllProcesses()
//END

 
def endtime = System.currentTimeMillis()
println " ${endtime - startime} "
