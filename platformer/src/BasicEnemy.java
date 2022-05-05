import javafx.scene.canvas.GraphicsContext;

public class BasicEnemy extends GameEntity {

    private static final double AIR_ACCELERATION = 5.0;
    private static final double GROUND_ACCELERATION = 25.0;

    private static final double RUNNING_BOOST = 1.85;

    private static final double SIDE_BOOST = 1;

    private static final double JUMP_SPEED = 800.0;




    private static final double SPRINT_HEIGHT_FACTOR = 0.7;

    private boolean hooking = false;



    private boolean runner;

    public BasicEnemy(double x, double y, Map map, boolean runner) {
        super(x, y, map, InputAction.Default, FillType.Image, 1);
        this.runner = runner;
        this.sizeX = 50;
        this.sizeY = 50;
        running = true;
        this.image = ImageLoader.enemy;

        canJump = false;
    }






    public void tick() {
        if (map.player.intersect(this)) {
            map.player.die();
        }
        if (map.isOutOfBounds(this.x, this.y)) {
            die();
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
            if (map.playerY+this.sizeX < this.y) {
                velY = -JUMP_SPEED / Main.FPS;

            }
        }
        if (map.playerX > this.x) {
            accelX = (accel / Main.FPS) * baseAccel;
        }

        if (map.playerX < this.x) {
            accelX = (-accel / Main.FPS) * baseAccel;
        }


    }


    public void render(GraphicsContext g) {





        renderSquare(g);

    }
}
