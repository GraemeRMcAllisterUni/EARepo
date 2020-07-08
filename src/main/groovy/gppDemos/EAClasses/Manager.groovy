package gppDemos.EAClasses

import gppDemos.UniversalResponse
import gppLibrary.DataClass
import java.util.concurrent.ThreadLocalRandom;


class Manager extends DataClass{

    List <Worker> population = []
    Double requiredFitness = 0.0D
    Double worstFitness
    Double bestFitness
    int worstLocation, bestLocation
    Integer N = 0
    static Random rng = new Random()
    static int requestedParents = 0
    static int requeiredarents = 2
    static int improvementattempts = 0
    static int improvements = 0

    static float editProportion = 0.0F
    //def board

    static long seed = System.currentTimeMillis()

    static String initMethod = "initialise"
    static String selectParentsFunction = "selectParents"
    static String incorporateChildrenMethod = "addChildren"
    static String addIndividualsMethod = "addIndividuals"
    static String carryOnFunction = "carryOn"
    static String finaliseMethod = "finalise"

    Manager(){
        improvementattempts = 0
        improvements = 0
        requestedParents =0
        //population.clear()
        worstFitness = null
        bestFitness = null
    }

    int initialise (List d) {
        N = (int)d[0]
        editProportion = (float)d[1]
        if (d[2] != null) seed = (long)d[2]
        rng.setSeed(seed)
        return completedOK
    }


    UniversalResponse selectParents(int parents) {
        requestedParents = requestedParents + parents // for analysis
        def response = new UniversalResponse()
        for ( i in 0 ..< parents) {
            int p = rng.nextInt(population.size())
            response.payload[i] = population[p]
        }

        return response
    }

    int addChildren(List <Worker> children) {
        improvementattempts++
//        println "addinf something"
//        for (c in children)
//            println c
        boolean childAdded = false
        for (c in 0 ..< children.size()) {
            Worker child = children[c]
            // only add child if it is better than the worst child in the population
            //if(!population.contains(child))
                if (child.fitness < worstFitness ) {
                childAdded = true
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



    void determineBestWorst(){

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
        if (bestFitness == worstFitness)
            editPopulation()
    }


    int addIndividuals(List <Worker> individuals) { //add the new individuals to the population

        for (int i in 0 ..<individuals.size()) {
            population.add(individuals[i])
        }
        determineBestWorst()
        return completedOK
    }

    boolean carryOn() { // returns true if the server should continue
        return bestFitness != requiredFitness
    }

    int finalise(List d) {
        for (p in 0..< population.size()) {
            println(population[p])
        }
        println "Solution Found ${population[bestLocation]}\n "
        return completedOK
    }
}