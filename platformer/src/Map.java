import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.HashMap;

public class Map {

    protected boolean reset = false;


    private static final int MAX_FRAMES = 43200;

    private static final int MAX_SPEED = 16;

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


    private boolean isReplay;





    private MenuButton backButton;


    public static final double BASE_DRAG_Y = 0.5;

    protected HashMap<Integer,Boolean> keys = new HashMap<>();

    private ArrayList<MenuButton> replayButtons = new ArrayList<>();


    protected double cameraX = 0;
    protected double cameraY = 0;



    private double currentTick = 0;
    public double playerX = 0;
    public double playerY = 0;

    private double speed = 1;

    GameEntity player = null;

    private int sizeX;
    private int sizeY;

    private String name;

    private ArrayList<Integer[]> frames = new ArrayList<>();

    public Map(int sizeX, int sizeY, String name, boolean isReplay) {

        this.isReplay = isReplay;
        this.name = name;
        this.sizeX = sizeX;
        this.sizeY = sizeY;

        if (!isReplay) {
           backButton = new LevelsMenuButton(400,500,200,100);
        } else {
            backButton = new ReplayMenuButton(400,500,200,100);
            replayButtons.add(new SpeedupButton(100,800,200, 100, "<<", -0.1,this));
            replayButtons.add(new SpeedupButton(300,800,200, 100, ">>", 0.1,this));
        }

        frames.add(new Integer[]{Main.fps, 0});

    }

    public void winGame() {
        if (!isReplay) {
            saveReplay();
            UserFileHandler.saveUserTime(name, getTime());

        }
        Main.switchMenu(Main.levelMenu);
    }


    public void increaseSpeed(double ammount) {
        speed+= ammount;
        speed = Math.max(speed, -MAX_SPEED);
        speed = Math.min(speed,MAX_SPEED);
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
        Main.playMap(MapLoader.loadMap(name, 1));

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
        return input * Main.gameUnit;

    }

    public double reverseCorrectUnit(double input) {
        return input / Main.gameUnit;
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

    public void saveReplay() {
        if (!isReplay) {
            if (UserFileHandler.getUserTime(name, 1) > getTime()) {
                ReplaySave.saveReplay(frames, name);
            }

        }
    }

    public int getCurrentTick() {

        return (int)currentTick;
    }

    public void tick() {


        //handle normal gameplay speed
        if (!isReplay) {
            currentTick++;
        }




        if (Main.getKey(InputAction.Menu) > 0) {


            //handle replay speed
            if (isReplay) {
                currentTick += speed;
                for (MenuButton button : replayButtons) {
                    button.tick();
                }
            } else {
                if (Main.isKeyDown(InputAction.Reset)) {
                    Main.deactivateKey(InputAction.Reset);
                    reset();
                }
                if (frames.size() < MAX_FRAMES) {
                    frames.add(new Integer[]{(int) playerX, (int) playerY});
                }

            }





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
        cameraX = Math.min(cameraX, sizeX-Main.DEFAULT_WIDTH_MAP);
        cameraX = Math.max(cameraX, -sizeX+ Main.DEFAULT_WIDTH_MAP);
        cameraY = Math.min(cameraY, sizeY-Main.DEFAULT_HEIGHT_MAP);
        cameraY = Math.max(cameraY, -sizeY+Main.DEFAULT_HEIGHT_MAP);
    }


    public double getTime() {
        return (currentTick*1.0)/Main.fps;
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



        g.setFont(new Font(Main.correctUnit(25)));
        g.setFill(Color.color(1, 1, 1));
        g.fillText("deaths: " + Main.deaths, correctUnit(50), correctUnit(50));
        g.fillText("time: " + String.format("%.2f",currentTick*1.0/Main.fps), correctUnit(200), correctUnit(50));


        if (!(Main.getKey(InputAction.Menu) > 0)) {

            g.setFill(Color.color(0, 0, 0, 0.4));
            g.fillRect(0, 0, g.getCanvas().getWidth(), g.getCanvas().getHeight());

            backButton.tick();
            backButton.render(g);

            g.setFill(Color.color(1, 1, 1));
            g.setFont(new Font(Main.correctUnit(40)));
            g.fillText("Paused", correctUnit(100), correctUnit(200));
            g.setFont(new Font(Main.correctUnit(25)));
            String currentAction = "";
            currentAction = currentAction + getStringKey(InputAction.Up);
            currentAction = currentAction + getStringKey(InputAction.Sprint);
            currentAction = currentAction + getStringKey(InputAction.Hook);
            currentAction = currentAction + getStringKey(InputAction.Left);
            currentAction = currentAction + getStringKey(InputAction.Right);
            currentAction = currentAction + getStringKey(InputAction.Down);
            g.fillText(currentAction, correctUnit(100), correctUnit(400));
        }
        if (isReplay) {
            for (MenuButton button : replayButtons) {
                button.render(g);
            }
            g.setFill(Color.color(1, 1, 1));
            g.setFont(new Font(Main.correctUnit(40)));
            g.fillText("x" + speed, correctUnit(500), correctUnit(800));
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

    public void addCornerWall(int x, int y, int sizeX, int sizeY, double parallax, double rotation) {



        CornerWall wallUp = new CornerWall(x + WALL_CORNER_SIZE, y, this, sizeX - WALL_CORNER_SIZE * 2, 1, InputAction.Corner, FillType.Tile, parallax, rotation);

        CornerWall wallImage = new CornerWall(x, y, this, sizeX, sizeY, InputAction.Default, FillType.Tile, parallax, rotation);

        addEntity(wallUp);

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
