package Map;

import Map.Map;
import Menu.Menu;
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

    public void setTpX(double x) {
        this.tpX = x;
    }

    public void setTpY(double y) {
        this.tpY = y;
    }


    public void tick() {
        if (map.player == null) {
            return;
        }

        if (this.getMainShape().intersect(map.player.getMainShape())) {
            map.trance();
            map.movePlayer(tpX, tpY);
            if (map.player instanceof  Player) {
                ((Player) map.player).smoke();
            }



        }
    }

    public void render(GraphicsContext g) {

        renderSquare(g);

        if (Menu.currentMenu.equals("editor")) {
            g.fillText("Destination", Main.Main.correctUnit(tpX-map.cameraX), Main.Main.correctUnit(tpY-map.cameraY));
        }
    }

    public String toString() {
        return "dimension " + (int)this.x + " " + (int)this.y + " " + (int)tpX + " " + (int)tpY;
    }
}
