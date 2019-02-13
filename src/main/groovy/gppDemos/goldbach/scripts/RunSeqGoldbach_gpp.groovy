package gppDemos.goldbach.scripts

import gppLibrary.DataDetails
import gppLibrary.GroupDetails
import gppLibrary.LocalDetails
import gppLibrary.ResultDetails
import gppLibrary.connectors.reducers.ListSeqOne
import gppLibrary.connectors.spreaders.OneSeqCastList
import gppLibrary.functionals.groups.ListGroupList
import gppLibrary.terminals.Collect
import gppLibrary.terminals.EmitWithLocal
import gppLibrary.functionals.transformers.CombineNto1

import gppDemos.goldbach.data.Prime as p
import gppDemos.goldbach.data.InternalPrimeList as ipl
import gppDemos.goldbach.data.Sieve as s
import gppDemos.goldbach.data.ResultantPrimes as rp
import gppDemos.goldbach.data.PartitionedPrimeList as ppl
import gppDemos.goldbach.data.GoldbachCollect as gc

//usage runDemo goldbach/scripts RunSeqGoldbach resultFile maxN

int maxN

if (args.size() == 0){
    maxN = 20000
}
else {
    maxN = Integer.parseInt(args[1])
}
int pWorkers = 1	// pWorkers

System.gc()
print "SeqGB, $maxN, $pWorkers, "
def startime = System.currentTimeMillis()

assert((maxN % pWorkers) == 0 )

int N = maxN / pWorkers
int filter = Math.sqrt(maxN) + 1
def primeInitData = []
int start = 1
int end = 0
for ( i in 1.. pWorkers){
  end = i * N
  primeInitData << [ N, start, end]
  start = end + 1
}


def eDetails = new DataDetails( dName:  p.getName(),
                dInitMethod: p.init,
                dCreateMethod: p.create,
                lName: s.getName(),
                lInitMethod: s.init,
                lInitData: [filter])

def gDetails = new GroupDetails(workers: pWorkers,
                groupDetails: new LocalDetails [pWorkers])

for (w in 0 ..< pWorkers) {
  gDetails.groupDetails[w] = new LocalDetails(lName: ppl.getName(),
                        lInitMethod: ppl.init,
                        lInitData: primeInitData[w],
                        lFinaliseMethod: ppl.finalise)
}

def resDetails = new ResultDetails(rName: gc.getName(),
                 rInitMethod:gc.init,
                 rCollectMethod: gc.collector,
                 rFinaliseMethod: gc.finalise)

def combineLocal = new LocalDetails(lName: ipl.getName(),
  lInitMethod: ipl.init,)

def combineOut = new LocalDetails(lName: rp.getName(),
  lInitMethod: rp.init,
  lInitData: [pWorkers],
  lFinaliseMethod: rp.finalise)


def emitter = new EmitWithLocal(eDetails: eDetails)

def spread = new OneSeqCastList()

def group = new ListGroupList ( gDetails: gDetails,
                workers: pWorkers,
                outData: false,
                function: p.sievePrime
                 )

def reduce = new ListSeqOne ( )

def combine = new CombineNto1(localDetails: combineLocal,
                outDetails: combineOut,
                combineMethod: ipl.toIntegers)


def collector = new Collect (rDetails: resDetails)

def endtime = System.currentTimeMillis()
println " ${endtime - startime} "
