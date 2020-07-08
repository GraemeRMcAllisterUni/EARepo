package gppDemos.TSP


import gppDemos.EAClasses.RunEA
import gppDemos.EAClasses.Manager
import gppDemos.EAClasses.Worker
import gppDemos.TSP.TSPWorker

int mapSize = 25



RunEA EA = new RunEA()

EA.manager = new TSPManager()

EA.worker = new TSPWorker()

EA.N = 10

EA.clients = 6

EA.initialPopulation = 2

EA.mutateProb = 80

//builder.cityList = [1: [342, 228], 2: [74, 386], 3: [142, 261], 4: [337, 394], 5: [211, 66], 6: [292, 242], 7: [290, 256], 8: [387, 212], 9: [272, 377], 10: [429, 179]]

EA.run()


class TSPManager extends Manager{

    //GraphDraw frame = new GraphDraw("Solution")

    int noImprovements = 0
    static int noImprovementsStop = 500000

    @Override
    int addChildren(List <Worker> children) {
        noImprovements++
        //improvementattempts++
        boolean childAdded = false
        for (c in 0 ..< children.size()) {
            Worker child = children[c]
            // only add child if it is better than the worst child in the population
            if(!population.contains(child))
                if (child.fitness < worstFitness ) {
                    childAdded = true
                    noImprovements = 0
                    println "noImprovements = 0"
                    improvements++ // for analysis
//                print "improvement $improvements with fit $child.fitness after $requestedParents parent requests"
                    worstFitness = child.fitness
                    population[worstLocation] = child
                    // new child could be better than the current best
                    determineBestWorst()
                } // end if
        } // end for loop
        return completedOK
    }

    @Override
    boolean carryOn() {
        if(noImprovements>0 && noImprovements % 100000 == 0) {
            for(p in population)
                println p
            println "best: " + population[bestLocation]
            println noImprovements + "<" + noImprovementsStop
        }
        return (noImprovements<noImprovementsStop)
    }


}