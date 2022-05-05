import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class MovingWall extends GameEntity {

    private MovingWall mainWall;
    private boolean isMainWall;



    public MovingWall(double x, double y, Map map, double sizeX, double sizeY, InputAction side, FillType fillType, double velX, double velY) {
        super(x, y, map, side, fillType, 1);

        this.isMainWall = true;
        this.velX = velX;
        this.velY = velY;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.color = Color.color(1, 0.5, 0);
        this.image = ImageLoader.wallTile;
        if (sizeX < 10 && sizeY < 10) {
            System.out.println("wall is too small");
        }
    }

    public MovingWall(double x, double y, Map map, double sizeX, double sizeY, InputAction side, FillType fillType, double velX, double velY, MovingWall mainWall) {
        super(x, y, map, side, fillType, 1);

        this.mainWall = mainWall;
        this.startX = x - mainWall.getX();
        this.startY = y - mainWall.getY();
        this.isMainWall = false;
        this.velX = velX;
        this.velY = velY;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.color = Color.color(1, 0.5, 0);
        this.image = ImageLoader.wallTile;
        if (sizeX < 10 && sizeY < 10) {
            System.out.println("wall is too small");
        }
    }

    protected void collision() {
        GameEntity entity = map.intersectionMovingWall(this);

        if (!(entity == null)) {


            while (!(entity.getAction() == InputAction.Default)) {
                InputAction action = entity.getAction();

                if (action == InputAction.Left) {

                    while (entity.intersect(this)) {
                        x -= 0.1;
                    }
                    velX = -velX;
                } else if (action == InputAction.Right) {


                    while (entity.intersect(this)) {
                        x += 0.1;
                    }
                    velX = -velX;
                } else if (action == InputAction.Up) {

                    while (entity.intersect(this)) {
                        y -= 0.1;

                    }
                    velY = -velY;

                } else if (action == InputAction.Down) {
                    if (runningBefore)
                        canJump = true;

                    while (entity.intersect(this)) {
                        y += 0.1;
                    }

                    velY = -velY;
                }
                entity =  map.intersectionMovingWall(this);
                if (action == null) {
                    entity = new Wall(0,0,map,1,1,InputAction.Default,FillType.Nothing, 1);

                }
            }
        }
    }


    public void tick() {

        if (isMainWall) {
            this.x = x + velX;
            this.y = y + velY;

            collision();
        } else {
            this.x = mainWall.getX() + startX;
            this.y = mainWall.getY() + startY;
        }
    }

    public void render(GraphicsContext g) {
        renderSquare(g);
    }
}
