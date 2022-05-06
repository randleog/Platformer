import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.awt.image.AreaAveragingScaleFilter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class Map {

    protected boolean reset = false;

    private ArrayList<GameEntity> entities = new ArrayList<>();
    private ArrayList<GameEntity> nextEntities = new ArrayList<>();

    private ArrayList<GameEntity> particles = new ArrayList<>();
    private ArrayList<GameEntity> nextParticles = new ArrayList<>();

    private ArrayList<GameEntity> startEntities = new ArrayList<>();

    public static final double GRAVITY = 15;

    public static final double WALL_CORNER_SIZE = 10.0;

    public static final double AIR_DRAG = 0.4;
    public static final double GROUND_DRAG = 0.0001;

    private static final int CRASH_PARTICLE_COUNT = 10;



    private LevelsMenuButton backButton = new LevelsMenuButton(400,500,200,100);


    public static final double BASE_DRAG_Y = 0.5;

    protected HashMap<Integer,Boolean> keys = new HashMap<>();


    protected double cameraX = 0;
    protected double cameraY = 0;



    private int currentTick = 0;
    public double playerX = 0;
    public double playerY = 0;

    GameEntity player = null;

    private int sizeX;
    private int sizeY;

    private String name;

    public Map(int sizeX, int sizeY, String name) {

        this.name = name;
        this.sizeX = sizeX;
        this.sizeY = sizeY;

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

    public void setStartEntities() {
        this.startEntities = new ArrayList<>();
        startEntities.addAll(entities);
    }

    private void reset() {
        Main.playMap(MapLoader.loadMap("1", true));

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
        currentTick++;
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

            if (reset) {
                reset();
            }
        }

        //camera bounds
        cameraX = Math.min(cameraX, sizeX-Main.gameUnit* Main.DEFAULT_WIDTH_MAP);
        cameraX = Math.max(cameraX, -sizeX+Main.gameUnit* Main.DEFAULT_WIDTH_MAP);
        cameraY = Math.min(cameraY, sizeY-Main.gameUnit* Main.DEFAULT_HEIGHT_MAP);
        cameraY = Math.max(cameraY, -sizeY+Main.gameUnit* Main.DEFAULT_HEIGHT_MAP);
    }


    public double getTime() {
        return (currentTick*1.0)/Main.FPS;
    }

    public String getName() {
        return name;
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
        g.fillText("time: " + String.format("%.2f",currentTick*1.0/Main.FPS), 200, 50);
        if (!(Main.getKey(InputAction.Menu) > 0)) {

            g.setFill(Color.color(0, 0, 0, 0.4));
            g.fillRect(0, 0, g.getCanvas().getWidth(), g.getCanvas().getHeight());

            backButton.tick();
            backButton.render(g);

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


        Wall wallDown = new Wall(x + WALL_CORNER_SIZE, y + sizeY - WALL_CORNER_SIZE-1, this, sizeX - WALL_CORNER_SIZE * 2, 1, InputAction.Down, FillType.Nothing, parallax);
        Wall wallUp = new Wall(x + WALL_CORNER_SIZE, y, this, sizeX - WALL_CORNER_SIZE * 2, 1, InputAction.Up, FillType.Nothing, parallax);
        Wall wallRight = new Wall(x + sizeX, y + WALL_CORNER_SIZE, this, 1, sizeY - WALL_CORNER_SIZE * 2, InputAction.Right, FillType.Nothing, parallax);
        Wall wallLeft = new Wall(x, y + WALL_CORNER_SIZE, this, 1, sizeY - WALL_CORNER_SIZE * 2, InputAction.Left, FillType.Nothing, parallax);
        Wall wallImage = new Wall(x, y, this, sizeX, sizeY, InputAction.Default, FillType.Tile, parallax);

        addEntity(wallUp);
        addEntity(wallRight);
        addEntity(wallLeft);
        addEntity(wallImage);

        addEntity(wallDown);


    }

    public void addGate(int x, int y, int sizeX, int sizeY, double parallax, int code) {


        Gate wallDown = new Gate(x + WALL_CORNER_SIZE
                , y + sizeY - WALL_CORNER_SIZE-1, this
                , sizeX - WALL_CORNER_SIZE * 2, 1
                , InputAction.Down, FillType.Nothing, parallax, code);

        Gate wallUp = new Gate(x + WALL_CORNER_SIZE, y, this
                , sizeX - WALL_CORNER_SIZE * 2, 1
                , InputAction.Up, FillType.Nothing, parallax, code);

        Gate wallRight = new Gate(x + sizeX, y + WALL_CORNER_SIZE, this
                , 1, sizeY - WALL_CORNER_SIZE * 2, InputAction.Right
                , FillType.Nothing, parallax, code);

        Gate wallLeft = new Gate(x, y + WALL_CORNER_SIZE, this, 1
                , sizeY - WALL_CORNER_SIZE * 2, InputAction.Left
                , FillType.Nothing, parallax, code);

        Gate wallImage = new Gate(x, y, this, sizeX, sizeY
                , InputAction.Default, FillType.Tile, parallax, code);

        addEntity(wallUp);
        addEntity(wallRight);
        addEntity(wallLeft);
        addEntity(wallImage);

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
