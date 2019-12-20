package darwinian;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class SidePanel implements ActionListener {

    public JPanel sidePanel;

    private Vector2d size;
    public JButton pauseButton;
    public boolean pausePressed;
    public FoldingMap map;

    JLabel dominatingGenomeText;

    private JLabel chosenAnimalChildrenText, chosenAnimalSuccessorsText, chosenAnimalDeathDate, chosenAnimalGenomeText;
    private Map<Vector2d, JLabel> labels;

    private Map<StatField, JLabel> statLabels;

    private List<IButtonPressedObserver> observers;

    public SidePanel(Vector2d size, FoldingMap map, boolean secondMap){

        this.size = size;
        this.sidePanel = new JPanel();
        this.sidePanel.setSize(this.size.y, this.size.x);
        this.sidePanel.setLayout(new GridLayout(0,1));
        this.sidePanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        this.observers = new ArrayList<>();

        if(!secondMap) {
            this.pauseButton = new JButton("pause");
            this.pauseButton.setPreferredSize(new Dimension(10, 20));
            this.pauseButton.addActionListener(this);
            this.sidePanel.add(this.pauseButton);
        }

        this.map = map;
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

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        String command = actionEvent.getActionCommand();
        if(command.equals("pause")){
            this.pausePressed=true;
            this.pauseButton.setText("continue");
        }else if(command.equals("continue")){
            this.pausePressed=false;
            this.pauseButton.setText("pause");
        }

        for(IButtonPressedObserver observer: this.observers) observer.pausePressed();
    }

    JLabel addTextLabel(String initialText){
        JLabel textLabel = new JLabel();
        textLabel.setVerticalAlignment(JLabel.TOP);
        textLabel.setHorizontalAlignment(JLabel.CENTER);
        textLabel.setText(initialText);
        this.sidePanel.add(textLabel);
        return textLabel;
    }

    public void displayStatistics(){
        for(StatField statField : StatField.values()){
            this.statLabels.get(statField).setText(statField.toString() + map.getStats(statField));
        }
        this.dominatingGenomeText.setText("Dominating genome: \n" + map.stats.dominatingGenome);
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

    public void addObserver(IButtonPressedObserver observer){
        this.observers.add(observer);
    }

    public void removeObserver(IButtonPressedObserver observer){
        this.observers.remove(observer);
    }
}