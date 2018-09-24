package gppDemos.concordance

//usage runDemo concordance/SeqConcordance resultFile workers title N

String title = "bible"
int N = 8
int minSeqLen = 2
boolean doFileOutput = false

if (args.size() > 0){
    title = args[0]
    N = Integer.parseInt(args[1])
}


// eclipse versions
def fileName = "./${title}.txt"
def outFileName = "./${title}seq"

//usage runDemo concordance/SeqConcordance resultsFile

print "Seq $doFileOutput, $N, $minSeqLen, "
System.gc()
def startime = System.currentTimeMillis()

def cd = new ConcordanceData()
cd.initClass([N, fileName, outFileName])
for ( n in 1 .. N){
  cd.createInstance(null)
  cd.createIntValueList(null)
  cd.createValueIndicesMap(null)
  cd.createWordMap(null)
  def cr = new ConcordanceResults(minSeqLen: 2)
  cr.initClass([minSeqLen, doFileOutput])
  cr.collector(cd)
  cr.finalise(null)
}


def endtime = System.currentTimeMillis()
println " ${endtime - startime} "


