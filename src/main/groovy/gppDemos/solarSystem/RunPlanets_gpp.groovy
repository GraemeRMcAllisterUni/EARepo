package gppDemos.solarSystem

import gppDemos.solarSystem.PlanetrySystem as ps
import gppLibrary.DataDetails
import gppLibrary.ResultDetails
import gppLibrary.functionals.matrix.MultiCoreEngine
import gppLibrary.terminals.Collect
import gppLibrary.terminals.Emit
import gppDemos.solarSystem.PlanetryResult as pr

// usage runDemo nbody/ParNbody resultFile N nodes iterations

int nodes = 0
int N = 0
int iterations = 0
double dt = 1e11

if (args.size() == 0){
    N = 100
    nodes = 2
    iterations = 100
}
else {
    N = Integer.parseInt(args[0])
    nodes = Integer.parseInt(args[1])
    iterations = Integer.parseInt(args[2])
}


 String readPath = "./planets_list.txt"
 String writePath = "./result_${iterations}_${N}_planets_${nodes}_Par.txt"

System.gc()
print "RunPlanets (arrayList of plants) $iterations, $N , $nodes ->"
long startTime = System.currentTimeMillis()


def eDetails = new DataDetails (dName: ps.getName(),
                 dCreateMethod: ps.createMethod,
                 dInitMethod: ps.initMethod,
                 dCreateData: [readPath, N, dt])

def rDetails = new ResultDetails(rName: pr.getName(),
                 rInitMethod: pr.init,
                 rInitData: [writePath,],
                 rCollectMethod: pr.collector,
                 rFinaliseMethod: pr.finalise)

def emit = new Emit( eDetails: eDetails)

def mcEngine = new MultiCoreEngine (nodes: nodes,
                finalOut: true,
                iterations: iterations,
                partitionMethod: ps.partitionMethod,
                calculationMethod: ps.calculationMethod,
                updateMethod: ps.updateMethod )

def collector = new Collect(rDetails: rDetails)


long endTime = System.currentTimeMillis()
println " ${endTime - startTime}"









