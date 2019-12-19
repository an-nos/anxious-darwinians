package darwinian;

public class ChosenAnimal {

    Animal animal;
    int observeDate;
    int numOfSuccessors;
    int numOfChildren;
    int deathDate;

    public ChosenAnimal(Animal animal, int observeDate){
        this.animal = animal;
        this.animal.isSuccessor = true;
        this.observeDate = observeDate;
        this.numOfChildren = 0;
        this.numOfSuccessors = 0;
        this.deathDate = -1;
    }

}
