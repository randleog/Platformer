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


    protected double parallax;
    private static final double DEFAULT_TILE_SIZE = 50;

    private static final double SQUASH_FACTOR = 1;


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
        tileSize = map.correctUnit(DEFAULT_TILE_SIZE);
        this.map = map;
        this.action = action;
        this.color = Color.color(1, 0, 0);
        this.image = ImageLoader.defaultTile;

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
            map.reset();
        }
        this.x = startX;
        this.y = startY;
        this.velY =0;
        this.velX = 0;
    }

    public double getParallax() {
        return parallax;
    }

    protected void gravity() {

        velY += Map.GRAVITY / Main.FPS;

    }

    public void setFlagRemoval() {
        flagRemoval = true;
    }

    public boolean isFlagRemoval() {
        return flagRemoval;
    }


    protected void physics() {
        velX = velX + accelX;
        velY = velY + accelY;
        velX = velX * Math.pow(currentDrag, 1.0 / Main.FPS);
        velY = velY * Math.pow(Map.BASE_DRAG_Y, 1.0 / Main.FPS);


        x += velX;
        y += velY;
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


    protected void collision() {
        GameEntity entity = map.intersectionEntity(this);
        canJump = false;
        canLeftJump = false;
        canRightJump = false;

        if (!(entity == null)) {


            int numberOfCollisions = 0;


            while (!(entity.getAction() == InputAction.Default)) {

                numberOfCollisions++;
                if (numberOfCollisions > MAX_COLLISIONS) {
                    die();
                    numberOfCollisions =0;

                }
                InputAction action = entity.getAction();
                if (action == InputAction.Left) {
                    canLeftJump = true;

                    while (entity.intersect(this)) {
                        x -= 0.1;
                    }
                    velX = 0;
                } else if (action == InputAction.Right) {
                    canRightJump = true;

                    while (entity.intersect(this)) {
                        x += 0.1;
                    }
                    velX = 0;
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


                } else if (action == InputAction.Down) {
                    if (runningBefore)
                        canJump = true;

                    while (entity.intersect(this)) {
                        y += Map.WALL_CORNER_SIZE;

                    }
                    velY = 0;

                }
                entity = map.intersectionEntity(this);
                if (entity == null) {
                    entity = new Wall(0, 0, map, 1, 1, InputAction.Default, FillType.Nothing, 1);
                }

            }
        }
    }



    public double getVelStretchX() {

        return (Math.sqrt(Math.abs(velX)) - Math.sqrt(Math.abs(velY))) * SQUASH_FACTOR;
    }

    public double getVelStretchY() {

        return (Math.sqrt(Math.abs(velY)) - Math.sqrt(Math.abs(velX))) * SQUASH_FACTOR;
    }

    public double getRenderX() {
        double x = map.correctUnit(this.x) - map.correctUnit(map.cameraX * parallax) - map.correctUnit(getVelStretchX());
        return x;

    }


    public double getRenderY() {
        double y = map.correctUnit(this.y) - map.correctUnit(map.cameraY * parallax) - map.correctUnit(getVelStretchY());
        return y;

    }

    public double getRenderSizeX() {
        double sizeX = map.correctUnit(this.sizeX) + map.correctUnit(getVelStretchX() * 2);
        return sizeX;
    }

    public double getRenderSizeY() {
        double sizeY = map.correctUnit(getSizeY()) + map.correctUnit(getVelStretchY() * 2);
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
                    g.setFill(new ImagePattern(image, x, y, tileSize * parallax, tileSize * parallax, false));
                }
                g.fillRect(x, y, sizeX, sizeY);
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
