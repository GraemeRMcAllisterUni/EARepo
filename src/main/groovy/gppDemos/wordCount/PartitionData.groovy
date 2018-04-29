package gppDemos.wordCount

import gppLibrary.DataClass
import gppLibrary.DataClassInterface as Constants

class PartitionData extends DataClass {
    List partitionMaps = []
    List partitionRanges = []
    int partitions

    static final String initClass = "initPartition"
    static final String partitionFunction = "partitionMethod"
    static final String getPartition = "getPartition"

    int initPartition( List d) {
       partitions = d[0]
       for ( p in 1..partitions)
            partitionRanges << d[p]
        for ( p in 0 ..< partitions){
            partitionMaps << new MapData(map: [:])
        }
        return Constants.completedOK
    }

    int partitionMethod (MapData inMap) {
        for ( m in inMap.map) {
            String k = m.key
            int v = m.value
            String firstCh = k.substring(0, 1)
            int range = 0
            while ( !(partitionRanges[range].contains(firstCh))) range += 1
            // assume that the first letter of a word is in one of the ranges!!
            int value = partitionMaps[range].map.get(k, 0)
            partitionMaps[range].map.put(k, value + v)
//            println "PM: $range, $k, $v, ${partitionMaps[range].map.get(k)}"
        }
        return Constants.completedOK
    }

    MapData getPartition (int r){
        return partitionMaps[r]
    }

}
