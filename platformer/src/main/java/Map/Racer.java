package Map;

import Map.Map;
import javafx.scene.canvas.GraphicsContext;
import Main.Main;

public class Racer extends GameEntity {


    private static final double AIR_ACCELERATION = 5.0;
    private static final double GROUND_ACCELERATION = 25.0;

    private static final double RUNNING_BOOST = 4;

    private static final double SIDE_BOOST = 1;

    private static final double JUMP_SPEED = 6;

    private static final double CROUCH_GRAVITY = 2;


    private static final double SPRINT_HEIGHT_FACTOR = 0.7;

    private static final double MAX_WALL_JUMP_FACTOR = 1.5;

    private boolean hooking = false;



    public Racer(double x, double y, Map map) {
        super(x, y, map, InputAction.Default, FillType.Image, 1);
        this.sizeX = 50;
        this.sizeY = 50;

        map.player = this;
        canJump = false;
        canLeftJump = false;
        canRightJump = false;

    }

    @Override
    public boolean isPlayer() {
        return true;
    }




    public void tick() {

        if (map.isOutOfBounds(this.x, this.y, this.sizeX, this.sizeY)) {
            die();
        }


        inputActions();


        physics();
        collision();


        map.cameraX = x-700;
        map.cameraY = y-500;
        //  x = startX;

        if (running) {
            map.addParticleLive(new Particle(x - getVelStretchX()
                    , y - getVelStretchY(), map, sizeX + getVelStretchX() * 2
                    , getSizeY() + getVelStretchY() * 2, image, false
                    , 0.04, 0.2));

        }

        map.playerX = x;
        map.playerY = y;
    }

    @Override
    public double getSizeY() {
        double sizeBuff = 1;
        if (Main.isKeyDown(InputAction.Down)) {
            sizeBuff = SPRINT_HEIGHT_FACTOR;
        }
        return sizeY * sizeBuff;
    }


    public boolean isHooking() {
        return hooking;
    }


    private void inputActions() {

        hooking = Main.isKeyDown(InputAction.Hook);

        runningBefore = running;
        running = Main.isKeyDown(InputAction.Sprint);



        double baseAccel = 1;
        if (running) {
            baseAccel = baseAccel * RUNNING_BOOST;
        }


        double accel = GROUND_ACCELERATION;


        currentDrag = Map.GROUND_DRAG;

        if (Main.isKeyDown(InputAction.Down) && !Main.isKeyDown(InputAction.Up)) {
            accelY = (accel) * baseAccel;
        }

        if (Main.isKeyDown(InputAction.Up) && !Main.isKeyDown(InputAction.Down)) {
            accelY = (-accel) * baseAccel;
        }
        if (Main.isKeyDown(InputAction.Down) && Main.isKeyDown(InputAction.Up)) {
            accelY = 0;
        }
        if (!Main.isKeyDown(InputAction.Down) && !Main.isKeyDown(InputAction.Up)) {
            accelY = 0;
        }



        if (Main.isKeyDown(InputAction.Right) && !Main.isKeyDown(InputAction.Left)) {
            accelX = (accel) * baseAccel;
        }

        if (Main.isKeyDown(InputAction.Left) && !Main.isKeyDown(InputAction.Right)) {
            accelX = (-accel) * baseAccel;
        }
        if (Main.isKeyDown(InputAction.Left) && Main.isKeyDown(InputAction.Right)) {
            accelX = 0;
        }
        if (!Main.isKeyDown(InputAction.Left) && !Main.isKeyDown(InputAction.Right)) {
            accelX = 0;
        }

    }

    public double getCornerRotation() {
        return cornerRotation;
    }


    public void render(GraphicsContext g) {
        if (cornerRotation != 0) {
            g.save();
            g.translate(getRenderX()+map.correctUnit(sizeX/2),getRenderY()+map.correctUnit(sizeY/2));
            g.rotate(Math.toDegrees(getRenderRotation()));

            renderStill(g);
            g.restore();
        } else {

            renderSquare(g);
        }

    }
}
