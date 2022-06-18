package Map;

import Map.Map;
import javafx.scene.canvas.GraphicsContext;
import Main.Main;
import Util.Settings;
import Util.Stats;

//edits = 1
public class Player extends GameEntity {

    private static final double AIR_ACCELERATION = 5.0;

    private static final double GROUND_ACCELERATION = 25.0;
    //private static final double SWIMMING_ACCELERATION = 25.0;

    private static final double RUNNING_BOOST = 1.85;

    private static final double SIDE_BOOST = 1;

    private static final double JUMP_SPEED = 6;

    private static final double SWIM_SPEED = 10;

    private static final double CROUCH_GRAVITY = 2;

    private static final double CLING_GRAVITY = 0.05;

    private static final double SPRINT_HEIGHT_FACTOR = 0.7;

    private static final double MAX_WALL_JUMP_FACTOR = 1.5;

    private double health;

    private boolean hooking = false;



    public Player(double x, double y, Map map) {
        super(x, y, map, InputAction.Default, FillType.Image, 1);
        this.sizeX = 50;
        this.sizeY = 50;

        map.player = this;
        canJump = false;
        canLeftJump = false;
        canRightJump = false;


        health = 10;
    }


    @Override
    public boolean isPlayer() {
        return true;
    }

    @Override
    protected void gravity() {

        double force = map.getGravity();

        if (Main.isKeyDown(InputAction.Down)) {
            force = force*CROUCH_GRAVITY  ;
        }

        if (clinging) {
            force = -force;
        }

        if (swimming) {
            force = force*SWIMMING_GRAVITY;
        }

        velY+=force/ Settings.getD("fps");


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

    @Override
    public double getVelStretchY() {
        if (clinging) {
            return (Math.sqrt(Math.abs(velY)) - Math.sqrt(Math.abs(velX))) * SQUASH_FACTOR;
        } else {
            return super.getVelStretchY();
        }

    }


    @Override
    public double getVelStretchX() {

        if (clinging) {
            return (Math.sqrt(Math.abs(velX)) - Math.sqrt(Math.abs(velY))) * SQUASH_FACTOR-Math.sqrt(wallCling)*SQUASH_FACTOR;
        } else {
            return super.getVelStretchX();
        }


    }

    public double getRenderX() {
        double x = map.correctUnit(this.x - getVelStretchX()) - map.correctUnit(map.cameraX * parallax);


        return x;

    }


    @Override
    public double getRenderY() {

        if (clinging) {
            return map.correctUnit(wallHeight)- map.correctUnit(map.cameraY * parallax);
        }

        double y = map.correctUnit(this.y - getVelStretchY()) - map.correctUnit(map.cameraY * parallax);

        return y;

    }


    @Override
    public double getRenderSizeY() {
        double sizeY = map.correctUnit(getSizeY() + getVelStretchY() * 2);

        if (clinging) {
            sizeY = sizeY+map.correctUnit(wallCling);
        }
        return sizeY;
    }

    public boolean isHooking() {
        return hooking;
    }


    private void inputActions() {

        boolean hasJumped = false;

        hooking = Main.isKeyDown(InputAction.Hook);

        runningBefore = running;
        running = Main.isKeyDown(InputAction.Sprint);



        double baseAccel = 1;
        if (running) {
            baseAccel = baseAccel * RUNNING_BOOST;
        }


        double accel = GROUND_ACCELERATION;
        if (!(canJump || (canCornerJump && !Main.isKeyDown(InputAction.Down)))) {
            accel = AIR_ACCELERATION;
        }

        if (clinging && Main.isKeyDown(InputAction.Up)) {
            velY = JUMP_SPEED;
            Main.deactivateKey(InputAction.Up);
        }

        currentDrag = Map.AIR_DRAG;
        if (canJump) {
            currentDrag = Map.GROUND_DRAG;
            if (Main.isKeyDown(InputAction.Up)) {
                hasJumped = true;
                velY = -JUMP_SPEED;


            }
        } else if (canLeftJump) {
            if (Main.isKeyDown(InputAction.Up)) {

                velY = -JUMP_SPEED;

                velX = -JUMP_SPEED * SIDE_BOOST * baseAccel;
                hasJumped = true;
            }
        } else if (canRightJump) {
            if (Main.isKeyDown(InputAction.Up)) {
                velX = JUMP_SPEED * SIDE_BOOST * baseAccel;
                velY = -JUMP_SPEED ;

                hasJumped = true;

            }
        }else if (canCornerJump) {
            if (!Main.isKeyDown(InputAction.Down)) {
                currentDrag = Map.GROUND_DRAG;
            }
            if (Main.isKeyDown(InputAction.Up)) {


                if (Main.isKeyDown(InputAction.Down)) {
                    velX = -Math.cos(cornerRotation) *JUMP_SPEED * SIDE_BOOST * baseAccel;
                    velY = Math.sin(cornerRotation) *JUMP_SPEED * SIDE_BOOST * baseAccel;
                } else {
                    velY = -JUMP_SPEED;
                }


                hasJumped = true;

            }
        }

        if (stuck) {
            currentDrag = currentDrag*STUCK_FACTOR;
        }



        if (hasJumped) {
            Main.deactivateKey(InputAction.Up);
       //     Util.SoundLoader.playSound(Util.SoundLoader.fall, 1, 0, Util.SoundLoader.getRandomSpeed()*1.1);
            Stats.add("total Jumps", 1);
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

      //  body[0].render(g, getRenderX(), getRenderY(), Main.Main.correctUnit(this.sizeX));
      //  g.drawImage(Util.ImageLoader.legs1, getRenderX(), getRenderY()+Main.Main.correctUnit(25), Main.Main.correctUnit(this.sizeX*2), Main.Main.correctUnit(this.sizeY*2));
       // g.drawImage(Util.ImageLoader.torso1, getRenderX(), getRenderY(), Main.Main.correctUnit(this.sizeX*2), Main.Main.correctUnit(this.sizeY*2));
     //   g.drawImage(Util.ImageLoader.head1, getRenderX(), getRenderY()-Main.Main.correctUnit(25), Main.Main.correctUnit(this.sizeX*2), Main.Main.correctUnit(this.sizeY*2));


        getMainShape().render(g, map.cameraX, map.cameraY, this);


    }



}
