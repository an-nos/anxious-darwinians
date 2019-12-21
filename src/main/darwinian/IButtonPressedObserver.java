package darwinian;

public interface IButtonPressedObserver {
    void pausePressed();
    void showDominatingPressed();
    void changeDelay(int newDelay);
}
