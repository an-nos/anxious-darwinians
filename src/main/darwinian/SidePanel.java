package darwinian;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

public class SidePanel implements ActionListener {

    public JPanel sidePanel;
    public int height;
    public int width;
    public JButton pauseButton;
    public JButton continueButton;
    public boolean pausePressed;
    public FoldingMap map;
    public MapStats mapStats;
    JLabel ageText, animalCountText, plantsCountText, dominatingGeneText, avgAnimalEnergyText, avgChildrenCountText;
    JLabel chosenAnimalChildrenText, chosenAnimalSuccessorsText, chosenAnimalDeathDate;
    Map<Vector2d, JLabel> labels;

    public SidePanel(int width, int height, FoldingMap map){

        this.height = height;
        this.width = width;
        this.sidePanel = new JPanel();
        this.sidePanel.setLayout(new GridLayout(10, 10,0,0));
        this.sidePanel.setSize(width, height);
        this.pauseButton = new JButton("pause");
        this.pauseButton.addActionListener(this);
        this.sidePanel.add(this.pauseButton);

        this.continueButton = new JButton("continue");
        this.continueButton.addActionListener(this);
        this.sidePanel.add(this.continueButton);

        this.map = map;
        this.labels = new HashMap<>();
        this.mapStats = new MapStats(map);

        this.ageText = addTextLabel("Current day: ");
        this.animalCountText = addTextLabel("Number of living animals: ");
        this.plantsCountText = addTextLabel("Number of plants on map: ");
        this.dominatingGeneText = addTextLabel("Dominating gene: ");
        this.avgAnimalEnergyText = addTextLabel("Average energy: ");
        this.avgChildrenCountText = addTextLabel( "Average number of children: ");

        this.chosenAnimalChildrenText = addTextLabel("");
        this.chosenAnimalSuccessorsText = addTextLabel("");
        this.chosenAnimalDeathDate = addTextLabel("");
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        String command = actionEvent.getActionCommand();
        if(command.equals("pause")){
            this.pausePressed=true;
        }else if(command.equals("continue")){
            this.pausePressed=false;
        }
    }

    JLabel addTextLabel(String initialText){
        JLabel textLabel = new JLabel();
        textLabel.setText(initialText);
        this.sidePanel.add(textLabel);
        return textLabel;
    }

    public void displayStatistics(){
        this.ageText.setText("Current day: "+mapStats.age);
        this.animalCountText.setText("Number of living animals: "+mapStats.numOfAnimals);
        this.plantsCountText.setText("Number of plants on map: "+mapStats.numOfPlants);
        this.dominatingGeneText.setText("Dominating gene: "+mapStats.dominatingGene);
        this.avgAnimalEnergyText.setText("Average energy: "+mapStats.avgEnergy);
        this.avgChildrenCountText.setText("Average number of children: "+mapStats.avgChildrenCount);

        displayStatisticOfAnimalBeingObserved();
    }

    public void displayStatisticOfAnimalBeingObserved(){
        if(map.animalBeingObserved == null) return;

        this.chosenAnimalChildrenText.setText("Number of children: "+this.map.numOfChildren);
        this.chosenAnimalSuccessorsText.setText("Number of successors: "+this.map.numOfSuccessors);
    }
}

