package darwinian;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class Animal {
    private int startEnergy;
    int moveEnergy;
    int energy;
    int childrenCount;
    Vector2d position;
    private MapDirection direction = MapDirection.NORTH;
    private FoldingMap map;
    Genome genome;
    int birthDate;
    boolean isSuccessor;
    private List<IAnimalStateChangeObserver> observers = new ArrayList<>();

    public Animal (int startEnergy, int initialEnergy, int moveEnergy, Genome genome, FoldingMap map){
        this.startEnergy = startEnergy;
        this.moveEnergy = moveEnergy;
        this.energy = initialEnergy;
        this.genome = genome;
        this.map = map;
        this.childrenCount = 0;
        this.direction = this.direction.randomDirection();
        this.position = map.randomPosition();
        this.birthDate = map.age;
        this.isSuccessor = false;
    }

    public Integer getEnergy(){ return this.energy; }

    public Integer getEnergyLevel(){
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

    public int getAge(){ return this.map.age - this.birthDate; }

    public Vector2d getPosition(){ return this.position; }

    private void changeDirection (){
        Random randomGenerator = new Random();
        int chosenDirection = this.genome.genes.get(randomGenerator.nextInt(Genome.numOfGenes));
        this.direction = this.direction.fromInt((chosenDirection + this.direction.toInt())%8);
    }

    public void move (){

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

    public void eat(int energy){
        this.energy += energy;
    }

    public boolean canMate (){ return this.energy * 2 >= this.startEnergy; }

    public Animal mate(Animal other){
        if(this.canMate() && other.canMate()){

            Vector2d randomPositionNearParents;
            int i = 0;
            do {
                randomPositionNearParents = this.getPosition().add(MapDirection.NORTH.randomDirection().toUnitVector());
                i++;
            }while(map.isOccupied(randomPositionNearParents) && i<8);

            i=0;
            while(map.isOccupied(randomPositionNearParents) && i<8){
                MapDirection nextDir = MapDirection.NORTH;
                randomPositionNearParents = this.getPosition().add(nextDir.toUnitVector());
                nextDir = nextDir.fromInt(nextDir.toInt()+1);
                i++;
            }

            if(map.isOccupied(randomPositionNearParents))
                randomPositionNearParents   = this.getPosition().add(MapDirection.NORTH.randomDirection().toUnitVector());

            Animal child = new Animal(this.startEnergy, (int) ((this.energy + other.energy) * 0.25), this.moveEnergy, new Genome(this.genome, other.genome), map);

            this.energy *= (0.75);

            other.energy *= (0.75);

            child.position = randomPositionNearParents;
            child.position = map.convertToAllowedPosition(child.position);
            this.giveBirthTo(child);
            child.isSuccessor = (this.isSuccessor || other.isSuccessor) && child.birthDate<=map.observeDate;
            this.childrenCount++;
            other.childrenCount++;

            return child;
        }
        return null;

    }

    public void addObserver(IAnimalStateChangeObserver observer){
        observers.add(observer);
    }

    public void removeObserver(IAnimalStateChangeObserver observer){
        observers.remove(observer);
    }

    public void giveBirthTo(Animal child){
        for(IAnimalStateChangeObserver observer: observers){
            observer.animalBorn(child);
        }
    }

    public void positionChanged(Animal animal, Vector2d oldPosition){
        for(IAnimalStateChangeObserver observer: observers){
            observer.positionChanged(animal, oldPosition);
        }
    }

    public void die(){
        for(IAnimalStateChangeObserver observer: observers) observer.animalDied(this);
    }

}
