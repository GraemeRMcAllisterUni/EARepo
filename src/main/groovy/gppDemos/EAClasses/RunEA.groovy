package gppDemos.EAClasses

import gppDemos.ParallelClientServerEngine
import gppDemos.empty.emptyWorker
import gppDemos.EAClasses.Manager
import gppDemos.EAClasses.Worker
import gppLibrary.GroupDetails
import gppLibrary.LocalDetails

class RunEA {

    int clients = 4
    int N = 8
    int initialPopulation = 4

    int crossoverProb = 95
    int mutateProb = 5
    int requiredParents = 2
    int resultantChildren = 2
    float editProportion = 0.1F

    Manager manager = new Manager()

    Worker worker = new emptyWorker()



    void run() {


        LocalDetails serverDetails = new LocalDetails(
                lName: manager.class.getName(),
                lInitMethod: manager.initMethod,
                lInitData: [N, editProportion],
                lFinaliseMethod: manager.finaliseMethod)


        def clientDetails = new GroupDetails(
                workers: clients,
                groupDetails: new LocalDetails[clients])

        for (c in 0..<clients) {
            clientDetails.groupDetails[c] = new LocalDetails(
                    lName: worker.class.getName(),
                    lInitMethod: worker.initialiseMethod,
                    lInitData: [N, crossoverProb, mutateProb])
        }


        def eaCSprocess = new ParallelClientServerEngine(
                clientDetails: clientDetails,
                serverDetails: serverDetails,
                clients: clients,
                initialPopulation: initialPopulation,
                requiredParents: requiredParents,
                resultantChildren: resultantChildren,
                evolveFunction: worker.evolveFunction,
                createIndividualFunction: worker.createFunction,
                addIndividualsMethod: manager.addIndividualsMethod,
                selectParentsFunction: manager.selectParentsFunction,
                incorporateChildrenMethod: manager.incorporateChildrenMethod,
                carryOnFunction: manager.carryOnFunction
        )

        System.gc()


        println("int clients = " + clients)

        println("int N = " + N)

        println("int initialPopulation = " + initialPopulation)
        eaCSprocess.run()
    }
}