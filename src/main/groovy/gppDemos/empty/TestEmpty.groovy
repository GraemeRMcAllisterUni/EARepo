package gppDemos.empty

import gppDemos.ParallelClientServerEngine
import gppDemos.empty.emptyManager as emptyS
import gppDemos.empty.emptyWorker as emptyC
import gppLibrary.GroupDetails
import gppLibrary.LocalDetails


int clients = 1
int N = 2
int initialPopulation = 4

int crossoverProb = 95
int mutateProb = 5
int requiredParents = 2
int resultantChildren = 2
float editProportion = 0.1F



if (args.size() == 3) {
    N = Integer.parseInt(args[1])
    clients = Integer.parseInt(args[2])
    initialPopulation = Integer.parseInt(args[3])
}



LocalDetails serverDetails = new LocalDetails(
        lName: emptyS.getName(),
        lInitMethod: emptyS.initMethod,
        lInitData: [N, editProportion],
        lFinaliseMethod: emptyS.finaliseMethod)


def clientDetails = new GroupDetails(
        workers: clients,
        groupDetails: new LocalDetails[clients])

for (c in 0..<clients) {
    clientDetails.groupDetails[c] = new LocalDetails(
            lName: emptyC.getName(),
            lInitMethod: emptyC.initialiseMethod,
            lInitData: [N, crossoverProb, mutateProb])
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

println("int clients = " + clients)

println("int N = " + N)

println("int initialPopulation = " + initialPopulation)
eaCSprocess.run()
