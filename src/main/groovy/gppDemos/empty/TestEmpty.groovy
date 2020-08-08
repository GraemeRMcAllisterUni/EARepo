package gppDemos.empty

import gppDemos.EAClasses.RunEA

RunEA EA = new RunEA();
EA.worker = new emptyWorker()
EA.manager = new emptyManager()

EA.initialPopulation = 2
EA.clients = 2

EA.run()
