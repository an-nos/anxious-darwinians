package darwinian;

import org.junit.Test;

import static org.junit.Assert.*;

public class GenomeTest {

    @Test
    public void testRandGenome(){
        Genome genome = new Genome();
        assertEquals(32,genome.genes.size());
        for(int i=0; i<32; i++){
            System.out.println(genome.genes.get(i));
        }
    }

    @Test
    public void testGenomeFromParents(){
        Genome fromMother = new Genome();
        Genome fromFather = new Genome();

        Genome childGenome = new Genome(fromMother, fromFather);
        assertEquals(32,childGenome.genes.size());
        for(int i=0; i<32; i++){
            System.out.println(childGenome.genes.get(i));
        }
    }

}