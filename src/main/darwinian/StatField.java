package darwinian;

enum StatField {

    DAY,
    NUM_OF_ANIMALS,
    NUM_OF_PLANTS,
   // DOMINATING_GENOME,
    AVG_ENERGY,
    AVG_LIFESPAN_DEAD,
    BORN_TODAY,
    AVG_CHILDREN,
    GENOME_OCCURRENCES;

    public String toString(){
        switch(this){
            case DAY: return "Day: ";
            case NUM_OF_ANIMALS: return "Number of animals: ";
            case NUM_OF_PLANTS: return "Number of plants: ";
            //case DOMINATING_GENOME: return "Dominating genome: ";
            case GENOME_OCCURRENCES: return "Number of occurrences of dominating genome: ";
            case AVG_ENERGY: return "Average energy: ";
            case AVG_LIFESPAN_DEAD: return "Average lifespan of the dead: ";
            case BORN_TODAY: return "Animals born today: ";
            case AVG_CHILDREN: return  "Average number of children: ";
            default: return "";
        }
    }
}