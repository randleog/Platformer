package Map;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.ArrayList;

public class Costume {


    private Image right;
    private Image left;

    private ArrayList<Fabric> fabrics;

    private GameEntity entity;

    public Costume(Image left, Image right, GameEntity entity) {

        this.entity = entity;

        this.right = right;
        this.left = left;

        fabrics = new ArrayList<>();

    }

    public void addFabric(Fabric fabric) {
        fabrics.add(fabric);

    }

    public void tick() {


        for (Fabric fabric :fabrics) {
            fabric.tick();
        }
    }

    public void render(GraphicsContext g, boolean facingRight, double xs, double ys) {


        double x = entity.getRenderX();
        double y = entity.getRenderY();
        double sizeX = entity.getRenderSizeX();
        double sizeY = entity.getRenderSizeY();

        if (facingRight) {
            g.drawImage(right, x, y, sizeX, sizeY);
        } else {
            g.drawImage(left, x, y, sizeX, sizeY);
        }


        for (Fabric fabric :fabrics) {
            fabric.render(g, facingRight, xs, ys);
        }
    }

    public void renderStill(GraphicsContext g, boolean facingRight) {


        double sizeX = entity.getRenderSizeX();
        double sizeY = entity.getRenderSizeY();
        if (facingRight) {
            g.drawImage(right, -sizeX / 2, -sizeY / 2, sizeX, sizeY);
        } else {
            g.drawImage(left, -sizeX / 2, -sizeY / 2, sizeX, sizeY);
        }


        for (Fabric fabric :fabrics) {
            fabric.renderStill(g, facingRight, 1);
        }
    }



}
