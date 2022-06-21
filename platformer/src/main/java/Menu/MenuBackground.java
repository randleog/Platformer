package Menu;

import Main.Main;
import Map.Background;
import Menu.MenuElement;
import Util.BackgroundLoader;

import Util.Settings;
import javafx.scene.canvas.GraphicsContext;

import javafx.scene.paint.Color;

public class MenuBackground extends MenuElement {

    private static final int MAX_X = 999999;

    private Background background;

    private String mapName;

    private double dx,dy;

    private double cameraX;
    private double cameraY;

    double time = 0;


    public MenuBackground(String mapName, double dx, double dy) {
        super(0,0,100,100, mapName, TextType.normal);
        this.mapName = mapName;
        this.dy=dy;
        this.dx=dx;
        cameraX = 0;
        cameraY= 0;

    }

    public void loadBackground() {
        background = BackgroundLoader.loadBackground(mapName);
    }



    public void tick() {
        cameraX+= Main.correctFPS(dx);
        cameraY+=Main.correctFPS(dy);
        if (Settings.get("full speedrun") >0) {
            cameraX+= Main.correctFPS(dx*5);
            cameraY+=Main.correctFPS(dy);
        }
        time+=Main.correctFPS(0.025);



        if (cameraX > MAX_X) {
            dx = -dx;
            cameraX = MAX_X;
        } else if (cameraX < -MAX_X) {
            dx = -dx;
            cameraX = -MAX_X;
        }


    }





    @Override
    public void render(GraphicsContext g) {

        background.renderSky(g,cameraX,time);
        // g.setFill(Color.color(0,0,0,0.5));
        // g.fillRect(0,0,g.getCanvas().getWidth(),g.getCanvas().getHeight());
     //   g.setFill(Color.color(0,0,0,0.4));
       // g.fillRect(0,0,g.getCanvas().getWidth(),g.getCanvas().getHeight());

    }
}
