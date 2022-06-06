import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.HashMap;

public class Map {

    protected boolean reset = false;


    private static final int MAX_FRAMES = 43200;

    private static final int MAX_SPEED = 5;

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

    protected HashMap<Integer, Boolean> keys = new HashMap<>();

    private ArrayList<MenuButton> screenMenu = new ArrayList<>();


    protected double cameraX = 0;
    protected double cameraY = 0;


    private double currentTick = 0;
    public double playerX = 0;
    public double playerY = 0;

    private double speed = 1;

    private double actualSpeed = 1;

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
            backButton = new LevelsMenuButton(400, 500, 200, 100);
            if (Main.settings.get("full speedrun") == 1) {
                screenMenu.add(new SpeedrunBar(1500, 25, this));
            }
        } else {
            backButton = new ReplayMenuButton(400, 500, 200, 100);
            screenMenu.add(new SpeedupButton(100, 800, 200, 100, "<<", -0.1, this));
            screenMenu.add(new SpeedupButton(300, 800, 200, 100, ">>", 0.1, this));

            screenMenu.add(new ToggleButton(1500, 400, 200,100, "show author"));
            screenMenu.add(new ToggleButton(1500, 550, 200,100, "show gold"));
            screenMenu.add(new ToggleButton(1500, 700, 200,100, "show player"));
            screenMenu.add(new ToggleButton(1500, 850, 200,100, "show full speedrun"));
        }

        frames.add(new Integer[]{Main.fps, 0});


    }

    public void winGame() {
        if (!isReplay) {
            if (Main.settings.get("full speedrun") == 1) {
                if (Replay.canProgress(Main.currentFull, getName())) {




                    Main.currentFull.add(new Replay(frames, getName()));
                    System.out.println(Main.currentFull.size());

                    if (getName().equals(Integer.toString(Main.lastLevel))) {

                        if (UserFileHandler.getUserTime("full", 1) > Replay.getTime(Main.currentFull) || UserFileHandler.getUserTime("full", 1) == -1) {

                            Replay.saveReplays(Main.currentFull);
                            System.out.println("congrats");
                        }
                        UserFileHandler.saveUserTime("full", Replay.getTime(Main.currentFull));

                    }
                }
            }
            saveReplay();
            UserFileHandler.saveUserTime(name, getTime());
        }
        Main.switchMenu(Main.levelMenu);
    }


    public void increaseSpeed(double ammount) {
        speed += ammount;
        speed = Math.max(speed, -MAX_SPEED);
        speed = Math.min(speed, MAX_SPEED);
        actualSpeed = Math.pow(speed, 2) * Math.signum(speed);
    }

    public boolean isRadius(double x, double y, double x2, double y2, double radius) {
        return (Math.sqrt(Math.pow(Math.abs(x2 - x), 2) + Math.pow(Math.abs(y2 - y), 2)) < radius);
    }

    public boolean isOutOfBounds(double x, double y, double sizeX, double sizeY) {
        return (x + sizeX > this.sizeX || x < -this.sizeX || y + sizeY > this.sizeY || y < -this.sizeY);
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

        Main.currentFull = new ArrayList<>();
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


    public void resetTimer() {
        this.currentTick = 0;
    }

    public void saveReplay() {
        if (!isReplay) {

            if (UserFileHandler.getUserTime(name, 1) > getTime() || UserFileHandler.getUserTime(name, 1) == -1) {
                ReplaySave.saveReplay(frames, name);
            }

        }
    }

    public double getCurrentTick() {

        return currentTick;
    }

    public void tick() {


        if (Main.getKey(InputAction.Menu) > 0) {

            for (MenuButton button : screenMenu) {
                button.tick();
            }
            //handle replay speed
            if (isReplay) {
                currentTick += actualSpeed;

            } else {
                currentTick++;
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
        cameraX = Math.min(cameraX, sizeX - Main.DEFAULT_WIDTH_MAP);
        cameraX = Math.max(cameraX, -sizeX + Main.DEFAULT_WIDTH_MAP);
        cameraY = Math.min(cameraY, sizeY - Main.DEFAULT_HEIGHT_MAP);
        cameraY = Math.max(cameraY, -sizeY + Main.DEFAULT_HEIGHT_MAP);
    }


    public double getTime() {
        return (currentTick ) / Main.fps;
    }

    public String getName() {
        return name;
    }

    private double rotation = 0;

    private static final double ROTATION_TIME = 0.1;


    public void render(GraphicsContext g) {

        /*
        g.save();
        g.translate(g.getCanvas().getHeight()*Main.EXPECTED_ASPECT_RATIO/2,g.getCanvas().getHeight()/2);

        double playerRotate = -player.getCornerRotation();

        g.rotate(Main.interpolate(rotation, Math.toDegrees(playerRotate), ROTATION_TIME*Main.fps, currentTick%  (ROTATION_TIME*Main.fps)));
        g.translate(-g.getCanvas().getHeight()*Main.EXPECTED_ASPECT_RATIO/2, -g.getCanvas().getHeight()/2);
        if (currentTick% (ROTATION_TIME*Main.fps) >(ROTATION_TIME*Main.fps)-1) {
            rotation = Math.toDegrees(playerRotate);
        }

         */


        for (GameEntity entity : particles) {
            entity.render(g);
        }
        for (GameEntity entity : entities) {
            entity.render(g);
        }
        //   g.restore();
        //bounds
        g.setStroke(Color.color(1, 0, 0));
        g.setLineWidth(10);
        g.strokeRect(correctUnit(-sizeX - cameraX), correctUnit(-sizeY - cameraY), correctUnit(sizeX * 2), correctUnit(sizeY * 2));


        g.setFont(new Font(Main.correctUnit(25)));
        g.setFill(Color.color(1, 1, 1));
        g.fillText("deaths: " + Main.deaths, correctUnit(50), correctUnit(50));
        g.fillText("time: " + String.format("%.2f", currentTick * 1.0 / Main.fps), correctUnit(200), correctUnit(50));

        if (Main.settings.get("full speedrun") == 1) {
            g.fillText("full speedrun progress: " + Main.currentFull.size(), correctUnit(400), correctUnit(50));
        }

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
        for (MenuButton button : screenMenu) {
            button.render(g);
        }
        if (isReplay) {

            g.setFill(Color.color(1, 1, 1));
            g.setFont(new Font(Main.correctUnit(40)));
            g.fillText("x" + String.format("%.2f", actualSpeed), correctUnit(500), correctUnit(800));
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


        addEntity(wallImage);


    }

    public void addCornerWall(int x, int y, int sizeX, int sizeY, double parallax, double rotation) {


        CornerWall wallImage = new CornerWall(x, y, this, sizeX, sizeY, InputAction.Corner, FillType.Tile, parallax, rotation);

        addEntity(wallImage);


    }


    public void addWall(int x, int y, int sizeX, int sizeY, double parallax) {


        Wall wallImage = new Wall(x, y, this, sizeX, sizeY, InputAction.Default, FillType.Tile, parallax);


        addEntity(wallImage);


    }

    public void addGate(int x, int y, int sizeX, int sizeY, double parallax, int code) {



        Gate wallImage = new Gate(x, y, this, sizeX, sizeY
                , InputAction.Default, FillType.Tile, parallax, code);

        addEntity(wallImage);




    }


    public ArrayList<InputAction> getActions(GameEntity entity) {
        ArrayList<InputAction> actions = new ArrayList<>();

        for (GameEntity currentEntity : entities) {
            if (currentEntity.getParallax() == entity.getParallax()) {
                if (!entity.equals(currentEntity)) {
                    if (currentEntity.getMainShape().intersect(entity.getMainShape())) {
                        currentEntity.flagAll();
                        if (!(currentEntity.getShape(entity.getMainShape()) == null)) {
                            actions.add(currentEntity.getShape(entity.getMainShape()).getAction());
                        }

                    }
                }


            }
        }

        return actions;
    }

    private boolean isValidEntity(GameEntity entity, GameEntity currentEntity) {
        if (currentEntity.isWall()) {
            if (currentEntity.getParallax() == entity.getParallax()) {
                if (!entity.equals(currentEntity)) {
                    return !(currentEntity.getShape(entity.getMainShape()) == null);
                }
            }
        }
        return false;
    }

    public Square intersectionWall(GameEntity entity) {
        Square returnEntity = null;
        for (GameEntity currentEntity : entities) {

            if (currentEntity.getMainShape().intersect(entity.getMainShape())) {
                currentEntity.flagAll();
                if (isValidEntity(entity, currentEntity)) {
                    if (InputAction.isYType(currentEntity.getShape(entity.getMainShape()).getAction())) {

                        return currentEntity.getShape(entity.getMainShape());
                    } else {

                        returnEntity = currentEntity.getShape(entity.getMainShape());
                    }

                }
            }
        }


        return returnEntity;
    }


}
