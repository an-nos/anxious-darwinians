package darwinian;

import java.util.HashMap;
import java.util.Map;

public class MapStats {

    private FoldingMap map;
    private int lifeSpanSum;
    private int deadCount;
    private int childrenCount;
    public Genome dominatingGenome;
    private Map<Genome, Integer> genomeCount;

    private Map<StatField, Integer> stats;
    private Map<StatField, Long> totals;

    public MapStats(FoldingMap map){
        this.map = map;

        this.genomeCount = new HashMap<>();
        this.stats = new HashMap<>();
        this.totals = new HashMap<>();
        this.lifeSpanSum = 0;
        this.deadCount = 0;

        for(StatField stat: StatField.values()){
            this.stats.put(stat, 0);
            this.totals.put(stat, (long) 0);
        }
    }

    public void updateGenomeCounter(Genome genome){
        if(this.genomeCount.containsKey(genome)) this.genomeCount.put(genome, this.genomeCount.get(genome)+1);
        else this.genomeCount.put(genome, 1);
    }

    public void decreaseGenomeCounter(Genome genome){
        if(this.genomeCount.get(genome).equals(1)) this.genomeCount.remove(genome);
        else this.genomeCount.put(genome, this.genomeCount.get(genome)-1);
    }

    public void setDominatingGenome(){
        int maxOccurrences = 0;
        Genome maxGenome = null;

        for(Genome genome : this.genomeCount.keySet()){
            if(this.genomeCount.get(genome) > maxOccurrences){
                maxOccurrences = this.genomeCount.get(genome);
                maxGenome = genome;
            }
        }

        this.stats.put(StatField.GENOME_OCCURRENCES, maxOccurrences);
        this.dominatingGenome = maxGenome;
    }

    public void updateDeadCount(int age){
        this.lifeSpanSum += age;
        this.deadCount++;
    }

    public void updateChildrenCount(){ this.childrenCount++; }

    private void setCurrentAnimalCount() {
        StatField numOfAnimals = StatField.NUM_OF_ANIMALS;
        this.stats.put(numOfAnimals, this.map.getAnimalsSize());
    }

    private void setCurrentPlantCount() {
        this.stats.put(StatField.NUM_OF_PLANTS, this.map.getPlantsSize());
    }

    private void setAvgLifespan(){
        if(this.deadCount == 0) this.stats.put(StatField.AVG_LIFESPAN_DEAD, 0);
        else this.stats.put(StatField.AVG_LIFESPAN_DEAD, this.lifeSpanSum / this.deadCount);
    }

    private void setCurrentAverageAnimalEnergy() {
        if(this.stats.get(StatField.NUM_OF_ANIMALS) == 0){
            this.stats.put(StatField.AVG_ENERGY, 0);
            return;
        }

        int sumOfEnergy = 0;
        for(Animal animal : this.map.animals){
            sumOfEnergy+=animal.getEnergy();
        }
        this.stats.put(StatField.AVG_ENERGY, sumOfEnergy / this.stats.get(StatField.NUM_OF_ANIMALS));
    }

    private void setBornToday(){
        this.stats.put(StatField.BORN_TODAY, this.childrenCount);
    }

    private void setAvgChildrenCount(){
        if(this.deadCount!=0) this.stats.put(StatField.AVG_CHILDREN, this.childrenCount/this.stats.get(StatField.NUM_OF_ANIMALS));
    }

    private void setDay(){
        this.stats.put(StatField.DAY, this.map.getAge());
    }

    private void updateTotals(){
        for(StatField stat: StatField.values()){
            this.totals.put(stat, this.totals.get(stat)+this.stats.get(stat));
        }
    }

    public String getStats(StatField field){
        return this.stats.get(field).toString();
    }

    public void updateAllStats() {
        this.setDay();
        this.setDominatingGenome();
        this.setCurrentAnimalCount();
        this.setCurrentPlantCount();
        this.setCurrentAverageAnimalEnergy();
        this.setAvgChildrenCount();
        this.setBornToday();
        this.setAvgLifespan();
        this.updateTotals();
        this.childrenCount = 0;
    }

}
