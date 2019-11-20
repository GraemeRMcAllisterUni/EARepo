package gppDemos

import gppLibrary.DataClass
import gppDemos.Population
import gppDemos.Chromosome
import gppDemos.Gene

abstract class CSPEAClient extends DataClass{


    static int chromosomeLength
    static int popSize

    static Population population
    static Chromosome chromosome
    static Gene gene
    Double fitness = 1.0D

    static int crossoverProb = -1
    static int mutateProb = -1

    static String initialiseMethod = "init"
    static String createFunction = "createFunction"
    static String evolveFunction = "evolve"

    int init(List d) {
        chromosomeLength = d[0]
        crossoverProb = d[1]
        mutateProb = d[2]
        Random rng = new Random()
        if (d[3] != null) rng.setSeed((long)d[3])
        return completedOK
    }

    abstract int createFunction()

    abstract double doFitness(Chromosome chromosome)

}
