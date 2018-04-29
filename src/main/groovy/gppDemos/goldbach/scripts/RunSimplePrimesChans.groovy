package gppDemos.goldbach.scripts

import gppLibrary.DataDetails
import gppLibrary.ResultDetails
import gppLibrary.terminals.Collect
import gppLibrary.terminals.EmitWithLocal
import groovyJCSP.PAR
import jcsp.lang.*

import gppDemos.goldbach.data.Prime as p
import gppDemos.goldbach.data.PrimeListSeqSimple as pl
import gppDemos.goldbach.data.Sieve as s

int N = 100

def connect = Channel.one2one()

def rDetails = new ResultDetails(rName: pl.getName(),
                 rInitMethod:pl.init,
                 rCollectMethod: pl.collector,
                 rFinaliseMethod: pl.finalise)

def eDetails = new DataDetails(dName:  p.getName(),
                dInitMethod: p.init,
                dCreateMethod: p.create,
                lName: s.getName(),
                lInitMethod: s.init,
                lInitData: [N])

def emitter = new EmitWithLocal(output: connect.out(),
                eDetails: eDetails)

def collector = new Collect(input: connect.in(),
              rDetails: rDetails)


def network = [emitter, collector]

def startime = System.currentTimeMillis()
new PAR (network).run()
def endtime = System.currentTimeMillis()
def elapsedTime = endtime - startime
println "Time taken = ${elapsedTime} milliseconds"
