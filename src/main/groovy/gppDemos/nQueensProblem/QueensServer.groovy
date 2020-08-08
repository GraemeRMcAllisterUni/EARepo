package gppDemos.nQueensProblem

import gppDemos.EAClasses.Manager
import gppLibrary.DataClass
import gppDemos.UniversalResponse
import groovy.transform.CompileStatic

@CompileStatic
class QueensServer extends Manager {
    List <QueensClient> population = []
    Double requiredFitness = 0.0D
    Double worstFitness = 0.0D
    Double bestFitness = 1.0D
    int worstLocation, bestLocation
    long seed = 0L
    static Random rng = new Random()

    static int requestedParents = 0
    static int improvements = 0
    static int N = 0
    static float editProportion = 0.0F

    static String selectParentsFunction = "selectParents"
    static String incorporateChildrenMethod = "addChildren"
    static String addIndividualsMethod = "addIndividuals"
    static String carryOnFunction = "carryOn"
    static String initMethod = "initialise"
    static String finaliseMethod = "finalise"

    int initialise (List d) {
        //seed = d[0]
        N = d[0]
        editProportion = d[1]
        rng.setSeed(System.currentTimeMillis())
        return completedOK
    }

    UniversalResponse selectParents(int parents) {

        //requestedParents = requestedParents + parents // for analysis

        def response = new UniversalResponse()

        for ( i in 0 ..< parents) {
            int p = rng.nextInt(population.size())
            response.payload[i] = population[p]
        }
        return response // returns population is a list of clients, p is a random integer between 0..population.size() so payload[n] is a random client from within the population
    }

    int addChildren(List <QueensClient> children) {
        boolean childAdded = false
        for ( c in 0 ..< children.size()) {
            QueensClient child = children[c]
            // only add child if it is better than the worst child in the population
            if (child.fitness < worstFitness) {
                childAdded = true
                improvements = improvements + 1 // for analysis
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
        int populationSize = population.size()
        int editNumber = (int)(populationSize * editProportion)
        for ( c in 1 .. editNumber) {
            int id = rng.nextInt(populationSize)
//            print "$c = $id: ${population[id]} ->"
            int m1 = rng.nextInt(N) + 1
            int m2 = rng.nextInt(N) + 1
            while ( m2 == m1) m2 = rng.nextInt(N) + 1
            ((QueensClient)population[id]).board.swap(m1, m2)
            ((QueensClient)population[id]).fitness = ((QueensClient)population[id]).doFitness(((QueensClient)population[id]).board)
//            println "$m1, $m2, ${population[id]}"
        }
        determineBestWorst()
    }
    
    void determineBestWorst(){
        bestFitness = 1.0D
        worstFitness = 0.0D
        for ( p in 0..< population.size()) {
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
    }

    int addIndividuals(List <QueensClient> individuals) { //add the new individuals to the population

        for ( i in 0 ..< individuals.size()) {
            population.add(individuals[i])
        }
        determineBestWorst()
//        population.each{println "$it"}
//        println " "
        return completedOK
    }

    boolean carryOn() { // returns true if the server should continue
        println(bestFitness + " " + requiredFitness)
        if ( bestFitness != requiredFitness)
            return true
        else {

            println(population[bestLocation].toString())
            return false
        }
    }

    int finalise(List d) {
//        population.each{println "$it"}
//        println "${requestedParents/2}, $improvements"
//        println "${population[bestLocation]}"
        return completedOK
    }

}
