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
import groovyJCSP.*
import jcsp.lang.*

import gppDemos.goldbach.data.Prime as p
import gppDemos.goldbach.data.InternalPrimeList as ipl
import gppDemos.goldbach.data.ResultantPrimes as rp
import gppDemos.goldbach.data.Sieve as s
import gppDemos.goldbach.data.PartitionedPrimeList as ppl
import gppDemos.goldbach.data.GoldbachRange as gr
import gppDemos.goldbach.data.GoldbachParCollect as gpc

int maxN = 50000
int pWorkers = 2	// pWorkers must divide maxN exactly but not checked!
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
def connect1 = Channel.one2one()
def connect2 = Channel.one2one()
def connect3 = Channel.one2one()
def connect4 = Channel.one2one()
def connect5 = Channel.one2one()

def listToGroup1 = Channel.one2oneArray(pWorkers)
def groupToList1 = Channel.one2oneArray(pWorkers)
def l2Gout1 = new ChannelOutputList (listToGroup1)
def l2Gin1  = new ChannelInputList (listToGroup1)
def g2Lout1 = new ChannelOutputList (groupToList1)
def g2Lin1  = new ChannelInputList (groupToList1)

def listToGroup2 = Channel.one2oneArray(gWorkers)
def groupToList2 = Channel.one2oneArray(gWorkers)
def l2Gout2 = new ChannelOutputList (listToGroup2)
def l2Gin2  = new ChannelInputList (listToGroup2)
def g2Lout2 = new ChannelOutputList (groupToList2)
def g2Lin2  = new ChannelInputList (groupToList2)

def eDetails = new DataDetails( dName:  p.getName(),
                dInitMethod: p.init,
                dCreateMethod: p.create,
                lName: s.getName(),
                lInitMethod: s.init,
                lInitData: [filter])

def emitter = new EmitWithLocal(output: connect1.out(),
  eDetails: eDetails)

def spread1 = new OneSeqCastList(input: connect1.in(),
                 outputList: l2Gout1)

def g1Details = new GroupDetails(workers: pWorkers,
                 groupDetails: new LocalDetails [pWorkers])

for (w in 0 ..< pWorkers) {
  g1Details.groupDetails[w] = new LocalDetails(lName: ppl.getName(),
                         lInitMethod: ppl.init,
                         lInitData: primeInitData[w],
                         lFinaliseMethod: ppl.finalise)
}

def group1 = new ListGroupList( inputList: l2Gin1,
                  outputList: g2Lout1,
                gDetails: g1Details,
                workers: pWorkers,
                outData: false,
                function: p.sievePrime
                 )

def reduce1 = new ListSeqOne (inputList: g2Lin1,
                output: connect2.out())

def combineLocal = new LocalDetails(lName: ipl.getName(),
                  lInitMethod: ipl.init,)

def combineOut = new LocalDetails(lName: rp.getName(),
                  lInitMethod: rp.init,
                  lInitData: [pWorkers],
                  lFinaliseMethod: rp.finalise)

def combine = new CombineNto1( input: connect2.in(),
                 output: connect3.out(),
                 localDetails: combineLocal,
                 outDetails: combineOut,
                 combineMethod: ipl.toIntegers)

def spread2 = new OneParCastList(input: connect3.in(),
                 outputList: l2Gout2)


def g2Details = new GroupDetails(workers: gWorkers,
                 groupDetails: new LocalDetails [gWorkers])

for (w in 0 ..< gWorkers) {
  g2Details.groupDetails[w] = new LocalDetails(lName: gr.getName(),
                         lInitMethod: gr.init,
                         lFinaliseMethod: ppl.finalise)
  g2Details.groupDetails[w].lInitData = [w]
}

def group2 = new ListGroupList ( inputList: l2Gin2,
                 outputList: g2Lout2,
                 gDetails: g2Details,
                 workers: gWorkers,
                 modifier:[[gWorkers], [gWorkers], [gWorkers], [gWorkers] ],
                 outData: false,
                 function: rp.getRange)

def reduce2 = new ListSeqOne (inputList: g2Lin2,
                output: connect4.out())

def resDetails = new ResultDetails(rName: gpc.getName(),
                 rInitMethod:gpc.init,
                 rCollectMethod: gpc.collector,
                 rFinaliseMethod: gpc.finalise)

def collector = new Collect (input: connect4.in(),
               rDetails: resDetails)

def network = [emitter, spread1, group1, reduce1, combine, spread2, group2, reduce2, collector ]

def startime = System.currentTimeMillis()
new PAR (network).run()
def endtime = System.currentTimeMillis()
def elapsedTime = endtime - startime
println "Time taken = ${elapsedTime} milliseconds"
