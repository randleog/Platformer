package Map;

import GameControl.Square;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;

import java.util.ArrayList;
import Util.ImageLoader;
import Main.Main;

public class CornerWall extends GameEntity {

    private double rotation;

    public CornerWall(double x, double y, Map map, double sizeX, double sizeY, InputAction side, FillType fillType, double parallax, double rotation) {
        super(x, y, map, side, fillType, parallax);
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.rotation = rotation;
        this.color = Color.color(1, 0.5, 0);
        this.image = ImageLoader.wallTile;

        loadHitbox();
    }

    @Override
    public boolean isWall() {
        return true;
    }


    public double getRotation() {
        return rotation;
    }


    public void tick() {

        if (changed) {
            loadHitbox();
        }

    }

    public void setRotation(double rotation) {
        this.rotation = rotation;
    }

    @Override
    protected void loadHitbox() {
        hitbox = new ArrayList<>();
        hitbox.add(new Square(x , y, sizeX, sizeY, parallax, InputAction.Corner, rotation));
        changed = false;
       // hitbox.add(new Shape(x + sizeX, y + WALL_CORNER_SIZE, 1, sizeY - WALL_CORNER_SIZE * 2, parallax, Map.InputAction.Right, rotation));
     //   hitbox.add(new Shape(x + WALL_CORNER_SIZE, y + sizeY - WALL_CORNER_SIZE-1, sizeX - WALL_CORNER_SIZE * 2, 1, parallax, Map.InputAction.Down, rotation));
     //   hitbox.add(new Shape(x, y + WALL_CORNER_SIZE, 1, sizeY - WALL_CORNER_SIZE * 2, parallax, Map.InputAction.Left, rotation));

    }




    public void render(GraphicsContext g) {


        double x = getRenderX();
        double y = getRenderY();
        g.setFill(new ImagePattern(image, getRenderX(), getRenderY(), map.correctUnit(tileSize) * parallax, map.correctUnit(tileSize) * parallax, false));
        if (rotation == 225) {

            g.fillPolygon(new double[]{x, x, x + Main.correctUnit(sizeX)}, new double[]{y, y + Main.correctUnit(sizeY), y + Main.correctUnit(sizeY)}, 3);

        } else if (rotation == 315){

            g.fillPolygon(new double[]{x, x+ Main.correctUnit(sizeX), x + Main.correctUnit(sizeX)}, new double[]{y+ Main.correctUnit(sizeY), y + Main.correctUnit(sizeY), y}, 3);

        }else if (rotation == 405) {

            g.fillPolygon(new double[]{x, x, x + Main.correctUnit(sizeX)}, new double[]{y + Main.correctUnit(sizeY), y , y}, 3);
        }else if (rotation == 495) {

            g.fillPolygon(new double[]{x, x + Main.correctUnit(sizeX), x + Main.correctUnit(sizeX)}, new double[]{y, y, y+ Main.correctUnit(sizeY)}, 3);
        }
        for (Square shape : hitbox) {
            shape.render(g, map.cameraX, map.cameraY, (Player)map.player);
        }



    }


    public String toString() {
        String line = "corner " + (int) x + " " + (int) y + " " + (int) sizeX + " " + (int) sizeY + " " + rotation;

        return line;
    }
}
