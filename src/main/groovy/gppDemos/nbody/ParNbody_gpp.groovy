package gppDemos.nbody

import gppLibrary.DataDetails
import gppLibrary.ResultDetails
import gppLibrary.functionals.matrix.MultiCoreEngine
import gppLibrary.terminals.Collect
import gppLibrary.terminals.Emit
import gppDemos.nbody.NbodyData as nd
import gppDemos.nbody.NbodyResults as nr

// usage runDemo nbody/ParNbody outFile N nodes iterations

int nodes = 0
int N = 0
int iterations = 100
double dt = 1e11

if (args.size() == 0){
    N = 100
    nodes = 2
//    iterations = 100
}
else {
    N = Integer.parseInt(args[0])
    nodes = Integer.parseInt(args[1])
//    iterations = Integer.parseInt(args[2])
}

 String readPath = "./planets_list.txt"
 String writePath = "./${N}_planets_${nodes}_Par.txt"

System.gc()
print "ParNbody (matrix of Planet data), $N , $nodes, "
long startTime = System.currentTimeMillis()


def eDetails = new DataDetails (dName: nd.getName(),
                 dCreateMethod: nd.createMethod,
                 dInitMethod: nd.initMethod,
                 dInitData: [readPath, N, dt])

def rDetails = new ResultDetails(rName: nr.getName(),
                 rInitMethod: nr.init,
                 rInitData: [writePath],
                 rCollectMethod: nr.collector,
                 rFinaliseMethod: nr.finalise)

def emit = new Emit( eDetails: eDetails)

def mcEngine = new MultiCoreEngine (nodes: nodes,
                finalOut: true,
                iterations: iterations,
                partitionMethod: nd.partitionMethod,
                calculationMethod: nd.calculationMethod,
                updateMethod: nd.updateMethod )

def collector = new Collect(rDetails: rDetails)


long endTime = System.currentTimeMillis()
println " ${endTime - startTime}"


