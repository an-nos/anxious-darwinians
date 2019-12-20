package darwinian;

import java.awt.desktop.AppReopenedEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;


public class Genome {

    List<Integer> genes;
    Integer[] presentGenes;
    public static int numOfGenes = 32;
    public static int numOfDiffGenes = 8;


    public Genome (Genome fromMother, Genome fromFather){

        this.presentGenes = new Integer[numOfDiffGenes];
        this.genes = new ArrayList<>();

        for(int i=0; i<numOfDiffGenes; i++) this.presentGenes[i]=0;

        Random randomGenerator = new Random();
        int firstDivider = randomGenerator.nextInt(numOfGenes);
        int secondDivider = firstDivider + randomGenerator.nextInt(numOfGenes - firstDivider);

        this.genes.addAll(fromMother.genes.subList(0,firstDivider));
        this.genes.addAll(fromFather.genes.subList(firstDivider,secondDivider));
        this.genes.addAll(fromMother.genes.subList(secondDivider,numOfGenes));

        Collections.sort(this.genes);
        for(int i = 0; i < numOfGenes; i++) this.presentGenes[this.genes.get(i)]++;
        correctGenome();
    }

    public Genome (){   //random genome
        this.presentGenes = new Integer[numOfDiffGenes];
        this.genes = new ArrayList<>();

        for(int i=0; i<numOfDiffGenes; i++){
            this.genes.add(i);
            this.presentGenes[i]=1;
        }

        for(int i = numOfDiffGenes; i<numOfGenes; i++) {
            Random randomGenerator = new Random();
            int randomGene = randomGenerator.nextInt(numOfDiffGenes);
            this.genes.add(randomGene);
            this.presentGenes[randomGene]++;
        }
        Collections.sort(this.genes);
    }

    private void correctGenome(){

        for(int gene = 0; gene<numOfDiffGenes; gene++){
            if(this.presentGenes[gene].equals(0)){
                Random randomGenerator = new Random();
                int randomGeneInd;
                do{
                    randomGeneInd = randomGenerator.nextInt(numOfGenes);
                } while(this.presentGenes[this.genes.get(randomGeneInd)]<2);

                this.genes.set(randomGeneInd,gene);
                this.presentGenes[this.genes.get(randomGeneInd)]--;
                this.presentGenes[gene]++;
            }
        }
    }

    public String toString(){
        StringBuilder geneStr= new StringBuilder();
        for(int gene : this.genes){
            geneStr.append(gene);
        }
        return geneStr.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if(! (obj instanceof Genome)) return false;
        return this.genes.equals((Genome) obj);
    }

    @Override
    public int hashCode() {
        long hash = 0;

        for(int i = 0; i < numOfDiffGenes; i++){
            hash = (hash+this.presentGenes[i]*32)%Integer.MAX_VALUE;
        }

        return (int) hash;
    }
}
