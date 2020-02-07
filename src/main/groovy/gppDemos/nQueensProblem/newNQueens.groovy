import gppDemos.EAClasses.RunEA
import gppDemos.nQueensProblem.QueensClient


RunEA newEA = new RunEA()

newEA.worker = new QueensClient()


newEA.run()



