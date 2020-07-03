package gppDemos.EAClasses

import gppDemos.maxOneProblem.MaxOneIndividual
import groovy.transform.CompileStatic
import gppDemos.UniversalResponse
import gppLibrary.DataClass
import gppDemos.QuickSort.Quicksort


abstract class Worker extends DataClass {


    int N = 2
    static int crossoverProb
    static int mutateProb
    public double fitness
    transient static Random rng = new Random()
    transient static String initialiseMethod = "init"
    transient static String createFunction = "createFunction"
    transient static String evolveFunction = "evolve"
    transient int id = -1
    static long seed = System.currentTimeMillis()
    def board = []


    int init(List d) {
        println("Super.init() called")
        N = (int)d[0]; println "N = $N"
        crossoverProb = (int)d[1]; println "crossoverProb = $crossoverProb"
        mutateProb = (int)d[2]; println "mutateProb = $mutateProb"
        if (d[3] != null)
            seed = (long)d[3]
        if (d[4] != null)
            id = (int)d[4]
        println(seed*id)
        rng.setSeed(seed*id)
        return completedOK
    }

    abstract int createFunction()

    abstract double doFitness(List board)

    abstract boolean evolve (List parameters)

    void crossover(Worker parent0, Worker parent1, Worker  child, int n){
        crossover(parent0, parent1, child, n, 1)
    }

    void crossover(Worker parent0, Worker parent1, Worker  child, int n, int k){
        int[] points = new int[k+1]
        List<Worker> parents = [parent0,parent1]

        for(int i in 0..k)
            points[i] = rng.nextInt(n-1)+1

        //quicksort.sort(points)

        int range = 0
        for(int i=0; i<k; i++)
        {
            for(int j in range.. points[i])
                int modifier = ((i % 2==0) ? 0 : 1)
                child.board[range..points[i]] = parents[modifier].board[range..points[i]]
            range += points[i] + 1
        }
        child.board[range..n] = parents[0].board[range..n]
    }

    void mutate(Worker child) {
        int point = rng.nextInt(N-1)
        def swap = child.board[point]
        child.board[point] = child.board[point+1]
        child.board[point+1] = swap
    }

    String toString() {
        return "$fitness $board"
    }

}

