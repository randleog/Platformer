package Map;

import javafx.scene.canvas.GraphicsContext;
import Util.SoundLoader;
import Util.ImageLoader;

public class BasicEnemy extends GameEntity {

    private static final double AIR_ACCELERATION = 5.0;
    private static final double GROUND_ACCELERATION = 25.0;

    private static final double RUNNING_BOOST = 1.85;

    private static final double SIDE_BOOST = 1;

    private static final double JUMP_SPEED = 6.0;




    private static final double SPRINT_HEIGHT_FACTOR = 0.7;

    private boolean hooking = false;



    private boolean runner;

    private boolean jumper;

    public BasicEnemy(double x, double y, Map map, boolean runner, boolean jumper) {
        super(x, y, map, InputAction.Default, FillType.Image, 1);
        this.runner = runner;
        this.jumper = jumper;
        this.sizeX = 50;
        this.sizeY = 50;
        running = true;
        this.image = ImageLoader.enemy;

        canJump = false;
    }






    public void tick() {

        if (map.player == null) {
            return;
        }

        if (map.player.getMainShape().intersect(this.getMainShape())) {
            map.player.die();
        }
        if (map.isOutOfBounds(this.x, this.y, this.sizeX, this.sizeY)) {
            die();
            System.out.println("me");
        }

        inputActions();

        gravity();

        physics();
        collision();



        if (running) {
            map.addParticleLive(new Particle(x - getVelStretchX()
                    , y - getVelStretchY(), map, sizeX + getVelStretchX() * 2
                    , getSizeY() + getVelStretchY() * 2, image, false
                    , 0.04, 0.2));

        }

    }







    private void inputActions() {

        runningBefore = running;
        if (runner) {
            running = map.isRadius(this.x, this.y, map.playerX, map.playerY, 400);
        } else {
            running = false;
        }

        double baseAccel = 1;
        if (running) {
            baseAccel = baseAccel * RUNNING_BOOST;
        }

        double accel = GROUND_ACCELERATION;
        if (!canJump) {
            accel = AIR_ACCELERATION;
        }

        currentDrag = Map.AIR_DRAG;
        if (canJump) {
            currentDrag = Map.GROUND_DRAG;
            if (jumper) {
                if (map.playerY + this.sizeX * 2 < this.y) {
                    velY = -JUMP_SPEED;

                }
            }
        }
        if (map.playerX > this.x) {
            accelX = (accel ) * baseAccel;
        }

        if (map.playerX < this.x) {
            accelX = (-accel) * baseAccel;
        }


    }


    public void render(GraphicsContext g) {





        renderSquare(g);

    }


    public String toString() {
        String line = "basicEnemy " + (int)x + " " + (int)y + " " + runner + " " + jumper;

        return line;
    }
}
