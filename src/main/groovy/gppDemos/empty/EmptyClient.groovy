package gppDemos.empty

import gppDemos.maxOneProblem.MaxOneIndividual
import groovy.transform.CompileStatic
import gppDemos.UniversalResponse
import gppLibrary.DataClass


@CompileStatic
class EmptyClient extends DataClass {


    static int N = 0   // number of Queens to be place
    static int initialPopulationSize = -1
    static int crossoverProb = -1
    static int mutateProb = -1
    Double fitness = 0.0D
    static Random rng = new Random()
    List <Integer> board = null
    static String initialiseMethod = "init"
    static String createFunction = "createFunction"
    static String evolveFunction = "evolve"


    int init(List d) {
//        println "QC-init: $d"
        N = d[0]
        crossoverProb = d[1]
        mutateProb = d[2]
        if (d[3] != null) rng.setSeed((long)d[3])
        return completedOK
    }


    int createFunction() {
        println("Client:creating board")
        permute()
        fitness = doFitness(board)
        return completedOK
    }

    void permute () {
        println("Client:permute")
    }

    double doFitness(List <Integer> board) {
        println("Client:doing fitness")
        double result
        return result
    }

    boolean evolve (List <MaxOneIndividual> parameters){
        println("Client:evolving")
        return true
    }

}

