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
import groovyJCSP.*
import jcsp.lang.*

import gppDemos.goldbach.data.Prime as p
import gppDemos.goldbach.data.InternalPrimeList as ipl
import gppDemos.goldbach.data.Sieve as s
import gppDemos.goldbach.data.ResultantPrimes as rp
import gppDemos.goldbach.data.PartitionedPrimeList as ppl
import gppDemos.goldbach.data.GoldbachCollect as gc

int maxN = 1000
int pWorkers = 4	// pWorkers must divide maxN exactly but not checked!
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
println "Prime workers = $pWorkers, \nFiltering up to $filter for primes up to $maxN"
def connect1 = Channel.one2one()
def connect2 = Channel.one2one()
def connect3 = Channel.one2one()
def listToGroup = Channel.one2oneArray(pWorkers)
def groupToList = Channel.one2oneArray(pWorkers)
def l2Gout = new ChannelOutputList (listToGroup)
def l2Gin  = new ChannelInputList (listToGroup)
def g2Lout = new ChannelOutputList (groupToList)
def g2Lin  = new ChannelInputList (groupToList)
/*
def emitter = new EmitWithLocal(workerClassName: s.getName(),
                workerInitData: [filter],
                output: connect1.out(),
                dName: p.getName())
*/
def eDetails = new DataDetails( dName:  p.getName(),
                dInitMethod: p.init,
                dCreateMethod: p.create,
                lName: s.getName(),
                lInitMethod: s.init,
                lInitData: [filter])

def emitter = new EmitWithLocal(output: connect1.out(),
                eDetails: eDetails)

def spread = new OneSeqCastList(input: connect1.in(),
                outputList: l2Gout)
/*
def group = new ListGroupList ( inputList: l2Gin,
                  outputList: g2Lout,
                workers: pWorkers,
                workerClassName: ppl.getName(),
                outData: false,
                workerInitData: primeInitData,
                operation: p.sievePrime
                 )
*/
def gDetails = new GroupDetails(workers: pWorkers,
                groupDetails: new LocalDetails [pWorkers])

for (w in 0 ..< pWorkers) {
  gDetails.groupDetails[w] = new LocalDetails(lName: ppl.getName(),
                        lInitMethod: ppl.init,
                        lInitData: primeInitData[w],
                        lFinaliseMethod: ppl.finalise)
}

def group = new ListGroupList ( inputList: l2Gin,
                  outputList: g2Lout,
                gDetails: gDetails,
                workers: pWorkers,
                outData: false,
                function: p.sievePrime
                 )

def reduce = new ListSeqOne (inputList: g2Lin,
               output: connect2.out())

def combineLocal = new LocalDetails(lName: ipl.getName(),
                  lInitMethod: ipl.init,)

def combineOut = new LocalDetails(lName: rp.getName(),
                  lInitMethod: rp.init,
                  lInitData: [pWorkers],
                  lFinaliseMethod: rp.finalise)

def combine = new CombineNto1(input: connect2.in(),
                localDetails: combineLocal,
                outDetails: combineOut,
                output: connect3.out(),
                combineMethod: ipl.toIntegers)
/*
def combine = new CombineNto1(input: connect2.in(),
                output: connect3.out(),
                localClassName: ipl.getName(),
                outputClassName: rp.getName(),
                outputInitData: [pWorkers],
                localOp: ipl.opToIntegers)

def collector = new CollectPrev (input: connect3.in(),
               resultClassName: gc.getName())
*/
def resDetails = new ResultDetails(rName: gc.getName(),
                 rInitMethod:gc.init,
                 rCollectMethod: gc.collector,
                 rFinaliseMethod: gc.finalise)

def collector = new Collect (input: connect3.in(),
               rDetails: resDetails)

def network = [emitter, spread, group, reduce, combine, collector]

def startime = System.currentTimeMillis()
new PAR (network).run()
def endtime = System.currentTimeMillis()
def elapsedTime = endtime - startime
println "Time taken = ${elapsedTime} milliseconds"
