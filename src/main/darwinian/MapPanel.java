package darwinian;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class MapPanel implements IButtonPressedObserver {

    private Vector2d size;
    private FoldingMap map;
    private MapIcons mapIcons;
    private Map<Vector2d, JLabel> labels;
    JPanel panel;
    private int delay;
    boolean paused = false;

    public MapPanel(Vector2d mainPanelSize, FoldingMap map, int delay) throws IOException {
        this.map = map;
        this.size = mainPanelSize;
        this.delay = delay;
        this.mapIcons = new MapIcons(this.size.x / this.map.getWidth(), this.size.y / this.map.getHeight());

        this.labels = new HashMap<>();

        this.panel = new JPanel();
        this.panel.setLayout(new GridLayout(this.map.getHeight(), this.map.getWidth(),0,0));
        this.panel.setSize(this.size.x, this.size.y);
        this.panel.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));

        for (int y = 0; y < map.getHeight(); y++) {
            for (int x = 0; x < map.getWidth(); x++) {

                Vector2d position = new Vector2d(x, y);

                JLabel label = createEmptyLabel();
                this.addAnimalChosenListener(label, position);
                this.panel.add(label);
                labels.put(position, label);

                updateIconOnPosition(position);
            }
        }
    }

    private void addAnimalChosenListener(JLabel label, Vector2d position){
        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if(paused && map.animalsAt(position)!=null) {
                    Vector2d oldChosenPosition = null;
                    if(map.hasChosenAnimal()) oldChosenPosition = map.getChosenAnimal().getPosition();
                    map.chooseAnimal(position);
                    insertAnimal(position);
                    if(oldChosenPosition!= null){
                        if(map.animalsAt(oldChosenPosition) != null) insertAnimal(oldChosenPosition);
                        else if(map.plantAt(oldChosenPosition) != null) insertPlant(oldChosenPosition);
                        else emptyLabel(oldChosenPosition);
                    }
                }
            }
        });
    }

    private JLabel createEmptyLabel() throws IOException {
        JLabel label = new JLabel();
        label.setSize(this.size.x / this.map.getWidth(), this.size.y / this.map.getHeight());
        label.setText("");
        label.setOpaque(true);
        label.setForeground(Color.WHITE);
        label.setBackground(Color.GRAY);
        return label;
    }

    private void updateIconOnPosition(Vector2d position){
        if (map.animalsAt(position) != null) insertAnimal(position);
        else if (map.plantAt(position) != null) insertPlant(position);
        else emptyLabel(position);
    }

    private void emptyLabel(Vector2d position) {
        JLabel label = this.labels.get(position);
        if (this.map.isInsideJungle(position)) label.setIcon(this.mapIcons.getJungleIcon());
        else label.setIcon(this.mapIcons.getBackgroundIcon());
        label.setText("");
    }

    private void insertAnimal(Vector2d position) {
        Animal animal = map.animalsAt(position).get(0);
        ImageIcon catIcon = this.mapIcons.getCatImage(animal.getEnergyLevel(), this.map.isInsideJungle(position));

        if(this.map.hasChosenAnimal() && map.animalsAt(position).contains(this.map.getChosenAnimal().animal))
            catIcon = this.mapIcons.getTacImage(animal.getEnergyLevel());

        JLabel animalLabel = this.labels.get(position);
        animalLabel.setIcon(catIcon);
    }

    private void insertPlant(Vector2d position) {
        JLabel plantLabel = this.labels.get(position);
        if (this.map.isInsideJungle(position)) plantLabel.setIcon(this.mapIcons.getPlantInJungleIcon());
        else plantLabel.setIcon(this.mapIcons.getPlantIcon());
    }

    private void insertTacGrave(Vector2d position){
        this.labels.get(position).setIcon(this.mapIcons.getTacGraveIcon());
    }

    private void insertKindred(List<Animal> kindred){
        for(Animal animal : kindred){
            this.labels.get(animal.getPosition()).setIcon(this.mapIcons.getKindredIcon());
        }
    }

    void renderMap() {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (int y = 0; y < map.getHeight(); y++) {
            for (int x = 0; x < map.getWidth(); x++) {

                Vector2d position = new Vector2d(x, y);
                if(map.hasChosenAnimal() && map.getChosenAnimal().getPosition().equals(position) && map.getChosenAnimal().isDead())
                    this.insertTacGrave(position);
                else updateIconOnPosition(position);

            }
        }
    }

    @Override
    public void changeDelay(int newDelay){
        this.delay = newDelay;
    }

    @Override
    public void pausePressed() {
        if(!this.paused) this.paused = true;
        else this.paused = false;
    }

    @Override
    public void showDominatingPressed() {
        this.insertKindred(map.getSuperiorRace());
    }

}
