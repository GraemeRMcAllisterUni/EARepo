package gppDemos

class Population {
    def pop = [:]
    List<Chromosome> chromosomes

    void Fitness(Chromosome chro, double f){
        pop.put(chro, f)
    }

    double getFitness(Chromosome chro){
        return pop.chro
    }

    void addChromosome(Chromosome c) {
        this.chromosomes.add(c)
    }

    Chromosome getChromosome(int i){
        return chromosomes[i]
    }

    void replaceChromosome(int i, Chromosome child){
        chromosomes[i] = child
    }

    int Count(){
       return chromosomes.size()

    }

}

class Chromosome {
    List<Gene> genes
    double fitness


    Chromosome(){
        this.genes = new ArrayList<Gene>()
    }

    Chromosome(Chromosome c ){
        genes = c.genes
    }

    void remove(int i){
        genes.remove(i)
    }

    Gene getGene(int i )
    {
        return genes[i]
    }

    void setGene(int i)
    {
        Gene gene = new Gene(i)
        genes.add(gene)
    }

    String toString(){
        return getGenes().toString()
    }

}

class Gene {
    int gene

    Gene(int i){
        gene = i
    }

    void setGene(int g){
        this.gene = g
    }

    Gene getGene(){
        return gene
    }
}