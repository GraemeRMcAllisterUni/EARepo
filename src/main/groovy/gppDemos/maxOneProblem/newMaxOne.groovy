package gppDemos.maxOneProblem

import gppDemos.EAClasses.Manager
import gppDemos.EAClasses.RunEA
import gppDemos.EAClasses.Worker

import java.util.concurrent.ThreadLocalRandom

Random random = new Random();




int N = 1000//rng.nextInt(5000)+1
int clients

int initialPopulation//possPops[ rng.nextInt(possPops.size()) ]
int crossoverProb = 100//random.nextInt(101)
//int mutateProb = 90//random.nextInt(31) + 70


class MaxRandom extends Random {
    public long seedUniquifierGetter(){
        return seedUniquifier();
    }
}

int itercounter = 1;

int noOfLoops = 2
int clientLoops = 100
long startrunTime = System.currentTimeSeconds()
File log = new File("PushItToTheLimitSlightChange.csv");
log.createNewFile()

List ClientList = [1,2,3, 4, 5, 6, 7, 8, 12, 24, 48]
int totalPop = 240
int mutateProb = 90

for (int l = 1; l < noOfLoops; l++) {
    Collections.shuffle(ClientList)
    //mutateProb = random.nextInt(91) + 10
    for (int i = 0; i < ClientList.size(); i++) {
        clientLoops.times {
            MaxRandom rng = new MaxRandom()
            long seed = rng.seedUniquifierGetter() * System.nanoTime()
            rng.setSeed(seed)
            System.gc()
            int j = random.nextInt(ClientList.size())
            clients = ClientList[i]
            initialPopulation = (int) (totalPop / clients)
            RunEA EA = new RunEA()
            EA.worker = new MaxOneIndividual()
            EA.manager = new MaxOneManager()
            EA.n = N
            EA.clients = clients
            EA.initialPopulation = initialPopulation
            EA.crossoverProb = crossoverProb //rng.nextInt(99)+1
            EA.mutateProb = mutateProb

            List runConfig = [clients, initialPopulation, mutateProb]
            FileWriter fr = new FileWriter("PushItToTheLimitSlightChange.csv", true)
            println("loop number: " + ((int) it + 1))
            for (r in 0..runConfig.size() - 1)
                fr.write(runConfig[r] + ",")
            long startTime = System.currentTimeMillis()
            fr.write(startTime + ",")
            fr.close()
            EA.run()
            long endTime = System.currentTimeMillis()
            println("finished loop: " + ((int) it + 1))
            FileWriter frr = new FileWriter("PushItToTheLimitSlightChange.csv", true)
            frr.write(endTime + "\n")
            frr.close()
            println "Iter at " + itercounter
            itercounter++
        }
    }

}

long endrunTime = System.currentTimeSeconds()
println endrunTime - startrunTime

class MaxOneManager extends Manager {

    @Override
    int finalise(List d) {
        writeFile()
        return completedOK
    }

    static void writeFile() {
        FileWriter fr = new FileWriter("PushItToTheLimitSlightChange.csv", true)
        fr.write("$requestedParents,$improvements,")
        fr.close()
    }

    @Override
    int addIndividuals(List<Worker> individuals) { //add the new individuals to the population
        if (population.size()+individuals.size() >= 238) {
            FileWriter fr = new FileWriter("PushItToTheLimitSlightChange.csv", true)
            fr.write(System.currentTimeMillis() + ",")
            fr.close()
        }
        return super.addIndividuals(individuals)
    }
}
