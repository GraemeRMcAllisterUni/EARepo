package gppDemos

import gppBuilder.*


def build = new GPPbuilder()
String rootPath = "D:\\IJGradle\\gppDemos\\src\\main\\groovy\\gppDemos\\"
// concordance
build.runBuilder(rootPath + "concordance\\RunCollectConcordance")
build.runBuilder(rootPath + "concordance\\RunConcordancePoG")
build.runBuilder(rootPath + "concordance\\RunExtendedConcordance")
build.runBuilder(rootPath + "concordance\\RunGoPConcordance")
build.runBuilder(rootPath + "concordance\\RunGoPConcordanceLog")
build.runBuilder(rootPath + "concordance\\RunGroupCollectConcordance")
build.runBuilder(rootPath + "concordance\\RunSkeletonConcordance")
build.runBuilder(rootPath + "concordance\\RunManyCollectConcordance")
build.runBuilder(rootPath + "concordance\\RunSimpleTest")
build.runBuilder(rootPath + "concordance\\RunExtendedTest")
// goldbach
build.runBuilder(rootPath + "goldbach\\scripts\\RunParGoldbach")
build.runBuilder(rootPath + "goldbach\\scripts\\RunMultiPrimesParGoldbach")
build.runBuilder(rootPath + "goldbach\\scripts\\RunCombiningPrimes")
build.runBuilder(rootPath + "goldbach\\scripts\\RunPrimes")
build.runBuilder(rootPath + "goldbach\\scripts\\RunSeqGoldbach")
build.runBuilder(rootPath + "goldbach\\scripts\\RunSimplePrimes")
// Jacobi
build.runBuilder(rootPath + "jacobi\\RunJacobiMC")
//mandelbrot
build.runBuilder(rootPath + "mandelbrot\\scripts\\RunMandelbrot")
build.runBuilder(rootPath + "mandelbrot\\scripts\\RunMandelbrotLine")
build.runBuilder(rootPath + "mandelbrot\\scripts\\RunMandelbrotNoGui")
build.runBuilder(rootPath + "mandelbrot\\scripts\\RunMandelbrotLineNoGui")
//nbody
build.runBuilder(rootPath + "nbody\\ParNbody")
build.runBuilder(rootPath + "solarSystem\\RunPlanets")
//image processing
build.runBuilder(rootPath + "imageProcessing\\RunGSImage")
build.runBuilder(rootPath + "imageProcessing\\RunRGBImage")
// quick sort
build.runBuilder(rootPath + "QuickSort\\RunGroupQuickSort")
//mcpi
build.runBuilder(rootPath + "MCpi\\RunSkelMCpi")
build.runBuilder(rootPath + "MCpi\\RunMCpiDPCPattern")
build.runBuilder(rootPath + "MCpi\\RunMCpiWorkerDPCPattern")
// evolutionary
//build.runBuilder(rootPath + "nQueensProblem\\TestQueens")
build.runBuilder(rootPath + "maxOneProblem\\TestMaxOne")
//build.runBuilder(rootPath + "nQueensProblem\\TestQueensOverwrite")
//build.runBuilder(rootPath + "nQueensProblem\\TestQueensTournament")



