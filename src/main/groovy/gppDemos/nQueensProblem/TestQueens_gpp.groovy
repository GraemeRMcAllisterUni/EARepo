package gppDemos.nQueensProblem

import gppLibrary.GroupDetails
import gppLibrary.LocalDetails
import gppLibrary.functionals.evolutionary.ParallelClientServerEngine
import gppDemos.nQueensProblem.QueensServer as qs
import gppDemos.nQueensProblem.QueensClient as qc


//usage runDemo nQueensProblem\TestQueens2 resultsFile N clients initialPopulation

int clients = 2
int N = 8
int initialPopulation = 15

if (args.size() != 0){
    N = Integer.parseInt(args[0])
    clients = Integer.parseInt(args[1])
    initialPopulation = Integer.parseInt(args[2])
}

int crossoverProb = 95
int mutateProb = 5
long serverSeed = 12345678910111213L
int requiredParents = 2
int resultantChildren = 2
List clientSeeds = [23456789101112131L, 34567891011121312L, 45678910111213123L, 56789101112131234L]
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
            lInitData:[N, crossoverProb, mutateProb, clientSeeds[c]])
}

//println """Queens: using any channels
//clients: $clients, N: $N, Individuals per Client: $initialPopulation, 
//Required Parents: $requiredParents, Resultant Children: $resultantChildren,
//Crossover%: $crossoverProb, Mutate%: $mutateProb"""
    
System.gc()

print """Queens, $crossoverProb, $mutateProb, $N, $clients, $initialPopulation, ->, """
long startTime = System.currentTimeMillis()

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



long endTime = System.currentTimeMillis()
println " ${endTime - startTime}"

