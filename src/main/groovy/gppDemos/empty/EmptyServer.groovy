package gppDemos.empty

import gppDemos.UniversalResponse
import gppDemos.maxOneProblem.MaxOneIndividual
import gppLibrary.DataClass


class EmptyServer extends DataClass{

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
        seed = (long)d[0]
        N = (int)d[1]
        editProportion = (float)d[2]
        rng.setSeed(seed)
        return completedOK
    }


    UniversalResponse selectParents(int parents) {
        println("Server:selecting parents")
        def response = new UniversalResponse()
        return response
    }

    int addChildren(List <EmptyClient> children) {
        println("Server:adding children")
        editPopulation()
        return completedOK
    }


    void editPopulation(){
        println("Server:editing population")
        determineBestWorst()
    }


    void determineBestWorst(){
        println("Server:determining best worst")
    }


    int addIndividuals(List <EmptyClient> individuals) {
        println("Server:adding individuals")
        determineBestWorst()
        return completedOK
    }



    boolean carryOn() { // returns true if the server should continue
        println("Server:checking if solution found")
        if ( bestFitness != requiredFitness)
            return true
        else
            return false
    }

    int finalise(List d) {
        println("Server:elapsed or solution found")
        return completedOK
    }
}
