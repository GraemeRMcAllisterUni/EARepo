package gppDemos.maxOneProblem

import gppDemos.EAClasses.Worker
import gppLibrary.DataClass
import groovy.transform.CompileStatic

import java.util.concurrent.SynchronousQueue

@CompileStatic
class MaxOneIndividual extends Worker{
    static int N = 16
    BitSet board = new BitSet()
    Double fitness = 1.0D

    static int crossoverProb = -1

    static String initialiseMethod = "init"
    static String createFunction = "createFunction"
    static String evolveFunction = "evolve"
    static int id = 1

    static Random rng = new Random()

    static long seed = System.currentTimeMillis()

    int init(List d) {
        N = (int)d[0]
        crossoverProb = (int)d[1]
        if (d[2] != null)
            seed = (long)d[2]
        if (d[3] != null)
            id = (int)d[3]
        rng.setSeed(seed*id)

        return completedOK
    }

    
    int createFunction() {
        board = new BitSet(N)
        for (i in 0 ..<N) {
            if (rng.nextInt(3)  == 1) board.set(i)
        }
        fitness = doFitness(board)
        return completedOK
    }

    @Override
    double doFitness(List board) {
        return 0
    }

    double doFitness(BitSet gene) {
        return 1.0D - (gene.cardinality()/ N)
    }  
    
    boolean evolve(List parameters) {
        // expecting two parents and returning two children
        MaxOneIndividual p1 = (MaxOneIndividual)parameters[0]
        MaxOneIndividual p2 = (MaxOneIndividual)parameters[1]
        MaxOneIndividual c1 = (MaxOneIndividual)parameters[2]
        MaxOneIndividual c2 = (MaxOneIndividual)parameters[3]
        //if (rng.nextInt(101) < crossoverProb) {
            // do the crossover
            int xOver = rng.nextInt(N)
            c1.board = new BitSet(N)
            c2.board = new BitSet(N)
            for ( b in 0 .. xOver) {
                if (p1.board[b]) c1.board.set(b)
                if (p2.board[b]) c2.board.set(b)
            }
            for ( b in xOver+1 .. N) {
                if (p2.board[b]) c1.board.set(b)
                if (p1.board[b]) c2.board.set(b)
            }
            if (rng.nextInt(101) < 100-crossoverProb) {
                // do mutate operation
                int m1 = rng.nextInt(N)
                int m2 = rng.nextInt(N)
                c1.board.flip(m1)
                c2.board.flip(m2)
            }
            c1.fitness = doFitness(c1.board)
            c2.fitness = doFitness(c2.board)
            return true
//        }
//        else
//            return false
    } // evolve
    
    String toString() {
        return "$board"
    }
    
}
