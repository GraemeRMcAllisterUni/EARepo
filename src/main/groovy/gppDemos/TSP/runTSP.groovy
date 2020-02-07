package gppDemos.empty


import gppDemos.EAClasses.RunEA
import gppDemos.TSP.TSPWorker
import gppDemos.maxOneProblem.MaxOneIndividual
import gppDemos.maxOneProblem.MaxOneServer
import gppDemos.EAClasses.Manager
import gppDemos.EAClasses.Worker



RunEA EA = new RunEA()

EA.worker = new TSPWorker()

EA.N = 4

EA.clients = 4

EA.run()

