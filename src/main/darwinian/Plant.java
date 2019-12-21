package darwinian;

public class Plant {

    private Vector2d position;
    private int energy;

    public Plant(Vector2d position, int energy){
        this.position=position;
        this.energy=energy;
    }

    Vector2d getPosition(){
        return this.position;
    }
    int getEnergy(){ return this.energy; }
}
