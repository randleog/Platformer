import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;

public class Particle extends GameEntity {

    private static final double PARTICLE_SPEED = 10.0;

    private int currentTick;

    public Particle(double x, double y, Map map,  double sizeX, double sizeY, InputAction side, double parallax) {
        super(x,y,map,side, FillType.Tile, parallax);
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.color = Color.color(1,0.5,0);
        this.image = ImageLoader.wallTile;
        currentTick = 0;
        velX= Main.random.nextDouble(PARTICLE_SPEED)-PARTICLE_SPEED/2;
        velY = Main.random.nextDouble(PARTICLE_SPEED)-PARTICLE_SPEED;
    }


    public void tick() {
        gravity();
        currentTick++;
        if (Main.interpolate(1,0,Main.FPS,currentTick) <=0.01) {
            map.removeEntity(this);
        }
        physics();

    }

    public void render(GraphicsContext g) {
        double x = map.correctUnit(this.x)-map.correctUnit(map.cameraX*parallax);
        double y = map.correctUnit(this.y)-map.correctUnit(map.cameraY*parallax);
        double sizeX = map.correctUnit(this.sizeX);
        double sizeY = map.correctUnit(this.sizeY);
        g.save();
        g.setGlobalAlpha(Main.interpolate(1,0,Main.FPS,currentTick));
        g.setFill(new ImagePattern(image, x, y, tileSize*parallax,tileSize*parallax, false));
        g.fillRect(x, y, sizeX, sizeY);
        g.restore();
    }
}
