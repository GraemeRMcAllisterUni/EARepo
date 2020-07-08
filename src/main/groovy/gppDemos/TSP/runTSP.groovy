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

EA.clients = 1

EA.initialPopulation = 4

//builder.cityList = [1: [342, 228], 2: [74, 386], 3: [142, 261], 4: [337, 394], 5: [211, 66], 6: [292, 242], 7: [290, 256], 8: [387, 212], 9: [272, 377], 10: [429, 179]]

EA.run()


class TSPManager extends Manager{

    void determineBestWorst(){
        println("TSP determine")
        bestFitness = population[0].fitness
        worstFitness = population[0].fitness
        for (i in 0..< population.size()) {
            if (population[i].fitness < bestFitness) {
                // update max fitness data
                bestFitness = population[i].fitness
                bestLocation = i
            }
            if (population[i].fitness > worstFitness) {
                // update min fitness data
                worstFitness = population[i].fitness
                worstLocation = i
            }
        }
        println "best = " + population[bestLocation]
        for(p in population)
            println p

    }

    int addChildren(List <Worker> children) {
        improvementattempts++
        boolean childAdded = false
        for (c in 0 ..< children.size()) {
            Worker child = children[c]
            // only add child if it is better than the worst child in the population
            if (child.fitness > worstFitness) {
                childAdded = true
                improvements++ // for analysis
//                print "improvement $improvements with fit $child.fitness after $requestedParents parent requests"
                worstFitness = child.fitness
                population[worstLocation] = child
                // new child could be better than the current best
                if (childAdded)
                    println(child.fitness)

                if (child.fitness < bestFitness) {
                    bestFitness = child.fitness
                    bestLocation = worstLocation
//                    print " new best"
//                    println "$child.fitness after ${requestedParents/2} evolutions "
                }
                // now update minFitness
                worstFitness = bestFitness
                for ( p in 0 ..< population.size()) {
                    if (population[p].fitness < worstFitness) {
                        // found a new minimum fitness
                        worstFitness = population[p].fitness
                        worstLocation = p
                    }
                }
//                println " $worstFitness, $bestFitness"
            } // end if
        } // end for loop
//        if (childAdded) {
//            if (bestFitness == worstFitness)
//                editPopulation()
//        }
        return completedOK
    }

    @Override
    boolean carryOn() {
        return true
    }
}