package gppDemos.nQueensProblem

import jcsp.lang.*
import groovyJCSP.*
 
import gppLibrary.GroupDetails
import gppLibrary.LocalDetails
import gppLibrary.functionals.evolutionary.ParallelClientServerEngine
import gppDemos.nQueensProblem.QueensServer as qs
import gppDemos.nQueensProblem.QueensClient as qc
 
 

//usage runDemo nQueensProblem TestQueens resultsFile N clients
 
int clients
int N
int initialPopulation
 
if (args.size() == 0){
// assumed to be running form within Intellij
clients = 2     // max 16
N = 8
initialPopulation = 12
}
else {
// assumed to be running via runDemo
// assumed working directory folder = args[0]
N = Integer.parseInt(args[1])
clients = Integer.parseInt(args[2])
initialPopulation = Integer.parseInt(args[3])
}
 
//initialPopulation = (N / clients) + clients
 
int crossoverProb = 95
int mutateProb = 5
long serverSeed = 12345678910111213L
int requiredParents = 2
int resultantChildren = 2
List clientSeeds = [23456789101112131L, 34567891011121314L, 45678910111213141L, 56789101112131415L,
67891011121314151L, 78910111213141516L, 89101112131415161L, 91011121314151617L,
10111213141516171L, 11121314151617181L, 12131415161718192L, 13141516171819202L,
14151617181920212L, 15161718192021222L, 16171819202122232L, 17181920212223242L]
float editProportion = 0.1F
 
LocalDetails serverDetails = new LocalDetails(
lName: qs.getName(),
lInitMethod: qs.initMethod,
lInitData: [serverSeed, N, editProportion],
lFinaliseMethod: qs.finaliseMethod )
 
 
def clientDetails = new GroupDetails(
workers: clients,
groupDetails: new LocalDetails [clients])
 
for (c in 0 ..< clients) {
clientDetails.groupDetails[c] = new LocalDetails(
lName: qc.getName(),
lInitMethod: qc.initialiseMethod,
        lInitData:[N, crossoverProb, mutateProb, null])
//    lInitData:[N, crossoverProb, mutateProb, clientSeeds[c]])
}
 
//println """Queens: using any channels
//clients: $clients, N: $N, Individuals per Client: $initialPopulation,
//Required Parents: $requiredParents, Resultant Children: $resultantChildren,
//Crossover%: $crossoverProb, Mutate%: $mutateProb"""

System.gc()
 
print """Queens, $clients, $N, $initialPopulation,  """
long startTime = System.currentTimeMillis()
 

//NETWORK


def eaCSprocess = new ParallelClientServerEngine(
    clientDetails: clientDetails,
    serverDetails: serverDetails,
    clients: clients,
    initialPopulation: initialPopulation,
    requiredParents: requiredParents,
    resultantChildren: resultantChildren,
    evolveFunction: qc.evolveFunction,
    createIndividualFunction: qc.createFunction,
    addIndividualsMethod: qs.addIndividualsMethod,
    selectParentsFunction: qs.selectParentsFunction,
    incorporateChildrenMethod: qs.incorporateChildrenMethod,
    carryOnFunction: qs.carryOnFunction
    )

eaCSprocess .run()

//END

 
 
 
long endTime = System.currentTimeMillis()
println " ${endTime - startTime}"
 
