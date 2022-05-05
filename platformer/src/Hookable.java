import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Hookable extends GameEntity {

    public Hookable(double x, double y, Map map) {
        super(x,y,map,InputAction.Default, FillType.Nothing, 1);
        this.sizeX = 1;
        this.sizeY = 1;
        this.color = Color.color(1,0.5,0);
        this.image = ImageLoader.wallTile;
    }


    public void tick() {

    }

    public void render(GraphicsContext g) {
     //   renderSquare(g);
    }
}
