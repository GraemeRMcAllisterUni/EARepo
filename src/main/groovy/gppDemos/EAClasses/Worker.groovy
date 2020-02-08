package gppDemos.EAClasses

import gppDemos.maxOneProblem.MaxOneIndividual
import groovy.transform.CompileStatic
import gppDemos.UniversalResponse
import gppLibrary.DataClass


abstract class Worker extends DataClass {


     static int N  // number of Queens to be placed
     static int initialPopulationSize
     static int crossoverProb
     static int mutateProb
    Double fitness
    transient static Random rng = new Random()
    transient static String initialiseMethod = "init"
    transient static String createFunction = "createFunction"
    transient static String evolveFunction = "evolve"
    transient int id = -1

    //List board



    int init(List d) {
        println("Master init called")
        N = (int)d[0]
        crossoverProb = (int)d[1]
        mutateProb = (int)d[2]
        return completedOK
    }

    abstract int createFunction()
    abstract double doFitness(List board)

    abstract boolean evolve (List parameters)




}

