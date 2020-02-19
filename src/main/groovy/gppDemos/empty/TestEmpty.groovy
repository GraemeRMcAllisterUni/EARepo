package gppDemos.empty

import gppDemos.EAClasses.RunEA


RunEA EA = new RunEA();

EA.worker = new emptyWorker()

EA.initialPopulation = 4

EA.n = 4

EA.manager = new emptyManager()

EA.run()



