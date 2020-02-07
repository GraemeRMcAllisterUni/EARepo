package gppDemos.Soduko


import gppDemos.EAClasses.Worker


class SodukoClient extends Worker {

    List board = [ 0,0,6,0,0,0,0,0,0,
    0,8,0,0,5,4,2,0,0,
    0,4,0,0,9,0,0,7,0,
    0,0,7,9,0,0,3,0,0,
    0,0,0,0,8,0,4,0,0,
    6,0,0,0,0,0,1,0,0,
    2,0,3,0,6,7,9,8,1,
    0,0,0,5,0,0,0,4,0,
    4,7,8,3,1,9,5,6,2]

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
