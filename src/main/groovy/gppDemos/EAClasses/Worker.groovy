package gppDemos.EAClasses

import gppDemos.maxOneProblem.MaxOneIndividual
import groovy.transform.CompileStatic
import gppDemos.UniversalResponse
import gppLibrary.DataClass


abstract class Worker extends DataClass {

    public static int N = -1
    static int crossoverProb =-1
    static int mutateProb =-1
    int mutationRate
    public double fitness
    static Random rng = new Random()
    transient static String initialiseMethod = "init"
    transient static String createFunction = "createFunction"
    transient static String evolveFunction = "evolve"
    static int k = 1
    transient int id = -1
    static long seed = System.currentTimeMillis()
    def board = []

    int init(List d) {
        N = (int)d[0]
        crossoverProb = (int)d[1]
        mutateProb = (int)d[2]
        if (d[3] != null)
            seed = (long)d[3]
        if (d[4] != null)
            id = (int)d[4]
        rng.setSeed(seed*id)
        return completedOK
    }

    abstract int createFunction()

    abstract double doFitness(List board)

    abstract boolean evolve (List parameters)

    void crossover(Worker parent0, Worker parent1, Worker  child){
        crossover(parent0, parent1, child, 1)
    }

    void crossover(Worker parent0, Worker parent1, Worker  child, int k){
        int[] points = new int[k+1]
        int modifier =0
        List<Worker> parents = [parent0,parent1]

        for(int i in 0..k)
            points[i] = rng.nextInt(N-1)+1
        int range = 0
        for(int i=0; i<k; i++)
        {
            for(int j in range.. points[i])
                modifier = ((i % 2==0) ? 0 : 1)
                child.board[range..points[i]] = parents[modifier].board[range..points[i]]
            range += points[i] + 1
        }
        child.board[range..n] = parents[0].board[range..N]
    }

    void onePointOrderedCrossover(Worker parent1, Worker parent2, Worker child1, Worker child2) {

        int cPoint = rng.nextInt(N -4) + 2

        List parent1a = parent1.board[0..cPoint-1]
        List parent1b = parent1.board[cPoint..N - 1]
        List parent2a = parent2.board[0..cPoint-1]
        List parent2b = parent2.board[cPoint..N - 1]

        List common = []

        for (int i in 0..<cPoint) {
            if (parent2a.contains(parent1a[i])) {
                common << parent1a[i]
            }
        }
        List p1aRem = parent1a.minus(common)
        List p2aRem = parent2a.minus(common)
       for (int i in 0..<cPoint) {
            child1.board[i] = parent1a[i]
        }
        for (int i in 0..<cPoint) {
            child2.board[i] = parent2a[i]
        }
        int p1P = 0
        int p2P = 0
        for (int i in 0..<parent2b.size()) {
            int v1 = parent2b[i]
            if (p1aRem.contains(v1)) {
                child1.board << p2aRem[p2P]
                p2P += 1
            } else
                child1.board << v1
            int v2 = parent1b[i]
            if (p2aRem.contains(v2)) {
                child2.board << p1aRem[p1P]
                p1P += 1
            } else
                child2.board << v2
        }

    }

    void twoPointOrderedCrossover(Worker p1,Worker p2, Worker child1, Worker child2){
        twoPointOrderedCrossover (p1, p2, child1)
        twoPointOrderedCrossover (p1, p2, child2)
    }

    void twoPointOrderedCrossover (Worker p1, Worker p2, Worker child) {
        int c1 = rng.nextInt(N -4) + 2
        int c2 = rng.nextInt(N -4) + 2
        while(c2==c1)
            c2 = rng.nextInt(N -4) + 2
        if (c1 > c2) (c1,c2)=[c2,c1]
        List sb1 = p1.board.getAt(0 .. c1-1)
        List mb1 = p1.board.getAt(c1 .. c2-1)
        List mb2 = p2.board.getAt(c1 .. c2-1)
        List eb1 = p1.board.getAt(c2 .. N-1)
        int mb2Size = mb2.size()
        for ( i in 0 ..< mb2Size) {
            int v = mb2[i]
            int j = 0
            boolean notFound = true
            while ( (notFound) && (j < mb2Size)) {
                if ( v == mb1[j]) {
                    notFound = false
                    mb1.remove(j)   // removes the jth element
                } else
                    j = j + 1
            }
        }
        for ( i in 0..< mb2Size) {
            if (sb1.contains(mb2[i])) {
                int v = mb2[i]
                int j = 0
                boolean notFound = true
                while  (notFound) {
                    if (v == sb1[j]) {
                        notFound = false
                        sb1[j] = mb1.pop()
                    } else
                        j = j + 1
                }
            }
        }
        for ( i in 0..< mb2Size) {
            if (eb1.contains(mb2[i])) {
                int v = mb2[i]
                int j = 0
                boolean notFound = true
                while  (notFound) {
                    if (v == eb1[j]) {
                        notFound = false
                        eb1[j] = mb1.pop()
                    } else
                        j = j + 1
                }
            }
        }
        child.board = sb1 + mb2 + eb1
    }

    void mutate(Worker child) {
        int point1 = rng.nextInt(child.board.size()-1)
        int point2 = rng.nextInt(child.board.size())
        def swap = child.board[point1]
        child.board[point1] = child.board[point2]
        child.board[point2] = swap
    }

    String toString() {
        return "$fitness $board"
    }

    @Override
    boolean equals(Object obj) {
        if(obj instanceof Worker) {
            return obj.board == this.board
        }
        else
            return false
    }
}

