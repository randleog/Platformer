import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;

public class Map {

    protected boolean reset = false;


    private static final int MAX_FRAMES = 100000;

    private static final int MAX_SPEED = 50;

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


    private MenuElement backButton;


    public static final double BASE_DRAG_Y = 0.5;

    protected HashMap<Integer, Boolean> keys = new HashMap<>();

    private ArrayList<MenuElement> screenMenu = new ArrayList<>();


    protected double cameraX = 0;
    protected double cameraY = 0;


    private double currentTick = 0;
    public double playerX = 0;
    public double playerY = 0;

    public int finished = 0;

    private double speed = 1;

    private double actualSpeed = 1;

    GameEntity player = null;


    private static final int SMALLER_GRID = 25;
    private static final int GRID_SIZE = 50;
    public HashMap<String, Replay> availableReplays = new HashMap<>();
    private ArrayList<String> availableSpeeds = new ArrayList<>();

    private double speedButtonGap = 85;
    private double replayButtonGap = 175;

    private int sizeX;
    private int sizeY;

    private String name;

    private ArrayList<Integer[]> frames = new ArrayList<>();

    public Map(int sizeX, int sizeY, String name, boolean isReplay) {
        Settings.put("speed", "1");
        finished = 0;


        this.isReplay = isReplay;
        this.name = name;
        this.sizeX = sizeX;
        this.sizeY = sizeY;


        frames.add(new Integer[]{Settings.get("fps"), 0});

        initialiseButtons();
    }

    public void initialiseButtons() {
        availableSpeeds = new ArrayList<>();
        availableSpeeds.add("-4");
        availableSpeeds.add("-2");
        availableSpeeds.add("-1.5");
        availableSpeeds.add("-1");
        availableSpeeds.add("-0.75");
        availableSpeeds.add("-0.5");
        availableSpeeds.add("-0.25");
        availableSpeeds.add("-0.1");
        availableSpeeds.add("-0.01");
        availableSpeeds.add("0");
        availableSpeeds.add("0.01");
        availableSpeeds.add("0.1");
        availableSpeeds.add("0.25");
        availableSpeeds.add("0.5");
        availableSpeeds.add("0.75");
        availableSpeeds.add("1");
        availableSpeeds.add("1.5");
        availableSpeeds.add("2");
        availableSpeeds.add("4");

        screenMenu = new ArrayList<>();
        if (!isReplay) {
            backButton = new SwitchMenuButton(400, 500, 200, 100, "back", "levels");
            if (Settings.get("full speedrun") == 1) {
                screenMenu.add(new SpeedrunBar(1500, 25, this));
            }
        } else {

            backButton = new SwitchMenuButton(400, 500, 200, 100, "back", "replays");

            screenMenu.add(new ToggleButton(25, 775, 200, 100, "change speed", "hide options", "playback speed"));


            for (int i = 0; i < availableSpeeds.size(); i++) {
                screenMenu.add(new SettingButton(25 + (int) (i * speedButtonGap), 900, 75, 75, "speed", availableSpeeds.get(i), MenuElement.TextType.choice));
            }


            ArrayList<String> replays = new ArrayList<>(availableReplays.keySet());

            for (int i = 0; i < replays.size(); i++) {


                screenMenu.add(new MenuText(1400, (int) (65 + i * replayButtonGap)
                        , replays.get(i) + ": ", 30, "time"));

                screenMenu.add(new MenuText(1400, (int) (85 + i * replayButtonGap)
                        , "time: " + Main.formatTime(availableReplays.get(replays.get(i)).getTime()), 15, "time"));

                screenMenu.add(new MenuText(1400, (int) (100 + i * replayButtonGap)
                        , "date: " + availableReplays.get(replays.get(i)).getDate(), 15, "time"));

                screenMenu.add(new MenuText(1400, (int) (115 + i * replayButtonGap)
                        , "fps: " + availableReplays.get(replays.get(i)).getFps(), 15, "time"));


                screenMenu.add(new ToggleButton(1525, (int) (50 + i * replayButtonGap)
                        , 100, 100, "show", "hide", "show " + replays.get(i), Settings.getReplayColor(replays.get(i))));


                screenMenu.add(new SettingButton(1650, (int) (50 + i * replayButtonGap)
                        , 100, 100, "focus", replays.get(i), MenuElement.TextType.normal, Settings.getReplayColor(replays.get(i))));
            }
        }
    }


    public void winGame() {

        Main.hasFinished = true;
        Stats.add("total Time", getTime());

        boolean isPb = isPb();


        if (!isReplay) {


            if (Settings.get("full speedrun") == 1) {
                if (Replay.canProgress(Main.currentFull, getName()) || getName().equals("1")) {

                    Main.currentFull.add(new Replay(frames, getName()));
                    System.out.println(Main.currentFull.size());

                    if (getName().equals(Integer.toString(Main.lastLevel))) {

                        if (UserFileHandler.getTime("full", 1) > Replay.getTime(Main.currentFull) || UserFileHandler.getTime("full", 1) == -1) {

                            Replay.saveReplays(Main.currentFull);
                            System.out.println("congrats");
                        }
                        String data = LocalDate.now() + "|" + Main.getCurrentTime();

                        for (Replay replay : Main.currentFull) {
                            data = data + "|" + Main.formatTime(replay.getTime());
                        }

                        UserFileHandler.saveUserTime("full", Replay.getTime(Main.currentFull), data);

                    }
                }
            }


            ReplaySave.saveReplay(frames, "last\\" + name);

            if (isPb) {
                saveReplay();
            }
            UserFileHandler.saveUserTime(name, getTime(), Main.getCurrentTime());
        }
        if (Settings.get("full speedrun") == 1) {

            Menu.switchMenu("levels");
        } else {
            String message = ((isPb) ? "New best time: " : "Map completed in: ") + Main.formatTime(getTime());
            Stats.add("total Finishes", 1);
            Main.victory(name, new MenuText(475, 250, message, 50, "message"));
        }


    }

    public boolean isPb() {
        return UserFileHandler.getTime(name, 1) > getTime() || UserFileHandler.getTime(name, 1) == -1;
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
        Stats.add("total Time", getTime());
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
        finished = 0;
    }

    public void saveReplay() {


        ReplaySave.saveReplay(frames, name);


    }

    public double getCurrentTick() {

        return currentTick;
    }


    private GameEntity lastOverlay;

    private double mouseOriginX = 0;
    private double mouseOriginY = 0;

    private double mouseLastX = 0;
    private double mouseLastY = 0;

    private boolean mouseDragging = false;
    private ArrayList<GameEntity> overlayEntites = new ArrayList<>();

    private void editorControls() {

        double speed = ((Main.isKeyDown(InputAction.Sprint)) ? 1000 :  500);

        overlayEntites = new ArrayList<>();
        if (Main.isKeyDown(InputAction.Down)) {
            cameraY += speed / Settings.get("fps");
        }
        if (Main.isKeyDown(InputAction.Up)) {
            cameraY -= speed / Settings.get("fps");
        }
        if (Main.isKeyDown(InputAction.Right)) {
            cameraX += speed / Settings.get("fps");
        }

        if (Main.isKeyDown(InputAction.Left)) {
            cameraX -= speed / Settings.get("fps");
        }


        if (Main.mouseDown) {
            if (!mouseDragging) {
                if (Main.reverseUnit(Main.mouseY) > Main.DEFAULT_HEIGHT_MAP * 0.7) {
                    return;
                }
                mouseOriginX = applyGrid(Main.reverseUnit(Main.mouseX) + cameraX);
                mouseOriginY = applyGrid(Main.reverseUnit(Main.mouseY) + cameraY);
                System.out.println(mouseOriginX + " " + mouseOriginY);
            }

            mouseDragging = true;
            GameEntity toolDisplay = null;
            if (Settings.getStr("editor tool").equals("wall")) {

                double sizeX = GRID_SIZE + applyGrid((Main.reverseUnit(Main.mouseX)) + cameraX) - mouseOriginX;
                double sizeY = GRID_SIZE + applyGrid((Main.reverseUnit(Main.mouseY)) + cameraY) - mouseOriginY;


                if (sizeX < 0) {

                    if (sizeY < 0) {
                        toolDisplay = new Wall(mouseOriginX + sizeX, mouseOriginY + sizeY, this
                                , -sizeX, -sizeY, InputAction.Default, FillType.Tile, 1);
                    } else {
                        toolDisplay = new Wall(mouseOriginX + sizeX, mouseOriginY, this
                                , -sizeX, sizeY, InputAction.Default, FillType.Tile, 1);
                    }


                } else if (sizeY < 0) {
                    toolDisplay = new Wall(mouseOriginX, mouseOriginY + sizeY, this
                            , sizeX, -sizeY, InputAction.Default, FillType.Tile, 1);
                } else {
                    toolDisplay = new Wall(mouseOriginX, mouseOriginY, this
                            , sizeX, sizeY, InputAction.Default, FillType.Tile, 1);
                }

                toolDisplay = maxWall((Wall) toolDisplay);


            } else if (Settings.getStr("editor tool").equals("eraser")) {

                erase();
            }


            if (toolDisplay != null) {
                overlayEntites.add(toolDisplay);
                lastOverlay = toolDisplay;
            }

        } else {
            if (mouseDragging) {
                mouseDragging = false;

                if (!(lastOverlay == null)) {
                    addEntityLive(lastOverlay);
                }
                //activate shit
            }
        }


    }


    private Wall maxWall(Wall wall) {
        int maxCollisions = 500;
        int collisions = 0;


        while (getActions(wall).contains(InputAction.Right)) {
            collisions++;
            if (collisions > maxCollisions) {
                return null;
            }
            wall.setX(wall.getX() + 1);
            wall.setSizeX(wall.getSizeX() - 2);
        }
        collisions = 0;
        while (getActions(wall).contains(InputAction.Down)) {
            collisions++;
            if (collisions > maxCollisions) {
                return null;
            }
            wall.setY(wall.getY() + 1);
            wall.setSizeY(wall.getSizeY() - 2);
        }
        collisions = 0;
        while (getActions(wall).contains(InputAction.Up)) {
            collisions++;
            if (collisions > maxCollisions) {
                return null;
            }
            wall.setSizeY(wall.getSizeY() - 1);
        }
        collisions = 0;
        while (getActions(wall).contains(InputAction.Down)) {
            collisions++;
            if (collisions > maxCollisions) {
                return null;
            }
            wall.setSizeX(wall.getSizeX() - 1);
        }

        wall.reset();

        return wall;
    }


    private void erase() {
        Square mouseIntesect = new Square(Main.reverseUnit(Main.mouseX) + cameraX, Main.reverseUnit(Main.mouseY) + cameraY, SMALLER_GRID, SMALLER_GRID, 1, InputAction.Default);

        ArrayList<GameEntity> removeEntites = new ArrayList<>();

        for (GameEntity entity : entities) {
            if (mouseIntesect.intersect(entity.getMainShape())) {
                removeEntites.add(entity);
            }

        }

        for (GameEntity entity : removeEntites) {
            entities.remove(entity);

        }
        lastOverlay = null;
    }

    private int applyGrid(double input) {
        int gridSize = (Main.isKeyDown(InputAction.Control) ? SMALLER_GRID : GRID_SIZE);

        return ((int) (input / gridSize)) * gridSize;
    }


    public void tick() {

        if (Menu.currentMenu.equals("editor")) {
            editorControls();
        }


        actualSpeed = Settings.getStrD("speed");


        for (MenuElement button : screenMenu) {
            if (availableSpeeds.contains(button.getText())) {
                if (Settings.get("playback speed") < 0) {
                    button.setHideButton(true);
                } else {
                    button.setHideButton(false);
                }
            }
        }


        if (Main.getKey(InputAction.Menu) > 0) {

            for (MenuElement button : screenMenu) {
                button.tick();
            }
            //handle replay speed
            if (isReplay || Menu.currentlyMenu) {

                currentTick += actualSpeed;


            } else if (!(Menu.currentMenu.equals("editor"))) {


                currentTick++;
                if (Main.isKeyDown(InputAction.Reset)) {
                    Main.deactivateKey(InputAction.Reset);
                    reset();
                }
                if (frames.size() < MAX_FRAMES) {
                    frames.add(new Integer[]{(int) player.getNonRenderX(), (int) player.getNonRenderY(), (int) player.getNonRenderSizeY()});
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


            if (isReplay || Menu.currentlyMenu) {

                if (finished >= availableReplays.size()) {
                    resetTimer();
                }
            }

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
        return (currentTick) / Settings.getD("fps");
    }

    public String getName() {
        return name;
    }


    public void render(GraphicsContext g) {


        for (GameEntity entity : particles) {
            entity.render(g);
        }
        for (GameEntity entity : entities) {
            if (entity.getMainShape().intersect(new Square(cameraX, cameraY, g.getCanvas().getWidth(), g.getCanvas().getHeight(), 1, InputAction.Default))) {
                entity.render(g);
            }

        }

        for (GameEntity entity : overlayEntites) {
            entity.render(g);
        }


        //bounds
        g.setStroke(Color.color(1, 0, 0));
        g.setLineWidth(10);
        g.strokeRect(correctUnit(-sizeX - cameraX), correctUnit(-sizeY - cameraY), correctUnit(sizeX * 2), correctUnit(sizeY * 2));


        //prevents actions not wanted during menu screen
        if (Menu.currentlyMenu) {
            return;
        }

        g.setFont(new Font(Settings.FONT, Main.correctUnit(25)));
        g.setFill(Color.color(1, 1, 1));
        g.fillText("possible fps: " + Main.possibleFps, correctUnit(25), correctUnit(50));
        g.fillText("time: " + Main.formatTime(currentTick / Settings.getD("fps")), correctUnit(350), correctUnit(50));


        if (!(Main.getKey(InputAction.Menu) > 0)) {

            g.setFill(Color.color(0, 0, 0, 0.4));
            g.fillRect(0, 0, g.getCanvas().getWidth(), g.getCanvas().getHeight());

            backButton.tick();
            backButton.render(g);

            g.setFill(Color.color(1, 1, 1));
            g.setFont(new Font(Settings.FONT, Main.correctUnit(40)));
            g.fillText("Paused", correctUnit(100), correctUnit(200));
            g.setFont(new Font(Settings.FONT, Main.correctUnit(25)));
            String currentAction = "";
            currentAction = currentAction + getStringKey(InputAction.Up);
            currentAction = currentAction + getStringKey(InputAction.Sprint);
            currentAction = currentAction + getStringKey(InputAction.Hook);
            currentAction = currentAction + getStringKey(InputAction.Left);
            currentAction = currentAction + getStringKey(InputAction.Right);
            currentAction = currentAction + getStringKey(InputAction.Down);
            g.fillText(currentAction, correctUnit(100), correctUnit(400));
        }
        for (MenuElement button : screenMenu) {
            button.render(g);
        }


        if (Menu.currentMenu.equals("editor")) {
            g.setFill(Color.color(0,1,0,0.2));
            g.fillRect(Main.correctUnit(playerX-cameraX), Main.correctUnit(playerY-cameraY), Main.correctUnit(100), Main.correctUnit(100));
        }

        if (isReplay) {

            g.setFill(Color.WHITE);
            g.setFont(new Font(Settings.FONT, Main.correctUnit(40)));
            g.fillText("x" + String.format("%.2f", actualSpeed), correctUnit(225), correctUnit(825));
        }


        if (Settings.get("debug") > 0) {
            if (Main.mouseDown) {

                g.setLineWidth(Main.correctUnit(1));
                g.setStroke(Color.RED);
                g.strokeLine(0, Main.correctUnit(mouseOriginY - cameraY), Main.correctUnit(Main.DEFAULT_WIDTH_MAP), Main.correctUnit(mouseOriginY - cameraY));
                g.strokeLine(Main.correctUnit(mouseOriginX - cameraX), 0, Main.correctUnit(mouseOriginX - cameraX), Main.correctUnit(Main.DEFAULT_HEIGHT_MAP));
            } else {


                g.setLineWidth(Main.correctUnit(1));
                g.setStroke(Color.color(1, 1, 1, 0.3));
                g.strokeLine(0, Main.mouseY, Main.correctUnit(Main.DEFAULT_WIDTH_MAP), Main.mouseY);
                g.strokeLine(Main.mouseX, 0, Main.mouseX, Main.correctUnit(Main.DEFAULT_HEIGHT_MAP));

            }


            g.setFill(Color.WHITE);
            g.setFont(new Font(Settings.FONT, Main.correctUnit(20)));
            g.fillText("x: " + String.format("%.2f", (Main.reverseUnit(Main.mouseX) + cameraX)) + "\ny: " + String.format("%.2f", (Main.reverseUnit(Main.mouseY) + cameraY)), Main.mouseX, Main.mouseY);
        }


    }






    public String toString() {
        String lines = sizeX + " " + sizeY + " " + (int)playerX + " " + (int)playerY + "\n/\n/";

        for (GameEntity entity : entities) {
            lines = lines + "\n" + entity.toString() + ";";
        }

        return lines;
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
