package gppDemos

import gppBuilder.*


def build = new GPPbuilder()
String rootPath = "./"  // as required for use in Intellij
// evolutionary
build.runBuilder(rootPath + "nQueensProblem\\TestQueens")
build.runBuilder(rootPath + "maxOneProblem\\TestMaxOne")
//build.runBuilder(rootPath + "nQueensProblem\\TestQueensOverwrite")
//build.runBuilder(rootPath + "nQueensProblem\\TestQueensTournament")



