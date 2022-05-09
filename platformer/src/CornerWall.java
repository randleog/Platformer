import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;


public class CornerWall extends GameEntity {

    private double rotation;

    public CornerWall(double x, double y, Map map,  double sizeX, double sizeY, InputAction side, FillType fillType, double parallax, double rotation) {
        super(x,y,map,side, fillType, parallax);
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.rotation = rotation;
        this.color = Color.color(1,0.5,0);
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
     //   the projection of EFGH on AB are all negative or all greater than |AB|;
      //  the projection of EFGH on AD are all negative or all greater than |AD|;
    //    the projection of ABCD on EF are all negative or all greater than |EF|;
       // the projection of ABCD on EH are all negative or all greater than |EH|;

        double x = entity.getX();
        double y = entity.getY();
        double sizeX = entity.getSizeX();
        double sizeY = entity.getSizeY();



        AffineTransform at = AffineTransform.getRotateInstance(Math.toRadians(rotation),this.x+this.sizeX/2,this.y+this.sizeY/2);

        Rectangle rect = new Rectangle((int)this.x, (int)this.y, (int)this.sizeX, (int)this.sizeY);

        return at.createTransformedShape(rect).intersects(x,y,sizeX,sizeY);



    }

    public void render(GraphicsContext g) {
        g.save();
        g.translate(getRenderX()+map.correctUnit(sizeX/2),getRenderY()+map.correctUnit(sizeY/2));
        g.rotate(rotation);



        renderStill(g);
        g.restore();
       // renderSquare(g);

    }
}
