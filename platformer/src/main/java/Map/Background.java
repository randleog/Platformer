package Map;

import Main.Main;
import Util.ImageLoader;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;

import java.util.ArrayList;

public class Background {

    private static final int DAY_LENGTH = 300;


    private double time = 0;


    private ArrayList<BackgroundLayer> layers;


    public Background() {
        time = 0;




        layers = new ArrayList<>();

    }

    public void addLayer(Image image, double parallax) {
        layers.add(new BackgroundLayer(parallax, image));

    }


    /**
     * returns the current day time 0-1
     * @return
     */
    public double getDayTime() {
        double time = -(this.time+(DAY_LENGTH/2.0) % DAY_LENGTH);


        return (time/(DAY_LENGTH*1.0));
    }

    /**
     * returns the current day time 0-1
     * @return
     */
    private double getSunBi() {
        return Math.sin((getDayTime()*Math.PI)*2);
    }
    /**
     * returns the current day time 0-1
     * @return
     */
    private double getSunBiCos() {
        return Math.cos((getDayTime()*Math.PI)*2);
    }


    private double getSun() {
        return Math.cos((getDayTime()*Math.PI));
    }

    private double getSunSin() {
        return Math.sin((getDayTime()*Math.PI));
    }

    public void renderSky(GraphicsContext g, double cameraX, double time) {
        double redFactor = Math.pow(Math.abs(getSunSin()),2);
        double red = (1-redFactor)*0.6;
        double extraGreen = (1-redFactor)*0.3;
        double opacity = Math.pow(Math.abs(getSunSin()), 0.5);
     //   if (getSunSin() > -0.9 && getSunSin() < 0.9) {
     //       red = 1;
     //   }



        this.time = time;

        g.setFill(Color.color(red,0.6*redFactor,1*redFactor+extraGreen, opacity));
        g.fillRect(0,0, Main.correctUnit(Main.DEFAULT_WIDTH_MAP), Main.correctUnit(Main.DEFAULT_HEIGHT_MAP));



        g.setFill(Color.color(1,0.72,0.2));
        g.fillRect(getSunBi()*Main.correctUnit(Main.DEFAULT_WIDTH_MAP)/2+Main.correctUnit(Main.DEFAULT_WIDTH_MAP)/2, getSunBiCos()*Main.correctUnit(Main.DEFAULT_HEIGHT_MAP)/2+Main.correctUnit(Main.DEFAULT_HEIGHT_MAP)/2, Main.correctUnit(100),Main.correctUnit(100));



        for (int i = 0; i < layers.size(); i++) {
            layers.get(i).render(g, cameraX);
        }




    }




}
