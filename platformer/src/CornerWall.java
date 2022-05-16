import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.TriangleMesh;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;


public class CornerWall extends GameEntity {

    private double rotation;

    public CornerWall(double x, double y, Map map, double sizeX, double sizeY, InputAction side, FillType fillType, double parallax, double rotation) {
        super(x, y, map, side, fillType, parallax);
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.rotation = rotation;
        this.color = Color.color(1, 0.5, 0);
        this.image = ImageLoader.wallTile;
        if (sizeX < 10 && sizeY < 10) {
            System.out.println("wall is too small");
        }
    }

    public double getRotation() {
        return rotation;
    }


    public void tick() {

    }

    @Override
    public boolean intersect(GameEntity entity) {


        if (rotation == 225) {
            Polygon polygon = new Polygon(new int[]{(int) x, (int) x, (int) x + (int) sizeX}, new int[]{(int) y, (int) y + (int) sizeY, (int) y + (int) sizeY}, 3);

            return polygon.intersects(entity.getX(), entity.getY(), entity.getSizeX(), entity.getSizeY());
        } else {
            Polygon polygon = new Polygon(new int[]{(int) x, (int) x+ (int) sizeX, (int) x+ (int) sizeX}, new int[]{(int) y+ (int) sizeY, (int) y + (int) sizeY, (int) y}, 3);

            return polygon.intersects(entity.getX(), entity.getY(), entity.getSizeX(), entity.getSizeY());
        }



    }

    public void render(GraphicsContext g) {

        if (rotation == 225) {
            g.setFill(new ImagePattern(image, getRenderX(), getRenderY(), map.correctUnit(tileSize) * parallax, map.correctUnit(tileSize) * parallax, false));
            g.fillPolygon(new double[]{getRenderX(), getRenderX(), getRenderX() + map.correctUnit(sizeX)}, new double[]{getRenderY(), getRenderY() + map.correctUnit(sizeY), getRenderY() + map.correctUnit(sizeY)}, 3);

        } else if (rotation == 315) {
            g.setFill(new ImagePattern(image, getRenderX(), getRenderY(), map.correctUnit(tileSize) * parallax, map.correctUnit(tileSize) * parallax, false));
            g.fillPolygon(new double[]{getRenderX(), getRenderX()+ map.correctUnit(sizeX), getRenderX() + map.correctUnit(sizeX)}, new double[]{getRenderY()+ map.correctUnit(sizeY), getRenderY() + map.correctUnit(sizeY), getRenderY()}, 3);
        } else {
            g.save();
            g.translate(getRenderX() + map.correctUnit(sizeX / 2), getRenderY() + map.correctUnit(sizeY / 2));
            g.rotate(rotation);


            renderStill(g);
            g.restore();
        }


    }


    public String toString() {
        String line = "corner " + (int) x + " " + (int) y + " " + (int) sizeX + " " + (int) sizeY + " " + rotation;

        return line;
    }
}
