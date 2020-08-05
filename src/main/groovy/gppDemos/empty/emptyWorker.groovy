package gppDemos.empty
import gppDemos.EAClasses.Worker

class emptyWorker extends Worker {

    @Override
    int createFunction() {
        println "Worker - Creating function"
        return completedOK
    }

    @Override
    double doFitness(List board) {
        println "Worker - Doing fitness on board"
        return 1
    }

    @Override
    boolean evolve(List parameters) {
        println "Worker - Evolving from 2 parents(from Manager)"
        return true
    }
}


