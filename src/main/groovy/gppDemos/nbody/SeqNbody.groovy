package gppDemos.nbody

int N = Integer.parseInt(args[0])
//int N = 100
int iterations = 100
double dt = 1e11

String readPath = "./planets_list.txt"
String writePath = "./${N}_planets_Seq.txt"

System.gc()
print "SeqNbody, $N, "
long startTime = System.currentTimeMillis()

NbodyData planets = new NbodyData()


planets.&"${planets.initMethod}"([readPath, N, dt])
planets.&"${planets.createMethod}"(null)

// there is only 1 node
planets.&"${planets.partitionMethod}"(1)

// now simulate the iterations

for ( i in 0 ..< iterations){
    planets.&"${planets.calculationMethod}"(0)
    planets.&"${planets.updateMethod}"()
}

// now collect the results
NbodyResults results = new NbodyResults()

results.&"${results.init}"([writePath])
results.&"${results.collector}"(planets)
results.&"${results.finalise}"(null)

long endTime = System.currentTimeMillis()
println " ${endTime - startTime}"

