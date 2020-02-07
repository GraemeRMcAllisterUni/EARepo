package gppDemos.empty

import gppDemos.ParallelClientServerEngine
import gppDemos.workingToAbstraction.Manager as Manager
import gppDemos.workingToAbstraction.Worker as Worker
import gppLibrary.GroupDetails
import gppLibrary.LocalDetails

class RunEA {

    int clients = 3
    int N = 6
    int initialPopulation = 2

    int crossoverProb = 95
    int mutateProb = 5
    int requiredParents = 2
    int resultantChildren = 2
    float editProportion = 0.1F

    Manager M = new emptyManager()

    Worker W = new emptyWorker()



    void run() {


        LocalDetails serverDetails = new LocalDetails(
                lName: M.class.getName(),
                lInitMethod: M.initMethod,
                lInitData: [N, editProportion],
                lFinaliseMethod: M.finaliseMethod)


        def clientDetails = new GroupDetails(
                workers: clients,
                groupDetails: new LocalDetails[clients])

        for (c in 0..<clients) {
            clientDetails.groupDetails[c] = new LocalDetails(
                    lName: w.class.getName(),
                    lInitMethod: W.initialiseMethod,
                    lInitData: [N, crossoverProb, mutateProb])
        }


        def eaCSprocess = new ParallelClientServerEngine(
                clientDetails: clientDetails,
                serverDetails: serverDetails,
                clients: clients,
                initialPopulation: initialPopulation,
                requiredParents: requiredParents,
                resultantChildren: resultantChildren,
                evolveFunction: W.evolveFunction,
                createIndividualFunction: W.createFunction,
                addIndividualsMethod: M.addIndividualsMethod,
                selectParentsFunction: M.selectParentsFunction,
                incorporateChildrenMethod: M.incorporateChildrenMethod,
                carryOnFunction: M.carryOnFunction
        )

        System.gc()


        println("int clients = " + clients)

        println("int N = " + N)

        println("int initialPopulation = " + initialPopulation)
        eaCSprocess.run()
    }
}