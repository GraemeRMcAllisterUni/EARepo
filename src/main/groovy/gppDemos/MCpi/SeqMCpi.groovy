package gppDemos.MCpi

//usage runDemo MCpi/SeqMCpi resultsFile instances iterations

import gppDemos.MCpi.MCpiData as piData
import gppDemos.MCpi.MCpiResults as piResults

int instances = 0
int iterations = 0

if (args.size() == 0 ) {
    instances =1024
    iterations = 100000
}
else {
    instances = Integer.parseInt(args[0])
    iterations = Integer.parseInt(args[1])
}

print "SeqMCpi  $instances, $iterations, "

  System.gc()
  def startime = System.currentTimeMillis()

  def mcpires = new piResults()
  for ( i in 1 .. instances){
    def mcpi = new piData()
    mcpi.initClass([instances])
    mcpi.createInstance([iterations])
    mcpi.getWithin(null)
    mcpires.collector(mcpi)
  }
  mcpires.finalise(null)

  def endtime = System.currentTimeMillis()
  println " ${endtime - startime}"

