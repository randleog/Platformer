import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Player extends GameEntity {


    private static final double AIR_ACCELERATION = 15.0;
    private static final double GROUND_ACCELERATION = 15.0;

    private static final double RUNNING_BOOST = 1.4;

    private static final double SIDE_BOOST = 1.3;

    private static final double JUMP_SPEED = 800.0;


    private double startX;
    private double startY;

    public Player(double x, double y, Map map) {
        super(x, y, map, InputAction.Default, FillType.Tile, 1);
        this.sizeX = 50;
        this.sizeY = 50;
        startX = x;
        startY = y;
        canJump = false;
        canLeftJump = false;
        canRightJump = false;
    }

    public void tick() {


        inputActions();
        gravity();
        physics();
        collision();


        if (map.cameraMap) {
            map.cameraX = x-startX ;
            map.cameraY = y-startY ;
            //  x = startX;
        }

    }


    private void inputActions() {

        running = Main.isKeyDown(InputAction.Sprint);

        double baseAccel = 1;
        if (running) {
            baseAccel = baseAccel * RUNNING_BOOST;
        }

        double accel = GROUND_ACCELERATION;
        if (!canJump) {
            accel = AIR_ACCELERATION;
        }


        if (canJump) {
            currentDrag = Map.GROUND_DRAG;
            if (Main.isKeyDown(InputAction.Up)) {
                velY = -JUMP_SPEED / Main.FPS;
                Main.deactivateKey(InputAction.Up);

            }
        } else if (canLeftJump) {
            if (Main.isKeyDown(InputAction.Up)) {
                velY = -JUMP_SPEED / Main.FPS;
                velX = -JUMP_SPEED*SIDE_BOOST*baseAccel / Main.FPS;
                Main.deactivateKey(InputAction.Up);
            }
        } else if (canRightJump) {
            if (Main.isKeyDown(InputAction.Up)) {
                velY = -JUMP_SPEED / Main.FPS;
                velX = JUMP_SPEED*SIDE_BOOST*baseAccel / Main.FPS;
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
