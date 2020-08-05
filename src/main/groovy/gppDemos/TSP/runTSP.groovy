package gppDemos.TSP
import gppDemos.EAClasses.RunEA
import gppDemos.EAClasses.Manager

RunEA EA = new RunEA()
EA.manager = new TSPManager()
EA.worker = new TSPWorker()
EA.n = 10
EA.clients = 11
EA.initialPopulation = 4
EA.mutateProb = 90
EA.resultantChildren = 2

EA.run()

class TSPManager extends Manager{

    TSPManager() {
        super()
        requiredFitness = 6656.0D
    }

    @Override
    boolean carryOn() {
        return super.carryOn() && hardCarryOn()
    }
}