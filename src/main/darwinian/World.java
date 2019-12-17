package darwinian;

import javafx.animation.Animation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class World {

    public static void main(String[] args) {
        int startEnergy = 10;
        int moveEnergy = 1;
        int plantEnergy = 8;
        int width = 20;
        int height = 20;
        FoldingMap map = new FoldingMap(width, height, 30, 1, plantEnergy, 0.25);

        for(int i=0; i<5; i++){
            Plant plant = new Plant(map.randomPosition(), plantEnergy);
            map.insertPlant(plant);
        }

        for(int i=0; i<10; i++){
            Plant plant = new Plant(map.randomPositionInJungle(), plantEnergy);
            map.insertPlant(plant);
        }

        for(int i =0; i<50; i++) {
            Animal nextCat = new Animal(startEnergy, startEnergy, moveEnergy, new Genome(), map);
            map.animalBorn(nextCat);
        }

        SwingVisualizer visualizerTest = null;
        try {
            visualizerTest = new SwingVisualizer(map);
            while(true){
                if(visualizerTest.sidePanel.pausePressed){
                    Thread.sleep(5);
                    continue;
                }
                map.day();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
    //TODO: Dominating genome, avg lifespan for dead animals, monitoring one animal
}
