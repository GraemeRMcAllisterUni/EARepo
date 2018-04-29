package gppDemos.jacobi

import gppLibrary.DataDetails
import gppLibrary.ResultDetails
import gppLibrary.functionals.matrix.MultiCoreEngine
import gppLibrary.terminals.Collect
import gppLibrary.terminals.Emit
import gppDemos.jacobi.JacobiDataMC as jd
import gppDemos.jacobi.JacobiResultMC as jr

/**
 * A demonstration of the matrix.MultiCoreEngine process to evaluate the solution to a matrix
 * containing a set of simultaneous equations.  The process network assumes that the input file will
 * contain a single matrix or a number of matrices of different sizes.  The file Jacobi.txt containes
 * four matrices and the remaining files Jacobinnn contain a single matrix of dimension nnn.  Scripts
 * are provided to create the required input file.
 */


int nodes = 0
String title = ""

//usage runDemo jacobi/RunJacobiMC JacobiB56 title nodes

if (args.size() == 0){
    nodes = 4
    title = "Jacobi1024"
}
else {
    nodes = Integer.parseInt(args[1])
    title = args[0]
}

def fileName = "src\\demos\\jacobi\\${title}.txt"

double margin = 1.0E-16

System.gc()
print "ParJacobi $title, $nodes, "
long startTime = System.currentTimeMillis()

def eDetails = new DataDetails (dName: jd.getName(),
                 dCreateMethod: jd.createMethod,
                 dInitMethod: jd.initMethod,
                 dInitData: [fileName])

def rDetails = new ResultDetails(rName: jr.getName(),
                 rInitMethod: jr.init,
                 rCollectMethod: jr.collector,
                 rFinaliseMethod: jr.finalise)

def emit = new Emit( eDetails: eDetails)

def mcEngine = new MultiCoreEngine (nodes: nodes,
                errorMargin: margin,
                finalOut: true,
                partitionMethod: jd.partitionMethod,
                calculationMethod: jd.calculationMethod,
                errorMethod: jd.errorMethod,
                updateMethod: jd.updateMethod )

def collector = new Collect(rDetails: rDetails)


long endTime = System.currentTimeMillis()
println " ${endTime - startTime}"

