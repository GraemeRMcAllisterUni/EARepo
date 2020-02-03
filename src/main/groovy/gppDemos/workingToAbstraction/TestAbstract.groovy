package gppDemos.workingToAbstraction

import gppLibrary.GroupDetails
import gppLibrary.LocalDetails
import gppDemos.ParallelClientServerEngine


import gppDemos.workingToAbstraction.Manager as emptyS
import gppDemos.workingToAbstraction.Worker as emptyC


int clients = 1
int N = 1
int initialPopulation = 1

println("int clients = " + clients)
println("int N = " + N)
println("int initialPopulation = " + initialPopulation)

if (args.size() == 3){
// assumed to be running via runDemo
// assumed working directory folder = args[0]
    N = Integer.parseInt(args[1])
    clients = Integer.parseInt(args[2])
    initialPopulation = Integer.parseInt(args[3])
}

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
        lName: emptyS.getName(),
        lInitMethod: emptyS.initMethod,
        lInitData: [serverSeed, N, editProportion],
        lFinaliseMethod: emptyS.finaliseMethod )


def clientDetails = new GroupDetails(
        workers: clients,
        groupDetails: new LocalDetails [clients])

for (c in 0 ..< clients) {
    clientDetails.groupDetails[c] = new LocalDetails(
            lName: emptyC.getName(),
            lInitMethod: emptyC.initialiseMethod,
            lInitData:[N, crossoverProb, mutateProb, clientSeeds[c]])
}

System.gc()

def eaCSprocess = new ParallelClientServerEngine(
        clientDetails: clientDetails,
        serverDetails: serverDetails,
        clients: clients,
        initialPopulation: initialPopulation,
        requiredParents: requiredParents,
        resultantChildren: resultantChildren,
        evolveFunction: emptyC.evolveFunction,
        createIndividualFunction: emptyC.createFunction,
        addIndividualsMethod: emptyS.addIndividualsMethod,
        selectParentsFunction: emptyS.selectParentsFunction,
        incorporateChildrenMethod: emptyS.incorporateChildrenMethod,
        carryOnFunction: emptyS.carryOnFunction
)
eaCSprocess .run()