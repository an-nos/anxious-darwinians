package darwinian;


import javax.swing.*;
import java.awt.*;
import java.io.IOException;



public class SwingVisualizer implements IMapStateChangeObserver {
    //TODO: should  visualizer be observer of SidePanel and MapPanel to communicate them??
    private FoldingMap map;
    private JFrame frame;
    public int menuWidth = 400, menuHeight;
    public int mapPanelWidth, mapPanelHeight;
    SidePanel sidePanel;
    private MapPanel mapPanel;

    public SwingVisualizer(FoldingMap map) throws IOException {
        this.map = map;
        this.map.addRemoveObserver(this);

        this.mapPanelWidth = this.map.getWidth()*40;
        this.mapPanelHeight = this.map.getHeight()*40;
        this.menuHeight = this.mapPanelHeight;

        this.mapPanel = new MapPanel(new Vector2d(this.mapPanelWidth, this.mapPanelHeight), this.map);

        this.frame = new JFrame("Evolution");
        this.frame.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));

        this.sidePanel = new SidePanel(this.menuWidth, this.menuHeight, this.map);

        this.frame.add(this.mapPanel.mapPanel);
        this.frame.add(this.sidePanel.sidePanel);
        this.frame.setSize(this.mapPanel.mapPanelSize.x+menuWidth, this.mapPanel.mapPanelSize.y);
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.setVisible(true);
    }

    @Override
    public void onDayEnd() {
        this.sidePanel.displayStatistics();
        this.mapPanel.renderMap();
        this.mapPanel.paused = this.sidePanel.pausePressed;
    }
}