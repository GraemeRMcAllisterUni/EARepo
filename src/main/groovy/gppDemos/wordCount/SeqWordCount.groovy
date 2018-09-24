package gppDemos.wordCount

import gppLibrary.DataClassInterface as Constants

String title = "ACMmr"
String fileName = "./${title}.txt"

def partitionData = new PartitionData()
partitionData.&"${partitionData.initClass}"([ 3, " " .. "f", "g" .. "n", "o" .. "~" ])

def mapData = new MapData()
mapData.&"${mapData.initClass}"()

def wordBuffer = new WordBuffer()
wordBuffer.initClass([fileName, 500])

int buffNo = 1
int result = wordBuffer.createInstance()
while (result == Constants.normalContinuation){
    //println "$buffNo: ${wordBuffer.buffer}"
    mapData.&"${mapData.mapFunction}"(wordBuffer)
//    println "${mapData.map}"
    partitionData.&"${partitionData.partitionFunction}"(mapData)
    result = wordBuffer.createInstance()
    mapData = new MapData()
    mapData.&"${mapData.initClass}"()
    buffNo += 1
}

for ( p in 0 .. 2){
    MapData mdata = partitionData.getPartition(p)
    println "$p: ${mdata.map}"
}

println "finished"
