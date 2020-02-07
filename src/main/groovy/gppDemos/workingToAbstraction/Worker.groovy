package gppDemos.workingToAbstraction

import gppDemos.maxOneProblem.MaxOneIndividual
import groovy.transform.CompileStatic
import gppDemos.UniversalResponse
import gppLibrary.DataClass


@CompileStatic
abstract class Worker extends DataClass {


    static int N  // number of Queens to be placed
    static int initialPopulationSize
    static int crossoverProb
    static int mutateProb
    Double fitness
    static Random rng = new Random()
    List <Integer> board = null
    static String initialiseMethod = "init"
    static String createFunction = "createFunction"
    static String evolveFunction = "evolve"
    int id = -1

    


    int init(List d) {
//        println "QC-init: $d"
        N = d[0]
        crossoverProb = d[1]
        mutateProb = d[2]
        if (d[3] != null) rng.setSeed((long)d[3])
        if (d[4] != null) id = d[4]
        return completedOK
    }

    int createFunction() {}


    double doFitness(List <Integer> board) {}

    boolean evolve (List <Worker> parameters){
    }




}

