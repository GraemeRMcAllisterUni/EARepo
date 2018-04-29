package gppDemos.wordCount

import gppLibrary.DataClass
import gppLibrary.DataClassInterface as Constants


class MapData extends DataClass {
    Map map = [:]
    static final String initClass = "initMap"
    static final String mapFunction = "mapMethod"

    int initMap( List d) {
        return Constants.completedOK
    }

    int mapMethod (WordBuffer b){
        int v
        b.buffer.each{word ->
            v = map.get(word, 0)
            map.put(word, v+1)
        }
        return Constants.completedOK
    }

    int combineMethod (MapData md){
        int v
        md.map.each{k, value ->
            v = map.get(k, 0)
            map.put(k, v + value)
        }
        return Constants.completedOK
    }

    String toString() {
        String s = "$map"
        return s
    }
}
