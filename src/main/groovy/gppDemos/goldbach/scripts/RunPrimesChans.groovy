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
import groovyJCSP.*
import jcsp.lang.*

import gppDemos.goldbach.data.Prime as p
import gppDemos.goldbach.data.Sieve as s
import gppDemos.goldbach.data.PartitionedPrimeList as ppl
import gppDemos.goldbach.data.PrimeList as pl

int maxN =1000
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
println "Worker = $pWorkers and Prime Boundaries: $primeInitData"
println "Filtering up to $filter for primes up to $maxN"
def connect1 = Channel.one2one()
def connect2 = Channel.one2one()
def listToGroup = Channel.one2oneArray(pWorkers)
def groupToList = Channel.one2oneArray(pWorkers)
def l2Gout = new ChannelOutputList (listToGroup)
def l2Gin  = new ChannelInputList (listToGroup)
def g2Lout = new ChannelOutputList (groupToList)
def g2Lin  = new ChannelInputList (groupToList)

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

def emitter = new EmitWithLocal(output: connect1.out(),
                eDetails: eDetails)


def spread = new OneSeqCastList(input: connect1.in(),
                outputList: l2Gout)

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

def collector = new Collect(input: connect2.in(),
              rDetails: rDetails)

def network = [emitter, spread, group, reduce, collector]

def startime = System.currentTimeMillis()
new PAR (network).run()
def endtime = System.currentTimeMillis()
def elapsedTime = endtime - startime
println "Time taken = ${elapsedTime} milliseconds"
