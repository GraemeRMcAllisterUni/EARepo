package gppDemos.maxOneProblem



import gppDemos.CSPEAClient
import gppDemos.Population
import gppDemos.Chromosome
import gppDemos.Gene
import gppLibrary.DataClass
import groovy.transform.CompileStatic

@CompileStatic
class MaxOneIndividual extends CSPEAClient{

    static int crossoverProb = -1
    static int mutateProb = -1

    static Random rng = new Random()


    
    int createFunction() {
        Chromosome chro = new Chromosome();
        for ( b in 0 ..< chromosomeLength) {
            if (rng.nextInt(2) == 1)
                chro.setGene(b)
        }
        fitness = doFitness(chro)
        return completedOK
    } 
    
    double doFitness(Chromosome c) {
        int count = 0
        c.each{count = count + 1}
        return 1.0D - (count/ chromosomeLength)
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
    } // evolve
    
    String toString() {
        return "Gene Fitness: $fitness"
    }
    
}
