package gppDemos.maxOneProblem

import gppDemos.EAClasses.Manager
import gppDemos.EAClasses.RunEA
import gppDemos.EAClasses.Worker

import java.util.concurrent.ThreadLocalRandom

Random random = new Random();


List ClientList=   [1, 2, 3, 4, 6, 12]


//Collections.shuffle(ClientList)

int N = 1000//rng.nextInt(5000)+1
int clients
int totalPop = 48
int initialPopulation//possPops[ rng.nextInt(possPops.size()) ]
int crossoverProb = 100//random.nextInt(101)
int mutateProb = 100//random.nextInt(101)

int loops = 3

long seed = 1234567L

Random rng = new Random()
rng.setSeed(seed)

int loopsonloops = 50

long startrunTime = System.currentTimeSeconds()
println(startrunTime)
File log = new File("clients2.csv");
log.createNewFile()
for(int l = 1;l<loopsonloops;l++) {
    Collections.shuffle(ClientList)
    seed = 1234567L
    for (int i = 0; i < ClientList.size(); i++) {
            loops.times {
            System.gc()
            int j = random.nextInt(ClientList.size())
            clients = ClientList[i]
            initialPopulation = (int)(totalPop/clients)
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
            FileWriter fr = new FileWriter("clients2.csv", true)
            println("loop number: " + ((int) it + 1))
            for (r in 0..runConfig.size() - 1)
                fr.write(runConfig[r] + ",")
            long startTime = System.currentTimeMillis()
            fr.write(startTime + ",")
            fr.close()
            EA.run()
            long endTime = System.currentTimeMillis()
            println("finished loop: " + ((int) it + 1))
            FileWriter frr = new FileWriter("clients2.csv", true)
            frr.write(endTime + "\n")
            frr.close()
            seed++
        }
    }

}


long endrunTime = System.currentTimeSeconds()
println endrunTime-startrunTime

class MaxOneManager extends Manager{


    int finalise(List d) {
//        StringBuilder s = new StringBuilder();
//        for( int i = 0; i < population[bestLocation].N;  i++ )
//        {
//            s.append(population[bestLocation].board.get( i ) == true ? 1: 0 );
//        }
//        System.out.println( s );
//        println "Fitness: ${((MaxOneIndividual)population[bestLocation]).board.cardinality()} / ${((MaxOneIndividual)population[bestLocation]).board.length()} - 1 = ${population[bestLocation].fitness}"
        //println "$requestedParents parents requested; creating $improvements improvements"
        //print "$requestedParents, $improvements\n"
        writeFile()
        return completedOK
    }

    static void writeFile(){
        FileWriter fr = new FileWriter("clients2.csv", true)
        fr.write("$requestedParents,$improvements,")
        fr.close()
    }
//    UniversalResponse selectParents(int parents) {
//        requestedParents = requestedParents + parents // for analysis
//        def response = new UniversalResponse()
//        response.payload[0] = population[bestLocation]
//        response.payload[1] = population[secondBestLocation]
//        return response
//    }


    @Override
    int addIndividuals(List <Worker> individuals) { //add the new individuals to the population
        for ( i in 0 ..< individuals.size()) {
            population.add(individuals[i])
        }
        if(population.size()>=48) {
            FileWriter fr = new FileWriter("clients2.csv", true)
            fr.write(System.currentTimeMillis() + ",")
            fr.close()
        }
        determineBestWorst()
        return completedOK
    }


    @Override
    void determineBestWorst(){
        bestFitness = 1
        worstFitness = 0
        for (i in 0..< population.size()) {
            if (population[i].fitness < bestFitness) {
                // update max fitness data
                bestFitness = population[i].fitness
                bestLocation = i
            }
            if (population[i].fitness > worstFitness) {
                // update min fitness data
                worstFitness = population[i].fitness
                worstLocation = i
            }
        }
    }


}