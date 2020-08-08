package gppDemos.FitnessDetector

import gppDemos.EAClasses.Manager
import gppDemos.EAClasses.RunEA
import gppDemos.EAClasses.Worker
import gppDemos.maxOneProblem.MaxOneIndividual



class fitnessClient extends Worker{


    @Override
    int createFunction() {
        return 0
    }

    @Override
    double doFitness(List board) {


        String s = Character.toString((char)c);


        return
    }

    @Override
    boolean evolve(List parameters) {
        return false
    }
}



RunEA EA = new RunEA()




EA.N = 10

EA.clients = 1

EA.initialPopulation = 2

EA.run()