package gppDemos

import gppLibrary.DataClass
import gppDemos.Population
import gppDemos.Chromosome
import gppDemos.Gene

abstract class CSPEAClient extends DataClass{


    static int chromosomeLength
    static int popSize

    static Population localPopulation = new Population()
    static Chromosome chromosome
    static Gene gene
    Double fitness = 1.0D
    double bestFitness, worstFitness
    int bestLocation, worstLocation

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

    Population getLast (){
        return localPopulation
    }

    void determineBestWorst(){
        bestFitness = 1.0D
        worstFitness = 0.0D
        for ( p in 0..< localPopulation.Count()) {
            if (localPopulation.getChromosome(p).fitness < bestFitness) {
                // update max fitness data
                bestFitness = localPopulation.getChromosome(p).fitness
                bestLocation = p
            }
            if (localPopulation[p].fitness > worstFitness) {
                // update min fitness data
                worstFitness = localPopulation.getChromosome(p).fitness
                worstLocation = p
            }
        }
    }

    int addIndividuals(List<Population> localPopulations) {
        //add the new individuals to the population
        for(x in 0 ..<localPopulations.size()) {
            Population localPop = new Population()
            localPop = localPopulations[x]
            for (i in 0..<localPop.Count()) {
                print(localPop.getChromosome(i).printGenes())
                population.addChromosome(localPop.getChromosome(i))
            }
        }
        determineBestWorst()
        return completedOK
    }

    boolean evolve(Population parents, int i) {
        // expecting two parents and returning two children
        Chromosome p1 = parents.getChromosome(0)
        Chromosome p2 = parents.getChromosome(1)
        if (rng.nextInt(101) < crossoverProb) {
            // do the crossover
            int xOver = rng.nextInt(chromosomeLength)
            Chromosome c1 = new Chromosome()
            Chromosome c2 = new Chromosome()
            for ( b in 0 .. xOver) {
                if (p1.getGene(b)) c1.setGene(b)
                if (p2.getGene(b)) c2.setGene(b)
            }
            for ( b in xOver+1 .. chromosomeLength) {
                if (p2.getGene(b)) c1.setGene(b)
                if (p1.getGene(b)) c2.setGene(b)
            }
            if (rng.nextInt(101) < mutateProb) {
                // do mutate operation
                int m1 = rng.nextInt(chromosomeLength)
                int m2 = rng.nextInt(chromosomeLength)
                c1.remove(m1)
                c2.remove(m2)
            }

            parents.Fitness(c1,doFitness(c1))
            parents.Fitness(c2,doFitness(c2))
            return true
        }
        else
            return false
    }

}
