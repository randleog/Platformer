import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Gate extends GameEntity {

    private int code;

    public Gate(double x, double y, Map map,  double sizeX, double sizeY, InputAction side, FillType fillType, double parallax, int code) {
        super(x,y,map,side, fillType, parallax);
        this.code = code;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.color = Color.color(1,0.5,0);
        this.image = ImageLoader.gate;
        if (sizeX < 10 && sizeY < 10) {
            System.out.println("wall is too small");
        }

        loadWallHitbox();
    }

    @Override
    public boolean isWall() {
        return true;
    }

    public void tick() {
        if (map.keys.keySet().contains(Integer.valueOf(code))) {
            map.removeEntity(this);
        }

    }

    public void render(GraphicsContext g) {

        renderSquare(g);

    }

    public String toString() {
        String line = "gate " + (int)x + " " + (int)y + " " + (int)sizeX + " " + (int)sizeY + " " + code;

        return line;
    }
}
