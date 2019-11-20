package gppDemos.maxOneProblem

import gppDemos.Chromosome
import gppDemos.Population
import gppDemos.UniversalResponse
import gppLibrary.DataClass

class MaxOneServer extends DataClass{
    Population population
    Double requiredFitness = 0.0D
    Double worstFitness = 0.0D
    Double bestFitness = 1.0D
    int worstLocation, bestLocation
    long seed = 0L
    static Random rng = new Random()

    static int requestedParents = 0
    static int improvements = 0
    static int modifications = 0
    static int chromosomeLength = 0
    static float editProportion = 0.0F

    static String selectParentsFunction = "selectParents"
    static String incorporateChildrenMethod = "addChildren"
    static String addIndividualsMethod = "addIndividuals"
    static String carryOnFunction = "carryOn"
    static String initMethod = "initialise"
    static String finaliseMethod = "finalise"

    int initialise (List d) {
        seed = (long)d[0]
        chromosomeLength = (int)d[1]
        editProportion = (float)d[2]
        rng.setSeed(seed)
        return completedOK
    }

    UniversalResponse selectParents(int parents) {
        Chromosome chro = new Chromosome()
        requestedParents = requestedParents + parents // for analysis
        def response = new UniversalResponse()
        for ( i in 0 ..< parents) {
            int p = rng.nextInt(400)
            response.payload[i] = chro[p]
        }
        return response
    }

    int addChildren(Population children) {
        boolean childAdded = false
        for ( c in 0 ..< children.size()) {
            Chromosome child = children.getChromosome(c)
            child.fitness = children.getFitness(child)
            // only add child if it is better than the worst child in the population
            if (child.fitness < worstFitness) {
                childAdded = true
                improvements = improvements + 1 // for analysis
//                print "improvement $improvements with fit $child.fitness after $requestedParents parent requests"
                worstFitness = child.fitness
                population.replaceChromosome(worstLocation, child)
                // new child could be better than the current best
                if (child.fitness < bestFitness) {
                    bestFitness = child.fitness
                    bestLocation = worstLocation
                    print " new best"
                    println "$child.fitness after ${requestedParents/2} evolutions "
                }
                // now update minFitness
                worstFitness = bestFitness
                for ( p in 0 ..< population.Count()) {
                    if (child.fitness > worstFitness) {
                        // found a new minimum fitness
                        worstFitness = child.fitness
                        worstLocation = p
                    }
                }
//                println " $worstFitness, $bestFitness"
            } // end if
        } // end for loop
        if (childAdded) {
            if (bestFitness == worstFitness)
                nuke()
        }        
        return completedOK        
    }

    void nuke(){
        int populationSize = population.Count()
        int editNumber = (int)(populationSize * editProportion)
        for ( c in 0 .. editNumber) {
            int id = rng.nextInt(populationSize)
//            print "$c = $id: ${population[id]} ->"
            int m1 = rng.nextInt(chromosomeLength)
            int m2 = rng.nextInt(chromosomeLength)
            while ( m2 == m1)
                m2 = rng.nextInt(chromosomeLength)
            // remove from list at m1
           // population.replaceChromosome(, )
           // ((MaxOneIndividual)population[id]).fitness = ((MaxOneIndividual)population[id]).doFitness(((Chromosome) // add in pop
//            println "$m1, $m2, ${population[id]}"
        }
        determineBestWorst()
        modifications += 1
    }
    
    void determineBestWorst(){
        bestFitness = 1.0D
        worstFitness = 0.0D
        for ( p in 0..< population.Count()) {
            if (population.getChromosome(p).fitness < bestFitness) {
                // update max fitness data
                bestFitness = population.getChromosome(p).fitness
                bestLocation = p
            }
            if (population[p].fitness > worstFitness) {
                // update min fitness data
                worstFitness = population.getChromosome(p).fitness
                worstLocation = p
            }
        }
    }

    int addIndividuals(Population localPop) {
        //add the new individuals to the population
        for ( i in 0 ..< localPop.size()) {
            population.addChromosome(localPop.getChromosome(i))
        }
        determineBestWorst()
        return completedOK
    }

    boolean carryOn() { // returns true if the server should continue
        if ( bestFitness != requiredFitness)
            return true
        else
            return false
    }

    int finalise(List d) {
//        println "Best ${population[bestLocation]}"
//        println "$requestedParents parents requested; creating $improvements improvements"
        print "$requestedParents, $improvements, $modifications, "
        return completedOK
    }

}
