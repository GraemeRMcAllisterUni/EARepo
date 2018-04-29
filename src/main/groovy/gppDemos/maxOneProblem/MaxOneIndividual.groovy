package gppDemos.maxOneProblem

import gppLibrary.DataClass
import groovy.transform.CompileStatic

@CompileStatic
class MaxOneIndividual extends DataClass{
    static int bitsPerGene = 16
    BitSet gene = new BitSet()
    Double fitness = 1.0D

    static int crossoverProb = -1
    static int mutateProb = -1

    static String initialiseMethod = "init"
    static String createFunction = "createFunction"
    static String evolveFunction = "evolve"
    

    static Random rng = new Random()

    int init(List d) {
        bitsPerGene = d[0]
        crossoverProb = d[1]
        mutateProb = d[2]
        if (d[3] != null) rng.setSeed((long)d[3])   
        return completedOK
    }
    
    int createFunction() {
        gene = new BitSet(bitsPerGene)
        for ( b in 0 ..< bitsPerGene) {
            if (rng.nextInt(2)  == 1) gene.set(b)
        }
        fitness = doFitness(gene)
        return completedOK
    } 
    
    double doFitness(BitSet gene) {
        return 1.0D - (gene.cardinality()/ bitsPerGene)
    }  
    
    boolean evolve(List <MaxOneIndividual> parameters) {
        // expecting two parents and returning two children
        MaxOneIndividual p1 = parameters[0]
        MaxOneIndividual p2 = parameters[1]
        MaxOneIndividual c1 = parameters[2]
        MaxOneIndividual c2 = parameters[3]
        if (rng.nextInt(101) < crossoverProb) {
            // do the crossover
            int xOver = rng.nextInt(bitsPerGene)
            c1.gene = new BitSet(bitsPerGene)
            c2.gene = new BitSet(bitsPerGene)
            for ( b in 0 .. xOver) {
                if (p1.gene[b]) c1.gene.set(b)
                if (p2.gene[b]) c2.gene.set(b)
            }
            for ( b in xOver+1 .. bitsPerGene) {
                if (p2.gene[b]) c1.gene.set(b)
                if (p1.gene[b]) c2.gene.set(b)
            }
            if (rng.nextInt(101) < mutateProb) {
                // do mutate operation
                int m1 = rng.nextInt(bitsPerGene)
                int m2 = rng.nextInt(bitsPerGene)
                c1.gene.flip(m1)
                c2.gene.flip(m2)
            }
            c1.fitness = doFitness(c1.gene)
            c2.fitness = doFitness(c2.gene)
            return true
        }
        else
            return false
    } // evolve
    
    String toString() {
        return "Gene Fitness: $fitness"
    }
    
}
