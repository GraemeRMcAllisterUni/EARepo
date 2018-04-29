package gppDemos.wordCount

import gppLibrary.DataClass
import gppLibrary.DataClassInterface as Constants

class ReduceData extends DataClass {
    MapData reducedMap

    static final String initClass = "initReduce"
    static final String reduceMethod = "reduceMethod"
    static final String getReduction = "getReduction"

    int initReduce(List d){
        reducedMap = new MapData()
        reducedMap.map = [:]
        return Constants.completedOK
    }

    int reduceMethod( MapData data) {
        int currentValue = 0
        data.map.each{ k, v ->
            currentValue = reducedMap.map.get(k, 0)
            reducedMap.putAt(k, v + currentValue)
        }
        return Constants.completedOK
    }

    MapData getReduction(){
        return reducedMap
    }
}
