import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Player extends GameEntity {


    private static final double AIR_ACCELERATION = 5.0;
    private static final double GROUND_ACCELERATION = 40.0;

    private static final double RUNNING_BOOST = 1.4;

    private static final double SIDE_BOOST = 1;

    private static final double JUMP_SPEED = 800.0;

    private static final double HOOK_FACTOR = 1000;


    private boolean hooking = false;


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
        hook();
        gravity();

        physics();
        collision();


        if (map.cameraMap) {
            map.cameraX = x - startX;
            map.cameraY = y - startY;
            //  x = startX;
        }
        map.addParticleLive(new Particle(x, y, map, sizeX, sizeY, image, false, 0.04, 0.2));
    }


    private void hook() {
        if (hooking) {
            Hookable hookable = map.getHookable();
            if (!(hookable == null)) {
                velY += (hookable.getY() - y) / HOOK_FACTOR;
                velX += (hookable.getY() - x) / HOOK_FACTOR;
            }
        }
    }


    private void inputActions() {

        hooking = Main.isKeyDown(InputAction.Hook);

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


        if (hooking) {
            Hookable hookable = map.getHookable();
            if (!(hookable == null)) {
                g.setStroke(Color.color(0.5, 0.5, 0.5));
                double x = map.correctUnit(this.x) - map.correctUnit(map.cameraX * parallax);
                double y = map.correctUnit(this.y) - map.correctUnit(map.cameraY * parallax);
                double hookX = map.correctUnit(hookable.getX()) - map.correctUnit(map.cameraX * parallax);
                double hookY = map.correctUnit(hookable.getY()) - map.correctUnit(map.cameraY * parallax);

                g.strokeLine(x, y, hookX, hookY);
            }
        }


        renderSquare(g);

    }
}
