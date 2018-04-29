package gppDemos.mapReduceV0

class WCWorker extends gppLibrary.DataClass {
  Map WCmap = [:]
  static String init = "nullInitialise"
  static String inFunction = "inFunction"
  static String workFunction = "workFunction"
  static String outFunction = "outFunction"

  int nullInitialise (List d)	{
    return completedOK
  }

  int inFunction (List d, WCWordCount wc){
    def v = WCmap.get(wc.word)
    if ( v == null)
      WCmap.put(wc.word, wc.counter)
    else
    WCmap.put(wc.word, wc.counter + v)
    return completedOK
  }

  Set sortedMapKeys = []

  int workFunction(){
    sortedMapKeys = WCmap.keySet().sort()
    return completedOK
  }

  int currentIndex = 0

  WCWordCount outFunction(){
    if (currentIndex < sortedMapKeys.size()){
      String key = sortedMapKeys[currentIndex]
      int val = WCmap.get(key)
      currentIndex = currentIndex + 1
      return new WCWordCount( word: key, counter: val)
    }
    else
      return null
  }

}
