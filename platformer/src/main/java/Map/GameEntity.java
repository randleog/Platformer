package Map;

import GameControl.Square;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;

import java.util.ArrayList;
import Util.ImageLoader;
import Main.Main;
import Util.SoundLoader;
import GameControl.TimedSound;
import Util.Stats;
import Util.Settings;

public abstract class GameEntity {

    private static final int LAVA_DAMAGE = 3;


    private static final double CRASH_SPEED = 8;

    private static final double COLLISION_AMMOUNT = 0.05;

    protected double x;
    protected double y;

    protected double velX;
    protected double velY;

    protected double sizeX;
    protected double sizeY;

    protected double accelX;
    protected double accelY;

    private final int MAX_COLLISIONS = 500;

    private final int SPEED_FACTOR = 144;

    private final double ROTATE_TIME = 1;


    protected boolean changed = false;

    public static final double WALL_CORNER_SIZE = 10.0;

    protected double parallax;
    private static final double DEFAULT_TILE_SIZE = 50;

    protected static final double SQUASH_FACTOR = 1.5;

    private static final double WALL_CLING_FORCE = 8;
    private static final double WALL_CLING_RADIUS = 2;
    protected boolean running;

    protected Color color;


    protected static final double SWIMMING_GRAVITY = 0.2;


    protected InputAction action;


    protected double currentDrag;

    protected FillType fillType;

    protected Image image;

    protected boolean runningBefore = false;
    protected boolean canJump;

    protected boolean canLeftJump;

    protected boolean canRightJump;


    protected boolean swimming = false;

    protected boolean canCornerJump;
    protected double lastRotation;
    protected double cornerRotation;

    protected int rotationTicks = 0;

    protected Map map;

    protected double tileSize;

    private boolean flagRemoval;
    protected double startX;
    protected double startY;

    protected double startVelX;
    protected double startVelY;


    protected double wallCling = 0;
    protected double wallClingRadius = 30;
    protected boolean clinging = false;

    protected boolean stuck = false;

    protected double wallHeight = 0;
    protected static final double STUCK_FACTOR = 0.00000001;

    protected static final double SWIM_FACTOR = 0.01;
    private static final double WALL_FACTOR = 1;
    protected ArrayList<Square> hitbox = new ArrayList<>();
    protected  double health;

    protected TimedSound walkSound;
    protected TimedSound swimSound;

    public static final int DEFAULT_HEALTH = 5;
    protected int maxHealth = 10;

    GameEntity(double x, double y, Map map, InputAction action, FillType fillType, double parallax) {
        flagRemoval = false;
        this.fillType = fillType;
        currentDrag = Map.AIR_DRAG;
        this.parallax = parallax;
        health = DEFAULT_HEALTH;
        running = false;
        this.x = x;
        this.y = y;
        this.startVelX = 0;
        this.startVelY = 0;
        startX = x;
        startY = y;
        sizeX = 100;
        sizeY = 100;
        tileSize = 50;
        this.map = map;
        this.action = action;
        this.color = Color.color(1, 0, 0);
        this.image = ImageLoader.player;
        walkSound = new TimedSound(75);
      //  swimSound = new GameControl.TimedSound(500);

        cornerRotation = 0;

        loadHitbox();

    }

    public double getHealthPercentage() {
        return health/(maxHealth*1.0);
    }



    public double getStartVelX() {
        return startVelX;
    }

    public double getStartVelY() {
        return startVelY;
    }

    public double getStartY() {
        return startY;
    }

    public double getStartX() {
        return startX;
    }

    public void die() {
        if (this.isPlayer()) {
            Main.deaths++;
            Stats.add("total Deaths", 1);
            map.reset = true;
            this.x = startX;
            this.y = startY;
            this.velY = 0;
            this.velX = 0;
        } else {
            map.removeEntity(this);
        }

    }


    protected void loadHitbox() {
        hitbox = new ArrayList<>();
        hitbox.add(new Square(x, y, sizeX, sizeY, parallax, action));


    }


    public double getParallax() {
        return parallax;
    }

    protected void gravity() {
        velY += map.getGravity()/ Settings.getD("fps");


    }


    public void setImage(Image image) {
        this.image = image;
    }

    public boolean isPlayer() {
        return false;
    }

    public void setFlagRemoval() {
        flagRemoval = true;
    }

    public boolean isFlagRemoval() {
        return flagRemoval;
    }


    protected void physics() {

        velX = velX + accelX / Settings.getD("fps");
        velY = velY + accelY / Settings.getD("fps");


        velX = velX * Math.pow(currentDrag, 1.0 / Settings.getD("fps"));


        double vertDrag = Map.BASE_DRAG_Y;

        if (this instanceof Racer) {
            vertDrag = currentDrag;


        } else if (swimming) {
            vertDrag = Map.BASE_DRAG_Y * SWIM_FACTOR;


        } else if (stuck) {
            vertDrag = Map.BASE_DRAG_Y * STUCK_FACTOR;

        } else if (canRightJump || canLeftJump) {
            vertDrag = Map.BASE_DRAG_Y * WALL_FACTOR;

        }
        velY = velY * Math.pow(vertDrag, 1.0 / Settings.getD("fps"));


        x += (velX / Settings.getD("fps")) * SPEED_FACTOR;
        y += (velY / Settings.getD("fps")) * SPEED_FACTOR;
    }

    public double getX() {

        return x;
    }

    public double getY() {

        return y;
    }


    public void setX(double x) {

        this.x = x;
        loadHitbox();
    }

    public void setY(double y) {
        this.y = y;
        loadHitbox();
    }

    public void setVelX(double velX) {

        this.velX = velX;
    }

    public double getVelX() {
        return velX;
    }

    public double getVelY() {
        return velY;
    }

    public void setVelY(double velY) {
        this.velY = velY;
    }

    public void setAction(InputAction action) {
        this.action = action;
    }

    public double getSizeX() {
        return sizeX;
    }

    public double getSizeY() {

        return sizeY;
    }

    public abstract void tick();

    public abstract void render(GraphicsContext g);


    public InputAction getAction() {
        return action;
    }

    public Square getShape(Square entity) {
        Square lastShape = null;

        for (Square shape : hitbox) {
            if (shape.intersect(entity)) {
                // shape.flag();
                if (InputAction.isYType(shape.getAction())) {
                    return shape;
                } else {
                    lastShape = shape;
                }
            }
        }


        return lastShape;
    }


    public void flagAll() {

        for (Square shape : hitbox) {
            shape.flag();
        }
    }


    public void setSizeX(double sizeX) {
        this.sizeX = sizeX;
        loadHitbox();
    }


    public void setSizeY(double sizeY) {
        this.sizeY = sizeY;
        loadHitbox();
    }

    protected void checkDeath() {
        if (health < 0) {
            die();
        }
    }


    protected void jumpCollision() {
        ArrayList<InputAction> actions = map.getActions(this);

        if (actions.contains(InputAction.Swim) || actions.contains(InputAction.Lava)) {
            if (!swimming) {

                map.splash(this);
                SoundLoader.playSound(SoundLoader.largeSplash, 1, 0, SoundLoader.getRandomSpeed());
            }
            swimming = true;
            if (actions.contains(InputAction.Lava)) {
                health-=(LAVA_DAMAGE*1.0)/Settings.get("fps");
               // System.out.println("what in the fuck do you mean");
            }

        } else {
            if (swimming) {
                map.splash(this);
            }
            swimming = false;
        }


        x += WALL_CLING_RADIUS;
        if (map.getActions(this).contains(InputAction.Left)) {
            canLeftJump = true;
            velX += WALL_CLING_FORCE / Settings.getD("fps");
        } else if (map.getActions(this).contains(InputAction.StickyLeft)) {
            stuck = true;


        }

        x -= WALL_CLING_RADIUS * 2;

        if (map.getActions(this).contains(InputAction.Right)) {
            canRightJump = true;
            velX += -WALL_CLING_FORCE / Settings.getD("fps");
        } else if (map.getActions(this).contains(InputAction.StickyRight)) {
            stuck = true;


        }


        x += WALL_CLING_RADIUS;


        y += WALL_CLING_RADIUS;
        if (map.getActions(this).contains(InputAction.Up)) {
            canJump = true;


        } else if (map.getActions(this).contains(InputAction.StickyUp)) {
            canJump = true;
            stuck = true;

        }


        y -= WALL_CLING_RADIUS;


        checkDeath();

    }


    public void placeAnimate() {

        double size = (sizeY + sizeX) / 4;

        for (int i = 0; i < this.sizeX / size; i++) {
            for (int j = 0; j < this.sizeY / size; j++) {
                map.crashParticle(i * size + this.x, j * size + this.y);
            }
        }


    }

    //deletes if intersecting anything
    public void scanDelete() {
        if (map.getActions(this).contains(InputAction.Default)) {
            map.removeEntity(this);
        }
    }


    public boolean isWall() {
        return false;
    }


    protected void collision() {
        clinging = false;

        if (rotationTicks > 0) {
            rotationTicks--;
        }

        stuck = false;

        boolean hasGoneRight = false;
        boolean hasGoneUp = false;
        Square entity = map.intersectionWall(this);
        canJump = false;
        canLeftJump = false;
        canCornerJump = false;
        canRightJump = false;

        double oldVelX = velX;

        if (!(entity == null)) {


            int numberOfCollisions = 0;
            boolean loop = true;

            while (loop) {


                numberOfCollisions++;
                if (numberOfCollisions > MAX_COLLISIONS) {

                    die();
                    numberOfCollisions = 0;
                    loop = false;

                }
                InputAction action = entity.getAction();

                if (InputAction.isLeftType(action)) {


                    if (hasGoneRight) {


                        intersectSquareUp(entity);
                    } else {
                        canLeftJump = true;

                        if (action == InputAction.StickyLeft) {
                            stuck = true;
                        }

                        intersectSquareLeft(entity);
                    }
                } else if (InputAction.isRightType(action)) {
                    hasGoneRight = true;
                    canRightJump = true;

                    if (action == InputAction.StickyRight) {
                        stuck = true;
                    }

                    intersectSquareRight(entity);

                } else if (InputAction.isUpType(action)) {
                    hasGoneUp = true;

                    if (this instanceof Player || this instanceof BasicEnemy) {

                        if (Math.abs(velX / Settings.getD("fps")) > 0.01) {
                            walkSound.playVaried(SoundLoader.stone, 1, SoundLoader.getRandomSpeed(), this.velX);

                        }

                        if (velY > CRASH_SPEED) {
                            map.crashParticle(this.x + sizeX / 2, this.y + sizeY / 2);
                            //Util.SoundLoader.playSound(Util.SoundLoader.fall, 1, 0, Util.SoundLoader.getRandomSpeed() * 0.7);
                        }

                        if (velY > 5) {
                            SoundLoader.playSound(SoundLoader.fall, velY / 25, 0, SoundLoader.getRandomSpeed() * 0.6);
                        }
                    }
                    canJump = true;

                    intersectSquareUp(entity);

                } else if (InputAction.isDownType(action)) {
                    if (action == InputAction.StickyDown) {


                        if (Math.abs(velY) > 2) {

                            SoundLoader.playSound(SoundLoader.slime, 1, 0, SoundLoader.getRandomSpeed() * 1.3);

                        }
                    }
                    if (hasGoneUp) {


                        if (oldVelX > 0) {
                            intersectSquareLeft(entity);
                        } else {
                            intersectSquareRight(entity);
                        }
                    } else {
                        intersectSquareDown(entity);
                    }


                    if (action == InputAction.StickyDown) {


                        this.velY = -1;


                        clinging = true;
                        wallHeight = this.y;
                    }

                } else if (action == InputAction.Corner) {


                    double rotation = Math.toRadians(entity.getRotation());

                    canCornerJump = true;
                    lastRotation = cornerRotation;
                    cornerRotation = rotation;
                    rotationTicks = (int) (Settings.getD("fps") * ROTATE_TIME);
                    double dy = Math.sin(rotation);
                    while (entity.intersect(getMainShape())) {
                        y += COLLISION_AMMOUNT * dy;



                        if (dy < 0) {
                            x -= COLLISION_AMMOUNT * Math.cos(rotation);
                        }
                    }

                    if (dy < 0) {
                        double totalVel = Math.sqrt(Math.pow(velY, 2) + Math.pow(velX, 2));
                        velY = -Math.sin(rotation) * totalVel;
                        velX = -Math.cos(rotation) * totalVel;
                    } else {
                        this.velX = 0;
                        this.velY = 0;

                    }



                }
                entity = map.intersectionWall(this);

                //   if (action == Map.InputAction.Default) {
                //     loop = false;
                //  }
                if (entity == null) {
                    loop = false;
                }

            }
        }


    }



    private void intersectSquareRight(Square square) {
        while (square.intersect(getMainShape())) {
            x += COLLISION_AMMOUNT;
        }

        velX = 0;
        cornerRotation = 0;
    }

    private void intersectSquareLeft(Square square) {
        while (square.intersect(getMainShape())) {
            x -= COLLISION_AMMOUNT;
        }
        velX = 0;
        cornerRotation = 0;
    }

    private void intersectSquareDown(Square square) {
        while (square.intersect(getMainShape())) {
            y += COLLISION_AMMOUNT;

        }
        velY = 0;
        cornerRotation = 0;
    }

    private void intersectSquareUp(Square square) {
        while (square.intersect(getMainShape())) {
            y -= COLLISION_AMMOUNT;

        }
        velY = 0;

        cornerRotation = 0;
    }


    public double getRenderRotation() {
        return Main.interpolate(cornerRotation, lastRotation, Settings.getD("fps") * ROTATE_TIME, rotationTicks);
    }


    public double getVelStretchX() {

        return (Math.sqrt(Math.abs(velX)) - Math.sqrt(Math.abs(velY))) * SQUASH_FACTOR;
    }

    public double getVelStretchY() {

        return (Math.sqrt(Math.abs(velY)) - Math.sqrt(Math.abs(velX))) * SQUASH_FACTOR;
    }

    public double getRenderX() {
        double x = map.correctUnit(this.x - getVelStretchX()) - map.correctUnit(map.cameraX * parallax);


        return x;

    }


    public double getRenderY() {
        double y = map.correctUnit(this.y - getVelStretchY()) - map.correctUnit(map.cameraY * parallax);

        return y;

    }

    public double getNonRenderX() {
        double x = this.x - getVelStretchX();


        return x;

    }


    public double getNonRenderY() {
        double y = this.y - getVelStretchY();

        return y;

    }


    public double getRenderSizeX() {
        double sizeX = map.correctUnit(this.sizeX + getVelStretchX() * 2);
        return sizeX;
    }

    public double getRenderSizeY() {
        double sizeY = map.correctUnit(getSizeY() + getVelStretchY() * 2);
        return sizeY;
    }


    public double getNonRenderSizeX() {
        double sizeX = this.sizeX + getVelStretchX() * 2;
        return sizeX;
    }

    public double getNonRenderSizeY() {
        double sizeY = getSizeY() + getVelStretchY() * 2;
        return sizeY;
    }


    protected void renderSquare(GraphicsContext g) {
        if (!(fillType == FillType.Nothing)) {

            double x = getRenderX();
            double y = getRenderY();
            double sizeX = getRenderSizeX();
            double sizeY = getRenderSizeY();


            if (fillType == FillType.Image) {
                g.drawImage(image, x, y, sizeX, sizeY);
            } else {
                if (fillType == FillType.Color) {
                    g.setFill(this.color);
                } else if (fillType == FillType.Tile) {
                    g.setFill(new ImagePattern(image, x, y, map.correctUnit(tileSize) * parallax, map.correctUnit(tileSize) * parallax, false));
                }
                g.fillRect(x, y, sizeX, sizeY);
            }
        }
        for (Square shape : hitbox) {
            shape.render(g, map.cameraX, map.cameraY, (Player) map.player);
        }

    }


    public ArrayList<Square> getHitbox() {
        return hitbox;
    }


    public double getCornerRotation() {
        return cornerRotation;
    }


    protected void renderStill(GraphicsContext g) {
        if (!(fillType == FillType.Nothing)) {


            double sizeX = getRenderSizeX();
            double sizeY = getRenderSizeY();


            if (fillType == FillType.Image) {
                g.drawImage(image, -sizeX / 2, -sizeY / 2, sizeX, sizeY);
            } else {
                if (fillType == FillType.Color) {
                    g.setFill(this.color);
                } else if (fillType == FillType.Tile) {
                    g.setFill(new ImagePattern(image, 0, 0, map.correctUnit(tileSize) * parallax, map.correctUnit(tileSize) * parallax, false));
                }
                g.fillRect(-sizeX / 2, -sizeY / 2, sizeX, sizeY);
            }
        }

    }


    public Square getMainShape() {


        return new Square(this.x - ((this.sizeX < 0) ? Math.abs(this.sizeX) : 0), this.y - ((this.sizeY < 0) ? Math.abs(this.sizeY) : 0), Math.abs(this.sizeX), Math.abs(getSizeY()), parallax, action);
    }


    public boolean intersect(GameEntity entity) {


        if (entity == null) {
            return false;
        }

        for (Square shape : hitbox) {
            for (Square shape2 : entity.getHitbox()) {
                if (shape.intersect(shape2)) {
                    return true;
                }
            }
        }
        return false;
    }


}

