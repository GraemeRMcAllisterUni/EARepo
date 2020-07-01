package gppDemos.TSP


import gppDemos.EAClasses.Worker


class TSPWorker extends Worker{



        List<Integer> board = []

        Map cityList = [1: [342, 228], 2: [74, 386], 3: [142, 261], 4: [337, 394], 5: [211, 66], 6: [292, 242], 7: [290, 256], 8: [387, 212], 9: [272, 377], 10: [429, 179]]

        int N = cityList.size()

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
            doFitness(board)
            return completedOK
        }

        @Override
        double doFitness(List board) {
            double fitnessResult = 0
            println("doing fitness on: " + board)
            for (int i in 0..N - 2) {
                List c1 = (List) cityList.get(board[i])
                List c2 = (List) cityList.get(board[i + 1])
                fitnessResult = fitnessResult + distance(c1, c2)
            }
            fitnessResult = fitnessResult + distance(cityList.get(board[0]), cityList.get(board[board.size()-1]))
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
            println "Evolving"
            TSPWorker p1 = (TSPWorker) parameters[0]
            TSPWorker p2 = (TSPWorker) parameters[1]
            TSPWorker child1 = (TSPWorker) parameters[2]
            TSPWorker child2 = (TSPWorker) parameters[3]
            println("Parent 1 board is: " + p1.board)
            println("Parent 2 board is: " + p2.board)
            int probability = rng.nextInt(101)
            println "probability = $probability\n crossoverProb = $crossoverProb"
            if (probability < crossoverProb) {
                // do the crossover operation
                println("doing fitness on: " + board)
                child1.board = new int[N]
                child2.board = new int[N]
                int cPoint = rng.nextInt(N-2)+2   // choose the crossover point >0 and <N
                println "cPoint = " + cPoint
                doOrderedCrossover(p1, p2, child1, child2, cPoint)
                probability = rng.nextInt(101)
                if (probability < mutateProb) {
                    // do the mutate operation
                    int mutate1 = rng.nextInt(N+1)
                    int mutate2 = rng.nextInt(N+1)
                    //ensure m1 and m2 are different
                    while (mutate2 == mutate1) mutate2 = rng.nextInt(N) + 1
                    // swaps bits m1 and m2 in evolute.board
                    child1.board.swap(mutate1, mutate2)
                    child2.board.swap(mutate1, mutate2)
                }

                println("Doing fitness from evolve")
                child1.fitness = doFitness(child1.board)
                println("Child 1 = " + child1.board)
                println("Parent 1 fitness: " + p1.fitness)


                child2.fitness = doFitness(child2.board)
                println("Parent 2 fitness: " + p2.fitness)
                println("Child 2 fitness" + child2.fitness)
//            println "C1: $child1 C2: $child2"

                return true
            } else
                return false
        }


        void doOrderedCrossover(TSPWorker parent1, TSPWorker parent2, TSPWorker child1, TSPWorker child2, int cPoint) {
        println "Parent1: $parent1.board Parent2: $parent2.board xOver: $cPoint"
            List p1a = parent1.board[0..cPoint]
            List p1b = parent1.board[cPoint..N - 1]
            List p2a = parent2.board[0..cPoint]
            List p2b = parent2.board[cPoint..N - 1]
            // find values in common between p1a and p2a
            List common = []
            for (int i in 0..<cPoint) {
                if (p2a.contains(p1a[i])) {
                    common << p1a[i]
                }
            }
            println(common)
            List p1aRem = p1a.minus(common)
            List p2aRem = p2a.minus(common)
        println "$p1a, $p1b, $p2a, $p2b, $p1aRem, $p2aRem, $common"
            for (int i in 0..<cPoint) {
                child1.board[i] = p1a[i]
            }
            //for (int p in startGene..endGene)
//        println "C1: $child1 C2: $child2"
            int p1P = 0
            int p2P = 0
            for (int i in 0..<p2b.size()) {
                int v1 = p2b[i]
                if (p1aRem.contains(v1)) {
                    child1.board << p2aRem[p2P]
                    p2P += 1
                } else
                    child1.board << v1
                int v2 = p1b[i]
                if (p2aRem.contains(v2)) {
                    child2.board << p1aRem[p1P]
                    p1P += 1
                } else
                    child2.board << v2
            }
        }
    }


