package gppDemos.workingToAbstraction

import gppDemos.UniversalResponse
import gppLibrary.DataClass


abstract class Manager extends DataClass{

    List <Worker> population = []
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
        N = (int)d[0]
        editProportion = (float)d[1]
        rng.setSeed(100)
        return completedOK
    }


    UniversalResponse selectParents(int parents) {
        println("Manager:need a method for selecting parents")
        requestedParents = requestedParents + parents // for analysis
        def response = new UniversalResponse()
        for ( i in 0 ..< parents) {
            int p = rng.nextInt(population.size())
            response.payload[i] = population[p]
        }
        return response
    }

    int addChildren(List <Worker> children) {
        println("Manager: need a method to add children, takes in list of workers as param")
        editPopulation()
        return completedOK
    }


    void editPopulation(){
        println("Manager:editing population")
        determineBestWorst()
    }


    void determineBestWorst(){
        println("Manager:determining best worst")
    }


    int addIndividuals(List <Worker> individuals) {
        println("Manager:adding individuals")
        determineBestWorst()
        return completedOK
    }



    boolean carryOn() { // returns true if the manager should continue
        println("Manager:checking if solution found")
        if ( bestFitness != requiredFitness)
            return true
        else
            return false
    }

    int finalise(List d) {
        println("Manager:elapsed or solution found")
        return completedOK
    }
}
