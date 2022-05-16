import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Key extends GameEntity {

    private int code;

    public Key(double x, double y, Map map, double parallax, int code) {
        super(x,y,map,InputAction.Default, FillType.Image, parallax);
        this.code = code;
        this.sizeX = 50;
        this.sizeY = 50;
        this.color = Color.color(1,0.5,0);
        this.image = ImageLoader.key;

    }


    public void collectKey() {
        map.removeEntity(this);
        map.keys.put(Integer.valueOf(code), true);
    }


    public void tick() {


        if(this.intersect(map.player)) {
            collectKey();
        }
    }

    public void render(GraphicsContext g) {
        renderSquare(g);
    }

    public String toString() {
        String line = "key " + (int)x + " " + (int)y + " " + code;

        return line;
    }
}
