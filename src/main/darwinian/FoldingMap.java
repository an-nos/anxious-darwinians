package darwinian;

import java.util.*;

public class FoldingMap implements IAnimalStateChangeObserver {

    private Vector2d upperBound, lowerBound, jungleUpperBound, jungleLowerBound;
    private int plantEnergy;
    private List<Animal> animals;
    private List<Plant> plants;
    private HashMap<Vector2d,List<Animal>> animalsByPosition;
    private HashMap<Vector2d,Plant> plantsByPosition;

    private HashMap<Genome, List<Animal>> animalsByGenome;

    private List <Animal> dead;
    private List <Plant> plantsToBeEaten;
    private List <IMapStateChangeObserver> mapStateChangeObservers;

    private List <Vector2d> freePositionsInJungle, freePositionsOutsideJungle;

    MapStats stats;
    private int age;

    private ChosenAnimal chosenAnimal;

    public FoldingMap (int mapWidth, int mapHeight, int startEnergy, int moveEnergy, int plantEnergy, double jungleRatio){

        int jungleHeight = (int) (Math.sqrt(jungleRatio) * mapHeight);
        int jungleWidth = (int) (Math.sqrt(jungleRatio) * mapWidth);

        this.lowerBound = new Vector2d(0,0);
        this.upperBound = new Vector2d(mapWidth-1,mapHeight-1);

        this.jungleLowerBound = new Vector2d((mapWidth-1)/2 - (jungleWidth-1)/2,(mapHeight-1)/2 - (jungleHeight-1)/2);
        this.jungleUpperBound = new Vector2d (jungleLowerBound.x+jungleWidth-1, jungleLowerBound.y+jungleHeight-1);

        this.plantEnergy = plantEnergy;

        this.animals = new ArrayList<>();
        this.plants = new ArrayList<>();
        this.dead = new ArrayList<>();
        this.plantsToBeEaten = new ArrayList<>();

        this.animalsByPosition = new HashMap<>();
        this.plantsByPosition = new HashMap<>();
        this.animalsByGenome = new HashMap<>();

        this.mapStateChangeObservers  = new ArrayList<>();
        this.age = 0;

        this.freePositionsInJungle = new ArrayList<>();
        this.freePositionsOutsideJungle = new ArrayList<>();

        for(int x = this.lowerBound.x; x <= this.upperBound.x; x++){
            for(int y = this.lowerBound.y; y <= this.upperBound.y; y++){
                addFreePosition(new Vector2d(x,y));
            }
        }

        this.chosenAnimal = null;
        this.stats = new MapStats(this);

    }

    public String getStats(StatField field){
        return this.stats.getStats(field);
    }

    public int getWidth() { return this.upperBound.x - this.lowerBound.x + 1; }

    public int getHeight() { return this.upperBound.y - this.lowerBound.y + 1; }

    public int getJungleWidth() { return this.jungleUpperBound.x - this.jungleLowerBound.x + 1; }

    public int getJungleHeight() { return this.jungleUpperBound.y - this.jungleLowerBound.y + 1; }

    public int getAge() { return this.age; }

    public List<Animal> getAnimals(){ return this.animals; }

    public int getAnimalsSize(){ return this.animals.size(); }

    public int getPlantsSize(){ return this.plants.size(); }

    public Animal getTopAnimalAt(Vector2d position){ return (Animal) Collections.min(this.animalsAt(position)); }

    public List<Animal> getSuperiorRace(){ return this.animalsByGenome.get(this.stats.getDominatingGenome()); }

    public ChosenAnimal getChosenAnimal(){ return this.chosenAnimal; }

    private void addFreePosition(Vector2d position){
        if(isOccupied(position)) return;
        if(isInsideJungle(position)) this.freePositionsInJungle.add(position);
        else this.freePositionsOutsideJungle.add(position);
    }

    private void removeFreePosition(Vector2d position){
        if(!isOccupied(position)) return;
        if(isInsideJungle(position)) this.freePositionsInJungle.remove(position);
        else this.freePositionsOutsideJungle.remove(position);
    }

    private void placeAnimal(Animal animal, Vector2d position){

        List<Animal> animalsOnThisPosition = animalsAt(animal.getPosition());

        if(animalsOnThisPosition==null){
            List<Animal> newList = new ArrayList<>();
            newList.add(animal);
            this.animalsByPosition.put(position, newList);

            this.removeFreePosition(position);
        }
        else{
            animalsOnThisPosition.add(animal);
        }
    }

    private void removeAnimalFromPosition(Animal animal, Vector2d oldPosition){
        List<Animal> animalsOnThisPosition = animalsAt(oldPosition);
        animalsOnThisPosition.remove(animal);
        if(animalsOnThisPosition.isEmpty()){
            this.animalsByPosition.remove(oldPosition);
            this.addFreePosition(oldPosition);
        }
    }

    private void insertPlant(Plant plant){
        if(this.plants.contains(plant)) return;
        this.plants.add(plant);
        this.plantsByPosition.put(plant.getPosition(),plant);
        this.removeFreePosition(plant.getPosition());
    }

    private void removePlants(List<Plant> plantsToBeRemoved){
        this.plants.removeAll(plantsToBeRemoved);
        for(Plant plant : plantsToBeRemoved){
            this.plantsByPosition.remove(plant.getPosition());
            this.removeFreePosition(plant.getPosition());
        }
    }

    List<Animal> animalsAt(Vector2d position){
        return this.animalsByPosition.get(position);
    }

    Plant plantAt(Vector2d position){ return plantsByPosition.get(position); }

    boolean isInsideJungle(Vector2d position){ return position.follows(this.jungleLowerBound) && position.precedes(this.jungleUpperBound); }

    private boolean allowedPosition(Vector2d position){ return position.follows(this.lowerBound) && position.precedes(this.upperBound); }

    Vector2d convertToAllowedPosition(Vector2d position){

        if(allowedPosition(position)) return position;

        int x = position.x;
        int y = position.y;

        if(x>this.upperBound.x) x = this.lowerBound.x;
        else if(x<this.lowerBound.x) x = this.upperBound.x;

        if(y>this.upperBound.y) y = this.lowerBound.y;
        else if(y<this.lowerBound.y) y = this.lowerBound.y;

        return new Vector2d(x,y);
    }

    Vector2d randomPosition(){
        int randBound = this.freePositionsInJungle.size() + this.freePositionsOutsideJungle.size();

        Random randomGenerator = new Random();
        int randomNumber = randomGenerator.nextInt(randBound);

        if(randomNumber < this.freePositionsInJungle.size()){
            return this.freePositionsInJungle.get(randomNumber);
        }
        else return this.freePositionsOutsideJungle.get(randomNumber - this.freePositionsInJungle.size());
    }

    Vector2d randomPositionOf(List<Vector2d> positions){
        Random randomGenerator = new Random();
        int randomIndex = randomGenerator.nextInt(positions.size());
        return positions.get(randomIndex);
    }

    boolean isOccupied(Vector2d position){
        if(this.animalsByPosition.get(position) == null){
            return this.plantsByPosition.get(position) != null;
        }
        return true;
    }

    private void addAnimalByGenome(Animal animal){
        List<Animal> kindred = this.animalsByGenome.computeIfAbsent(animal.getGenome(), k -> new ArrayList<>());
        kindred.add(animal);
    }

    private void removeAnimalByGenome(Animal animal){
        List<Animal> kindred = this.animalsByGenome.get(animal.getGenome());
        if(kindred.size() == 1) this.animalsByGenome.remove(animal.getGenome());
        else kindred.remove(animal);
    }

    @Override
    public void positionChanged(Animal animal, Vector2d oldPosition) {
        removeAnimalFromPosition(animal, oldPosition);
        placeAnimal(animal, animal.getPosition());
        if(plantAt(animal.getPosition())!=null) {
            this.plantsToBeEaten.add(plantAt(animal.getPosition()));
        }
    }

    @Override
    public void animalBorn(Animal animal) {
        this.animals.add(animal);
        animal.addObserver(this);
        if(animal.isSuccessor) this.chosenAnimal.numOfSuccessors++;
        placeAnimal(animal, animal.getPosition());
        this.stats.updateChildrenCount();

        this.stats.updateGenomeCounter(animal.getGenome());

        this.addAnimalByGenome(animal);
    }

    @Override
    public void animalDied(Animal animal) {
        this.dead.add(animal);
        this.stats.updateDeadCount(animal.getAge());
        this.stats.decreaseGenomeCounter(animal.getGenome());
        this.removeAnimalByGenome(animal);
    }

    void corpseSweeper(){
        if(this.dead.size() == 0) return;

        this.animals.removeAll(this.dead);

        for(Animal animal : this.dead){
            removeAnimalFromPosition(animal, animal.getPosition());
            if(this.chosenAnimal != null && animal.equals(this.chosenAnimal.animal)) this.chosenAnimal.deathDate = this.age;
        }
        this.dead.clear();
    }

    private void eatingPlants(){
        for(Plant plant : this.plantsToBeEaten){

            List<Animal> competingAnimals = animalsAt(plant.getPosition());
            Collections.sort(competingAnimals);

            int maxEnergy = competingAnimals.get(0).getEnergy();
            int numOfEquals = 0;

            for(Animal animal : competingAnimals){
                if(animal.getEnergy()==maxEnergy){
                    numOfEquals++;
                }
            }

            int energyForEach = plant.getEnergy() / numOfEquals;

            for(Animal animal: competingAnimals){
                if(animal.getEnergy() == maxEnergy) {
                    animal.eat(energyForEach);
                }
                else break;
            }
        }
        removePlants(this.plantsToBeEaten);
        this.plantsToBeEaten.clear();
    }

    private void growNewPlants(){

        if(!this.freePositionsInJungle.isEmpty()) {
            Vector2d positionInsideJungle = this.randomPositionOf(freePositionsInJungle);
            this.insertPlant(new Plant(positionInsideJungle, this.plantEnergy));
        }

        if(!freePositionsOutsideJungle.isEmpty()) {
            Vector2d positionOutsideJungle = this.randomPositionOf(this.freePositionsOutsideJungle);
            this.insertPlant(new Plant(positionOutsideJungle, this.plantEnergy));
        }

    }

    private void mating(){
        List<Vector2d> keysToMating =new ArrayList<>();
        Set<Vector2d> keys = this.animalsByPosition.keySet();

        for(Vector2d key : keys){
            if(this.animalsAt(key).size()>1) keysToMating.add(key);
        }

        for(Vector2d position : keysToMating){
            List<Animal> matingCompetitors = animalsAt(position);
            Collections.sort(matingCompetitors);

            Animal first = null, second = null;

            for(Animal animal : matingCompetitors){
                if(animal.canMate()){
                    second = first;
                    first = animal;
                }
                if(second != null) break;
            }
            if(second!= null){
                first.mate(second);
                if(this.hasChosenAnimal() && (first.equals(this.chosenAnimal.animal) || second.equals(this.chosenAnimal.animal)))
                    this.chosenAnimal.numOfChildren++;
            }

        }

    }

    public void day(){
        corpseSweeper();
        for(Animal animal : animals) animal.move();
        eatingPlants();
        mating();
        growNewPlants();
        this.age++;
        this.stats.updateAllStats();
        for(IMapStateChangeObserver observer : this.mapStateChangeObservers) observer.onDayEnd();
    }

    boolean hasChosenAnimal(){
        return this.chosenAnimal != null;
    }

    void chooseAnimal(Vector2d position){
        this.chosenAnimal = new ChosenAnimal(this.getTopAnimalAt(position), this.age);
        for(IMapStateChangeObserver observer : this.mapStateChangeObservers) observer.onAnimalChosen(this);
    }

    public void addMapStateChangeObserver(IMapStateChangeObserver observer){
        this.mapStateChangeObservers.add(observer);
    }

    public void removeMaPStateChangeObserver (IMapStateChangeObserver observer){
        this.mapStateChangeObservers.remove(observer);
    }

}
