package gppDemos.jacobi

//usage runDemo jacobi/JacobiSeq JacobiB56 title

import gppDemos.jacobi.JacobiDataMC as jd
import gppDemos.jacobi.JacobiResultMC as jr

String title = ""

//usage runDemo jacobi/RunJacobiMC JacobiB56 title

if (args.size() == 0){
    title = "Jacobi1024"
}
else {
    nodes = Integer.parseInt(args[1])
}


def fileName = ".\\${title}.txt"

double margin = 1.0E-16

System.gc()
print "SeqJacobi $title, "
long startTime = System.currentTimeMillis()

def data = new jd()
data.&"init"([fileName])
data.&"create"(null)
data.&"partition"(1)
data.&"doCalculation"(0)
while (data.&"repeat"(margin)){
    data.&"update"()
    data.&"doCalculation"(0)
}
def result = new jr()
result.&"initClass"(null)
result.&"collector"(data)
result.&"finalise"(null)

long endTime = System.currentTimeMillis()
println " ${endTime - startTime}"


