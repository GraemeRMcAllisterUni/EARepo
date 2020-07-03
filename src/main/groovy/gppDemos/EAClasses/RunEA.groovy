package gppDemos.EAClasses

import gppDemos.ParallelClientServerEngine
import gppLibrary.GroupDetails
import gppLibrary.LocalDetails

public class RunEA {

    int clients = 2
    int N = 4
    int initialPopulation = 4

    int crossoverProb = 95
    int mutateProb = 5
    int requiredParents = 2
    int resultantChildren = 2
    float editProportion = 0.1F
    long seed = System.currentTimeMillis()
    Manager manager = new Manager()
    Worker worker
    List <Object> param = null

    void run() {

        //def d = [N, crossoverProb, seed]

        if (param!=null) param.each{ p -> d.add((int)p)}


        LocalDetails serverDetails = new LocalDetails(
                lName: manager.class.getName(),
                lInitMethod: manager.initMethod,
                lInitData: [N, editProportion, seed],
                lFinaliseMethod: manager.finaliseMethod)


        def clientDetails = new GroupDetails(
                workers: clients,
                groupDetails: new LocalDetails[clients])

        for (int c in 0..<clients) {
            def d = [N, crossoverProb, mutateProb, seed, c]
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

        System.gc()

//        println("int clients = " + clients)
//        println("int N = " + N)
//        println("int initialPopulation = " + initialPopulation)
        eaCSprocess.run()
    }
}