package gppDemos.maxOneProblem

import gppDemos.EAClasses.Worker
import gppLibrary.DataClass
import groovy.transform.CompileStatic

import java.util.concurrent.SynchronousQueue

@CompileStatic
class MaxOneIndividual extends Worker {
    BitSet board = new BitSet()
    int createFunction() {
        board = new BitSet(N)
        for (i in 0..<N) {
            if (rng.nextInt(3) == 1) board.set(i)
        }
        fitness = doFitness(board)
        return completedOK
    }

    @Override
    double doFitness(List board) {
        return 0
    }

    double doFitness(BitSet gene) {
        return N - gene.cardinality()
    }

    void mutate(Worker child) {
        child = (MaxOneIndividual) child
        int point = rng.nextInt(N)
        child.board.flip(point)
    }

    void crossover(Worker parent0, Worker parent1, Worker child, int k){
        def points = []
        int modifier
        List<MaxOneIndividual> parents = [(MaxOneIndividual)parent0,(MaxOneIndividual)parent1]
        for(int i in 0..k-1)
            points[i] = rng.nextInt(N-1)+1
        points = points.sort()
        int slice = 0
        for(int i=0; i<k; i++)
        {
            modifier = ((i % 2 == 0) ? 0 : 1)
            for(int j in slice..(int)points[i])
                if (parents[modifier].board[j]) ((MaxOneIndividual)child).board.set(j)

            slice += (int)points[i] + 1
        }
        modifier = ((k % 2 == 0) ? 0 : 1)
        for(int j in slice..N)
            if (parents[modifier].board[j]) ((MaxOneIndividual)child).board.set(j)

    }

 boolean evolve(List parameters) {
     List<MaxOneIndividual> parents = [(MaxOneIndividual)parameters[0],(MaxOneIndividual)parameters[1]]
     List<MaxOneIndividual> children = []
     for(i in 2..parameters.size()-1)
     children[i-2] = (MaxOneIndividual) parameters[i]
     if (rng.nextInt(101) < crossoverProb) {
         int xOver = rng.nextInt(N)
         for (c in children) {
             c.board = new BitSet(N)
             crossover(parents[0],parents[1],c, 1)
             parents.reverse()
             if (rng.nextInt(101) < mutateProb) {
                 mutate(c)
             }
             c.fitness = doFitness(c.board)
         }
         return true
     } else
         return false
 } // evolve

    String toString() {
        return "$fitness $board"
    }

    @Override
    boolean equals(Object obj) {
        if(obj instanceof MaxOneIndividual) {
            return obj.board.equals(this.board)
        }
        else
            return false
    }

}
