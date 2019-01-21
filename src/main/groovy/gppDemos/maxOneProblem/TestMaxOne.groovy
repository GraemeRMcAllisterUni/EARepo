package gppDemos.maxOneProblem

import jcsp.lang.*
import groovyJCSP.*
 
import gppLibrary.GroupDetails
import gppLibrary.LocalDetails
import gppLibrary.functionals.evolutionary.ParallelClientServerEngine
import gppDemos.maxOneProblem.MaxOneServer as moServer
import gppDemos.maxOneProblem.MaxOneIndividual as moIndividual
 

//usage runDemo maxOneProblem TestMaxOne resultsFile N clients initialPopulation
 
int N
int clients
int initialPopulation
 
if (args.size() == 0) {
// assumed to be running form within Intellij
N = 400
clients = 3
initialPopulation = 480
}
else {
// assumed to be running via runDemo
//    String folder = args[0] not required
N = Integer.parseInt(args[1])
clients = Integer.parseInt(args[2])
initialPopulation = Integer.parseInt(args[3])
 
}
 
if (args.size() != 0){
clients = Integer.parseInt(args[1])
N = Integer.parseInt(args[0])
initialPopulation = Integer.parseInt(args[2])
}
 
int crossoverProb = 95
int mutateProb = 5
long serverSeed = 22334455L
int requiredParents = 2
int resultantChildren = 2
List clientSeeds = [11223344L, 33445566L, 44556677L, 55667788L]
float editProportion = 0.1F
 
LocalDetails serverDetails = new LocalDetails(
lName: moServer.getName(),
lInitMethod: moServer.initMethod,
lInitData: [serverSeed, N, editProportion],
lFinaliseMethod: moServer.finaliseMethod )
 
 
def clientDetails = new GroupDetails(
workers: clients,
groupDetails: new LocalDetails [clients])
 
for (c in 0 ..< clients) {
clientDetails.groupDetails[c] = new LocalDetails(
lName: moIndividual.getName(),
lInitMethod: moIndividual.initialiseMethod,
lInitData:[N, crossoverProb, mutateProb, clientSeeds[c]])
}
 
//println """Max One: using any channels
//clients: $clients, N: $N, Individuals per Client: $initialPopulation,
//Required Parents: $requiredParents, Resultant Children: $resultantChildren,
//Crossover%: $crossoverProb, Mutate%: $mutateProb"""
 
print "Max One, $N, $clients, $initialPopulation, "
System.gc()
 
long startTime = System.currentTimeMillis()
 
 

//NETWORK


def eaCSprocess = new ParallelClientServerEngine(
    clientDetails: clientDetails,
    serverDetails: serverDetails,
    clients: clients,
    initialPopulation: initialPopulation,
    requiredParents: requiredParents,
    resultantChildren: resultantChildren,
    evolveFunction: moIndividual.evolveFunction,
    createIndividualFunction: moIndividual.createFunction,
    addIndividualsMethod: moServer.addIndividualsMethod,
    selectParentsFunction: moServer.selectParentsFunction,
    incorporateChildrenMethod: moServer.incorporateChildrenMethod,
    carryOnFunction: moServer.carryOnFunction
    )

eaCSprocess .run()

//END

 
long endTime = System.currentTimeMillis()
println "${endTime - startTime} "
 
