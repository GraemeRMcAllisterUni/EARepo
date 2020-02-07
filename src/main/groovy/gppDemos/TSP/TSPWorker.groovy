package gppDemos.TSP


import gppDemos.EAClasses.Worker


class TSPWorker extends Worker {

    List <Integer> board = []

    def cityList = [:]

    int cities = 5

    int N = 5

    int mapSize = 25


    @Override
    int createFunction() {



        for (int i in 0..N) {
            cityList = [i, new City(rng.nextInt(mapSize), rng.nextInt(mapSize))]
            board.add(i)
        }
        return completedOK

    }

    @Override
    double doFitness(List board) {
        for (int i in 0..board.size() - 1) {
            fitness =+ distance((City) cityList[i], (City) cityList[i + 1])
        }
        return fitness
    }

    double distance(City c1, City c2) {
        double xDis = Math.abs(c1.x - c2.x)
        double yDis = Math.abs(c1.y - c2.y)
        return Math.sqrt((xDis**2) + (yDis**2))
    }

    @Override
    boolean evolve(List parameters) {
        TSPWorker p1 = (TSPWorker) parameters[0]
        TSPWorker p2 = (TSPWorker) parameters[1]
        TSPWorker child1 = (TSPWorker) parameters[2]
        TSPWorker child2 = (TSPWorker) parameters[3]
        int probability = rng.nextInt(101)
        if (probability < crossoverProb) {
            // do the crossover operation
            child1.board = new ArrayList(N + 1)
            child2.board = new ArrayList(N + 1)
            int cPoint = rng.nextInt(N)// - 3) + 2    // choose the crossover point >0 and <N
            doCrossoverOnePoint(p1, p2, child1, child2, cPoint)
            probability = rng.nextInt(101)
            if (probability < mutateProb) {
                // do the mutate operation
                int mutate1 = rng.nextInt(N) + 1
                int mutate2 = rng.nextInt(N) + 1
                //ensure m1 and m2 are different
                while (mutate2 == mutate1) mutate2 = rng.nextInt(N) + 1
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
        } else
            return false
    }


    void doCrossoverOnePoint(p1, p2, child1, child2, int cPoint) {
        // zeroth element is null
//        println "P1: $p1 P2: $p2 xOver: $cPoint"
        List p1a = p1.board.getAt(1..cPoint)
        List p2a = p2.board.getAt(1..cPoint)
        List p1b = p1.board.getAt(cPoint + 1..N)
        List p2b = p2.board.getAt(cPoint + 1..N)
        // find values in common between p1a and p2a
        List common = []
        for (int i in 0..<cPoint) {
            if (p2a.contains(p1a[i])) {
                common << p1a[i]
            }
        }
        List p1aRem = p1a.minus(common)
        List p2aRem = p2a.minus(common)
//        println "$p1a, $p1b, $p2a, $p2b, $p1aRem, $p2aRem, $common"
        child1.board << null
        child2.board << null
        for (int i in 0..<cPoint) {
            child1.board[i + 1] = p1a[i]
            child2.board[i + 1] = p2a[i]
        }
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


class City{

    int x = 0
    int y = 0

    City(x,y){
        this.x = x
        this.y = y
    }

}