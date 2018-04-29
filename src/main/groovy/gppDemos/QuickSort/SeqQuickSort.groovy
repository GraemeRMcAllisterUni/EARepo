package gppDemos.QuickSort


//usage runDemo QuickSort/SeqQickSort QuickB56 instances

//int instances = Integer.parseInt(args[1])
int instances = 500000

int workers = 1

print "SeqSort $workers, $instances -> "

System.gc()
def startime = System.currentTimeMillis()

def qsd = new QSData()
qsd.initClass([instances, workers])
def qsw = new QSWorker()
def qsr = new QSResult()
for ( i in 0..< instances) {
    qsd = new QSData()
    qsd.createInstance(null)
    qsw.iFunc(null, qsd)
}
qsw.wFunc()
def sortedBuffer = qsw.qsBuffer
for ( i in 0..< instances) qsr.collector(sortedBuffer[i])
qsr.finalise(null)

def endtime = System.currentTimeMillis()
println " ${endtime - startime}"
