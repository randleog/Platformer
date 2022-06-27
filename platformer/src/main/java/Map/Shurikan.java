package Map;

import GameControl.Square;
import Main.Main;
import Map.Map;
import Util.Settings;
import Util.SoundLoader;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import Util.ImageLoader;
import Menu.Menu;
import jdk.jfr.Percentage;

import java.util.ArrayList;

public class Shurikan extends GameEntity {

    double rotation;

    private static final double ROTATION_SPEED = 30;

    private static final double MAX_TIME = 2000;
    private double originTime;

    private boolean stuck;



    public Shurikan(double x, double y, Map map) {
        super(x,y,map, InputAction.Shurikan, FillType.Image, 1);
        this.sizeX = 25;
        this.sizeY = 25;
        this.color = Color.color(1,0.5,0);
        this.image = ImageLoader.shurikan;
        rotation = 0;
        originTime = System.currentTimeMillis();
        stuck = false;
    }

    @Override
    public Square getMainShape() {


        return new Square(this.x, this.y,this.sizeX/1.5, sizeY/1.5, 1, action);
    }


    @Override
    public Square getShape(Square entity) {
        return getMainShape();
    }





    public void tick() {


        Shurikan speed = new Shurikan(this.x, this.y, map);
        speed.setVelX(this.velX/5);
        speed.setVelY(this.velX/5);

        ArrayList<InputAction> actions = map.getActions(this);

        if (actions.contains(InputAction.Swim) || actions.contains(InputAction.Lava)) {
            if (!swimming) {

                map.splash(speed);

            }
            swimming = true;
        } else {
            swimming = false;
        }



        if (!InputAction.containsWall(actions)) {
            rotation+=ROTATION_SPEED/Settings.getD("fps");

            x += (velX / Settings.getD("fps")) * SPEED_FACTOR;
            y += (velY / Settings.getD("fps")) * SPEED_FACTOR;


        } else {
            if (!stuck) {
                map.crashParticle(this.x, this.y);
                SoundLoader.playSound(SoundLoader.fall, 0.5, 0, SoundLoader.getRandomSpeed() * 0.6);
            }
            stuck = true;


            if (map.player == null) {
                return;
            }

            if (map.player.getMainShape().intersect(this.getMainShape())) {
                if (map.player instanceof Player) {
                    ((Player) (map.player)).increaseShurikans();
                }
                map.removeEntity(this);
            }
        }








    }

    public void render(GraphicsContext g) {

        g.save();
        g.translate(getRenderX()+map.correctUnit(sizeX/2),getRenderY()+map.correctUnit(sizeY/2));
        g.rotate(Math.toDegrees(rotation));
        g.drawImage(image, -Main.correctUnit(this.sizeX)/2,-Main.correctUnit(this.sizeY)/2, Main.correctUnit(this.sizeX), Main.correctUnit(this.sizeY));

        g.restore();

    }


    public String toString() {
        return "shurikan " + (int)this.x + " " + (int)this.y;
    }



}
