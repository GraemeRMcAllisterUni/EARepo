package gppDemos.MCpi

import jcsp.lang.*
import groovyJCSP.*
 
import gppDemos.MCpi.MCpiData as piData
import gppDemos.MCpi.MCpiResults as piResults
import gppLibrary.DataDetails
import gppLibrary.ResultDetails
import gppLibrary.patterns.DataParallelCollect
 

//usage runDemo MCpi RunMCpiDPCPattern resultsFile workers instances iterations
 
int workers
int instances
int iterations
 
if (args.size() == 0 ) {
workers = 4
instances =1024
iterations = 100000
}
else {
//    String folder = args[0] not required
workers = Integer.parseInt(args[1])
instances = Integer.parseInt(args[2])
iterations = Integer.parseInt(args[3])
}
 
System.gc()
print "MCpi DPC pattern, $workers, $instances, $iterations, "
def startime = System.currentTimeMillis()
 
 
DataDetails emitData = new DataDetails( dName: piData.getName(),
dInitMethod: piData.init,
dInitData: [instances],
dCreateMethod: piData.create,
dCreateData: [iterations])
 
 
ResultDetails resultDetails = new ResultDetails(rName: piResults.getName(),
rInitMethod: piResults.init,
rCollectMethod: piResults.collector,
rFinaliseMethod: piResults.finalise)
 
 

//NETWORK


def piFarm = new DataParallelCollect (
    eDetails: emitData,
    rDetails: resultDetails,
    workers: workers,
    function: piData.withinOp )

piFarm .run()

//END

 
 
 
def endtime = System.currentTimeMillis()
println "${endtime - startime} "
