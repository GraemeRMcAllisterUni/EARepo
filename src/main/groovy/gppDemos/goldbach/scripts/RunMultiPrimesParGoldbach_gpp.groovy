package gppDemos.goldbach.scripts

import gppLibrary.DataDetails
import gppLibrary.GroupDetails
import gppLibrary.LocalDetails
import gppLibrary.ResultDetails
import gppLibrary.connectors.reducers.ListSeqOne
import gppLibrary.connectors.spreaders.OneParCastList
import gppLibrary.connectors.spreaders.OneSeqCastList
import gppLibrary.functionals.groups.ListGroupList
import gppLibrary.terminals.Collect
import gppLibrary.terminals.EmitWithLocal
import gppLibrary.functionals.transformers.CombineNto1

import gppDemos.goldbach.data.Prime as p
import gppDemos.goldbach.data.InternalPrimeList as ipl
import gppDemos.goldbach.data.ResultantPrimes as rp
import gppDemos.goldbach.data.Sieve as s
import gppDemos.goldbach.data.PartitionedPrimeList as ppl
import gppDemos.goldbach.data.GoldbachRange as gr
import gppDemos.goldbach.data.GoldbachParCollect as gpc

int maxN = 200000
int pWorkers = 4	// pWorkers must divide maxN exactly but not checked!
int gWorkers = 4	// number of Goldbach partitions
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
//println "Worker = $pWorkers and Prime Boundaries: $primeInitData"
println "Prime workers = $pWorkers, \nGoldbach Partitions = $gWorkers \nFiltering up to $filter for primes up to $maxN"
def startime = System.currentTimeMillis()

def eDetails = new DataDetails( dName:  p.getName(),
                dInitMethod: p.init,
                dCreateMethod: p.create,
                lName: s.getName(),
                lInitMethod: s.init,
                lInitData: [filter])

def g1Details = new GroupDetails(workers: pWorkers,
    groupDetails: new LocalDetails [pWorkers])

for (w in 0 ..< pWorkers) {
g1Details.groupDetails[w] = new LocalDetails(lName: ppl.getName(),
            lInitMethod: ppl.init,
            lInitData: primeInitData[w],
            lFinaliseMethod: ppl.finalise)
}

def g2Details = new GroupDetails(workers: gWorkers,
    groupDetails: new LocalDetails [gWorkers])

for (w in 0 ..< gWorkers) {
g2Details.groupDetails[w] = new LocalDetails(lName: gr.getName(),
            lInitMethod: gr.init,
            lFinaliseMethod: ppl.finalise)
g2Details.groupDetails[w].lInitData = [w]
}

def combineLocal = new LocalDetails(lName: ipl.getName(),
    lInitMethod: ipl.init,)

def combineOut = new LocalDetails(lName: rp.getName(),
    lInitMethod: rp.init,
    lInitData: [pWorkers],
    lFinaliseMethod: rp.finalise)

def resDetails = new ResultDetails(rName: gpc.getName(),
    rInitMethod:gpc.init,
    rCollectMethod: gpc.collector,
    rFinaliseMethod: gpc.finalise)


def emitter = new EmitWithLocal(eDetails: eDetails)

def spread1 = new OneSeqCastList()

def group1 = new ListGroupList( 
                gDetails: g1Details,
                workers: pWorkers,
                outData: false,
                function: p.sievePrime
                 )

def reduce1 = new ListSeqOne ()

def combine = new CombineNto1( 
                 localDetails: combineLocal,
                 outDetails: combineOut,
                 combineMethod: ipl.toIntegers)

def spread2 = new OneParCastList()


def group2 = new ListGroupList ( 
                 gDetails: g2Details,
                 workers: gWorkers,
                 modifier:[[gWorkers], [gWorkers], [gWorkers], [gWorkers] ],
                 outData: false,
                 function: rp.getRange)

def reduce2 = new ListSeqOne ()

def collector = new Collect (
               rDetails: resDetails)


def endtime = System.currentTimeMillis()
def elapsedTime = endtime - startime
println "Time taken = ${elapsedTime} milliseconds"
