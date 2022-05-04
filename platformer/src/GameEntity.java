import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public abstract class GameEntity {

    protected double x;
    protected double y;

    protected double velX;
    protected double velY;

    protected double sizeX;
    protected double sizeY;

    protected double accelX;
    protected double accelY;

    protected boolean running;

    protected Color color;

    protected InputAction action;


    protected double currentDrag;



    protected Map map;

    GameEntity(double x, double y, Map map, InputAction action) {
        currentDrag = Map.AIR_DRAG;
        running = false;
        this.x = x;
        this.y = y;
        sizeX = 100;
        sizeY = 100;
        this.map = map;
        this.action = action;
        this.color = Color.color(1,0,0);

    }

    protected void gravity() {

        velY+=Map.GRAVITY/Main.FPS;

    }


    protected void physics() {
        velX = velX+accelX;
        velY = velY+accelY;
        velX = velX*Math.pow(currentDrag, 1.0/Main.FPS);
        velY = velY*Math.pow(Map.BASE_DRAG_Y, 1.0/Main.FPS);



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


    protected void renderSquare(GraphicsContext g) {

            double x = map.correctUnitX(this.x);
            double y = map.correctUnitY(this.y);
            double sizeX = map.correctUnit(this.sizeX);
            double sizeY = map.correctUnit(this.sizeY);


            g.setFill(this.color);
            g.fillRect(x , y, sizeX, sizeY);

    }

    public boolean intersect(GameEntity entity) {


        double x = entity.getX();
        double y = entity.getY();
        double sizeX = entity.getSizeX();
        double sizeY = entity.getSizeY();
        return x+sizeX > this.x && x< this.x + this.sizeX
                && y+sizeY > this.y && y < this.y + this.sizeY;
    }



}
