package darwinian;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class MapIcons {

    private List<ImageIcon> catIcons, catInJungleIcons, tacIcons;

    private ImageIcon plantIcon, backgroundIcon, jungleIcon, plantInJungleIcon, tacGraveIcon, kindredIcon;

    private int width, height;

    public MapIcons(int width, int height) throws IOException {
        this.width = width;
        this.height = height;

        this.catIcons = new ArrayList<>();
        this.catInJungleIcons = new ArrayList<>();
        this.tacIcons = new ArrayList<>();

        for(int i=1; i<=8; i++){
            this.catIcons.add(getScaledIcon(ImageIO.read(new File("src\\main\\resources\\img\\cat"+i+".png"))));
            this.catInJungleIcons.add(getScaledIcon(ImageIO.read(new File("src\\main\\resources\\img\\catInJungle"+i+".png"))));
            this.tacIcons.add(getScaledIcon(ImageIO.read(new File("src\\main\\resources\\img\\tac"+i+".png"))));
        }

        this.plantIcon = getScaledIcon(ImageIO.read(new File("src\\main\\resources\\img\\toast.png")));
        this.backgroundIcon = getScaledIcon(ImageIO.read(new File("src\\main\\resources\\img\\background.png")));
        this.jungleIcon = getScaledIcon(ImageIO.read(new File("src\\main\\resources\\img\\jungleBackground.png")));
        this.plantInJungleIcon = getScaledIcon(ImageIO.read(new File("src\\main\\resources\\img\\toastInJungle.png")));
        this.tacGraveIcon = getScaledIcon(ImageIO.read(new File("src\\main\\resources\\img\\tombstone.png")));
        this.kindredIcon = getScaledIcon(ImageIO.read(new File("src\\main\\resources\\img\\kindred.png")));
    }


    ImageIcon getCatImage(int energyLevel, boolean inJungle){
        return inJungle ? this.catInJungleIcons.get(energyLevel-1) : this.catIcons.get(energyLevel-1);
    }

    ImageIcon getTacImage(int energyLevel){
        return this.tacIcons.get(energyLevel-1);
    }

    ImageIcon getTacGraveIcon(){
        return this.tacGraveIcon;
    }

    ImageIcon getKindredIcon(){ return this.kindredIcon; }

    ImageIcon getBackgroundIcon(){ return this.backgroundIcon; }

    ImageIcon getPlantIcon(){ return this.plantIcon; }

    ImageIcon getJungleIcon(){ return this.jungleIcon; }

    ImageIcon getPlantInJungleIcon(){ return this.plantInJungleIcon; }

    private ImageIcon getScaledIcon(BufferedImage image){
        return new ImageIcon (image.getScaledInstance(this.width, this.height, Image.SCALE_SMOOTH));
    }
}
