package Map;

import Map.Map;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import Util.ImageLoader;


public class DimensionPortal extends GameEntity {



   private  double tpX;
    private double tpY;

    public DimensionPortal(double x, double y, Map map, double tpX, double tpY) {
        super(x,y,map, InputAction.Default, FillType.Image, 1);
        this.sizeX = 75;
        this.sizeY = 75;

        this.color = Color.color(1,0.5,0);

        this.tpX = tpX;
        this.tpY = tpY;
        this.image = ImageLoader.dimension;

    }


    public void tick() {
        if (map.player == null) {
            return;
        }

        if (this.getMainShape().intersect(map.player.getMainShape())) {
            map.removeEntity(this);
            map.trance();
            map.movePlayer(tpX, tpY);



        }
    }

    public void render(GraphicsContext g) {
        renderSquare(g);
    }
}
