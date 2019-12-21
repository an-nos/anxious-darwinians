package darwinian;

import org.w3c.dom.Text;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.zip.CheckedOutputStream;

public class SidePanel implements ActionListener {

    public JPanel sidePanel;

    private Vector2d size;
    private JButton pauseButton, showDominatingButton, saveButton;
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
        this.sidePanel.setAlignmentY(JPanel.CENTER_ALIGNMENT);
        this.observers = new ArrayList<>();

        if(!secondMap) {
            this.pauseButton = new JButton("pause");
            this.pauseButton.setPreferredSize(new Dimension(10, 20));
            this.pauseButton.addActionListener(this);
            this.sidePanel.add(this.pauseButton);

            this.showDominatingButton = new JButton("show dominating");
            this.showDominatingButton.addActionListener(e -> showDominating());
            this.sidePanel.add(this.showDominatingButton);

            this.saveButton = new JButton("save");
            this.saveButton.addActionListener(e -> saveToFile());
            this.sidePanel.add(this.saveButton);

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
            if (command.equals("pause")) {
                this.pausePressed = true;
                this.pauseButton.setText("continue");
            } else if (command.equals("continue")) {
                this.pausePressed = false;
                this.pauseButton.setText("pause");
            }
            for (IButtonPressedObserver observer : this.observers) observer.pausePressed();
    }

    private void showDominating(){
        if(this.pausePressed) for(IButtonPressedObserver observer : this.observers) observer.showDominatingPressed();
    }

    private void saveToFile(){
        if(!pausePressed) return;
        JFileChooser fileChooser = new JFileChooser();
        int retval = fileChooser.showSaveDialog(this.saveButton);
        if (retval == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (file == null) return;
            if (!((File) file).getName().toLowerCase().endsWith(".txt"))
                file = new File(file.getParentFile(), file.getName() + ".txt");
            try {
                JTextArea textArea = new JTextArea(24, 80);
                writeStatsIn(textArea);
                textArea.write(new OutputStreamWriter(new FileOutputStream(file),
                        "utf-8"));
                Desktop.getDesktop().open(file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void writeStatsIn(JTextArea textArea){
        textArea.append("Following statistics are average values after "+ map.getAge()+" days\n");
        for(String statStr: this.map.stats.getAvgStats())
            textArea.append(statStr+"\n");
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

    public void addObserver(IButtonPressedObserver observer){
        this.observers.add(observer);
    }

    public void removeObserver(IButtonPressedObserver observer){
        this.observers.remove(observer);
    }
}