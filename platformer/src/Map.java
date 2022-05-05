import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Map {

    private ArrayList<GameEntity> entities = new ArrayList<>();
    private ArrayList<GameEntity> nextEntities = new ArrayList<>();

    private ArrayList<GameEntity> particles = new ArrayList<>();
    private ArrayList<GameEntity> nextParticles = new ArrayList<>();

    public static final double GRAVITY = 15;

    public static final double WALL_CORNER_SIZE = 10.0;

    public static final double AIR_DRAG = 0.4;
    public static final double GROUND_DRAG = 0.0001;

    private static final int CRASH_PARTICLE_COUNT = 10;


    public static final double BASE_DRAG_Y = 0.5;


    protected double cameraX = 0;
    protected double cameraY = 0;

    protected boolean cameraMap;

    public double playerX = 0;
    public double playerY = 0;

    GameEntity player = null;

    private int sizeX;
    private int sizeY;

    public Map(boolean cameraMap, int sizeX, int sizeY) {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.cameraMap = cameraMap;
    }

    public boolean isRadius(double x, double y, double x2, double y2, double radius) {
        return (Math.sqrt(Math.pow(Math.abs(x2 - x), 2) + Math.pow(Math.abs(y2 - y), 2)) < radius);
    }

    public boolean isOutOfBounds(double x, double y, double sizeX, double sizeY) {
        return (x+sizeX > this.sizeX || x < -this.sizeX || y+sizeY > this.sizeY || y < -this.sizeY);
    }

    public void removeEntity(GameEntity entity) {
        entity.setFlagRemoval();
        removeFlagged();
    }

    public void reset() {
        for (GameEntity entity : entities) {
            entity.setX(entity.getStartX());
            entity.setY(entity.getStartY());
            entity.setVelY(entity.getStartVelY());
            entity.setVelX(entity.getStartVelX());
        }
    }


    public Hookable getHookable() {
        for (GameEntity entity : entities) {
            if (entity instanceof Hookable) {
                return (Hookable) entity;
            }
        }
        return null;
    }

    public void removeParticle(GameEntity entity) {
        entity.setFlagRemoval();
        removeFlagged();
    }

    private void removeFlagged() {
        ArrayList<GameEntity> bufferEntities = new ArrayList<>();
        bufferEntities.addAll(entities);
        entities = new ArrayList<>();
        for (GameEntity entity : bufferEntities) {
            if (!entity.isFlagRemoval()) {
                entities.add(entity);
            }
        }


        bufferEntities = new ArrayList<>();
        bufferEntities.addAll(particles);
        particles = new ArrayList<>();
        for (GameEntity entity : bufferEntities) {
            if (!entity.isFlagRemoval()) {
                particles.add(entity);
            }
        }

    }

    public double correctUnit(double input) {
        return input / Main.gameUnit;
    }

    public double correctUnitX(double input) {
        return correctUnit(input) - correctUnit(cameraX);
    }

    public double correctUnitY(double input) {
        return correctUnit(input) - correctUnit(cameraY);
    }

    public void addEntity(GameEntity entity) {
        entities.add(entity);
    }

    public void addParticle(GameEntity entity) {
        particles.add(entity);
    }

    public void addEntityLive(GameEntity entity) {
        nextEntities.add(entity);
    }


    public void addParticleLive(GameEntity entity) {
        nextParticles.add(entity);
    }

    public void tick() {
        if (Main.getKey(InputAction.Menu) > 0) {
            for (GameEntity entity : entities) {
                entity.tick();
            }

            for (GameEntity entity : particles) {
                entity.tick();
            }

            entities.addAll(nextEntities);
            nextEntities = new ArrayList<>();

            particles.addAll(nextParticles);
            nextParticles = new ArrayList<>();

        }

        //camera bounds
        cameraX = Math.min(cameraX, sizeX-Main.gameUnit* Main.DEFAULT_WIDTH_MAP);
        cameraX = Math.max(cameraX, -sizeX+Main.gameUnit* Main.DEFAULT_WIDTH_MAP);
        cameraY = Math.min(cameraY, sizeY-Main.gameUnit* Main.DEFAULT_HEIGHT_MAP);
        cameraY = Math.max(cameraY, -sizeY+Main.gameUnit* Main.DEFAULT_HEIGHT_MAP);
    }


    public void render(GraphicsContext g) {


        for (GameEntity entity : particles) {
            entity.render(g);
        }
        for (GameEntity entity : entities) {
            entity.render(g);
        }

        //bounds
        g.setStroke(Color.color(1,0,0));
        g.setLineWidth(10);
        g.strokeRect(correctUnit(-sizeX-cameraX),correctUnit(-sizeY-cameraY),correctUnit(sizeX*2),correctUnit(sizeY*2));



        g.setFont(new Font(25));
        g.setFill(Color.color(1, 1, 1));
        g.fillText("deaths: " + Main.deaths, 50, 50);

        if (!(Main.getKey(InputAction.Menu) > 0)) {
            g.setFill(Color.color(0, 0, 0, 0.4));
            g.fillRect(0, 0, g.getCanvas().getWidth(), g.getCanvas().getHeight());

            g.setFill(Color.color(1, 1, 1));
            g.setFont(new Font(40));
            g.fillText("Paused", 100, 200);
            g.setFont(new Font(25));
            String currentAction = "";
            currentAction = currentAction + getStringKey(InputAction.Up);
            currentAction = currentAction + getStringKey(InputAction.Sprint);
            currentAction = currentAction + getStringKey(InputAction.Hook);
            currentAction = currentAction + getStringKey(InputAction.Left);
            currentAction = currentAction + getStringKey(InputAction.Right);
            currentAction = currentAction + getStringKey(InputAction.Down);
            g.fillText(currentAction, 100, 400);
        }


    }

    public ArrayList<GameEntity> getEntities() {
        return entities;
    }


    private String getStringKey(InputAction action) {
        String currentAction = "";
        int current = 0;
        ArrayList<KeyCode> codes = Main.getDisplayOptions(action);

        for (KeyCode code : codes) {
            currentAction = currentAction + code.getName();
            if (current < codes.size() - 1) {
                currentAction = currentAction + " | ";
            }
            current++;
        }
        currentAction = currentAction + " to: " + action + "\n";

        return currentAction;
    }





    public void crashParticle(double x, double y) {
        for (int i = 0; i < CRASH_PARTICLE_COUNT; i++) {
            Particle particle = new Particle(x, y, this, 10, 10, ImageLoader.particle, true, 1, 1);
            particle.setVelX(Main.random.nextDouble(Particle.PARTICLE_SPEED) - Particle.PARTICLE_SPEED / 2);
            particle.setVelY(Main.random.nextDouble(Particle.PARTICLE_SPEED) - Particle.PARTICLE_SPEED);

            addParticleLive(particle);
        }

    }



    private void addStandardWallSegments(int x, int y, int sizeX, int sizeY, double parallax) {
        Wall wallUp = new Wall(x + WALL_CORNER_SIZE, y, this, sizeX - WALL_CORNER_SIZE * 2, 1, InputAction.Up, FillType.Nothing, parallax);
        Wall wallRight = new Wall(x + sizeX, y + WALL_CORNER_SIZE, this, 1, sizeY - WALL_CORNER_SIZE * 2, InputAction.Right, FillType.Nothing, parallax);
        Wall wallLeft = new Wall(x, y + WALL_CORNER_SIZE, this, 1, sizeY - WALL_CORNER_SIZE * 2, InputAction.Left, FillType.Nothing, parallax);
        Wall wallImage = new Wall(x, y, this, sizeX, sizeY, InputAction.Default, FillType.Tile, parallax);

        addEntity(wallUp);
        addEntity(wallRight);
        addEntity(wallLeft);
        addEntity(wallImage);
    }

    public void addMovingWall(int x, int y, int sizeX, int sizeY, double velY, double velX) {
        MovingWall wallImage = new MovingWall(x, y, this, sizeX, sizeY
                , InputAction.Default, FillType.Tile, velX, velY);

        MovingWall wallUp = new MovingWall(x + WALL_CORNER_SIZE, y, this
                , sizeX - WALL_CORNER_SIZE * 2, 1, InputAction.Up, FillType.Nothing, 0, 0, wallImage);

        MovingWall wallRight = new MovingWall(x + sizeX, y + WALL_CORNER_SIZE
                , this, 1, sizeY - WALL_CORNER_SIZE * 2, InputAction.Right, FillType.Nothing, 0, 0, wallImage);

        MovingWall wallLeft = new MovingWall(x, y + WALL_CORNER_SIZE, this
                , 1, sizeY - WALL_CORNER_SIZE * 2, InputAction.Left, FillType.Nothing, 0, 0, wallImage);

        MovingWall wallDown = new MovingWall(x + WALL_CORNER_SIZE, y + sizeY - 1, this, sizeX - WALL_CORNER_SIZE * 2, 1, InputAction.Down, FillType.Nothing, 0,0, wallImage);


        addEntity(wallUp);
        addEntity(wallRight);
        addEntity(wallLeft);
        addEntity(wallDown);
        addEntity(wallImage);


    }


    public void addWall(int x, int y, int sizeX, int sizeY, double parallax) {

        addStandardWallSegments(x, y, sizeX, sizeY, parallax);
        Wall wallDown = new Wall(x + WALL_CORNER_SIZE, y + sizeY - WALL_CORNER_SIZE-1, this, sizeX - WALL_CORNER_SIZE * 2, 1, InputAction.Down, FillType.Nothing, parallax);


        addEntity(wallDown);


    }

    public GameEntity intersectionMovingWall(GameEntity entity) {
        GameEntity returnEntity = entity;
        for (GameEntity currentEntity : entities) {
            if (!(currentEntity instanceof MovingWall)) {
                if (currentEntity.getParallax() == entity.getParallax()) {
                    if (!entity.equals(currentEntity)) {
                        if (currentEntity.intersect(entity)) {
                            if (InputAction.isYType(currentEntity.getAction())) {
                                return currentEntity;
                            } else {
                                if (InputAction.isXType(currentEntity.getAction())) {
                                    returnEntity = currentEntity;
                                }
                            }
                        }
                    }

                }
            }
        }

        return returnEntity;
    }

    public ArrayList<InputAction> getActions(GameEntity entity) {
        ArrayList<InputAction> actions = new ArrayList<>();

        for (GameEntity currentEntity : entities) {
            if (currentEntity.getParallax() == entity.getParallax()) {
                if (!entity.equals(currentEntity)) {
                    if (currentEntity.intersect(entity)) {
                        actions.add(currentEntity.getAction());
                    }
                }


            }
        }

        return actions;
    }

    public GameEntity intersectionEntity(GameEntity entity) {
        GameEntity returnEntity = entity;
        for (GameEntity currentEntity : entities) {
            if (currentEntity.getParallax() == entity.getParallax()) {
                if (!entity.equals(currentEntity)) {
                    if (currentEntity.intersect(entity)) {
                        if (InputAction.isYType(currentEntity.getAction())) {
                            return currentEntity;
                        } else {
                            if (InputAction.isXType(currentEntity.getAction())) {
                                returnEntity = currentEntity;
                            }
                        }
                    }
                }


            }
        }

        return returnEntity;
    }


}
