package darwinian;

import java.util.*;
import java.util.List;

public class Animal implements Comparable {
    private int startEnergy, moveEnergy;
    private int energy;
    private Vector2d position;
    private MapDirection direction = MapDirection.NORTH;
    private FoldingMap map;
    private Genome genome;
    private int birthDate;
    boolean isSuccessor;
    private List<IAnimalStateChangeObserver> observers = new ArrayList<>();

    public Animal (int startEnergy, int initialEnergy, int moveEnergy, Genome genome, FoldingMap map){
        this.startEnergy = startEnergy;
        this.moveEnergy = moveEnergy;
        this.energy = initialEnergy;
        this.genome = genome;
        this.map = map;
        this.direction = this.direction.randomDirection();
        this.position = map.randomPosition();
        this.birthDate = map.getAge();
        this.isSuccessor = false;
    }

    Integer getEnergy(){ return this.energy; }

    Integer getEnergyLevel(){
        double relativeEnergy = (double)this.energy / (double)this.startEnergy;

        if(relativeEnergy <= 0.25) return 1;
        if(relativeEnergy <= 0.5) return 2;
        if(relativeEnergy <= 0.75) return 3;
        if(relativeEnergy <= 1) return 4;
        if(relativeEnergy <= 1.25) return 5;
        if(relativeEnergy <= 1.5) return 6;
        if(relativeEnergy <= 1.75) return 7;
        return 8;

    }

    Genome getGenome(){ return this.genome; }

    int getAge(){ return this.map.getAge() - this.birthDate; }

    Vector2d getPosition(){ return this.position; }

    private void changeDirection (){
        Random randomGenerator = new Random();
        int chosenDirection = this.genome.genes.get(randomGenerator.nextInt(Genome.numOfGenes));
        this.direction = this.direction.fromInt((chosenDirection + this.direction.toInt())%8);
    }

    void move(){

        if(this.energy-this.moveEnergy>0) {

            Vector2d oldPosition = this.position;
            this.changeDirection();
            this.position = oldPosition.add(this.direction.toUnitVector());
            this.position = this.map.convertToAllowedPosition(this.position);

            this.energy -= this.moveEnergy;

            this.positionChanged(this, oldPosition);
       }
        else this.die();
    }

    void eat(int energy){
        this.energy += energy;
    }

    boolean canMate(){ return this.energy * 2 >= this.startEnergy; }

    Animal mate(Animal other){
        if(this.canMate() && other.canMate()){

            List<Vector2d> positionsNearParents = new ArrayList<>();
            Vector2d randomPositionNearParents;
            MapDirection direction = MapDirection.NORTH;

            for(int i=0; i<8; i++){
                Vector2d position = this.position.add(direction.toUnitVector());
                if(map.isOccupied(position)) positionsNearParents.add(position);
            }

            if(!positionsNearParents.isEmpty())
                randomPositionNearParents = this.map.randomPositionOf(positionsNearParents);
            else
                randomPositionNearParents = this.position.add(direction.randomDirection().toUnitVector());

            Animal child = new Animal(this.startEnergy, (int) ((this.energy + other.energy) * 0.25), this.moveEnergy, new Genome(this.genome, other.genome), map);

            this.energy *= (0.75);
            other.energy *= (0.75);

            child.position = randomPositionNearParents;
            child.position = map.convertToAllowedPosition(child.position);

            if(map.hasChosenAnimal())
                child.isSuccessor = (this.isSuccessor || other.isSuccessor) && child.birthDate >= map.getChosenAnimal().observeDate;

            this.giveBirthTo(child);

            return child;
        }
        return null;

    }

    void addObserver(IAnimalStateChangeObserver observer){
        observers.add(observer);
    }

    void removeObserver(IAnimalStateChangeObserver observer){
        observers.remove(observer);
    }

    private void giveBirthTo(Animal child){
        for(IAnimalStateChangeObserver observer: observers){
            observer.animalBorn(child);
        }
    }

    private void positionChanged(Animal animal, Vector2d oldPosition){
        for(IAnimalStateChangeObserver observer: observers){
            observer.positionChanged(animal, oldPosition);
        }
    }

    private void die(){
        for(IAnimalStateChangeObserver observer: observers) observer.animalDied(this);
    }

    @Override
    public int compareTo(Object other) {
        if(! (other instanceof Animal)) return -1;
        return ((Animal) other).getEnergy() - this.getEnergy();
    }
}
