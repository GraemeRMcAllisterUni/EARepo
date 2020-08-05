package gppDemos.EAClasses

import gppDemos.ParallelClientServerEngine
import gppLibrary.GroupDetails
import gppLibrary.LocalDetails

public class RunEA {

    int clients = 4
    int n = 100
    int initialPopulation = 4

    int crossoverProb = 100
    int mutateProb = 50
    int requiredParents = 2
    int resultantChildren = 2
    float editProportion = 0.1F
    long seed = System.nanoTime()
    Manager manager
    Worker worker
    List <Object> param = null

    void run() {

        if (param!=null) param.each{ p -> d.add((int)p)}

        LocalDetails serverDetails = new LocalDetails(
                lName: manager.class.getName(),
                lInitMethod: manager.initMethod,
                lInitData: [n, editProportion, seed],
                lFinaliseMethod: manager.finaliseMethod)

        def clientDetails = new GroupDetails(
                workers: clients,
                groupDetails: new LocalDetails[clients])

        for (int c in 0..<clients) {
            def d = [n, crossoverProb, mutateProb, seed, c]
            clientDetails.groupDetails[c] = new LocalDetails(
                    lName: worker.class.getName(),
                    lInitMethod: worker.initialiseMethod,
                    lInitData: d)
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
        eaCSprocess.run()
    }
}