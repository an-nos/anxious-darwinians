package darwinian;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapStats {

    private FoldingMap map;
    private int lifeSpanSum, deadCount, childrenCount;

    private Genome dominatingGenome, everDominatingGenome;
    private int everDominatingGenomeCount;
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
        this.everDominatingGenomeCount = 0;

        for(StatField stat: StatField.values()){
            this.stats.put(stat, 0);
            this.totals.put(stat, (long) 0);
        }
    }

    Genome getDominatingGenome(){
        return this.dominatingGenome;
    }

    void updateGenomeCounter(Genome genome){
        if(this.genomeCount.containsKey(genome)) this.genomeCount.put(genome, this.genomeCount.get(genome)+1);
        else this.genomeCount.put(genome, 1);
    }

    void decreaseGenomeCounter(Genome genome){
        if(this.genomeCount.get(genome).equals(1)) this.genomeCount.remove(genome);
        else this.genomeCount.put(genome, this.genomeCount.get(genome)-1);
    }

    private void setDominatingGenome(){
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

        if(maxOccurrences>this.everDominatingGenomeCount){
            this.everDominatingGenomeCount = maxOccurrences;
            this.everDominatingGenome = this.dominatingGenome;
        }
    }

    void updateDeadCount(int age){
        this.lifeSpanSum += age;
        this.deadCount++;
    }

    void updateChildrenCount(){ this.childrenCount++; }

    private void setCurrentAverageAnimalEnergy() {
        if(this.stats.get(StatField.NUM_OF_ANIMALS) == 0){
            this.stats.put(StatField.AVG_ENERGY, 0);
            return;
        }

        int sumOfEnergy = 0;
        for(Animal animal : this.map.getAnimals()){
            sumOfEnergy+=animal.getEnergy();
        }
        this.stats.put(StatField.AVG_ENERGY, sumOfEnergy / this.stats.get(StatField.NUM_OF_ANIMALS));
    }

    private void updateTotals(){
        for(StatField stat: StatField.values()){
            this.totals.put(stat, this.totals.get(stat)+this.stats.get(stat));
        }
    }

    String getStats(StatField field){
        return this.stats.get(field).toString();
    }

    List<String> getAvgStats(){

        List<String> avgStats = new ArrayList<>();

        for(StatField stat : StatField.values()){
            if(stat.equals(StatField.DAY)) continue;
            StringBuilder line = new StringBuilder();
            line.append(stat);
            line.append(this.totals.get(stat)/this.stats.get(StatField.DAY));
            avgStats.add(line.toString());
        }

        avgStats.add("Ever dominating genome: "+this.everDominatingGenome);
        avgStats.add("Highest number of animals with ever dominating genome: "+this.everDominatingGenomeCount);

        return avgStats;
    }

    private void setStat(StatField stat) {
        switch (stat) {
            case DAY:
                this.stats.put(StatField.DAY, this.map.getAge());
                break;
            case NUM_OF_ANIMALS:
                this.stats.put(stat, this.map.getAnimalsSize());
                break;
            case NUM_OF_PLANTS:
                this.stats.put(stat, this.map.getPlantsSize());
                break;
            case GENOME_OCCURRENCES:
                this.setDominatingGenome();
                break;
            case AVG_LIFESPAN_DEAD:
                if (this.deadCount == 0) this.stats.put(stat, 0);
                else this.stats.put(stat, this.lifeSpanSum / this.deadCount);
                break;
            case AVG_ENERGY:
                this.setCurrentAverageAnimalEnergy();
                break;
            case BORN_TODAY:
                this.stats.put(stat, this.childrenCount);
                break;
            case AVG_CHILDREN:
                if (this.stats.get(StatField.NUM_OF_ANIMALS) != 0)
                    this.stats.put(StatField.AVG_CHILDREN, this.childrenCount / this.stats.get(StatField.NUM_OF_ANIMALS));
                break;
        }
    }

    public void updateAllStats() {
        for(StatField stat : StatField.values()) setStat(stat);
        this.updateTotals();
        this.childrenCount = 0;
    }

}
