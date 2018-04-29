package gppDemos.goldbach.scripts

import gppLibrary.DataDetails
import gppLibrary.GroupDetails
import gppLibrary.LocalDetails
import gppLibrary.ResultDetails
import gppLibrary.connectors.reducers.ListSeqOne
import gppLibrary.connectors.spreaders.OneParCastList
import gppLibrary.functionals.groups.ListGroupList
import gppLibrary.terminals.Collect
import gppLibrary.terminals.EmitWithLocal
import gppLibrary.functionals.workers.ThreePhaseWorker
import gppDemos.goldbach.data.Prime as p
import gppDemos.goldbach.data.ResultantPrimes as rp
import gppDemos.goldbach.data.Sieve as s
import gppDemos.goldbach.data.CollectedPrimes as cp
import gppDemos.goldbach.data.GoldbachRange as gr
import gppDemos.goldbach.data.GoldbachParCollect as gpc

//usage runDemo goldbach/scripts/RunParGoldbach resultsFile maxN gWorkers

int maxN = 0
int gWorkers = 0	// number of Goldbach workers

if (args.size() == 0){
    maxN = 20000
    gWorkers = 16
}
else {
    maxN = Integer.parseInt(args[0])
    gWorkers = Integer.parseInt(args[1])
}


System.gc()
print "ParGB $maxN, $gWorkers, "
def startime = System.currentTimeMillis()

int N = maxN 

int filter = Math.sqrt(maxN) + 1
def primeInitData = [N, 1, N]

def eDetails = new DataDetails( dName:  p.getName(),
                dInitMethod: p.init,
                dCreateMethod: p.create,
                lName: s.getName(),
                lInitMethod: s.init,
                lInitData: [filter])

def workerLocal = new LocalDetails(lName: cp.getName(),
                         lInitMethod: cp.init,
                         lInitData: primeInitData )


def gDetails = new GroupDetails(workers: gWorkers,
                 groupDetails: new LocalDetails [gWorkers])

for (w in 0 ..< gWorkers) {
  gDetails.groupDetails[w] = new LocalDetails(lName: gr.getName(),
                         lInitMethod: gr.init,
                         lFinaliseMethod: gr.finalise)
  gDetails.groupDetails[w].lInitData = [w]
}

def resDetails = new ResultDetails(rName: gpc.getName(),
                 rInitMethod:gpc.init,
                 rCollectMethod: gpc.collector,
                 rFinaliseMethod: gpc.finalise)



def emitter = new EmitWithLocal(eDetails: eDetails)

def worker = new ThreePhaseWorker( lDetails: workerLocal,
                                   inputMethod: cp.inputMethod, 
                                   workMethod: cp.workMethod,
                                   outFunction: cp.outFunction)

def spread = new OneParCastList()

def modifiers = []
for ( w in 0..<gWorkers) modifiers << [gWorkers]

def group = new ListGroupList ( gDetails: gDetails,
                 workers: gWorkers,
                 modifier:modifiers,
                 outData: false,
                 function: rp.getRange)

def reduce = new ListSeqOne ( )

def collector = new Collect (rDetails: resDetails)


def endtime = System.currentTimeMillis()
println " ${endtime - startime}"
