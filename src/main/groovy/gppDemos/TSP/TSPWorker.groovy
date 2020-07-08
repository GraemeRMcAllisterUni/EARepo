package gppDemos.TSP


import gppDemos.EAClasses.Worker


class TSPWorker extends Worker {


    List<Integer> board = []

    Map cityList// = [1: [74, 386], 2: [142, 261], 3: [337, 394], 4: [211, 66], 5: [400,270], 6: [272, 377], 7: [429, 179]]

    //def cityList = [[342, 228], [74, 386], [142, 261], [337, 394], [211, 66], [292, 242], [290, 256], [387, 212], [272, 377], [429, 179]]

    int N// = cityList.size()

    TSPWorker() {
        cityList = [1: [74, 386], 2: [142, 261], 3: [337, 394], 4: [211, 66], 5: [400,270], 6: [272, 377], 7: [429, 179]]
        N = cityList.size()
    }

    @Override
    int init(List d) {
        N = (Integer) d[0]
        N = cityList.size()
        crossoverProb = (int) d[1]
        mutateProb = (int) d[2]
        return completedOK
    }

    @Override
    int createFunction() {
        for (int i in 1..cityList.size())
            board.add(i)
        Collections.shuffle(board)
        fitness = doFitness(board)
        return completedOK
    }

    @Override
    double doFitness(List board) {
        double fitnessResult = 0
        for (int i in 0..N - 2) {
            List city1 = (List) cityList.get(board[i])
            List city2 = (List) cityList.get(board[i + 1])
            fitnessResult = fitnessResult + distance(city1, city2)
        }
        fitnessResult = fitnessResult + distance(cityList.get(board[0]), cityList.get(board[board.size() - 1]))
        println(fitnessResult)
        return fitnessResult
    }

    double distance(List city1, List city2) {
        double xDis = Math.abs((int) city1[0] - (int) city2[0])
        double yDis = Math.abs((int) city1[1] - (int) city2[1])
        return Math.sqrt((xDis**2) + (yDis**2))
    }

    @Override
    boolean evolve(List parameters) {
        TSPWorker p1 = (TSPWorker) parameters[0]
        TSPWorker p2 = (TSPWorker) parameters[1]
        TSPWorker child1 = (TSPWorker) parameters[2]
        TSPWorker child2 = (TSPWorker) parameters[3]
//        println("Parent 1 board is: " + p1.board)
//        println("Parent 2 board is: " + p2.board)
        int probability = rng.nextInt(101)
        if (probability < crossoverProb) {
            // do the crossover operation
            child1.board = new int[N]
            child2.board = new int[N]
            onePointCrossover(p1, p2, child1, child2)
//            probability = rng.nextInt(101)
//            if (probability < mutateProb) {
//                // do the mutate operation
//                int mutate1 = rng.nextInt(N + 1)
//                int mutate2 = rng.nextInt(N + 1)
//                //ensure m1 and m2 are different
//                while (mutate2 == mutate1) mutate2 = rng.nextInt(N) + 1
//                // swaps bits m1 and m2 in evolute.board
//                child1.board.swap(mutate1, mutate2)
//                child2.board.swap(mutate1, mutate2)
//            }

//            println("Child 1 = " + child1.board)
//            println("Parent 1 fitness: " + p1.fitness)
//
//
//            println("Child 2 =" + child2.board)
//            child2.fitness = doFitness(child2.board)
//            println("Parent 2 fitness: " + p2.fitness)

            //println "C1: $child1 C2: $child2"

            return true
        } else
            return false
    }

    void doOrderedCrossover (TSPWorker p1, TSPWorker p2, TSPWorker child1, TSPWorker child2) {
        int c1 = rng.nextInt(N-3) + 2   // c1 in range 2 .. N-2
        int c2 = rng.nextInt(N-1) + 1   // c2 in range 1 .. N-1
        // ensure c1 and c2 are different
        while (Math.abs(c1 - c2) <= 2) c2 = rng.nextInt(N-1) + 1
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

    void onePointCrossover(TSPWorker parent1, TSPWorker parent2, TSPWorker child1, TSPWorker child2) {
        int cPoint = rng.nextInt(N -4) + 2   // choose the crossover point >1 and <N-2
        //println "One Point cossiver - Parent1: $parent1.board Parent2: $parent2.board xOver: $cPoint"
        List parent1a = parent1.board[0..cPoint-1]
        List parent1b = parent1.board[cPoint..N - 1]
        List parent2a = parent2.board[0..cPoint-1]
        List parent2b = parent2.board[cPoint..N - 1]
        // find values in common between p1a and p2a
        List common = []
        for (int i in 0..<cPoint) {
            if (parent2a.contains(parent1a[i])) {
                common << parent1a[i]
            }
        }

        List p1aRem = parent1a.minus(common)
        List p2aRem = parent2a.minus(common)
//        println("common: " + common)
//        println "parent1a $parent1a, parent1b $parent1b, parent2a $parent2a, parent2b $parent2b, p1aRem $p1aRem, p2aRem $p2aRem, common $common"
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


//    void doOrderedCrossover(TSPWorker parent1, TSPWorker parent2, TSPWorker child1, TSPWorker child2, int cPoint) {
//        println "Parent1: $parent1.board Parent2: $parent2.board xOver: $cPoint"
//        List p1a = parent1.board[0..cPoint]
//        List p1b = parent1.board[cPoint..N - 1]
//        List p2a = parent2.board[0..cPoint]
//        List p2b = parent2.board[cPoint..N - 1]
//        // find values in common between p1a and p2a
//        List common = []
//        for (int i in 0..<cPoint) {
//            if (p2a.contains(p1a[i])) {
//                common << p1a[i]
//            }
//        }
//        println(common)
//        List p1aRem = p1a.minus(common)
//        List p2aRem = p2a.minus(common)
//        println "$p1a, $p1b, $p2a, $p2b, $p1aRem, $p2aRem, $common"
//        for (int i in 0..<cPoint) {
//            child1.board[i] = p1a[i]
//        }
//        //for (int p in startGene..endGene)
////        println "C1: $child1 C2: $child2"
//        int p1P = 0
//        int p2P = 0
//        for (int i in 0..<p2b.size()) {
//            int v1 = p2b[i]
//            if (p1aRem.contains(v1)) {
//                child1.board << p2aRem[p2P]
//                p2P += 1
//            } else
//                child1.board << v1
//            int v2 = p1b[i]
//            if (p2aRem.contains(v2)) {
//                child2.board << p1aRem[p1P]
//                p1P += 1
//            } else
//                child2.board << v2
//        }
//    }
}


