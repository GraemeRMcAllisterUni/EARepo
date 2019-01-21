package gppDemos.QuickSort

import gppLibrary.DataDetails
import gppLibrary.LocalDetails
import gppLibrary.ResultDetails
import gppLibrary.connectors.reducers.N_WayMerge
import gppLibrary.connectors.spreaders.OneDirectedList
import gppLibrary.functionals.groups.ListThreePhaseWorkerList
import gppLibrary.terminals.Collect
import gppLibrary.terminals.Emit
import gppDemos.QuickSort.QSData as qsd
import gppDemos.QuickSort.QSResult as qsr
import gppDemos.QuickSort.QSWorker as qsw

//usage runDemo QuickSort RunGroupQuickSort resultsFile workers instances


int workers
int instances
if (args.size() == 0){
    workers = 4
    instances = 5000
}
else {
//    String folder = args[0] not used
    workers = Integer.parseInt(args[1])
    instances = Integer.parseInt(args[2])
}
print "QuickSort $workers, $instances, "

System.gc()
def startime = System.currentTimeMillis()

def emitterDetails = new DataDetails(dName: qsd.getName() ,
                     dInitMethod: qsd.init,
					 dInitData: [instances, workers],
					 dCreateMethod: qsd.create)

def resultDetails = new ResultDetails(rName: qsr.getName(),
                    rInitMethod: qsr.init,
                    rCollectMethod:  qsr.collector,
                    rFinaliseMethod: qsr.finalise)

def workerDetails = new LocalDetails( lName: qsw.getName(),
                    lInitMethod: qsw.init)

def emitter = new Emit( eDetails: emitterDetails )

def fan = new OneDirectedList ( indexProperty: "batch")

def tpws = new ListThreePhaseWorkerList(inputMethod: qsw.inFunction,
                    workMethod: qsw.workFunction,
                    outFunction: qsw.outFunction,
                    lDetails: workerDetails,
                    workers: workers)


def merge = new N_WayMerge(mergeChoice: qsd.mergeChoice,
                    inClassName: qsd.getName())

def collector = new Collect( rDetails: resultDetails)


def endtime = System.currentTimeMillis()
println " ${endtime - startime}"


