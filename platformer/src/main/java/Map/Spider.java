package Map;

import Main.Main;
import Util.Settings;
import javafx.scene.canvas.GraphicsContext;
import Util.SoundLoader;
import Util.ImageLoader;
import javafx.scene.paint.Color;

public class Spider extends GameEntity {

    private static final double AIR_ACCELERATION = 5.0;
    private static final double GROUND_ACCELERATION = 25.0;

    private static final double RUNNING_BOOST = 1.85;

    private static final double SIDE_BOOST = 1;

    private static final double JUMP_SPEED = 6.0;




    private static final double SPRINT_HEIGHT_FACTOR = 0.7;

    private boolean hooking = false;



    private boolean runner;

    private boolean jumper;

    private double rotation;

    private Costume costume;

    private MetaballFrame metaballFrame;

    public Spider(double x, double y, Map map, boolean runner, boolean jumper) {
        super(x, y, map, InputAction.Default, FillType.Image, 1);
         metaballFrame = new MetaballFrame(x, y, map, 1, 1);
        this.runner = runner;
        this.jumper = jumper;
        rotation = 0;
        this.sizeX = 30;
        this.sizeY = 30;
        running = true;
        this.image = ImageLoader.enemy;

        canJump = false;


        costume = new Costume(this);

        costume.addFabric(new SpiderLeg(40, this, Color.color(0,0,0), 1, 50, 50, 600));

        costume.addFabric(new SpiderLeg(40, this, Color.color(0,0,0), 1, 50, 0, 600));

        costume.addFabric(new SpiderLeg(40, this, Color.color(0,0,0), 1, 50, -50, 600));


        costume.addFabric(new SpiderLeg(40, this, Color.color(0,0,0), 1, -50, 50, 600));

        costume.addFabric(new SpiderLeg(40, this, Color.color(0,0,0), 1, -50, 0, 600));

        costume.addFabric(new SpiderLeg(40, this, Color.color(0,0,0), 1, -50, -50, 600));


        costume.addFabric(new SpiderLeg(25, this, Color.color(0,0,0), 2, 50, 50, 400));

        costume.addFabric(new SpiderLeg(25, this, Color.color(0,0,0), 2, 50, 0, 400));

        costume.addFabric(new SpiderLeg(25, this, Color.color(0,0,0), 2, 50, -50, 400));


        costume.addFabric(new SpiderLeg(25, this, Color.color(0,0,0), 2, -50, 50, 400));

        costume.addFabric(new SpiderLeg(25, this, Color.color(0,0,0), 2, -50, 0, 400));

        costume.addFabric(new SpiderLeg(25, this, Color.color(0,0,0), 2, -50, -50, 400));


        //costume.addFabric(new SpiderEye(20, this, Color.color(0,0,0), 2, 0, 50));

    }




    private double lastSmoke = 0;
    private static final double SMOKE_TIME = 50;


    public void tick() {
        if (System.currentTimeMillis()-lastSmoke > SMOKE_TIME) {
            metaballFrame.smoke(this.x, this.y, this);
            lastSmoke = System.currentTimeMillis();
        }
        metaballFrame.tick();
        if (running) {
            rotation+= 144/Settings.getD("fps");
        }

      //  costume.alignLegs(map);
        costume.tick();
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
        metaballFrame.render(g);

        double x = getRenderX();
        double y = getRenderY();
        double sizeX = getRenderSizeX();
        double sizeY = getRenderSizeY();



        g.save();
        g.translate(getRenderX()+map.correctUnit(this.sizeX/2),getRenderY()+map.correctUnit(this.sizeY/2));
        costume.renderStill(g, true);
        g.rotate(180);
        costume.renderStill(g, true);
        g.restore();

        if (running) {
            g.drawImage(ImageLoader.eye, x, y, sizeX, sizeY);
        } else {
            g.drawImage(ImageLoader.eyeClosed, x, y, sizeX, sizeY);
        }




    }


    public String toString() {
        String line = "spider " + (int)x + " " + (int)y + " " + runner + " " + jumper;

        return line;
    }
}
