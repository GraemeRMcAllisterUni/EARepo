package gppDemos.MCpi

//usage runDemo MCpi/SeqMCpiUnrolled resultsFile instances iterations

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
print "SeqMCpiUnrolled, $instances, $iterations, "

System.gc()
def startime = System.currentTimeMillis()

  def mcpi = new MCpiData(iterations: instances * iterations)
  def mcpires = new MCpiResults()

  mcpi.getWithin(null)
  mcpires.collector(mcpi)
  mcpires.finalise(null)

  def endtime = System.currentTimeMillis()
  def elapsedTime = endtime - startime
  println "\t${elapsedTime}"


