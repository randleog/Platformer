import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Player extends GameEntity {


    private static final double AIR_ACCELERATION = 5.0;
    private static final double GROUND_ACCELERATION = 25.0;

    private static final double RUNNING_BOOST = 1.85;

    private static final double SIDE_BOOST = 1;

    private static final double JUMP_SPEED = 800.0;


    private static final double SPRINT_HEIGHT_FACTOR = 0.7;

    private boolean hooking = false;


    public Player(double x, double y, Map map) {
        super(x, y, map, InputAction.Default, FillType.Image, 1);
        this.sizeX = 50;
        this.sizeY = 50;

        map.player = this;
        canJump = false;
        canLeftJump = false;
        canRightJump = false;

    }


    public void tick() {
        if (map.isOutOfBounds(this.x, this.y, this.sizeX, this.sizeY)) {
            die();
        }

        inputActions();

        gravity();

        physics();
        collision();
        jumpCollision();


        map.cameraX = x - startX;
        map.cameraY = y - startY;
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
        if (!canJump) {
            accel = AIR_ACCELERATION;
        }

        currentDrag = Map.AIR_DRAG;
        if (canJump) {
            currentDrag = Map.GROUND_DRAG;
            if (Main.isKeyDown(InputAction.Up)) {
                velY = -JUMP_SPEED / Main.FPS;
                Main.deactivateKey(InputAction.Up);

            }
        } else if (canLeftJump) {
            if (Main.isKeyDown(InputAction.Up)) {
                velY = -JUMP_SPEED / Main.FPS;
                velX = -JUMP_SPEED * SIDE_BOOST * baseAccel / Main.FPS;
                Main.deactivateKey(InputAction.Up);
            }
        } else if (canRightJump) {
            if (Main.isKeyDown(InputAction.Up)) {
                velY = -JUMP_SPEED / Main.FPS;
                velX = JUMP_SPEED * SIDE_BOOST * baseAccel / Main.FPS;
                Main.deactivateKey(InputAction.Up);

            }
        }
        if (Main.isKeyDown(InputAction.Right) && !Main.isKeyDown(InputAction.Left)) {
            accelX = (accel / Main.FPS) * baseAccel;
        }

        if (Main.isKeyDown(InputAction.Left) && !Main.isKeyDown(InputAction.Right)) {
            accelX = (-accel / Main.FPS) * baseAccel;
        }
        if (Main.isKeyDown(InputAction.Left) && Main.isKeyDown(InputAction.Right)) {
            accelX = 0;
        }
        if (!Main.isKeyDown(InputAction.Left) && !Main.isKeyDown(InputAction.Right)) {
            accelX = 0;
        }

    }


    public void render(GraphicsContext g) {


        renderSquare(g);

    }
}
