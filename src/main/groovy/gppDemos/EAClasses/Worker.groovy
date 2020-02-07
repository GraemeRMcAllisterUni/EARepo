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
    static Random rng = new Random()
    static String initialiseMethod = "init"
    static String createFunction = "createFunction"
    static String evolveFunction = "evolve"
    int id = -1

    //List board




    int init(List d) {
        N = d[0]
        crossoverProb = d[1]
        mutateProb = d[2]
        return completedOK
    }

    abstract int createFunction()

    abstract double doFitness(List board)

    abstract boolean evolve (List parameters)




}

