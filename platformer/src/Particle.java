import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;

public class Particle extends GameEntity {

    public static final double PARTICLE_SPEED = 10.0;

    private int currentTick;

    public boolean gravity;

    private double time;

    public Particle(double x, double y, Map map, double sizeX, double sizeY, Image image, boolean gravity, double time) {
        super(x,y,map,InputAction.Default, FillType.Image, 1);

        this.time = time;

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
        if (Main.interpolate(1,0,Main.FPS*time,currentTick) <=0.01) {
            map.removeParticle(this);
        }


    }

    public void render(GraphicsContext g) {

        g.save();
        g.setGlobalAlpha(Main.interpolate(0.2,0,Main.FPS,currentTick));
        renderSquare(g);
        g.restore();
    }
}
