package gppDemos.EAClasses

import gppDemos.UniversalResponse
import gppLibrary.DataClass


class Manager extends DataClass {

    List<Worker> population = []
    Double requiredFitness = 0.0D
    Double worstFitness, bestFitness
    int worstLocation, bestLocation
    int resetCounter = 0
    int maxResets = 100
    static Random rng = new Random()

    static int requestedParents = 0
    static int improvements = 0
    int noImprovements = 0
    static int noImprovementsStop = 50000


    static String initMethod = "initialize"
    static String selectParentsFunction = "selectParents"
    static String incorporateChildrenMethod = "addChildren"
    static String addIndividualsMethod = "addIndividuals"
    static String carryOnFunction = "carryOn"
    static String finaliseMethod = "finalise"

    Manager(){
        noImprovements = 0
        improvements = 0
        requestedParents =0
        //population.clear()
        worstFitness = null
        bestFitness = null
    }

    int addIndividuals(List<Worker> individuals) { //add the new individuals to the population
        for (int i in 0..<individuals.size()) {
            population.add(individuals[i])
        }
        determineBestWorst()
        return completedOK
    }

    UniversalResponse selectParents(int parents) {
        requestedParents = requestedParents + parents // for analysis
        def response = new UniversalResponse()
        for (i in 0..<parents) {
            int p = rng.nextInt(population.size())
            response.payload[i] = population[p]
        }
        return response
    }

    int addChildren(List<Worker> children) {
        noImprovements++
        for (child in children) {
            if (child.fitness < worstFitness && !population.contains(child)) {
                noImprovements = 0
                improvements++
                worstFitness = child.fitness
                population[worstLocation] = child
                determineBestWorst()
            }
        }
        return completedOK
    }

    void determineBestWorst() {
        bestFitness = Double.MAX_VALUE
        worstFitness = 0
        for (i in 0..<population.size()) {
            if (population[i].fitness < bestFitness) {
                bestFitness = population[i].fitness
                bestLocation = i
            }
            if (population[i].fitness > worstFitness) {
                worstFitness = population[i].fitness
                worstLocation = i
            }
        }
    }

    boolean carryOn() {
        if (noImprovements >= noImprovementsStop && resetCounter < maxResets) {
            resetCounter++
            resetPop()
            noImprovements = 0
        }
        return bestFitness > requiredFitness
    }

    boolean hardCarryOn() {
        if (noImprovements >= noImprovementsStop && resetCounter < maxResets) {
            resetCounter++
            resetPop()
            noImprovements = 0
        }
        return (noImprovements < noImprovementsStop)
    }

    void resetPop() {
//        for (p in population)
//            println p
        println "Best: " +  population[bestLocation]
        for (int i = 0; i < population.size(); i++)
            if (i != bestLocation)
                this.population[i].fitness = (Double.MAX_VALUE - 1)
        worstFitness = Double.MAX_VALUE
    }

    int finalise(List d) {
        for (p in 0..<population.size()) {
            println(population[p])
        }
        println "Solution Found ${population[bestLocation]}\n "
        return completedOK
    }
}