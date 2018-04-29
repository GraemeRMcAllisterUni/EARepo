package gppDemos.concordance

import gppLibrary.DataClass as dc

def title = "bible"
int N = 8
int minSeqLen = 2
boolean doFileOutput = false

// eclipse versions
def fileName = "src\\demos\\concordance\\${title}.txt"
def outFileName = "src\\demos\\concordance\\${title}Xseq"

//usage runDemo concordance/SeqExpandedConcordance

print "SeqExpanded $doFileOutput, $N, $minSeqLen, "
System.gc()
def startime = System.currentTimeMillis()

def cw = new ConcordanceWords()
def cc = new ConcordanceCombine()
def cd = new ConcordanceData()
int retCode = -1
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

