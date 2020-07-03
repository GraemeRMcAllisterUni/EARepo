package gppDemos.empty


import gppDemos.EAClasses.RunEA
import gppDemos.maxOneProblem.MaxOneIndividual
import gppDemos.maxOneProblem.MaxOneServer
import gppDemos.EAClasses.Manager
import gppDemos.EAClasses.Worker



List ClientList=    [48, 24, 16, 12, 8,  6,  4, 3,  2,  1]

int[] possPops=     [1, 2,  3,  4,  6,  8,  12, 16, 24, 48]

//Collections.shuffle(ClientList)

int N = 1000 //rng.nextInt(5000)+1
int clients = 3
int initialPopulation = 2 //possPops[ rng.nextInt(possPops.size()) ]
int crossoverProb = 95 //rng.nextInt(99)+1

int loops = 200

long seed = 1234567


Random rng = new Random()
rng.setSeed(seed)

File log = new File("running.csv");
log.createNewFile()

for(int i = 0; i<ClientList.size(); i++) {
    seed = 1234567


    clients = (int)ClientList.get(i)
    initialPopulation = possPops[i]

    loops.times {
        RunEA EA = new RunEA()

        EA.worker = new MaxOneIndividual()

        EA.manager = new MaxOneManager()

        EA.N = N //rng.nextInt(5000)+1
        EA.clients = clients
        EA.initialPopulation = initialPopulation
        EA.crossoverProb = crossoverProb //rng.nextInt(99)+1
        EA.seed = seed

        List runConfig = [seed, N, clients, initialPopulation, crossoverProb]
        FileWriter fr = new FileWriter("running.csv", true)
        println("loop number: " + it)
        for (r in 0..runConfig.size() - 1)
            fr.write(runConfig[r] + ",")
        long startTime = System.currentTimeMillis()
        fr.write(startTime + ",")
        fr.close()
        EA.run()
        long endTime = System.currentTimeMillis()
        FileWriter frr = new FileWriter("running.csv", true)
        frr.write(endTime + "\n")
        frr.close()
        println("finished loop: " + it)
        EA = null
        seed = System.currentTimeMillis()
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
        FileWriter fr = new FileWriter("running.csv", true)
        fr.write("$requestedParents,$improvements,")
        fr.close()
    }

    void determineBestWorst(){
        bestFitness = population[0].fitness
        worstFitness = population[0].fitness
        for (p in 0..< population.size()) {
            if (population[p].fitness < bestFitness) {
                // update max fitness data
                bestFitness = population[p].fitness
                bestLocation = p
            }
            if (population[p].fitness > worstFitness) {
                // update min fitness data
                worstFitness = population[p].fitness
                worstLocation = p
            }

        }
    }

    int addIndividuals(List <Worker> individuals) { //add the new individuals to the population

        for ( i in 0 ..< individuals.size()) {
            population.add(individuals[i])
        }
        if(population.size()>=48) {
            FileWriter fr = new FileWriter("running.csv", true)
            fr.write(System.currentTimeMillis() + ",")
            fr.close()
        }
        determineBestWorst()

        return completedOK
    }

}
