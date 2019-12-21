package darwinian;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class SidePanel {

    public JPanel panel;
    private Vector2d size;
    public FoldingMap map;

    private JLabel chosenAnimalText;
    private JLabel dominatingGenomeText, chosenAnimalChildrenText, chosenAnimalSuccessorsText, chosenAnimalDeathDate, chosenAnimalGenomeText;

    private Map<StatField, JLabel> statLabels;


    public SidePanel(Vector2d size, FoldingMap map, List<JButton> buttonList){

        this.size = size;
        this.panel = new JPanel();

        if(buttonList != null){
            this.panel.setLayout(new GridLayout(22,1));
            this.panel.setSize(this.size.y, this.size.x);
        }
        else{
            this.panel.setLayout(new GridLayout(20,1));
            this.panel.setSize(this.size.y, this.size.x);
        }
        this.panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        this.panel.setAlignmentY(JFrame.TOP_ALIGNMENT);

        this.map = map;

        JPanel buttonContainer = new JPanel();
        buttonContainer.setLayout(new GridLayout(1,3));
        if(buttonList != null){

            for(JButton button: buttonList){
                buttonContainer.add(button);
            }
            this.panel.add(buttonContainer);

        }

        if(buttonList != null) this.addTextLabel("Statistics of the first map:");
        else this.addTextLabel("Statistics of the second map:");

        this.statLabels = new HashMap<>();
        for(StatField stat : StatField.values()){
            this.statLabels.put(stat, addTextLabel(stat.toString()));
        }
        this.dominatingGenomeText = addTextLabel("");

        this.chosenAnimalText = this.addTextLabel("");

        this.chosenAnimalGenomeText = addTextLabel("");
        this.chosenAnimalChildrenText = addTextLabel("");
        this.chosenAnimalSuccessorsText = addTextLabel("");
        this.chosenAnimalDeathDate = addTextLabel("");

    }


    JLabel addTextLabel(String initialText){
        JLabel textLabel = new JLabel();
        textLabel.setVerticalAlignment(JLabel.CENTER);
        textLabel.setHorizontalAlignment(JLabel.LEFT);
        textLabel.setText(initialText);
        this.panel.add(textLabel);
        return textLabel;
    }

    public void displayStatistics(){
        for(StatField statField : StatField.values()){
            this.statLabels.get(statField).setText(statField.toString() + map.getStats(statField));
        }
        this.dominatingGenomeText.setText("Dominating genome: " + map.stats.getDominatingGenome());
        displayStatisticOfAnimalBeingObserved();
    }

    public void displayStatisticOfAnimalBeingObserved(){
        if(!map.hasChosenAnimal()) return;

        this.chosenAnimalText.setText("Chosen animal:");
        this.chosenAnimalGenomeText.setText("Genome: "+this.map.getChosenAnimal().animal.getGenome());
        this.chosenAnimalChildrenText.setText("Number of children: "+this.map.getChosenAnimal().numOfChildren);
        this.chosenAnimalSuccessorsText.setText("Number of successors: "+this.map.getChosenAnimal().numOfSuccessors);

        if(this.map.getChosenAnimal().isDead())
            this.chosenAnimalDeathDate.setText("Death date: "+this.map.getChosenAnimal().deathDate);
        else this.chosenAnimalDeathDate.setText("");
    }

}