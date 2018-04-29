package gppDemos.jacobi

//usage runDemo jacobi/JacobiSeq JacobiB56 title

import gppDemos.jacobi.JacobiDataMC as jd
import gppDemos.jacobi.JacobiResultMC as jr

def title = args[0]
//def title = "Jacobi"

def fileName = "src\\demos\\jacobi\\${title}.txt"

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


