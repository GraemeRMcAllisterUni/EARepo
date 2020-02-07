package gppDemos.maxOneProblem

import gppDemos.EAClasses.Worker
import gppLibrary.DataClass
import groovy.transform.CompileStatic

@CompileStatic
class MaxOneIndividual extends Worker{
    static int bitsPerGene = 16
    BitSet board = new BitSet()
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
        board = new BitSet(bitsPerGene)
        for ( b in 0 ..< bitsPerGene) {
            if (rng.nextInt(2)  == 1) board.set(b)
        }
        fitness = doFitness(board)
        return completedOK
    } 
    
    double doFitness(BitSet gene) {
        return 1.0D - (gene.cardinality()/ bitsPerGene)
    }  
    
    boolean evolve(List <Worker> parameters) {
        // expecting two parents and returning two children
        MaxOneIndividual p1 = (MaxOneIndividual)parameters[0]
        MaxOneIndividual p2 = (MaxOneIndividual)parameters[1]
        MaxOneIndividual c1 = (MaxOneIndividual)parameters[2]
        MaxOneIndividual c2 = (MaxOneIndividual)parameters[3]
        if (rng.nextInt(101) < crossoverProb) {
            // do the crossover
            int xOver = rng.nextInt(bitsPerGene)
            c1.board = new BitSet(bitsPerGene)
            c2.board = new BitSet(bitsPerGene)
            for ( b in 0 .. xOver) {
                if (p1.board[b]) c1.board.set(b)
                if (p2.board[b]) c2.board.set(b)
            }
            for ( b in xOver+1 .. bitsPerGene) {
                if (p2.board[b]) c1.board.set(b)
                if (p1.board[b]) c2.board.set(b)
            }
            if (rng.nextInt(101) < mutateProb) {
                // do mutate operation
                int m1 = rng.nextInt(bitsPerGene)
                int m2 = rng.nextInt(bitsPerGene)
                c1.board.flip(m1)
                c2.board.flip(m2)
            }
            c1.fitness = doFitness(c1.board)
            c2.fitness = doFitness(c2.board)
            return true
        }
        else
            return false
    } // evolve
    
    String toString() {
        return "$board\nGene Fitness: $fitness"
    }
    
}
