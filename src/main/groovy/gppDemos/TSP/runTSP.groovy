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

EA.initialPopulation = 4

EA.mutateProb = 20

//builder.cityList = [1: [342, 228], 2: [74, 386], 3: [142, 261], 4: [337, 394], 5: [211, 66], 6: [292, 242], 7: [290, 256], 8: [387, 212], 9: [272, 377], 10: [429, 179]]

EA.run()


class TSPManager extends Manager{

    GraphDraw frame = new GraphDraw("Worker")

    int noImprovements = 0
    static int noImprovementsStop = 100000

    Map cityList = [1: [74, 386], 2: [142, 261], 3: [337, 394], 4: [211, 66], 5: [400,270], 6: [272, 377], 7: [429, 179] , 8:[150,500], 9:[300,100], 10:[100,100], 11:[220,420], 12:[100,200], 13:[100,450], 14:[350,120], 15:[400,330], 16:[100,330]]

    void determineBestWorst(){
        println("TSP determine")
        bestFitness = Double.MAX_VALUE
        worstFitness = 0
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

    TSPManager(){
        drawWorld()
    }

//    int addChildren(List <Worker> children) {
//        improvementattempts++
//        boolean childAdded = false
//        for (c in 0 ..< children.size()) {
//            Worker child = children[c]
//            // only add child if it is better than the worst child in the population
//            if(!population.contains(child))
//            if (child.fitness > worstFitness) {
//                childAdded = true
//                improvements++ // for analysis
////                print "improvement $improvements with fit $child.fitness after $requestedParents parent requests"
//                worstFitness = child.fitness
//                population[worstLocation] = child
//                // new child could be better than the current best
//                determineBestWorst()
//
////                if (child.fitness < bestFitness) {
////                    bestFitness = child.fitness
////                    bestLocation = worstLocation
//////                    print " new best"
//////                    println "$child.fitness after ${requestedParents/2} evolutions "
////                }
////                // now update minFitness
////                worstFitness = bestFitness
////                for ( p in 0 ..< population.size()) {
////                    if (population[p].fitness < worstFitness) {
////                        // found a new minimum fitness
////                        worstFitness = population[p].fitness
////                        worstLocation = p
////                    }
////                }
////                println " $worstFitness, $bestFitness"
//            } // end if
//        } // end for loop
////        if (childAdded) {
////            if (bestFitness == worstFitness)
////                editPopulation()
////        }
//        return completedOK
//    }

    @Override
    boolean carryOn() {
        println noImprovements +"<"+ noImprovementsStop
        return (noImprovements<noImprovementsStop)
    }


    int addChildren(List <Worker> children) {
        noImprovements++
        println noImprovements
        for (c in children)
            println "received " + c
        improvementattempts++
        boolean childAdded = false
        for (c in 0 ..< children.size()) {
            Worker child = children[c]
            // only add child if it is better than the worst child in the population
            if(!population.contains(child))
                if (child.fitness < worstFitness ) {
                    println "added child " + child
                    println "better than " + population[worstLocation]
                    childAdded = true
                    noImprovements = 0
                    improvements++ // for analysis
//                print "improvement $improvements with fit $child.fitness after $requestedParents parent requests"
                    worstFitness = child.fitness
                    population[worstLocation] = child
                    // new child could be better than the current best
                    determineBestWorst()

//                if (child.fitness < bestFitness) {
//                    bestFitness = child.fitness
//                    bestLocation = worstLocation
////                    print " new best"
////                    println "$child.fitness after ${requestedParents/2} evolutions "
//                }
//                // now update minFitness
//                worstFitness = bestFitness
//                for ( p in 0 ..< population.size()) {
//                    if (population[p].fitness > worstFitness) {
//                        // found a new minimum fitness
//                        worstFitness = population[p].fitness
//                        worstLocation = p
//                    }
//                }
//                println " $worstFitness, $bestFitness"
                } // end if
        } // end for loop
        return completedOK
    }

    void drawWorld() {
        frame.setSize(500, 500)
        frame.setVisible(true)
        cityList.each { k, v ->
            List<Integer> city = (List<Integer>) v
            frame.addNode(k.toString(), city[0], city[1])
        }
    }

    void drawRoute(){
        for (int i in 0..cityList.size() - 2) {
            frame.addEdge((int) population[bestLocation].board[i], (int) population[bestLocation].board[i + 1])
        }
    }

}