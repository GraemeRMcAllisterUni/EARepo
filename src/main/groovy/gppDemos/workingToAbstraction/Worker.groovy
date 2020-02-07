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
    static String initialiseMethod = "init"
    static String createFunction = "createFunction"
    static String evolveFunction = "evolve"
    int id = -1




    int init(List d) {
//        println "QC-init: $d"
        N = d[0]
        crossoverProb = d[1]
        mutateProb = d[2]
        return completedOK
    }

    int createFunction() {}

    double doFitness(List <Integer> board) {}

    boolean evolve (List <Worker> parameters){}




}

