package gppDemos.TSP


import gppDemos.EAClasses.RunEA
import gppDemos.EAClasses.Manager
import gppDemos.EAClasses.Worker
import gppDemos.TSP.TSPWorker

int mapSize = 25



RunEA EA = new RunEA()



EA.worker = new TSPWorker()

EA.N = 10

EA.clients = 1

EA.initialPopulation = 2

//builder.cityList = [1: [342, 228], 2: [74, 386], 3: [142, 261], 4: [337, 394], 5: [211, 66], 6: [292, 242], 7: [290, 256], 8: [387, 212], 9: [272, 377], 10: [429, 179]]

EA.run()

