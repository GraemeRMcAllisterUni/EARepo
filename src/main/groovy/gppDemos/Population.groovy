package gppDemos

import gppLibrary.DataClassInterface

class Population implements Serializable{
    def pop = [:]
    List<Chromosome> chromosomes = new ArrayList<Chromosome>()

    void Fitness(Chromosome chro, double f){
        pop.put(chro, f)
    }

    Chromosome getLast(){
        return chromosomes.last()
    }

    double getFitness(Chromosome chro){
        return pop.chro
    }

    void addChromosome(Chromosome c) {
        chromosomes.add(c)
    }

    void addChromosome(Chromosome c, double f) {
        chromosomes.add(c)
        pop.put(c, f)
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

    String toString(){

        return chromosomes.toString()
    }

}

class Chromosome implements Serializable{
    List<Gene> genes
    double fitness


    Chromosome(){
        genes = new ArrayList<Gene>()
    }

    Chromosome(Chromosome c ){
        genes = c.genes
    }

    void remove(int i){
        genes.remove(i)
    }

    void Swap(int i, int j){

    }


    int getGene(int i )
    {
        return genes[i]
    }

    void setGene(int i)
    {
        Gene gene = new Gene(i)
        genes.add(gene)
    }

    String toString(){
        return genes.toString()
    }

    String printGenes(){
        String s
        Gene gene
        (gene in genes).each{
            s = gene.toString()
        }
    }

}

class Gene implements Serializable{
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

    String toString(){
        return gene.toString()
    }
}