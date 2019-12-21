package darwinian;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class SidePanel {

    public JPanel panel;
    private Vector2d size;
    public FoldingMap map;

    JLabel dominatingGenomeText;

    private JLabel chosenAnimalChildrenText, chosenAnimalSuccessorsText, chosenAnimalDeathDate, chosenAnimalGenomeText;
    private Map<Vector2d, JLabel> labels;

    private Map<StatField, JLabel> statLabels;


    public SidePanel(Vector2d size, FoldingMap map, List<JButton> buttonList){

        this.size = size;
        this.panel = new JPanel();
        this.panel.setSize(this.size.y, this.size.x);

        this.panel.setLayout(new GridLayout(0,1));
        this.panel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        this.panel.setAlignmentY(JFrame.TOP_ALIGNMENT);

        this.map = map;

        if(buttonList != null)
            for(JButton button: buttonList) this.panel.add(button);

        this.labels = new HashMap<>();

        this.statLabels = new HashMap<>();
        for(StatField stat : StatField.values()){
            this.statLabels.put(stat, addTextLabel(stat.toString()));
        }
        this.dominatingGenomeText = addTextLabel("");

        this.chosenAnimalGenomeText = addTextLabel("");
        this.chosenAnimalChildrenText = addTextLabel("");
        this.chosenAnimalSuccessorsText = addTextLabel("");
        this.chosenAnimalDeathDate = addTextLabel("");

    }

    JLabel addTextLabel(String initialText){
        JLabel textLabel = new JLabel();
        textLabel.setVerticalAlignment(JLabel.TOP);
        textLabel.setHorizontalAlignment(JLabel.LEFT);
        textLabel.setText(initialText);
        this.panel.add(textLabel);
        return textLabel;
    }

    public void displayStatistics(){
        for(StatField statField : StatField.values()){
            this.statLabels.get(statField).setText(statField.toString() + map.getStats(statField));
        }
        this.dominatingGenomeText.setText("Dominating genome: \n" + map.stats.getDominatingGenome());
        displayStatisticOfAnimalBeingObserved();
    }

    public void displayStatisticOfAnimalBeingObserved(){
        if(map.chosenAnimal == null) return;

        this.chosenAnimalGenomeText.setText("Genome: \n"+this.map.chosenAnimal.animal.getGenome());
        this.chosenAnimalChildrenText.setText("Number of children: "+this.map.chosenAnimal.numOfChildren);
        this.chosenAnimalSuccessorsText.setText("Number of successors: "+this.map.chosenAnimal.numOfSuccessors);

        if(this.map.chosenAnimal.deathDate != -1)
            this.chosenAnimalDeathDate.setText("Death date: "+this.map.chosenAnimal.deathDate);
        else this.chosenAnimalDeathDate.setText("");
    }

}