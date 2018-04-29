package gppDemos.mandelbrot.scripts

import gppDemos.mandelbrot.data.MandelbrotLine
import gppDemos.mandelbrot.data.MandelbrotLineCollect
import gppLibrary.DataClass as Constants

// usage runDemo mandelbrot/scripts/SeqMbrot MbrotB56 maxInterations width height pixeldelta



int maxIterations = Integer.parseInt(args[0])

int width = Integer.parseInt(args[1])               //700       350     1400
int height = Integer.parseInt(args[2])              //400       200      800
double pixelDelta = Double.parseDouble(args[3])     //0.005     0.01    0.0025


//int width = 350                 // 700  1400
//int height = 200                //400   800
//double pixelDelta = 0.01        //0.005 0.0025
//int maxIterations = 1024        //1024   2048

System.gc()

print "Seq Mbrot $width, $height, $maxIterations -> "
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





