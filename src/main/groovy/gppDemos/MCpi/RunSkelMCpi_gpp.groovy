package gppDemos.MCpi

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

def emit = new Emit (eDetails: emitData)

def ofa = new OneFanAny (destinations: workers)

def group = new AnyGroupAny (workers: workers, function: piData.withinOp)

def afo = new AnyFanOne (sources: workers)

def collector = new Collect (rDetails: resultDetails)

def endtime = System.currentTimeMillis()
println " ${endtime - startime} "
