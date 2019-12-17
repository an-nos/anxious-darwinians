package darwinian;

public class MapStats implements IMapStateChangeObserver{

    public FoldingMap map;
    public int age, numOfAnimals, numOfPlants, dominatingGene, avgLifespan, avgEnergy, avgChildrenCount;

    public MapStats(FoldingMap map){
        this.map=map;
        this.map.addRemoveObserver(this);
    }

    private void setCurrentAnimalCount() { this.numOfAnimals = this.map.animals.size(); }

    private void setCurrentPlantCount() { this.numOfPlants = this.map.plants.size(); }

    private void setDominatingGene() {        //TODO: test
        int dominatingGene = 0;
        int dominatingGeneFrequency = 0;
        for(int i = 0; i < Genome.numOfDiffGenes; i++){
            if(this.map.geneFrequency[i]>this.map.geneFrequency[dominatingGene]){
                dominatingGene = i;
                dominatingGeneFrequency = this.map.geneFrequency[i];
            }
        }
        this.dominatingGene = dominatingGene;
    }

    private void setCurrentAverageAnimalEnergy() {
        if(this.numOfAnimals == 0){
            this.avgEnergy = 0;
            return;
        }
        int sumOfEnergy = 0;
        for(Animal animal : this.map.animals){
            sumOfEnergy+=animal.getEnergy();
        }
        this.avgEnergy = sumOfEnergy / this.numOfAnimals;
    }

    private void setAge(){
        this.age=this.map.age;
    }

    private void setAvgChildrenCount(){
        if(this.numOfAnimals == 0){
            this.avgChildrenCount = 0;
            return;
        }
        int childrenSum = 0;
        for(Animal animal: this.map.animals){
            childrenSum+=animal.childrenCount;
        }
        this.avgChildrenCount = childrenSum / this.numOfAnimals;
    }

    @Override
    public void onDayEnd() {
        this.setCurrentAnimalCount();
        this.setCurrentAnimalCount();
        this.setCurrentPlantCount();
        this.setDominatingGene();
        this.setCurrentAverageAnimalEnergy();
        this.setAge();
        this.setAvgChildrenCount();
    }
}
