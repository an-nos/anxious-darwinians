//package darwinian;
//
//import org.junit.Test;
//
//import static org.junit.Assert.*;
//
//public class FoldingMapTest {
//
//    @Test
//    public void randomPosition() {
//        assertTrue(true);
//        FoldingMap map = new FoldingMap(10, 10, 10, 1, 5, 0.25);
//        assertTrue(map.allowedPosition(map.randomPosition()));
//        for(int i=0; i<50; i++){
//            Vector2d position = map.randomPosition();
//
//            System.out.println(position);
//            assertFalse(map.isOccupied(position));
//
//        }
//    }
//
//    @Test
//    public void removeAnimalTest(){
//        FoldingMap map = new FoldingMap(10, 10, 10, 1, 5, 0.25);
//        Animal animal = new Animal(10,10,1,new Genome(), map);
//        Animal animal2 = new Animal(10,10,1,new Genome(), map);
//        Animal animal3 = new Animal(10,8,1,new Genome(), map);
//
//        animal2.position = animal.getPosition();
//        animal3.position = animal.getPosition();
//
//        map.animalBorn(animal);
//        map.animalBorn(animal2);
//        map.animalBorn(animal3);
//
//        assertEquals(3,map.animalsAt(animal.getPosition()).size());
//        Vector2d oldPosition = animal.getPosition();
//        animal.move();
//        assertEquals(1,map.animalsAt(animal.getPosition()).size());
//        assertEquals(2,map.animalsAt(oldPosition).size());
//
//
//        animal2.mate(animal3);
//
//        assertEquals(2,map.animalsAt(oldPosition).size());
//
//        assertEquals(4, map.getCurrentAnimalCount());
//
//        animal2.move();
//
//        assertEquals(1,map.animalsAt(oldPosition).size());
//
//        animal3.eat(3);
//
//
//        assertEquals(1, map.animalsAt(oldPosition).size());
//
//        animal3.die();
//        map.corpseSweeper();
//
//        assertEquals(null, map.animalsAt(oldPosition));
//
//
//    }
//
//    @Test
//    public void dayTest(){
//        FoldingMap map = new FoldingMap(10, 10, 10, 1, 5, 0.25);
//        Animal animal = new Animal(10,10,1,new Genome(), map);
//        Animal animal2 = new Animal(10,10,1,new Genome(), map);
//        Animal animal3 = new Animal(10,8,1,new Genome(), map);
//
//        animal2.position = animal.getPosition();
//        animal3.position = animal.getPosition();
//
//        map.animalBorn(animal);
//        map.animalBorn(animal2);
//        map.animalBorn(animal3);
//        for(int i = 0; i< 20; i++)
//            map.day();
//
//
//    }
//
//
//}