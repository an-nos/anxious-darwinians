package darwinian;


import javax.swing.*;
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

    private JFrame frame;
    private Vector2d mapPanelSize, sidePanelSize;
    private Vector2d frameSize;
    private SidePanel sidePanel, secondSidePanel;
    private MapPanel mapPanel;
    private MapPanel secondMapPanel;
    private JPanel buttonPanel;
    private JPanel statsPanel;
    private int speed;
    boolean pausePressed;

    private JButton pauseButton, showDominatingButton, saveButton;

    private List<IButtonPressedObserver> observers;



    public SwingVisualizer(FoldingMap map, FoldingMap secondMap) throws IOException {
        this.map = map;
        this.secondMap = secondMap;
        this.observers = new ArrayList<>();

        this.statsPanel = new JPanel();
        this.statsPanel.setLayout(new BoxLayout(this.statsPanel, BoxLayout.Y_AXIS));

        if(this.secondMap == null){
            this.mapPanelSize = new Vector2d(600, 600);
            this.sidePanelSize = new Vector2d(430, 600);
            this.frameSize = new Vector2d(this.mapPanelSize.x+this.sidePanelSize.x, this.mapPanelSize.y+40);
        }
        else{
            this.mapPanelSize = new Vector2d(400, 400);
            this.sidePanelSize = new Vector2d( 400, 320);
            this.frameSize = new Vector2d(this.mapPanelSize.x*2+24, this.mapPanelSize.y+this.sidePanelSize.y);
        }

        this.speed = 10;
        if(secondMap != null) this.speed/=2;
        this.map.addMapStateChangeObserver(this);
        this.mapPanel = new MapPanel(this.mapPanelSize, this.map, this.speed);


        this.frame = new JFrame("Evolution");
        this.frame.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        this.sidePanel = new SidePanel(this.sidePanelSize, this.map, this.createButtonList());
        this.addObserver(this.mapPanel);

        this.frame.add(this.mapPanel.panel);

        if(this.secondMap != null){

            this.secondMap.addMapStateChangeObserver(this);
            this.secondMapPanel = new MapPanel(this.mapPanelSize, this.secondMap, this.speed);

            this.secondSidePanel = new SidePanel(this.sidePanelSize, this.secondMap, null);
            this.frame.add(this.secondMapPanel.panel);

            this.addObserver(this.secondMapPanel);
        }

        this.frame.add(this.sidePanel.panel);
        if(this.secondMap != null)
        this.frame.add(this.secondSidePanel.panel);


        this.frame.setSize(this.frameSize.x, this.frameSize.y);
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.setVisible(true);

    }

    private List<JButton> createButtonList(){
        List<JButton> buttonList = new ArrayList<>();
        this.pauseButton = new JButton("pause");
        this.pauseButton.addActionListener(this::pause);
        if(this.secondMap!= null) this.pauseButton.setPreferredSize(new Dimension(130,15));
        else this.pauseButton.setPreferredSize(new Dimension(130,20));
        buttonList.add(this.pauseButton);

        this.showDominatingButton = new JButton("show dominating");
        this.showDominatingButton.addActionListener(e -> showDominating());
        this.showDominatingButton.setPreferredSize(new Dimension(130,15));
        if(this.secondMap!= null) this.showDominatingButton.setPreferredSize(new Dimension(130,15));
        else this.showDominatingButton.setPreferredSize(new Dimension(130,25));

        buttonList.add(this.showDominatingButton);

        this.saveButton = new JButton("save");
        this.saveButton.addActionListener(e -> saveToFile());
        if(this.secondMap!= null) this.saveButton.setPreferredSize(new Dimension(130,15));
        else this.saveButton.setPreferredSize(new Dimension(130,25));
        buttonList.add(this.saveButton);

        return buttonList;
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
        if(secondMap!=null) textArea.append("\nFirst map:\n");
        for(String statStr: this.map.stats.getAvgStats())
            textArea.append(statStr+"\n");
        if(secondMap == null) return;
        textArea.append("\nSecond map:\n");
        for(String statStr: this.secondMap.stats.getAvgStats())
            textArea.append(statStr+"\n");

    }
        public void addObserver(IButtonPressedObserver observer){
        this.observers.add(observer);
    }

    public void removeObserver(IButtonPressedObserver observer){
        this.observers.remove(observer);
    }

}