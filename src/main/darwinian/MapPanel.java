package darwinian;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MapPanel {

    Vector2d mapPanelSize;
    private FoldingMap map;
    private MapIcons mapIcons;
    private Map<Vector2d, JLabel> labels = new HashMap<>();
    JPanel mapPanel;
    boolean paused = false;

    public MapPanel(Vector2d mainPanelSize, FoldingMap map) throws IOException {
        this.map = map;
        this.mapPanelSize = mainPanelSize;
        this.mapIcons = new MapIcons(this.mapPanelSize.x / this.map.getWidth(), this.mapPanelSize.y / this.map.getHeight());

        this.labels = new HashMap<>();

        this.mapPanel = new JPanel();
        this.mapPanel.setLayout(new GridLayout(this.map.getHeight(), this.map.getWidth(),0,0));
        this.mapPanel.setSize(this.mapPanelSize.x, this.mapPanelSize.y);


        for (int y = 0; y < map.getHeight(); y++) {
            for (int x = 0; x < map.getWidth(); x++) {
                Vector2d position = new Vector2d(x, y);
                JLabel label = createEmptyLabel();
                label.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        super.mouseClicked(e);
                        if(paused && map.animalsAt(position)!=null) {
                            map.animalBeingObserved =map.animalsAt(position).get(0);
                            map.animalBeingObserved.isSuccessor = true;
                            map.observeDate = map.age;
                            map.numOfSuccessors = 0;
                            map.numOfChildren = 0;
                            System.out.println(map.animalBeingObserved.getPosition());
                        }
                    }
                });
                this.mapPanel.add(label);
                labels.put(position, label);
                if (map.animalsAt(position) != null) {
                    insertAnimal(position);
                } else if (map.plantAt(position) != null) {
                    insertPlant(position);
                } else {
                    emptyLabel(label, position);
                }
            }
        }

    }

    private JLabel createEmptyLabel() throws IOException {
        JLabel label = new JLabel();
        label.setText("");
        label.setOpaque(true);
        label.setForeground(Color.WHITE);
        label.setBackground(Color.GRAY);
        return label;
    }

    private void emptyLabel(JLabel label, Vector2d position) {
        if (this.map.isInsideJungle(position)) label.setIcon(this.mapIcons.jungleIcon);
        else label.setIcon(this.mapIcons.backgroundIcon);
        label.setText("");
    }

    public void insertAnimal(Vector2d position) {
        Animal animal = map.animalsAt(position).get(0);
        ImageIcon catIcon = this.mapIcons.getCatImage(animal.getEnergyLevel(), this.map.isInsideJungle(position));

        JLabel animalLabel = this.labels.get(position);
        animalLabel.setIcon(catIcon);

        animalLabel.setText(""+map.animalsAt(position).get(0).getEnergy());
        animalLabel.setHorizontalTextPosition(JLabel.CENTER);
        animalLabel.setVerticalTextPosition(JLabel.CENTER);
    }

    public void insertPlant(Vector2d position) {
        JLabel plantLabel = this.labels.get(position);
        if (this.map.isInsideJungle(position)) plantLabel.setIcon(this.mapIcons.plantInJungleIcon);
        else plantLabel.setIcon(this.mapIcons.plantIcon);
        plantLabel.setText("");
    }

    public void renderMap() {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (int y = 0; y < map.getHeight(); y++) {
            for (int x = 0; x < map.getWidth(); x++) {

                Vector2d position = new Vector2d(x, y);
                JLabel label = this.labels.get(position);

                if (map.animalsAt(position) != null) {
                    this.insertAnimal(position);
                } else if (map.plantAt(position) != null) {
                    this.insertPlant(position);
                } else {
                    this.emptyLabel(label, position);
                }
            }
        }
    }
}
