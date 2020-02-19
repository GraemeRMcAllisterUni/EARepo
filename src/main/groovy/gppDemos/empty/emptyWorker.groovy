package gppDemos.empty

import gppDemos.EAClasses.Worker
import gppLibrary.DataClass


//@CompileStatic
class emptyWorker extends Worker {


    //int n = 0

    @Override
    int createFunction() {
        println "Worker - Creating solutions. Solutions per client = $n"
        return completedOK
    }

    @Override
    double doFitness(List board) {
        //println "Worker - Doing fitness on board(from Manager)"
        return 1
    }

    @Override
    boolean evolve(List parameters) {
        //println "Worker - Evolving from 2 parents(from Manager)"

        return true
    }
}


