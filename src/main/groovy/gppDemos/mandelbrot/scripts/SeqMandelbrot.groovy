package gppDemos.mandelbrot.scripts

import gppDemos.mandelbrot.data.MandelbrotLine
import gppDemos.mandelbrot.data.MandelbrotLineCollect
import gppLibrary.DataClass as Constants

// usage runDemo mandelbrot/scripts SeqMbrot resultsFile maxInterations width height pixeldelta

int workers
int maxIterations
int width                  //1400   700        350
int height                 //800    400        200
double pixelDelta         //0.0025 0.005      0.01


if (args.size() == 0){
    workers = 4
    maxIterations = 100
    width = 700
    height = 400
    pixelDelta = 0.005
}
else {
    // assumed to be running via runDemo
//    String folder = args[0] not required
    workers = Integer.parseInt(args[1])
    maxIterations = Integer.parseInt(args[2])
    width = Integer.parseInt(args[3])
    height = Integer.parseInt(args[4])
    pixelDelta = Double.parseDouble(args[5])
}

System.gc()

print "Seq Mbrot, $width, $height, $maxIterations, "
long startTime = System.currentTimeMillis()

def ml = new MandelbrotLine()
def mlc = new MandelbrotLineCollect()
ml.initClass([width, height, pixelDelta, maxIterations])
def mbl = new MandelbrotLine()
int retcode = mbl.createInstance(null)
while  ( retcode ==  Constants.normalContinuation){
    mbl.calcColour(null)
    mlc.collector(mbl)
    mbl = new MandelbrotLine()
    retcode = mbl.createInstance(null)
}

print "W: ${mlc.whiteCount} B: ${mlc.blackCount} "

def endtime = System.currentTimeMillis()
println " ${endtime - startTime}"





