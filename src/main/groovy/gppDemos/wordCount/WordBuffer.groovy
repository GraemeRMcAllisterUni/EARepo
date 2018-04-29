package gppDemos.wordCount

import gppLibrary.DataClass
import gppLibrary.DataClassInterface as Constants

class WordBuffer extends DataClass {
  def buffer = null
  String fileName = null
  static int maxBufferSize = -1
  def static fileHandle = null
  def static fileReader = null

  static final String init = "initClass"
  static final String create = "createInstance"

  public int initClass( List d) {
      fileName = d[0]
      maxBufferSize = d[1]
      fileHandle = new File (fileName)
      fileReader = new FileReader(fileHandle)
      return Constants.completedOK
  }

  public int createInstance (List d) {
    if (createBuffer(fileReader, maxBufferSize) == null)
      return Constants.normalTermination
    else
      return Constants.normalContinuation
  }

  /**
   * Reads the file a line at a time using the fileReader and returns
   * at least maxWords in buffer, except for the last buffer.  Once the end of file
   * is reached the method returns a null value.  Words in the buffer may
   * include punctuation.
   *
   * @param fileReader The file fileName's associated fileReader
   * @param maxWords The minimum number of words in a returned buffer
   * @return a buffer of words or null when the end of file is reached
   */
  boolean finishing = false

  def createBuffer (FileReader fileReader, int maxWords){
    buffer = []
    def line
    def wordCount = 0
    boolean notFull = true
    line = fileReader.readLine()
    while ( notFull && (! finishing) ) {
        if ( line != null){
             def words = processLine(line)
             words.each{ word ->
                 buffer << removePunctuation(word).toLowerCase()
             }
             if (buffer.size() >= maxWords) notFull = false
             else line = fileReader.readLine()
        }
        else {
            finishing = true
        }
    }
//    println "CB: ${buffer.size()} = ${buffer}"
    if (finishing && (buffer.size() == 0)) return null
    else return buffer
  }

  /**
   * processLine takes a line of text
   * which is split into words using tokenize(' ')
   *
   * @param line a line that has been read from a file
   * @return list of words containing each word in the line
   */
  def processLine (line){
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
  def removePunctuation(w) {
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

  String toString() {
      String s = "$buffer"
  }
}
