package gppDemos.mandelbrot.scripts

import gppLibrary.DataDetails
import gppLibrary.ResultDetails
import gppLibrary.connectors.reducers.AnyFanOne
import gppLibrary.connectors.spreaders.OneFanAny
import gppLibrary.functionals.groups.AnyGroupAny
import gppLibrary.terminals.CollectUI
import gppLibrary.terminals.Emit

import java.awt.*

import gppDemos.mandelbrot.data.MandelbrotPixel as mp
import gppDemos.mandelbrot.data.MandelbrotResult as mr

//usage runDemo mandelbrot\scripts\RunMadelbrot MBrotB56 workers iterations width height delta

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

System.gc()

print "Pixel GUI $width, $height, $maxIterations, $workers -> "
long startTime = System.currentTimeMillis()

def emitDetails = new DataDetails(dName: mp.getName(),
                  dInitMethod: mp.init,
                  dInitData: [width, height, pixelDelta, maxIterations],
                  dCreateMethod: mp.create)

def guiDetails = new ResultDetails( rName: mr.getName(),
                  rInitMethod: mr.init,
                  rInitData: [width, height, Color.CYAN],
                  rCollectMethod : mr.updateDList,
                  rFinaliseMethod : mr.finalise )

def emit = new Emit(eDetails: emitDetails)

def spread = new OneFanAny(destinations: workers)

def group = new AnyGroupAny ( function: mp.calcColour,
                workers: workers)

def reduce = new AnyFanOne (sources: workers)

def gui = new CollectUI(guiDetails: guiDetails)


long endTime = System.currentTimeMillis()
println " ${endTime - startTime}"
