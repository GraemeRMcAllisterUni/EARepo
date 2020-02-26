package gppDemos.EAClasses

import gppDemos.maxOneProblem.MaxOneIndividual
import groovy.transform.CompileStatic
import gppDemos.UniversalResponse
import gppLibrary.DataClass


abstract class Worker extends DataClass {


     static int N
     static int initialPopulationSize
     static int crossoverProb
     static int mutateProb
    Double fitness
    transient static Random rng = new Random()
    transient static String initialiseMethod = "init"
    transient static String createFunction = "createFunction"
    transient static String evolveFunction = "evolve"
    transient int id = -1

    //def board = []



    int init(List d) {
        println("Super.init() called")
        N = (int)d[0]; println "N = $N"
        crossoverProb = (int)d[1]; println "crossoverProb = $crossoverProb"
        mutateProb = (int)d[2]; println "mutateProb = $mutateProb"
        return completedOK
    }

    abstract int createFunction()
    abstract double doFitness(List board)

    abstract boolean evolve (List parameters)

}

