package darwinian;

public interface IAnimalStateChangeObserver {
    void positionChanged(Animal animal, Vector2d oldPosition);
    void animalBorn(Animal animal);
    void animalDied(Animal animal);
}