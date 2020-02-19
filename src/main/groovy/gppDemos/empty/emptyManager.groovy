package gppDemos.empty


import gppDemos.EAClasses.Manager
import gppDemos.EAClasses.Worker
import gppDemos.UniversalResponse



class emptyManager extends Manager{

    int counter = 1
    int iterations = 10

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
        //requestedParents = requeiredarents + parents // for analysis
        println tabber() + "Manager - Selecting Parents(Randomly, from local list of workers). Requested Parents = $requestedParents"
        def response = new UniversalResponse()
        return response
    }

    int addChildren(List <Worker> children) {
        println tabber() + "Manager - adding evolved children from workers"
        editPopulation()
        return completedOK
    }


    void editPopulation(){

        println tabber() +  "Manager - Shuffling board"
        determineBestWorst()
    }


    void determineBestWorst(){
        println tabber() + "Manager - Saving array location of Best and Worst Worker"
    }


    int addIndividuals(List <Worker> individuals) { //add the new individuals to the population

        println tabber() +  "Manager - adding new clidren from workers"
        determineBestWorst()
        return completedOK
    }



    boolean carryOn() { // returns true if the server should continue
        counter++
        println tabber() + "Manager - Checking if we should carry on. Counter = $counter"
        return(counter<=iterations)

    }

    int finalise(List d) {
        println "EA terminated - Manager finalising and formatting solution"
        return completedOK
    }
}
