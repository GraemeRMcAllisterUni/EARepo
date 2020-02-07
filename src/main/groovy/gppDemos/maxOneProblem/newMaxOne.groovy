package gppDemos.empty


import gppDemos.EAClasses.RunEA
import gppDemos.maxOneProblem.MaxOneIndividual
import gppDemos.maxOneProblem.MaxOneServer
import gppDemos.EAClasses.Manager
import gppDemos.EAClasses.Worker


class MaxOneManager extends Manager{

    static int bitsPerGene = 16


    int initialise (List d) {
        bitsPerGene = (int)d[0]
        editProportion = (float)d[1]
        rng.setSeed(System.currentTimeMillis())
        return completedOK
    }

    void editPopulation(){
        int populationSize = population.size()
        int editNumber = (int)(populationSize * editProportion)
        for ( c in 1 .. editNumber) {
            int id = rng.nextInt(populationSize)
//            print "$c = $id: ${population[id]} ->"
            int m1 = rng.nextInt(bitsPerGene) + 1
            ((MaxOneIndividual)population[id]).board.flip(m1)
            ((MaxOneIndividual)population[id]).fitness = ((MaxOneIndividual)population[id]).doFitness(((MaxOneIndividual)population[id]).board)
            println "$m1, ${population[id]}"
        }
        determineBestWorst()

    }

    int finalise(List d) {
        println "Best ${((MaxOneIndividual)population[bestLocation]).board}\nFitness: ${population[bestLocation].fitness}"
        println "$requestedParents parents requested; creating $improvements improvements"
        print "$requestedParents, $improvements"
        return completedOK
    }
}


RunEA EA = new RunEA()

EA.worker = new MaxOneIndividual()

EA.manager = new MaxOneManager()


EA.N = 100

EA.clients = 5

EA.run()

