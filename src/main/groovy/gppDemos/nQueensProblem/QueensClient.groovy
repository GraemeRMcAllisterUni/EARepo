package gppDemos.nQueensProblem

import gppDemos.EAClasses.Worker
import gppLibrary.DataClass
import groovy.transform.CompileStatic

// based on http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.129.720&rep=rep1&type=pdf

@CompileStatic
class QueensClient extends Worker {
    static int N = 0   // number of Queens to be placed
    List <Integer> board = null
    Double fitness = 0.0D   // can be negative 0.0 => solution found
    
    static int initialPopulationSize =-1
    static int crossoverProb = -1
    static int mutateProb = -1

    static String initialiseMethod = "init"
    static String createFunction = "createFunction"
   // static String evolveFunction = "evolveTwoPoint"
    static String evolve = "evolveOnePoint"

    @Override
    boolean evolve(List<Worker> parameters) {
        return 0
    }

    static Random rng = new Random()

    int individuals = 0

    int init(List d) {
//        println "QC-init: $d"
        N = d[0]
        crossoverProb = d[1]
        mutateProb = d[2]
        if (d[3] != null) rng.setSeed((long)d[3])
        return completedOK
    }
    
    int instance = 0

    int createFunction() {
        permute()
        fitness = doFitness(board)
//        println "Initial Board: $board, Fit: $fitness"
        return completedOK
    }    
    
    void permute () {
        board = new ArrayList(N+1)
        for ( int i in 1.. N) board[i] = i
//        println "QC-permute: Client: $clientId board = $board"
        for (int i in 1 .. N) {
//            println "QC-permute: Client: $clientId i: $i"
            rng.setSeed(System.currentTimeMillis())
            int j = rng.nextInt(N) + 1  //range is 1..N
            board.swap(i,j)
        }
    }
    
    double doFitness(List <Integer> board) {
        List <Integer> leftDiagonal = new ArrayList(2*N)
        List <Integer> rightDiagonal = new ArrayList(2*N)
        double sum = 0.0D
        
        for ( i in 1 .. 2*N) {
            leftDiagonal[i] = 0
            rightDiagonal[i] = 0
        }
        for ( int i in 1 .. N) {
            leftDiagonal[i+board[i]-1]++
            int idx = N-i+board[i]
            rightDiagonal[idx]++
//            rightDiagonal[N-i+board[i]]++
        }
        for ( i in 1 .. ((2*N) - 1)) {
            int counter = 0
            if ( leftDiagonal[i] > 1)
                counter += leftDiagonal[i] - 1
            if ( rightDiagonal[i] > 1)
                counter += rightDiagonal[i] - 1
            sum += counter / (N - Math.abs(i-N))
        }
        // target fitness is 0.0
        // sum can be negative so return absolute value
        return Math.abs(sum)   
    }

    
    void doCrossoverTwoPoint (QueensClient p1, QueensClient p2, QueensClient evolute,
                        int c1, int c2) {
        if (c1 > c2) (c1,c2)=[c2,c1]    // groovy way to swap two values lhs is a double assignment
//        println "QC crossover: Client: $clientId : $c1, $c2, $N"
        // the crossover works by taking two distinct crossover points and swapping the bit between
        // assume c1 < c2
        // evolute 1..<c1 = p1 1..<c1
        // evolute c1..c2 = p2 c1..<c2
        // evolute c2+1..N = p1 c2..N
        // then have to ensure that no value is repeated to maintain board consistency
        List sb1 = p1.board.getAt(1 ..< c1) // first part of p1.board
        List mb1 = p1.board.getAt(c1 .. c2) // middle part of p1.board
        List mb2 = p2.board.getAt(c1 .. c2) // middle part of p2.board
        List eb1 = p1.board.getAt(c2+1 .. N)//end part of p1.board
        // now find common values in mb1 and mb2 and remove from mb1
        // this leaves values in mb1 that need to be inserted into result
        // to maintain board consistency as a permutation
        int mb2Size = mb2.size()
        for ( i in 0 ..< mb2Size) {
            int v = mb2[i]
            int j = 0
            boolean notFound = true
            while ( (notFound) && (j < mb2Size)) {
                if ( v == mb1[j]) {
                    notFound = false
                    mb1.remove(j)   // removes the jth element
                }
                else
                    j = j + 1
            }
        }
        // now iterate through mb2 looking for matches in sb1
        // replace any with values from mb1
        for ( i in 0..< mb2Size) {
            if (sb1.contains(mb2[i])) {
                int v = mb2[i]
                int j = 0
                boolean notFound = true
                while  (notFound) {
                    if (v == sb1[j]) {
                        notFound = false
                        sb1[j] = mb1.pop()
                    }
                    else
                        j = j + 1
                }
            }
        }
            
        // now iterate through mb2 looking for matches in eb1
        // replace any with values from mb1
        for ( i in 0..< mb2Size) {
            if (eb1.contains(mb2[i])) {
                int v = mb2[i]
                int j = 0
                boolean notFound = true
                while  (notFound) {
                    if (v == eb1[j]) {
                        notFound = false
                        eb1[j] = mb1.pop()
                    }
                    else
                        j = j + 1
                }
            }
        }
        // now construct the final result in evolute
        evolute.board = [null] + sb1 + mb2 + eb1    // zeroth element is always null        
    }
    
    boolean evolveTwoPoint (List parameters) {
        QueensClient p1 = parameters[0]
        QueensClient p2 = parameters[1]
        QueensClient evolute = parameters[2]
        int probability = rng.nextInt(101)
        if (probability < crossoverProb) {
            // do the crossover operation
            evolute.board = new ArrayList(N+1)
            int c1 = rng.nextInt(N-3) + 2   // c1 in range 2 .. N-2
            int c2 = rng.nextInt(N-1) + 1   // c2 in range 1 .. N-1
            // ensure c1 and c2 are different
            while (Math.abs(c1 - c2) <= 2) c2 = rng.nextInt(N-1) + 1
            doCrossoverTwoPoint(p1, p2, evolute, c1, c2)            
            probability = rng.nextInt(101)
            if (probability < mutateProb) {
                // do the mutate operation
                int mutate1 = rng.nextInt(c1-1) + 1
                int mutate2 = rng.nextInt(N-c2) + c2 +1
//                //ensure m1 and m2 are different
//                while (mutate2 == mutate1)  mutate2 = rng.nextInt(N) + 1
                // swaps bits m1 and m2 in evolute.board
                evolute.board.swap(mutate1, mutate2)
            }            
             
            evolute.fitness = doFitness(evolute.board)

            println("Parent 1 fitness" + p1.fitness)
            println("Parent 2 fitness" + p2.fitness)
            println("evolute fitness" + evolute.fitness)
            return true            
        }
        else
            return false
    }
    
    void doCrossoverOnePoint (QueensClient p1, QueensClient p2, QueensClient child1, QueensClient child2, int cPoint) {
        // zeroth element is null
//        println "P1: $p1 P2: $p2 xOver: $cPoint"
        List p1a = p1.board.getAt(1 .. cPoint)                         
        List p2a = p2.board.getAt(1 .. cPoint)   
        List p1b = p1.board.getAt(cPoint+1 .. N)                      
        List p2b = p2.board.getAt(cPoint+1 .. N) 
        // find values in common between p1a and p2a 
        List common = []  
        for ( int i in 0 ..< cPoint) {
            if ( p2a.contains(p1a[i])) {
                common << p1a[i]
            }
        }   
        List p1aRem = p1a.minus(common)              
        List p2aRem = p2a.minus(common) 
//        println "$p1a, $p1b, $p2a, $p2b, $p1aRem, $p2aRem, $common"   
        child1.board << null
        child2.board << null
        for ( int i in 0 ..< cPoint) {
            child1.board[i+1] = p1a[i]
            child2.board[i+1] = p2a[i]
        }
//        println "C1: $child1 C2: $child2"
        int p1P = 0
        int p2P = 0
        for ( int i in 0 ..< p2b.size())  {
            int v1 = p2b[i]
            if ( p1aRem.contains(v1)) { 
                child1.board << p2aRem[p2P]
                p2P +=1                
            }
            else 
                child1.board << v1
            int v2 = p1b[i]   
            if ( p2aRem.contains(v2)) {
                child2.board << p1aRem[p1P]
                p1P += 1
            }
            else 
                child2.board << v2
        } 
    }
    
    boolean evolveOnePoint (List <QueensClient> parameters) {

        QueensClient p1 = parameters[0]
        QueensClient p2 = parameters[1]
        QueensClient child1 = parameters[2]
        QueensClient child2 = parameters[3]
        int probability = rng.nextInt(101)
        if (probability < crossoverProb) {
            // do the crossover operation
            child1.board = new ArrayList(N+1)
            child2.board = new ArrayList(N+1)
            int cPoint = rng.nextInt(N-3) + 2    // choose the crossover point >0 and <N
            doCrossoverOnePoint (p1, p2, child1, child2, cPoint)            
            probability = rng.nextInt(101)
            if (probability < mutateProb) {
                // do the mutate operation
                int mutate1 = rng.nextInt(N) + 1
                int mutate2 = rng.nextInt(N) +1
                //ensure m1 and m2 are different
                while (mutate2 == mutate1)  mutate2 = rng.nextInt(N) + 1
                // swaps bits m1 and m2 in evolute.board
                child1.board.swap(mutate1, mutate2)
                child2.board.swap(mutate1, mutate2)
            }

            child1.fitness = doFitness(child1.board)
            println("Parent 1 fitness" + p1.fitness)
            println("Child 1 fitness" + child1.fitness)

            child2.fitness = doFitness(child2.board)
            println("Parent 2 fitness" + p2.fitness)
            println("Child 2 fitness" + child2.fitness)
//            println "C1: $child1 C2: $child2"
            
            return true            
        }
        else
            return false
    }

    boolean CheckSolution() {
        boolean [] test = new boolean[N + 1]
        for ( i in 1 .. N) test[i] = false
        for ( j in 1 .. N ) test[board[j]] = true
        int t = 1
        while ( t <= N){
            if (test[t])
                t = t + 1
            else
                break
        }
        if ( t == N + 1 )
            return true
        else
            return  false
    }



    
    String toString(){
        String s = ""
//        s = s + "Board: $board, Fit: $fitness"
        s = s + "Board: \n"
        int p = 1
        int rows
        rows = (int)(N / 8)
        for ( r in 1 .. rows) {
            for ( c in 1 .. 8) {
                 s = s + " ${board[(int)p]}, "
                 p = p + 1
            }
            s = s + "\n"
        }
        s = s + "Fit: $fitness,  Check: ${CheckSolution()}"
    }
    
}
