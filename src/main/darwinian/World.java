package darwinian;

import java.io.*;

import java.io.IOException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class World {

    public static void main(String[] args) throws FileNotFoundException {
        int width = 50;
        int height = 50;
        int startEnergy = 50;
        int moveEnergy = 1;
        int plantEnergy = 50;
        int numOfAnimals = 100;
        boolean secondMap = true;

        JSONParser jsonParser = new JSONParser();

        try{

            JSONObject jsonObject = (JSONObject) jsonParser.parse(new FileReader("src\\main\\darwinian\\parameters.json"));
            width =  (int) (long) jsonObject.get("width");
            height = (int) (long) jsonObject.get("height");
            startEnergy = (int) (long) jsonObject.get("startEnergy");
            moveEnergy = (int) (long) jsonObject.get("moveEnergy");
            plantEnergy = (int) (long) jsonObject.get("plantEnergy");
            numOfAnimals = (int) (long)jsonObject.get("numOfAnimals");
            secondMap = (boolean) jsonObject.get("secondMap");

        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        FoldingMap map1 = new FoldingMap(width, height, startEnergy, moveEnergy, plantEnergy, 0.1);
        FoldingMap map2 = null;
        if(secondMap) map2 = new FoldingMap(width, height, startEnergy, moveEnergy, plantEnergy, 0.1);


        for(int i = 0; i<numOfAnimals; i++) {
            Animal nextCat1 = new Animal(startEnergy, startEnergy, moveEnergy, new Genome(), map1);
            map1.animalBorn(nextCat1);
            if(secondMap) {
                Animal nextCat2 = new Animal(startEnergy, startEnergy, moveEnergy, new Genome(), map2);
                map2.animalBorn(nextCat2);
            }
        }

        SwingVisualizer visualizerTest = null;
        try {
            visualizerTest = new SwingVisualizer(map1, map2);
            while(true){
                if(visualizerTest.pausePressed){
                    Thread.sleep(5);
                    continue;
                }
                map1.day();
                if(secondMap) map2.day();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    //TODO: Get top ever genome in statistics
    //TODO: THREADS?

}
