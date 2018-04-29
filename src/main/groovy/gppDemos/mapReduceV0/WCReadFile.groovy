package gppDemos.mapReduceV0

import gppLibrary.DataClassInterface as Constants

class WCReadFile extends gppLibrary.DataClass {

//	static String fileName = ""		// name of file containing source text
  static final int errorState = -1

  static final String initMRReadFile = "initMRReadFile"



  /**
   * endPunctuationList the list of punctuation symbols
   * that can be removed from the ends of words
   */
  def static endPunctuationList =[',','.',';',':','?','!', '\'', '"', '_', '}', ')']

  /**
   * startPunctuationList the list of punctuation symbols
   * that can be removed from the start of words
   */
  def static startPunctuationList = ['\'' ,'"', '_', '\t', '{', '(']

  /**
   * processLine takes a line of text
   * which is split into words using tokenize(' ')
   *
   * @param line a line that has been read from a file
   * @return list of words containing each word in the line
   */
  def static processLine (line){
    def words = []
    words = line.tokenize(' ')
    return words
  }

  /**
   *
   * removePunctuation removes any punctuation characters
   * from the start and end of a word
   * @param w String containing the word to be processed
   * @return String rw containing the input word less any punctuation symbols
   *
   */
  def static removePunctuation(w) {
    def ew = w
    def rw
    def len = w.size()
    if ( len == 1 )
      rw = w
    else {
      def lastCh = w.substring(len-1, len)
      while (endPunctuationList.contains(lastCh)){
        ew = w.substring(0, len-1)
        len = len - 1
        lastCh = ew.substring(len-1, len)
      }
      def firstCh = ew.substring(0, 1)
      if ( startPunctuationList.contains(firstCh) ) {
        rw = w.substring(1, len)
      }
      else {
        rw = ew
      }
    }
    return rw
  }


  int lines = -1
  List <WCLine> lineBuffer = []

  /* (non-Javadoc)
   * @see jcsp.gppLibrary.DataClass#initClass(java.lang.Object)
   */
  public int initMRReadFile( List d) {
    if ( d == null ) return -2
    String fileName = d[0]
    def fileHandle = new File (fileName)
    def fileReader = new FileReader(fileHandle)
    fileReader.eachLine { l ->
      def words = processLine(l)
      def line = []
      for ( w in words) {
        line << removePunctuation(w)
      }
      if (line != []){
        lineBuffer << line
        lines = lines + 1
      }
    }
    println "EmitWL has processed $fileName with $lines lines"
    return completedOK
  }

  static int currentLine = -1

  def getNextLine() {
    if ( currentLine == lines)
      return [Constants.normalTermination, null]
    else {
      currentLine = currentLine + 1
      return [Constants.normalContinuation, lineBuffer[currentLine]]
    }
  }
}
