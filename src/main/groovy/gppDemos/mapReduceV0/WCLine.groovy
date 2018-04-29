package gppDemos.mapReduceV0

import gppLibrary.DataClassInterface as Constants

class WCLine extends gppLibrary.DataClass {
  def line = []	// a list of words from one line of the text file
  static final String initMRLine = "initMRLine"
  static final String createMRLine = "createMRLine"
  static final String MRMapFunction ="MRMapFunction"

  int initMRLine (List d){
    return Constants.completedOK
  }
  static int instance = 0
  int logInstance
  int createMRLine (List d) {
    WCReadFile localClass = d[0]
    def returnValues = localClass.getNextLine()
    if (returnValues[1] != null){
      returnValues[1].each{w ->
        line << w
      }
    }
    logInstance = instance
    instance += 1
    return returnValues[0]  // return code
  }

  Map MRMapFunction (){
    Map lineMap = [:]
    int v
    line.each{ word ->
      v = lineMap.get(word)
      if (v == null)
        lineMap.put(word, 1)
      else
        lineMap.put(word, v+1)
    }
//	println "MRMap: $lineMap"
    return lineMap
  }

  String toString() {
    String s = "MR Line: "
    line.each{w->
      s = s + w + ", "
    }
    return s
  }
}
