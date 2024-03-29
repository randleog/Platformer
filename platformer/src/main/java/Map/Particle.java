package Map;

import Map.Map;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import Util.Settings;
import Main.Main;

public class Particle extends GameEntity {

    public static final double PARTICLE_SPEED = 5.0;

    private int currentTick;

    public boolean gravity;

    private double time;

    private double start;

    public Particle(double x, double y, Map map, double sizeX, double sizeY, Image image, boolean gravity, double time, double start) {
        super(x,y,map, InputAction.Default, FillType.Image, 1);

        this.time = time;

        this.start = start;
        this.gravity=  gravity;
        this.image = image;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.color = Color.color(1,0.5,0);

        currentTick = 0;

    }




    public void tick() {
        if (gravity) {
            gravity();
            physics();
        }
        currentTick++;
        if (Main.interpolate(1,0, Settings.getD("fps") *time,currentTick) <=0.01) {
            map.removeParticle(this);
        }


    }





    public void render(GraphicsContext g) {

        g.save();
        g.setGlobalAlpha(Main.interpolate(start,0, Settings.getD("fps"),currentTick));
        renderSquare(g);
        g.restore();
    }
}
