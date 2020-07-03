package gppDemos.empty


import gppDemos.EAClasses.RunEA
import gppDemos.UniversalResponse
import gppDemos.maxOneProblem.MaxOneIndividual
import gppDemos.EAClasses.Manager
import gppDemos.EAClasses.Worker

Random random = new Random();


List ClientList=   [1,  2,  3,  4,  6,  12]//, 16, 24, 48]


//Collections.shuffle(ClientList)

int N = 1000 //rng.nextInt(5000)+1
int clients = 1
int totalPop = 12
int initialPopulation = 2 //possPops[ rng.nextInt(possPops.size()) ]
int crossoverProb = 95//random.nextInt(101)
int mutateProb = 5//random.nextInt(101)

int loops = 150

long seed = 1234567L


Random rng = new Random()
rng.setSeed(seed)

File log = new File("trying2048.csv");
log.createNewFile()

for(int i = 0; i<ClientList.size(); i++) {
    seed = 1234567L

    loops.times {

        int j = random.nextInt(ClientList.size())
        clients = (int)ClientList.get(j)
        initialPopulation = (int) (totalPop / clients)
        RunEA EA = new RunEA()

        EA.worker = new MaxOneIndividual()

        EA.manager = new MaxOneManager()

        EA.N = N //rng.nextInt(5000)+1
        EA.clients = clients
        EA.initialPopulation = initialPopulation
        EA.crossoverProb = crossoverProb //rng.nextInt(99)+1
        EA.mutateProb = mutateProb
        EA.seed = seed

        List runConfig = [seed, N, clients, initialPopulation, crossoverProb, mutateProb]
        FileWriter fr = new FileWriter("trying2048.csv", true)
        println("loop number: " + it)
        for (r in 0..runConfig.size() - 1)
            fr.write(runConfig[r] + ",")
        System.gc()
        long startTime = System.currentTimeMillis()
        fr.write(startTime + ",")
        fr.close()
        EA.run()
        long endTime = System.currentTimeMillis()
        FileWriter frr = new FileWriter("trying2048.csv", true)
        frr.write(endTime + "\n")
        frr.close()
        println("finished loop: " + it)
        EA = null
        seed++
    }
    }


class MaxOneManager extends Manager{


    int finalise(List d) {
        //println "Fitness: ${((MaxOneIndividual)population[bestLocation]).board.cardinality()} / ${((MaxOneIndividual)population[bestLocation]).board.length()} - 1 = ${population[bestLocation].fitness}"
        //println "$requestedParents parents requested; creating $improvements improvements"
        //print "$requestedParents, $improvements\n"
        writeFile()
        return completedOK
    }

    static void writeFile(){
        FileWriter fr = new FileWriter("trying2048.csv", true)
        fr.write("$requestedParents,$improvements,")
        fr.close()
    }


    UniversalResponse selectParents(int parents) {
        requestedParents = requestedParents + parents // for analysis
        def response = new UniversalResponse()
        //response.payload[0] = population[bestLocation]
        for (i in 0 ..< parents) {
            response.payload[i] = population[i]
        }
        return response
    }

    int addIndividuals(List <Worker> individuals) { //add the new individuals to the population
        for ( i in 0 ..< individuals.size()) {
            population.add(individuals[i])
        }
        if(population.size()>=12) {
            FileWriter fr = new FileWriter("trying2048.csv", true)
            fr.write(System.currentTimeMillis() + ",")
            fr.close()

        }
        determineBestWorst()

        return completedOK
    }

}
