package gppDemos.goldbach.scripts

import gppLibrary.DataDetails
import gppLibrary.LocalDetails
import gppLibrary.ResultDetails
import gppLibrary.functionals.workers.Worker
import gppLibrary.terminals.Collect
import gppLibrary.terminals.EmitWithLocal
import gppDemos.goldbach.data.Prime as p
import gppDemos.goldbach.data.Sieve as s
import gppDemos.goldbach.data.PartitionedPrimeList as ppl
import gppDemos.goldbach.data.PrimeList as pl

//usage runDemo goldbach/scripts/RunPrimes resultsFile maxN pWorkers

int maxN = 0
int pWorkers = 1    // number of prime workers

if (args.size() == 0){
    maxN = 100000
}
else {
    maxN = Integer.parseInt(args[0])
}

System.gc()
print "Primes, $maxN, $pWorkers, "
def startime = System.currentTimeMillis()

int N = maxN
int filter = Math.sqrt(maxN) + 1
def primeInitData = []
int start = 1
int end = 0
for ( i in 1.. pWorkers){
  end = i * N
  primeInitData << [ N, start, end]
  start = end + 1
}

def rDetails = new ResultDetails(rName: pl.getName(),
                 rInitMethod:pl.init,
                 rCollectMethod: pl.collector,
                 rFinaliseMethod: pl.finalise)

def eDetails = new DataDetails(dName:  p.getName(),
                dInitMethod: p.init,
                dCreateMethod: p.create,
                lName: s.getName(),
                lInitMethod: s.init,
                lInitData: [filter])

def workerLocal = new LocalDetails(lName: ppl.getName(),
                        lInitMethod: ppl.init,
                        lInitData: primeInitData[0],
                        lFinaliseMethod: ppl.finalise)

def emitter = new EmitWithLocal(eDetails: eDetails)

def worker = new Worker(outData: false,
                        lDetails: workerLocal,
                        function: p.sievePrime)

def collector = new Collect(rDetails: rDetails)

def endtime = System.currentTimeMillis()
println " ${endtime - startime} "
