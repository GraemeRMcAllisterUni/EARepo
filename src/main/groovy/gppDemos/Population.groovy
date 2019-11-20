package gppDemos

class Population {
    def pop = [:]
    List<Chromosome> chromosomes

    void Fitness(Chromosome c, double f){
        pop = [c:f]
    }

    Chromosome getChromosome(int i){
        return chromosomes[i]
    }
}

class Chromosome {
    List<Gene> genes

    Chromosome(){
        this.genes = new ArrayList<Gene>()
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

}

class Gene {
    int gene

    Gene(int i){
        this.gene = i
    }

    void setGene(int g){
        this.gene = g
    }

    Gene getGene(){
        return gene
    }
}