package gppDemos.MCpi

import gppDemos.MCpi.MCpiData as piData
import gppDemos.MCpi.MCpiResults as piResults
import gppLibrary.DataDetails
import gppLibrary.ResultDetails
import gppLibrary.patterns.DataParallelCollect

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


def piFarm = new DataParallelCollect (
          eDetails: emitData,
          rDetails: resultDetails,
          workers: workers,
          function: piData.withinOp )



def endtime = System.currentTimeMillis()
println "${endtime - startime} "
