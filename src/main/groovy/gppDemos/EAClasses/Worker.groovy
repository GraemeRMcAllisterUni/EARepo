package gppDemos.EAClasses

import gppDemos.maxOneProblem.MaxOneIndividual
import groovy.transform.CompileStatic
import gppDemos.UniversalResponse
import gppLibrary.DataClass


abstract class Worker extends DataClass {


    int N = 2
    static int initialPopulationSize
    static int crossoverProb
    Double fitness
    transient static Random rng = new Random()
    transient static String initialiseMethod = "init"
    transient static String createFunction = "createFunction"
    transient static String evolveFunction = "evolve"
    transient int id = -1
    static long seed = System.currentTimeMillis()
    //def board


    int init(List d) {
        println("Super.init() called")
        N = (int)d[0]; println "N = $N"
        crossoverProb = (int)d[1]; println "crossoverProb = $crossoverProb"
        if (d[2] != null)
            seed = (long)d[2]
        if (d[3] != null)
            id = (int)d[3]
        println(seed*id)
        rng.setSeed(seed*id)
        return completedOK
    }

    abstract int createFunction()

    abstract double doFitness(List board)

    abstract boolean evolve (List parameters)

}

