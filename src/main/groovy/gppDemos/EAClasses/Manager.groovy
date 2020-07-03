package gppDemos.EAClasses

import gppDemos.UniversalResponse
import gppLibrary.DataClass


class Manager extends DataClass{

    List <Worker> population = []
    Double requiredFitness = 0.0D
    Double worstFitness = 0.0D
    Double bestFitness = 1.0D
    int worstLocation, bestLocation
    Integer N = 0
    static Random rng = new Random()
    static int requestedParents = 0
    static int requeiredarents = 2
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

    int initialise (List d) {
        improvements = 0
        requestedParents =0
        population = []
        worstFitness = 0.0D
        bestFitness = 1.0D
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
        boolean childAdded = false
        for (c in 0 ..< children.size()) {
            Worker child = children[c]
            // only add child if it is better than the worst child in the population
            if (child.fitness < worstFitness) {
                childAdded = true
                improvements++ // for analysis
//                print "improvement $improvements with fit $child.fitness after $requestedParents parent requests"
                worstFitness = child.fitness
                population[worstLocation] = child
                // new child could be better than the current best
                if (child.fitness < bestFitness) {
                    bestFitness = child.fitness
                    bestLocation = worstLocation
//                    print " new best"
//                    println "$child.fitness after ${requestedParents/2} evolutions "
                }
                // now update minFitness
                worstFitness = bestFitness
                for ( p in 0 ..< population.size()) {
                    if (population[p].fitness > worstFitness) {
                        // found a new minimum fitness
                        worstFitness = population[p].fitness
                        worstLocation = p
                    }
                }
//                println " $worstFitness, $bestFitness"
            } // end if
        } // end for loop
        if (childAdded) {
            if (bestFitness == worstFitness)
                editPopulation()
        }
        return completedOK
    }


    void editPopulation(){
//        int populationSize = population.size()
//        int editNumber = (int)(populationSize * editProportion)
//        for ( c in 1 .. editNumber) {
//            int id = rng.nextInt(populationSize)
////            print "$c = $id: ${population[id]} ->"
//            int m1 = rng.nextInt(N) + 1
//            int m2 = rng.nextInt(N) + 1
//            while ( m2 == m1) m2 = rng.nextInt(N) + 1
//            ((Worker)population[id]).board.swap(m1, m2)
//            ((Worker)population[id]).fitness = ((Worker)population[id]).doFitness(((Worker)population[id]).board)
////            println "$m1, $m2, ${population[id]}"
//        }
        determineBestWorst()
    }


    void determineBestWorst(){
        bestFitness = population[0].fitness
        worstFitness = population[0].fitness
        for (p in 0..< population.size()) {
            if (population[p].fitness < bestFitness) {
                // update max fitness data
                bestFitness = population[p].fitness
                bestLocation = p
            }
            if (population[p].fitness > worstFitness) {
                // update min fitness data
                worstFitness = population[p].fitness
                worstLocation = p
            }
        }
        //println population[bestLocation]
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