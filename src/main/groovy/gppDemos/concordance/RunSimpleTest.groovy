package gppDemos.concordance

import jcsp.lang.*
import groovyJCSP.*
 
import gppLibrary.DataDetails
import gppLibrary.ResultDetails
import gppLibrary.terminals.Collect
import gppLibrary.terminals.Emit
import gppDemos.concordance.ConcordanceData as cd
 

//usage runDemo concordance/RunSimpleTest resultFile title N
 
int blockWorkers = 2
String title = "bible"
int N = 8
int minSeqLen = 2
boolean doFileOutput = false
 
if (args.size() == 0){
title = "bible"
N = 8
}
else {
title = args[0]
N = Integer.parseInt(args[1])
}
 
 
// eclipse versions
def fileName = "./${title}.txt"
def outFileName = "./${title}PoG"
 
def dDetails = new DataDetails( dName: cd.getName(),
dInitMethod: cd.init,
dInitData: [N, fileName, outFileName],
dCreateMethod: cd.create,
dCreateData: [null])
 
def rDetails = new ResultDetails( rName: cd.getName(),
rInitMethod: cd.initTest,
rCollectMethod: cd.collector,
rFinaliseMethod: cd.finaliseCollect,
rFinaliseData: [null])
 
System.gc()
print "SimpleTest, $title,  $N, "
 
def startime = System.currentTimeMillis()
 

//NETWORK

def chan1 = Channel.one2one()

def emitter = new Emit(
    // input channel not required
    output: chan1.out(),
    eDetails: dDetails)
 
def collector = new Collect(
    input: chan1.in(),
    // no output channel required
    rDetails: rDetails)

PAR network = new PAR()
 network = new PAR([emitter , collector ])
 network.run()
 network.removeAllProcesses()
//END

 
def endtime = System.currentTimeMillis()
println " ${endtime - startime}"
