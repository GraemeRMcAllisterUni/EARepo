package gppDemos.empty


import gppDemos.empty.RunEA
import gppDemos.maxOneProblem.MaxOneIndividual
import gppDemos.maxOneProblem.MaxOneServer
import gppDemos.workingToAbstraction.Manager
import gppDemos.workingToAbstraction.Worker

int clients = 1
int N = 2
int initialPopulation = 4

int crossoverProb = 95
int mutateProb = 5
int requiredParents = 2
int resultantChildren = 2
float editProportion = 0.1F

class MaxOneManager extends Manager{

    static int bitsPerGene = 16

    @Override
    void editPopulation(){
        int populationSize = population.size()
        int editNumber = (int)(populationSize * editProportion)
        for ( c in 1 .. editNumber) {
            int id = rng.nextInt(populationSize)
//            print "$c = $id: ${population[id]} ->"
            int m1 = rng.nextInt(bitsPerGene) + 1
            ((MaxOneIndividual)population[id]).board.flip(m1)
            ((MaxOneIndividual)population[id]).fitness = ((MaxOneIndividual)population[id]).doFitness(((MaxOneIndividual)population[id]).board)
//            println "$m1, $m2, ${population[id]}"
        }
        determineBestWorst()
        modifications += 1
    }

   int finalise(List d) {
        println "Best ${population[bestLocation].board}\nFitness: ${population[bestLocation].fitness}"
        println "$requestedParents parents requested; creating $improvements improvements"
        print "$requestedParents, $improvements, $modifications"
        return completedOK
    }
}


RunEA EA = new RunEA()

EA.worker = new MaxOneIndividual()

EA.manager = new MaxOneServer()

EA.N = 100

EA.clients = 5

EA.run()

