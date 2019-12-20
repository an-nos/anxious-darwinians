package darwinian;


import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class SwingVisualizer implements IMapStateChangeObserver {
    //TODO: should  visualizer be observer of SidePanel and MapPanel to communicate them??
    private FoldingMap map;
    private FoldingMap secondMap;

    private JFrame frame;
    Vector2d mapPanelSize, sidePanelSize;
    private Vector2d frameSize;
    SidePanel sidePanel, secondSidePanel;
    private MapPanel mapPanel;
    private MapPanel secondMapPanel;

    public SwingVisualizer(FoldingMap map, FoldingMap secondMap) throws IOException {
        this.map = map;
        this.secondMap = secondMap;

        this.mapPanelSize = new Vector2d(440, 440);
        this.sidePanelSize = new Vector2d( 350, this.mapPanelSize.y);
        this.frameSize = new Vector2d(this.mapPanelSize.x+this.sidePanelSize.x+20, this.mapPanelSize.y+38);

        this.map.addMapStateChangeObserver(this);
        this.mapPanel = new MapPanel(this.mapPanelSize, this.map);

        this.frame = new JFrame("Evolution");
        this.frame.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        this.sidePanel = new SidePanel(this.sidePanelSize, this.map, false);
        this.sidePanel.addObserver(this.mapPanel);

        this.frame.add(this.mapPanel.mapPanel);
        this.frame.add(this.sidePanel.sidePanel);

        if(this.secondMap != null){
            this.frameSize = new Vector2d(this.mapPanelSize.x+this.sidePanelSize.x+8, this.mapPanelSize.y*2);
            this.secondMap.addMapStateChangeObserver(this);
            this.secondMapPanel = new MapPanel(this.mapPanelSize, this.secondMap);
            this.frame.add(this.secondMapPanel.mapPanel);
            this.secondSidePanel = new SidePanel(this.sidePanelSize, this.secondMap, true);
            this.frame.add(this.secondSidePanel.sidePanel);
            this.sidePanel.addObserver(this.secondMapPanel);
        }

        this.frame.setSize(this.frameSize.x, this.frameSize.y);
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.setVisible(true);
    }

    @Override
    public void onDayEnd() {
        this.sidePanel.displayStatistics();     //move this to sidePanel
        this.mapPanel.renderMap();              //move this to mapPanel
        if(this.secondMap != null){
            this.secondSidePanel.displayStatistics();
            this.secondMapPanel.renderMap();
        }
    }

    @Override
    public void onAnimalChosen() {
        this.sidePanel.displayStatisticOfAnimalBeingObserved();
    }

}