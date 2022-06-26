package Map;

import GameControl.Square;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import Util.Settings;
import Main.Main;

public class ControlledParticle {

    public static final double PARTICLE_SPEED = 5.0;

    private int currentTick;


    private int endTick;

    boolean finished = false;

    private double velY;
    private double velX;
    private Image image;

    private double sizeX;
    private double sizeY;


    private double x;
    private double y;

    private final int SPEED_FACTOR = 144;

    private double opacity;


    private static double GRAVITY = 5;
    private boolean gravity;

    public ControlledParticle(double x, double y, double sizeX, double sizeY, Image image, int endTick, double opacity, boolean gravity) {
        this.gravity = gravity;

        this.endTick = endTick;
        this.opacity = opacity;
        this.velX = 0;
        this.velY = 0;
        this.image = image;
        this.sizeX = sizeX;
        this.sizeY = sizeY;

        this.x =x;
        this.y = y;

        currentTick = 0;

    }


    protected void gravity() {

        velY += GRAVITY / Settings.getD("fps");

    }
    public void setRemove() {
        this.finished = true;
    }


    public void setVelY(double velY) {
        this.velY = velY;
    }

    public void setVelX(double velX) {
        this.velX = velX;
    }



    public Square getMainShape() {
        return new Square(this.x+sizeX/2, this.y+sizeY/2, 1, 1, 1, InputAction.Default);
    }

    protected void physics() {


        //velX = velX * Math.pow(Map.Map.AIR_DRAG, 1.0 / Util.Settings.getD("fps"));


        double vertDrag = Map.BASE_DRAG_Y;


        velY = velY * Math.pow(vertDrag, 1.0 / Settings.getD("fps"));


        x += (velX / Settings.getD("fps")) * SPEED_FACTOR;
        y += (velY / Settings.getD("fps")) * SPEED_FACTOR;
    }


    public void tick() {

        if (gravity) {
            gravity();
        }
        physics();

        currentTick++;

        if (currentTick > endTick) {
            finished = true;
        }


    }



    public double getSize() {
        return sizeX;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }


   public boolean isFinished() {
        return finished;
    }

    public void render(GraphicsContext g, double cameraX, double cameraY) {
        double x = Main.correctUnit(this.x-cameraX);
        double y = Main.correctUnit(this.y-cameraY);


        g.save();

        g.setGlobalBlendMode(BlendMode.ADD);

        g.setGlobalAlpha(0.3);
      //  g.translate(x,y);
       // g.rotate(Math.toDegrees());
        g.drawImage(image, x,y, Main.correctUnit(sizeX), Main.correctUnit(sizeY));



        g.restore();
    }
}
