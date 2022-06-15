import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import javax.annotation.processing.SupportedOptions;
import java.util.ArrayList;

public class MovingWall extends GameEntity {

    private MovingWall mainWall;
    private boolean isMainWall;
    private final int MAX_COLLISIONS = 500;
    private static final double COLLISION_AMMOUNT = 0.1;

    public MovingWall(double x, double y, Map map, double sizeX, double sizeY, InputAction side, FillType fillType, double velX, double velY) {
        super(x, y, map, side, fillType, 1);

        this.isMainWall = true;
        this.velX = velX;
        this.velY = velY;
        this.startVelX = velX;
        this.startVelY = velY;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.color = Color.color(1, 0.5, 0);
        this.image = ImageLoader.wallTile;
        if (sizeX < 10 && sizeY < 10) {
            System.out.println("wall is too small");
        }
    }

    @Override
    public boolean isWall() {
        return true;
    }




    public MovingWall(double x, double y, Map map, double sizeX, double sizeY, InputAction side, FillType fillType, double velX, double velY, MovingWall mainWall) {
        super(x, y, map, side, fillType, 1);

        this.mainWall = mainWall;
        this.startX = x - mainWall.getX();
        this.startY = y - mainWall.getY();
        this.isMainWall = false;
        this.startVelX = velX;
        this.startVelY = velY;
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
        if (rotationTicks > 0) {
            rotationTicks--;
        }
        Square entity = map.intersectionWall(this);
        canJump = false;
        canLeftJump = false;
        canCornerJump = false;
        canRightJump = false;

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
                if (this.action.equals(InputAction.Right)) {
                    if (map.getActions(this).contains(InputAction.Left)) {
                        loop = false;
                    }
                } else if (this.action.equals(InputAction.Left)) {
                    if (map.getActions(this).contains(InputAction.Left)) {
                        loop = false;
                    }
                }

                if (action == InputAction.Left) {
                    canLeftJump = true;

                    while (entity.intersect(getMainShape())) {
                        x -= COLLISION_AMMOUNT;
                    }
                    velX = -velX;
                    cornerRotation = 0;
                } else if (action == InputAction.Right) {
                    canRightJump = true;

                    while (entity.intersect(getMainShape())) {
                        x += COLLISION_AMMOUNT;
                    }

                    velX = -velX;
                    cornerRotation = 0;
                } else if (action == InputAction.Up) {



                    while (entity.intersect(getMainShape())) {
                        y -= COLLISION_AMMOUNT;

                    }
                    velY = -velY;

                    cornerRotation = 0;

                } else if (action == InputAction.Down) {


                    while (entity.intersect(getMainShape())) {
                        y += COLLISION_AMMOUNT;

                    }
                    velY = -velY;
                    cornerRotation = 0;
                }
                entity = map.intersectionWall(this);

                //   if (action == InputAction.Default) {
                //     loop = false;
                //  }
                if (entity == null) {
                    loop = false;
                }

            }
        }
    }

    @Override
    public Square getShape(Square entity) {
        loadHitbox();


        Square lastShape = null;

        for (Square shape : hitbox) {
            if (shape.intersect(entity)) {
                // shape.flag();
                lastShape = shape;
            }
        }


        return lastShape;
    }

    @Override
    protected void loadHitbox() {
        hitbox = new ArrayList<>();
        hitbox.add(new Square(x + WALL_CORNER_SIZE, y, sizeX - WALL_CORNER_SIZE * 2, 1, parallax, InputAction.Up));
        hitbox.add(new Square(x + sizeX - 1, y + WALL_CORNER_SIZE, 1, sizeY - WALL_CORNER_SIZE * 2, parallax, InputAction.Right));
        hitbox.add(new Square(x + WALL_CORNER_SIZE, y + sizeY - 1 - WALL_CORNER_SIZE / 2, sizeX - WALL_CORNER_SIZE * 2, 1, parallax, InputAction.Down));
        hitbox.add(new Square(x, y + WALL_CORNER_SIZE, 1, sizeY - WALL_CORNER_SIZE * 2, parallax, InputAction.Left));

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
