package Map;


import GameControl.Square;
import Util.ImageLoader;
import Util.Settings;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.ArrayList;

public class Gear extends GameEntity{

    double gearFactor;
    double gearSpeed;

    double rotation;

    public static final double WALL_CORNER_SIZE = 25.0;

    ArrayList<Gear> neibhbours;

    boolean startSpeed = true;

    double startVel = 0;
    boolean sourcePowered = false;
    private int code;

    public Gear(double x, double y, double sizeX, double sizeY, Map map, double startingSpeed, int code) {
        super(x, y, map, InputAction.Gear, FillType.Image, 1);

        this.code = code;
        startVel = startingSpeed*sizeX;

        rotation = 0;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        image = ImageLoader.gear;
        neibhbours = new ArrayList<>();



        if (startingSpeed > 0) {
            startSpeed = true;
            this.sourcePowered = true;
        }

        this.gearFactor = (1.0/this.sizeX);


        map.keys.put(code, false);
    }






    @Override
    public boolean isWall() {
        return true;
    }

    @Override
    protected void loadHitbox() {
        hitbox = new ArrayList<>();
        hitbox.add(new Square(x, y, sizeX, sizeY, 1, InputAction.Gear));


        double sizeFactor = 6.5;

        double x = this.x+this.sizeX/sizeFactor;
        double y = this.y+this.sizeY/sizeFactor;
        double sizeX = this.sizeX - this.sizeX/(sizeFactor/2);
        double sizeY = this.sizeY - this.sizeY/(sizeFactor/2);



        hitbox.add(new Square(x + WALL_CORNER_SIZE, y + sizeY - 1, sizeX - WALL_CORNER_SIZE * 2, 1, parallax, InputAction.Down));
        if (sizeY > 25) {
            hitbox.add(new Square(x + sizeX - 1, y + WALL_CORNER_SIZE, 1, sizeY - WALL_CORNER_SIZE * 2, parallax, InputAction.Right));
            hitbox.add(new Square(x, y + WALL_CORNER_SIZE, 1, sizeY - WALL_CORNER_SIZE * 2, parallax, InputAction.Left));
        }


    }

    private boolean isActive() {

            return (code == -1 || map.keys.get(this.code));

    }


    @Override
    public void tick() {
        if (map.player == null) {
            return;
        }
        if (this.getMainShape().intersect(map.player.getMainShape())) {
            map.player.health-=(Math.abs((gearSpeed*gearFactor)/ Settings.get("fps")))*5;
        }

        gearSpeed = gearSpeed*Math.pow(0.5, 1/Settings.getD("fps"));
        rotation+=(gearSpeed*gearFactor)/ Settings.get("fps");


        if (isActive()) {
            gearSpeed += startVel / Settings.get("fps");
        }

        if (startVel != 0) {
            return;
        }




        boolean hasSet = false;

        ArrayList<Gear> touching = new ArrayList<>();

        ArrayList<GameEntity> entities = map.getEntities();
        for (GameEntity entity : entities) {
            if (entity instanceof Gear) {
                if (!(entity.equals(this))){

                    Gear gear = (Gear) entity;
                    if (gear.getGearShape().intersect(this.getGearShape())) {
                        touching.add(gear);
                    }
                }

            }
        }


        for (Gear gear : touching) {
            if (!hasSet) {
                if (gear.getGearSpeed() != 0) {

                    this.gearSpeed = -gear.getGearSpeed();
                    gearSpeed = gearSpeed*Math.pow(0.5, 1/Settings.getD("fps"));
                    hasSet = true;

                }

            }
            if (gear.startVel != 0) {

                this.gearSpeed = -gear.getGearSpeed();
                gearSpeed = gearSpeed*Math.pow(0.5, 1/Settings.getD("fps"));
                hasSet = true;

            }
        }


    }


    @Override

    public Square getShape(Square entity) {

        ArrayList<Square> hitbox = new ArrayList<>();
        hitbox.addAll(this.hitbox);

        double moveRange = Math.cos(rotation)*this.sizeX*0.5;

        hitbox.add(new Square(x + WALL_CORNER_SIZE-moveRange/2, y, sizeX - WALL_CORNER_SIZE * 2+moveRange, 1, parallax, InputAction.Up));
        Square lastShape = null;

        for (Square shape : hitbox) {
            if (!InputAction.isUnactionable(shape.getAction())) {
                if (shape.intersect(entity)) {


                    // shape.flag();
                    if (InputAction.isXType(shape.getAction())) {
                        return shape;
                    } else {
                        lastShape = shape;
                    }
                }
            }
        }


        return lastShape;
    }

    public double getRotation() {
        return rotation;
    }

    public double getGearSpeed() {
        return gearSpeed;
    }

    public void setGearSpeed(double gearSpeed) {
        this.gearSpeed = gearSpeed;
    }

    @Override
    public void render(GraphicsContext g) {
        g.save();
        g.translate(getRenderX()+map.correctUnit(sizeX/2),getRenderY()+map.correctUnit(sizeY/2));


        g.rotate(Math.toDegrees((( gearSpeed > 0) ? rotation + 45 : rotation)));

        renderStill(g);
        g.restore();

        if (isActive()) {
            if (startVel > 0) {
                g.save();
                g.setGlobalBlendMode(BlendMode.MULTIPLY);
                g.setFill(Color.color(1,0.5,0, 0.3));
                g.fillRect(getRenderX(), getRenderY(), getRenderSizeX(), getRenderSizeX());
                g.restore();
            }
        }

     //   for (Square shape : hitbox) {
     //       shape.render(g, map.cameraX, map.cameraY, (Player) map.player);
    //    }
/*
        double rotation = this.rotation+Math.toRadians(5);
        double iterations = 45;
        for (int i = 0; i < 8; i++) {
            if (Math.sin(rotation) > 0) {
                new Square(x + Math.cos(rotation + Math.toRadians(i * iterations)) * this.sizeX / 2 + this.sizeX / 2
                        , y + Math.sin(rotation + Math.toRadians(i * iterations)) * this.sizeY / 2 + this.sizeX / 2
                        , 5, 1, parallax, InputAction.Up).render(g, map.cameraX
                        , map.cameraY, (Player) map.player);
            } else {
                new Square(x + Math.cos(rotation + Math.toRadians(i * iterations)) * this.sizeX / 2 + this.sizeX / 2
                        , y + Math.sin(rotation + Math.toRadians(i * iterations)) * this.sizeY / 2 + this.sizeX / 2
                        , 5, 1, parallax, InputAction.Down).render(g, map.cameraX
                        , map.cameraY, (Player) map.player);
            }

 */

        //   new Square(x + Math.cos(rotation+Math.toRadians(i*iterations))* this.sizeX/2 , y + Math.sin(rotation+Math.toRadians(i*iterations))* this.sizeY/2, sizeX/6, 1, parallax, InputAction.Up).render(g, map.cameraX, map.cameraY, (Player) map.player);


    }


    public Square getGearShape() {




        return new Square(x - ((sizeX < 0) ? Math.abs(sizeX) : 0), y - ((sizeY < 0) ? Math.abs(sizeY) : 0), Math.abs(sizeX), Math.abs(getSizeY()), parallax, InputAction.Gear);
    }


    public Square getMainShape() {

        double sizeFactor = 10;

        double x = this.x+this.sizeX/sizeFactor;
        double y = this.y+this.sizeY/sizeFactor;
        double sizeX = this.sizeX - this.sizeX/(sizeFactor/2);
        double sizeY = this.sizeY - this.sizeY/(sizeFactor/2);

        return new Square(x - ((sizeX < 0) ? Math.abs(sizeX) : 0), y - ((sizeY < 0) ? Math.abs(sizeY) : 0), Math.abs(sizeX), Math.abs(getSizeY()), parallax, InputAction.Gear);
    }




    public void scan() {
        //use map to scan map for neighboring gears and add them to the collection, this is then progressively worked through, using their direction of motion to factor in. if even, 0.
    }

    public String toString() {
        String line ="gear " + (int)x + " " + (int)y + " " + (int)sizeX + " " + (int)sizeY + " " + startVel/sizeX + " " + code;

        return line;
    }

}
