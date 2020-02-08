package gppDemos.empty


import gppDemos.EAClasses.RunEA
import gppDemos.TSP.TSPWorker

int mapSize = 25


int N = 10



RunEA EA = new RunEA()

TSPWorker builder = new TSPWorker()

EA.worker = builder

EA.n = 1

EA.clients = 1

EA.initialPopulation = 2

def world = builder.buildWorld(N, 400)

println(world)


EA.run()

