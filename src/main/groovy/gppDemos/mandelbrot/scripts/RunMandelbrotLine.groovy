package gppDemos.mandelbrot.scripts

import jcsp.lang.*
import groovyJCSP.*
 
import gppLibrary.connectors.reducers.AnyFanOne
import gppLibrary.connectors.spreaders.OneFanAny
import gppLibrary.functionals.groups.AnyGroupAny
import gppLibrary.terminals.CollectUI
import gppLibrary.terminals.Emit
import gppLibrary.*
 
import java.awt.*
 
import gppDemos.mandelbrot.data.MandelbrotLine as ml
import gppDemos.mandelbrot.data.MandelbrotLineResult as mlr
 

//usage runDemo mandelbrot\scripts\RunMadelbrotLine MBrotB56 workers iterations
 
//int workers = Integer.parseInt(args[0])
//int maxIterations = Integer.parseInt(args[1])
 
 
int workers = 0
int maxIterations = 0
int width = 700                 //1400   700        350
int height = 400                //800    400        200
double pixelDelta = 0.005       //0.0025 0.005      0.01
 
 
if (args.size() == 0){
workers = 4
maxIterations = 100
width = 700
height = 400
pixelDelta = 0.005
}
else {
workers = Integer.parseInt(args[0])
maxIterations = Integer.parseInt(args[1])
width = Integer.parseInt(args[2])
height = Integer.parseInt(args[3])
pixelDelta = Double.parseDouble(args[4])
}
 
print "Line GUI $width, $height, $maxIterations, $workers -> "
long startTime = System.currentTimeMillis()
 
def emitDetails = new DataDetails(dName: ml.getName(),
dInitMethod: ml.init,
dInitData: [width, height, pixelDelta, maxIterations],
dCreateMethod: ml.create)
 
def guiDetails = new ResultDetails( rName: mlr.getName(),
rInitMethod: mlr.init,
rInitData: [width, height, Color.CYAN],
rCollectMethod : mlr.updateDList,
rFinaliseMethod : mlr.finalise )
 
 

//NETWORK

def chan1 = Channel.one2one()
def chan2 = Channel.one2any()
def chan3 = Channel.any2any()
def chan4 = Channel.one2one()

def emit = new Emit(
    // input channel not required
    output: chan1.out(),
    eDetails: emitDetails)
 
def spread = new OneFanAny(
    input: chan1.in(),
    outputAny: chan2.out(),
    destinations: workers)
 
def group = new AnyGroupAny (
    inputAny: chan2.in(),
    outputAny: chan3.out(),
    function: ml.calcColour,
    workers: workers)
 
def reduce = new AnyFanOne (
    inputAny: chan3.in(),
    output: chan4.out(),
    sources: workers)
 
def gui = new CollectUI(
    input: chan4.in(),
    // no output channel required
    guiDetails: guiDetails)

PAR network = new PAR()
 network = new PAR([emit , spread , group , reduce , gui ])
 network.run()
 network.removeAllProcesses()
//END

 
 
long endTime = System.currentTimeMillis()
println " ${endTime - startTime}"
