package gppDemos.empty


import gppDemos.EAClasses.Manager
import gppDemos.EAClasses.Worker
import gppDemos.UniversalResponse



class emptyManager extends Manager{

    int counter = 1
    int iterations = 9

    int initialise (List d) {
        println "Creating Manager Class Object"
        return completedOK
    }

    String tabber(){
        String s = ""
        for(int i = 0; i<counter;i++){
            s += "\t"
        }
        return s
    }

    UniversalResponse selectParents(int parents) {
        println tabber() + "Selecting Parents(Randomly, from local list of workers)"
        def response = new UniversalResponse()
        return response
    }

    int addChildren(List <Worker> children) {
        println tabber() + "Adding evolved children from workers"
        determineBestWorst()
        return completedOK
    }

    void determineBestWorst(){
        println tabber() + "Saving array location of Best and Worst Worker"
    }

    int addIndividuals(List <Worker> individuals) { //add the new individuals to the population
        println tabber() +  "Adding new children from workers"
        determineBestWorst()
        return completedOK
    }

    boolean carryOn() { // returns true if the server should continue
        counter++
        println tabber() + "Checking if we should carry on. Counter = $counter"
        return(counter<=iterations)
    }

    int finalise(List d) {
        println "EA terminated - Manager finalising and formatting solution"
        return completedOK
    }
}