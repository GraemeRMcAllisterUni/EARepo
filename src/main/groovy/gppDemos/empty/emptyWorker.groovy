package gppDemos.empty

import gppDemos.EAClasses.Worker
import gppLibrary.DataClass


//@CompileStatic
class emptyWorker extends Worker {

    @Override
    int createFunction() {
        return 0
    }

    @Override
    double doFitness(List<Integer> board) {
        return 0
    }

    @Override
    boolean evolve(List<Worker> parameters) {
        return false
    }
}

