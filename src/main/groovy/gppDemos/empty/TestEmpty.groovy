package gppDemos.empty

import gppDemos.ParallelClientServerEngine
import gppDemos.empty.RunEA
import gppDemos.empty.emptyManager as S
import gppDemos.empty.emptyWorker as C
import gppDemos.nQueensProblem.QueensClient
import gppLibrary.GroupDetails
import gppLibrary.LocalDetails


int clients = 1
int N = 2
int initialPopulation = 4

int crossoverProb = 95
int mutateProb = 5
int requiredParents = 2
int resultantChildren = 2
float editProportion = 0.1F



RunEA EA = new RunEA()

EA.W = new QueensClient()

EA.N = 100

EA.clients = 5

EA.run()

