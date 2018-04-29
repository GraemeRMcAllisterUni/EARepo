package gppDemos.mapReduceV0

class WCResult extends gppLibrary.DataClass {

  def results = []
  int wordCount = 0
  static String init = "initClass"
  static String collector = "collector"
  static String finalise = "finalise"

  int initClass ( List d){
    results << d[0]
    return completedOK
  }

  int collector ( WCWordCount d) {
    //results << [d.word, d.counter]
    wordCount +=1
    return completedOK
  }
  int finalise ( List d) {
    results << d[0] + "$wordCount"
    results.each {println "$it"}
    return completedOK
  }
}
