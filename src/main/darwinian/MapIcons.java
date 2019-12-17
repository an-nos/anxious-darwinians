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

    List<ImageIcon> catIcons;
    List<ImageIcon> catInJungleIcons;


    ImageIcon plantIcon, backgroundIcon, jungleIcon, plantInJungleIcon;

    int width, height;

    public MapIcons(int width, int height) throws IOException {
        this.width = width;
        this.height = height;

        this.catIcons = new ArrayList<>();
        this.catInJungleIcons = new ArrayList<>();

        for(int i=1; i<=8; i++){
            this.catIcons.add(getScaledIcon(ImageIO.read(new File("src\\main\\darwinian\\img\\staticCat"+i+".png"))));
            this.catInJungleIcons.add(getScaledIcon(ImageIO.read(new File("src\\main\\darwinian\\img\\catInJungle"+i+".png"))));
        }

        this.plantIcon = getScaledIcon(ImageIO.read(new File("src\\main\\darwinian\\img\\toast.png")));
        this.backgroundIcon = getScaledIcon(ImageIO.read(new File("src\\main\\darwinian\\img\\background.png")));
        this.jungleIcon = getScaledIcon(ImageIO.read(new File("src\\main\\darwinian\\img\\jungleBackground.png")));
        this.plantInJungleIcon = getScaledIcon(ImageIO.read(new File("src\\main\\darwinian\\img\\toastInJungle.png")));
    }


    ImageIcon getCatImage(int energyLevel, boolean inJungle){
        return inJungle ? this.catInJungleIcons.get(energyLevel-1) : this.catIcons.get(energyLevel-1);
    }

    private ImageIcon getScaledIcon(BufferedImage image){
        return new ImageIcon (image.getScaledInstance(this.width, this.height, Image.SCALE_SMOOTH));
    }
}
