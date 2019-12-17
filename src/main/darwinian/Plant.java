package darwinian;

public class Plant {

    private Vector2d position;
    public int energy;
    public Plant(Vector2d position, int energy){
        this.position=position;
        this.energy=energy;
    }

    public Vector2d getPosition(){
        return this.position;
    }
}
