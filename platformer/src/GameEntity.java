import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;

public abstract class GameEntity {


    private static final double CRASH_SPEED = 8;

    protected double x;
    protected double y;

    protected double velX;
    protected double velY;

    protected double sizeX;
    protected double sizeY;

    protected double accelX;
    protected double accelY;

    private final int MAX_COLLISIONS = 100;

    private final int SPEED_FACTOR = 144;

    private final double ROTATE_TIME = 1;


    protected double parallax;
    private static final double DEFAULT_TILE_SIZE = 50;

    private static final double SQUASH_FACTOR = 1.5;

    private static final double WALL_CLING_FORCE = 8;
    private static final double WALL_CLING_RADIUS = 5;
    protected boolean running;

    protected Color color;

    protected InputAction action;


    protected double currentDrag;

    protected FillType fillType;

    protected Image image;

    protected boolean runningBefore = false;
    protected boolean canJump;

    protected boolean canLeftJump;

    protected boolean canRightJump;

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

    GameEntity(double x, double y, Map map, InputAction action, FillType fillType, double parallax) {
        flagRemoval = false;
        this.fillType = fillType;
        currentDrag = Map.AIR_DRAG;
        this.parallax = parallax;
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
        this.image = ImageLoader.defaultTile;

        cornerRotation = 0;

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
        if (this instanceof Player) {
            Main.deaths++;
            map.reset = true;
            this.x = startX;
            this.y = startY;
            this.velY = 0;
            this.velX = 0;
        } else {
            map.removeEntity(this);
        }

    }

    public double getParallax() {
        return parallax;
    }

    protected void gravity() {

        velY += Map.GRAVITY / Main.fps;

    }

    public void setFlagRemoval() {
        flagRemoval = true;
    }

    public boolean isFlagRemoval() {
        return flagRemoval;
    }


    protected void physics() {
        velX = velX + accelX / Main.fps;
        velY = velY + accelY / Main.fps;
        velX = velX * Math.pow(currentDrag, 1.0 / Main.fps);
        velY = velY * Math.pow(Map.BASE_DRAG_Y, 1.0 / Main.fps);




        x += (velX / Main.fps) * SPEED_FACTOR;
        y += (velY / Main.fps) * SPEED_FACTOR;
    }

    public double getX() {

        return x;
    }

    public double getY() {

        return y;
    }


    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
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


    protected void jumpCollision() {
        x += WALL_CLING_RADIUS;
        if (map.getActions(this).contains(InputAction.Left)) {
            canLeftJump = true;
            velX += WALL_CLING_FORCE / Main.fps;
        }
        x -= WALL_CLING_RADIUS * 2;
        if (map.getActions(this).contains(InputAction.Right)) {
            canRightJump = true;
            velX += -WALL_CLING_FORCE / Main.fps;
        }
        x += WALL_CLING_RADIUS;


        y += WALL_CLING_RADIUS;
        if (map.getActions(this).contains(InputAction.Up)) {
            canJump = true;

        }
        if (map.getActions(this).contains(InputAction.Corner)) {
            canCornerJump = true;
        }
        y -= WALL_CLING_RADIUS;


    }


    protected void collision() {
        if (rotationTicks > 0) {
            rotationTicks--;
        }
        GameEntity entity = map.intersectionEntity(this);
        canJump = false;
        canLeftJump = false;
        canCornerJump = false;
        canRightJump = false;

        if (!(entity == null)) {


            int numberOfCollisions = 0;


            while (!(entity.getAction() == InputAction.Default)) {

                numberOfCollisions++;
                if (numberOfCollisions > MAX_COLLISIONS) {
                    die();
                    numberOfCollisions = 0;

                }
                InputAction action = entity.getAction();
                if (action == InputAction.Left) {
                    canLeftJump = true;

                    while (entity.intersect(this)) {
                        x -= 0.1;
                    }
                    velX = 0;
                    cornerRotation = 0;
                } else if (action == InputAction.Right) {
                    canRightJump = true;

                    while (entity.intersect(this)) {
                        x += 0.1;
                    }

                    velX = 0;
                    cornerRotation = 0;
                } else if (action == InputAction.Up) {
                    if (this instanceof Player || this instanceof BasicEnemy) {

                        if (velY > CRASH_SPEED) {
                            map.crashParticle(this.x + sizeX / 2, this.y + sizeY / 2);
                        }
                    }
                    canJump = true;

                    while (entity.intersect(this)) {
                        y -= 0.1;

                    }
                    velY = 0;
                    cornerRotation = 0;

                } else if (action == InputAction.Down) {


                    while (entity.intersect(this)) {
                        y += Map.WALL_CORNER_SIZE;

                    }
                    velY = 0;
                    cornerRotation = 0;
                } else if (action == InputAction.Corner) {
                    CornerWall corner = ((CornerWall) entity);

                    double rotation = Math.toRadians((corner).getRotation());
                    canCornerJump = true;
                    lastRotation = cornerRotation;
                    cornerRotation = rotation;
                    rotationTicks = (int)(Main.fps*ROTATE_TIME);
                    while (corner.intersect(this)) {
                        y += 0.1 * Math.sin(rotation);


                        x -= 0.1 * Math.cos(rotation);

                    }
                    double totalVel = Math.sqrt(Math.pow(velY, 2) + Math.pow(velX, 2));
                    velY = -Math.sin(rotation) * totalVel;
                    velX = -Math.cos(rotation) * totalVel;


                    //down to slide
                    /*
                                        while (corner.intersect(this)) {
                        y += 0.1 * Math.sin(rotation);


                        if (this instanceof Player && Main.isKeyDown(InputAction.Down)) {
                            x -= 0.1 * Math.cos(rotation);

                        }

                    }
                    if (this instanceof Player && Main.isKeyDown(InputAction.Down)) {
                        double totalVel = Math.sqrt(Math.pow(velY, 2) + Math.pow(velX, 2));
                        velY = -Math.sin(rotation) * totalVel;
                        velX = -Math.cos(rotation) * totalVel;
                    } else {
                        velY = velY * 0.9;
                    }

                     */
                    /*
                    slide
                    while (corner.intersect(this)) {
                        y += 0.1*Math.sin(rotation);
                        x-= 0.1*Math.cos(rotation);

                    }
                    double totalVel = Math.sqrt(Math.pow(velY, 2) + Math.pow(velX, 2));
                    velY = -Math.sin(rotation)*totalVel;
                    velX = -Math.cos(rotation)*totalVel;

                     */

                    /*
                    bounce
                    while (corner.intersect(this)) {
                        y += 0.1*Math.sin(rotation);
                        x-= 0.1*Math.cos(rotation);

                    }
                    double totalVel = Math.sqrt(Math.pow(velY, 2) + Math.pow(velX, 2));
                    velY = Math.sin(rotation)*totalVel;
                    velX = -Math.cos(rotation)*totalVel;

                     */

                }
                entity = map.intersectionEntity(this);
                if (entity == null) {
                    entity = new Wall(0, 0, map, 1, 1, InputAction.Default, FillType.Nothing, 1);
                }

            }
        }
    }


    public double getRenderRotation() {
        return Main.interpolate(cornerRotation, lastRotation, Main.fps*ROTATE_TIME, rotationTicks);
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

    public double getRenderSizeX() {
        double sizeX = map.correctUnit(this.sizeX + getVelStretchX() * 2);
        return sizeX;
    }

    public double getRenderSizeY() {
        double sizeY = map.correctUnit(getSizeY() + getVelStretchY() * 2);
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


    public boolean intersect(GameEntity entity) {


        double x = entity.getX();
        double y = entity.getY();
        double sizeX = entity.getSizeX();
        double sizeY = entity.getSizeY();
        return x + sizeX > getX() && x < getX() + getSizeX()
                && y + sizeY > getY() && y < getY() + getSizeY();
    }


}
