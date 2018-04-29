package gppDemos.mapReduceV0

class WCWordCount extends gppLibrary.DataClass {
  String word
  int counter
  static String createWCClass = "createClass"
  static String reduceFunction = "reduceFunction"
  static String indexFunction = "indexingFunction"

  static int instance = 0

  int logInstance
  public createClass (String s, int c) {
    word = s
    counter = c
    logInstance = instance
    instance += 1
  }

  String toString(){
    String s = "WC = [$word, $counter]"
  }

  List reduceFunction ( List <WCWordCount> buffers, Class outClass){
    // this is only called when it is known that  there is valid data
    // in at least one of the buffers elements
    List returnValues = []
    // we need to find the smallest word in any of the buffers
    // and also if the same word occurs in many of the buffers
    // assume first non-null buffer is the least and then look for equal
    // words and update the list of buffer indexes until one is found with
    // a lesser word and then restart
    int bSize = buffers.size()
    int i = 0
    List minBuffIndices = []
    // skip over empty buffers; we know at least one will be full
    while ((i < bSize) && (buffers[i] == null)) i = i + 1
    String minWord = "zzzzzzzz" // greater than all valid words
    while (i < bSize){
      if (buffers[i] == null) i = i + 1
      else {
        String test = buffers[i].word
        if ( test < minWord){
          // found a new minWord
          minBuffIndices = []
          minBuffIndices << i
          minWord = test
        } else if (test == minWord)
          // found an equal minWord
          minBuffIndices << i
        i = i + 1
      } // end else
    } // end while
    // now sum all the counts for the equal buffers
    int sum = 0
    minBuffIndices.each{ b ->
      sum = sum + buffers[b].counter
    }
    WCWordCount result = new WCWordCount( word: minWord,
                        counter: sum )
    return [normalContinuation, minBuffIndices, result]
  } // reduceFunction

  int indexingFunction(String key){
	  //println "IF: $key"
    String firstLetter = key.charAt(0)
    switch (firstLetter){
      case ['a'..'h', 'A'..'H']: return 0
      case ['i'..'p', 'I'..'P']: return 1
      default: return 2
    }
  }

}
