package darwinian;


import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class SwingVisualizer implements IMapStateChangeObserver {
    private FoldingMap map;
    private FoldingMap secondMap;

    private SidePanel sidePanel, secondSidePanel;
    private MapPanel mapPanel;
    private MapPanel secondMapPanel;
    private boolean pausePressed;

    private JButton pauseButton, showDominatingButton, saveButton;

    private List<IButtonPressedObserver> observers;

    public SwingVisualizer(FoldingMap map, FoldingMap secondMap) throws IOException {
        this.map = map;
        this.secondMap = secondMap;
        this.observers = new ArrayList<>();

        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));

        Vector2d mapPanelSize;
        Vector2d sidePanelSize;
        Vector2d frameSize;

        if(this.secondMap == null){
            mapPanelSize = new Vector2d(600, 600);
            sidePanelSize = new Vector2d(430, 600);
            frameSize = new Vector2d(mapPanelSize.x+ sidePanelSize.x, mapPanelSize.y+40);
        }
        else{
            mapPanelSize = new Vector2d(400, 400);
            sidePanelSize = new Vector2d( 400, 350);
            frameSize = new Vector2d(mapPanelSize.x*2+24, mapPanelSize.y+ sidePanelSize.y);
        }

        int speed = 10;
        if(secondMap != null) speed /=2;
        this.map.addMapStateChangeObserver(this);
        this.mapPanel = new MapPanel(mapPanelSize, this.map, speed);


        JFrame frame = new JFrame("Evolution");
        frame.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        this.sidePanel = new SidePanel(sidePanelSize, this.map, this.createButtonList());
        this.addObserver(this.mapPanel);

        frame.add(this.mapPanel.panel);

        if(this.secondMap != null){

            this.secondMap.addMapStateChangeObserver(this);
            this.secondMapPanel = new MapPanel(mapPanelSize, this.secondMap, speed);

            this.secondSidePanel = new SidePanel(sidePanelSize, this.secondMap, null);
            frame.add(this.secondMapPanel.panel);

            this.addObserver(this.secondMapPanel);
        }

        frame.add(this.sidePanel.panel);
        if(this.secondMap != null)
        frame.add(this.secondSidePanel.panel);

        this.insertSpeedSlider();

        frame.setSize(frameSize.x, frameSize.y);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

    }

    public boolean isPaused(){ return this.pausePressed;}

    private List<JButton> createButtonList(){
        List<JButton> buttonList = new ArrayList<>();

        this.pauseButton = createButton("pause", buttonList);
        this.pauseButton.addActionListener(this::pause);

        this.showDominatingButton = createButton("show dominating", buttonList);
        this.showDominatingButton.addActionListener(e -> showDominating());

        this.saveButton = createButton("save", buttonList);
        this.saveButton.addActionListener(e -> saveToFile());

        return buttonList;
    }

    private JButton createButton(String text, List<JButton> buttonList){
        JButton button = new JButton(text);
        if(this.secondMap!= null) button.setPreferredSize(new Dimension(130,15));
        else button.setPreferredSize(new Dimension(130,20));
        buttonList.add(button);
        return button;
    }

    @Override
    public void onDayEnd() {
        this.sidePanel.displayStatistics();
        this.mapPanel.renderMap();
        if(this.secondMap != null){
            this.secondSidePanel.displayStatistics();
            this.secondMapPanel.renderMap();
        }
    }

    @Override
    public void onAnimalChosen(FoldingMap map) {
        if(map == this.map) this.sidePanel.displayStatisticOfAnimalBeingObserved();
        else this.secondSidePanel.displayStatisticOfAnimalBeingObserved();
    }

    private void pause(ActionEvent actionEvent){
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
        if (fileChooser.showSaveDialog(this.saveButton) == JFileChooser.APPROVE_OPTION) {
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
        if(secondMap!=null) textArea.append("\nFirst map:\n");
        for(String statStr: this.map.stats.getAvgStats())
            textArea.append(statStr+"\n");
        if(secondMap == null) return;
        textArea.append("\nSecond map:\n");
        for(String statStr: this.secondMap.stats.getAvgStats())
            textArea.append(statStr+"\n");
    }

    private void insertSpeedSlider(){
        JSlider speedSlider = new JSlider(JSlider.HORIZONTAL, 0, 200, 20);
        speedSlider.setSize(200, 10);
        speedSlider.addChangeListener(this::changeDelay);
        speedSlider.setMajorTickSpacing(40);
        speedSlider.setMinorTickSpacing(10);
        this.sidePanel.addTextLabel("Change delay:");
        this.sidePanel.panel.add(speedSlider);
    }

    private void changeDelay(ChangeEvent event){
        JSlider source = (JSlider)event.getSource();
        if (!source.getValueIsAdjusting()) {
            int newDelay = (int)source.getValue();
            for(IButtonPressedObserver observer : this.observers) observer.changeDelay(newDelay);
        }
    }

    public void addObserver(IButtonPressedObserver observer){
        this.observers.add(observer);
    }

    public void removeObserver(IButtonPressedObserver observer){
        this.observers.remove(observer);
    }

}