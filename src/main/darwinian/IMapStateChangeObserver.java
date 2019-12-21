package darwinian;

public interface IMapStateChangeObserver {
    void onDayEnd();
    void onAnimalChosen(FoldingMap map);
}