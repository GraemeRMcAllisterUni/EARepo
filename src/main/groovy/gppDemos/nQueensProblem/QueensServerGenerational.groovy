package gppDemos.nQueensProblem

import gppLibrary.DataClass
import gppLibrary.UniversalResponse
import groovy.transform.CompileStatic

@CompileStatic
class QueensServerGenerational extends DataClass{
    List <QueensClient> population = []
    List <QueensClient> children = []
    Double requiredFitness = 0.0D
    Double worstFitness = 0.0D
    Double bestFitness = 1.0D
    int worstLocation, bestLocation
    long seed = 0L
    static Random rng = new Random()

    static int requestedParents = 0
    static int improvements = 0
    static int N = 0
    static float editProportion = 0.33F
    static int populationCount = 0
    static int childrenCount = 0
    static int tournamentSize = 2

//    static String selectParentsFunction = "selectParents"
    static String selectParentsFunction = "selectParentsByTournament"
//    static String incorporateChildrenMethod = "addChildren"
//    static String incorporateChildrenMethod = "addChildrenToGeneration"
    static String incorporateChildrenMethod = "addAllButBestChildren"
    static String addIndividualsMethod = "addIndividuals"
    static String carryOnFunction = "carryOn"
    static String initMethod = "initialise"
    static String finaliseMethod = "finalise"

    int initialise (List d) {
        seed = d[0]
        N = d[1]
        editProportion = d[2]
        tournamentSize = d[3]
        rng.setSeed(seed)
        return completedOK
    }

    UniversalResponse selectParents(int parents) {
        requestedParents = requestedParents + parents // for analysis
        int populationSize = population.size()
        def response = new UniversalResponse()
        for ( i in 0 ..< parents) {
            int p = rng.nextInt(populationSize)
            response.payload[i] = population[p]
        }
        return response
    }
    
    UniversalResponse selectParentsByTournament(int parents){
        requestedParents = requestedParents + parents // for analysis
        int populationSize = population.size()
        def response = new UniversalResponse()
        double bestFit = 1.0D
        int bestLocn
        for ( i in 0 ..< parents) {
            for ( t in 1 .. tournamentSize) {
                int p = rng.nextInt(populationSize)  
                if (population[p].fitness < bestFit){
                    bestFit = population[p].fitness
                    bestLocn = p
                }
            }            
            response.payload[i] = population[bestLocn]
        }
        return response
    }

    int partition(List <QueensClient> m, int first, int last){
        double pivotValue = m[first].fitness
        int left = first+1
        int right = last
        boolean done = false
        while (!done){
            while ((left <= right) && (m[left].fitness <= pivotValue)) left = left + 1
            while ((m[right].fitness >= pivotValue) && (right >= left)) right = right - 1
            if (right < left)
                done = true
            else
                m.swap(left, right)
        }
        m.swap(first, right)
        return right
    }

    void quickSortRun(List <QueensClient> b, int first, int last){
        if (first < last) {
            int splitPoint = partition(b, first, last)
            quickSortRun(b, first, splitPoint-1)
            quickSortRun(b, splitPoint+1, last)
        }
    }

    int addChildrenToGeneration (List <QueensClient> evolvedChildren) {
        for ( c in 0 ..< evolvedChildren.size()) {
            childrenCount += 1
            children << evolvedChildren[c]
        }
        if (childrenCount < populationCount)
            return completedOK
        else {
            // now combine children into existing population
            improvements += 1
            quickSortRun( population, 0, populationCount - 1)
            quickSortRun( children, 0, childrenCount - 1)
            int exchange = (int)(populationCount * editProportion)  // defaults to one third
            int exchangeSize = exchange + rng.nextInt(exchange)    // between one Third to two Thirds
            int populationPointer = populationCount
            int childrenPointer = 0
            for (p in 1 .. exchangeSize) {// copy best children to population
                population[populationPointer - p] = children[childrenPointer]
                childrenPointer += 1
            }
            childrenCount = 0
            children = []
            determineBestWorst()
            return completedOK
        }
    }
    
    int addAllButBestChildren (List <QueensClient> evolvedChildren) {
        // keeps best parent and overwrites remainder with children
        for ( c in 0 ..< evolvedChildren.size()) {
            childrenCount += 1
            children << evolvedChildren[c]
        }
        if (childrenCount < populationCount)
            return completedOK
        else {
            // now combine children into existing population keeping only best parent
            improvements += 1
            print "copying $bestLocation"
            for ( i in 0 ..< bestLocation) population[i] = children[i]
            for ( i in bestLocation+1 ..< population.size()) population[i] = children[i]
            childrenCount = 0
            children = []
            determineBestWorst()
            if (bestFitness == worstFitness) println " and equality $bestFitness"
            else println " "  
            return completedOK
        }
    }

    int addChildren(List <QueensClient> children) {
        boolean childAdded = false
        for ( c in 0 ..< children.size()) {
            QueensClient child = children[c]
            // only add child if it is better than the worst child in the population
            if (child.fitness < worstFitness) {
                childAdded = true
                improvements = improvements + 1 // for analysis
                //                print "improvement $improvements with fit $child.fitness after $requestedParents parent requests"
                worstFitness = child.fitness
                population[worstLocation] = child
                // new child could be better than the current best
                if (child.fitness < bestFitness) {
                    bestFitness = child.fitness
                    bestLocation = worstLocation
                    //                    print " new best"
                    //                    println "$child.fitness after ${requestedParents/2} evolutions "
                }
                // now update minFitness
                worstFitness = bestFitness
                for ( p in 0 ..< population.size()) {
                    if (population[p].fitness > worstFitness) {
                        // found a new minimum fitness
                        worstFitness = population[p].fitness
                        worstLocation = p
                    }
                }
                //                println " $worstFitness, $bestFitness"
            } // end if
        } // end for loop
        if (childAdded) {
            if (bestFitness == worstFitness)
                editPopulation()
        }
        return completedOK
    }

    void editPopulation(){
        int populationSize = population.size()
        int editNumber = (int)(populationSize * editProportion)
        for ( c in 1 .. editNumber) {
            int id = rng.nextInt(populationSize)
            //            print "$c = $id: ${population[id]} ->"
            int m1 = rng.nextInt(N) + 1
            int m2 = rng.nextInt(N) + 1
            while ( m2 == m1) m2 = rng.nextInt(N) + 1
            ((QueensClient)population[id]).board.swap(m1, m2)
            ((QueensClient)population[id]).fitness = ((QueensClient)population[id]).doFitness(((QueensClient)population[id]).board)
            //            println "$m1, $m2, ${population[id]}"
        }
        determineBestWorst()
    }

    void determineBestWorst(){
        bestFitness = 1.0D
        worstFitness = 0.0D
        for ( p in 0..< population.size()) {
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

    int addIndividuals(List <QueensClient> individuals) {
        //add the new individuals to the population
        for ( i in 0 ..< individuals.size()) {
            population.add(individuals[i])
        }
        populationCount += individuals.size()
        determineBestWorst()
        //        population.each{println "$it"}
        //        println " "
        return completedOK
    }

    boolean carryOn() { // returns true if the server should continue
        if ( bestFitness != requiredFitness)
            return true
        else
            return false
    }

    int finalise(List d) {
        //        population.each{println "$it"}
        print "${requestedParents}, $improvements, $bestLocation, "
        //println "${population[bestLocation]}"
        return completedOK
    }

}
