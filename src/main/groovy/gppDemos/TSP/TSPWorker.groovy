package gppDemos.TSP


import gppDemos.EAClasses.Worker


class TSPWorker extends Worker {

    String filePath = "wi29.csv";
    List<Integer> board = []

    Map cityList = new HashMap() // [1: [74, 386], 2: [142, 261], 3: [337, 394], 4: [211, 66], 5: [400,270], 6: [272, 377], 7: [429, 179] , 8:[150,500], 9:[300,100], 10:[100,100], 11:[220,420], 12:[100,200], 13:[100,450], 14:[350,120], 15:[400,330], 16:[100,330]]


    TSPWorker() {
        //readTSP()
        cityList = [1: [74, 386], 2: [142, 261], 3: [337, 394], 4: [211, 66], 5: [400,270], 6: [272, 377], 7: [429, 179] , 8:[150,500], 9:[300,100], 10:[100,100], 11:[220,420], 12:[100,200], 13:[100,450], 14:[350,120], 15:[400,330], 16:[100,330]]
        N = cityList.size()
        mutationRate = (int) (cityList.size() / 2)
    }

    @Override
    int init(List d) {
        N = cityList.size()
        crossoverProb = (int) d[1]
        mutateProb = (int) d[2]
        return completedOK
    }

    @Override
    int createFunction() {
            for (int i in 1..N)
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
        fitnessResult = fitnessResult + distance((List)cityList.get(board[0]), (List)cityList.get(board[board.size() - 1]))
        return fitnessResult
    }

    static int distance(List city1, List city2) {
            double xDis = Math.abs((int) city1[0] - (int) city2[0])
            double yDis = Math.abs((int) city1[1] - (int) city2[1])
            return Math.sqrt((xDis**2) + (yDis**2))
    }


    @Override
    boolean evolve(List parameters) {
        TSPWorker p1 = (TSPWorker) parameters[0]
        if(p1.fitness==Double.MAX_VALUE-1){
            p1 = new TSPWorker()
            p1.N = this.N
            p1.createFunction()
        }
        TSPWorker p2 = (TSPWorker) parameters[1]
        if(p2.fitness==Double.MAX_VALUE-1){
            p2 = new TSPWorker()
            p2.N = this.N
            p2.createFunction()
        }
        TSPWorker child1 = (TSPWorker) parameters[2]
        TSPWorker child2 = (TSPWorker) parameters[3]

        int probability = rng.nextInt(101)
        if (probability < crossoverProb) {
            child1.board = new int[N]
            child2.board = new int[N]
            onePointOrderedCrossover(p1, p2, child1, child2)
            probability = rng.nextInt(101)
            if (probability < mutateProb) {
                rng.nextInt(mutationRate).times{
                    mutate(child1)
                }
                rng.nextInt(mutationRate).times{
                    mutate(child2)
                }
            }
            child1.fitness = this.doFitness(child1.board)
            child2.fitness = this.doFitness(child2.board)
            return true
        } else
            return false
    }


    @Override
    boolean equals(Object obj) {
        if(obj instanceof TSPWorker) {
            return obj.fitness.equals(this.fitness) && obj.board.equals(this.board)
        }
        else
            return false
    }

    @Override
    String toString() {
        return fitness + " " + board + " " + board.first()
    }

    Map readTSP() {
        String line;
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",", 3);
            int key = Integer.parseInt(parts[0])
            List value = [Double.parseDouble(parts[1]),Double.parseDouble(parts[2])]
            cityList.put(key, value);
        }
        reader.close();
    }
}


