package gppDemos.concordance

//usage runDemo concordance SeqExpandedConcordance resultFile  title N

String title
int N
int minSeqLen = 2
boolean doFileOutput = false
String workingDirectory = System.getProperty('user.dir')
String fileName
String outFileName

if (args.size() == 0){
  // assumed to be running form within Intellij
  title = "bible"
  N = 8
  fileName = "./${title}.txt"
  outFileName = "./${title}ExtSeq"
}
else {
  // assumed to be running via runDemo
  String folder = args[0]
  title = args[1]
  fileName = workingDirectory + "/src/main/groovy/gppDemos/${folder}/${title}.txt"
  outFileName = workingDirectory + "/src/main/groovy/gppDemos/${folder}/${title}ExtSeq"
  N = Integer.parseInt(args[2])
}

print "SeqExpanded $doFileOutput, $N, $minSeqLen, "
System.gc()
def startime = System.currentTimeMillis()

def cw = new ConcordanceWords()
def cc = new ConcordanceCombine()
def cd = new ConcordanceData()
int retCode
retCode = cc.initClass([N, outFileName])
retCode = cw.initClass([fileName, 15])
retCode = cw.createInstance(null)
while (retCode == dc.normalContinuation) {
  retCode = cw.createWordsAndIntValues ()
  retCode = cc.appendBuffers(cw)
  cw = new ConcordanceWords()
  retCode = cw.createInstance(null)
}
retCode = cd.finalise([cc])
for ( n in 1 .. N){
  cd.createInstance(null)
  cd.createIntValueList(null)
  cd.createValueIndicesMap(null)
  cd.createWordMap()
  def cr = new ConcordanceResults(minSeqLen: 2)
  cr.initClass([minSeqLen, doFileOutput])
  cr.collector(cd)
  cr.finalise(null)
}


def endtime = System.currentTimeMillis()
println "${endtime - startime} "

