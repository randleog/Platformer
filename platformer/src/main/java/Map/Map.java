package Map;

import GameControl.Square;
import GameControl.TimedSound;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

import Main.Main;
import Util.Settings;
import Menu.Menu;
import Menu.MenuElement;
import Menu.SettingButton;
import Util.Replay;
import Util.UserFileHandler;
import Util.ImageLoader;
import Menu.SpeedrunBar;
import Menu.ToggleButton;
import Menu.MenuText;
import Menu.SwitchMenuButton;
import Util.ReplaySave;
import Util.Stats;
import Util.MapLoader;
import Util.SoundLoader;

//edits = 1
public class Map {

    protected boolean reset = false;

    private int currentCode = 0;


    public boolean trance = false;


    private static final int MAX_FRAMES = 10000;

    private static final int MAX_SPEED = 50;


    private static final int MAX_PARTICLES = 500;

    private ArrayList<GameEntity> entities = new ArrayList<>();
    private ArrayList<GameEntity> nextEntities = new ArrayList<>();

    private ArrayList<GameEntity> particles = new ArrayList<>();
    private ArrayList<GameEntity> nextParticles = new ArrayList<>();

    private ArrayList<GameEntity> startEntities = new ArrayList<>();

    public static final double GRAVITY = 15;

    public static final double TRANCE_GRAVITY = 3;

    public static final double WALL_CORNER_SIZE = 10.0;

    public static final double AIR_DRAG = 0.4;
    public static final double GROUND_DRAG = 0.0001;
    public static final double SWIMMING_DRAG = 0.0001;

    private static final int CRASH_PARTICLE_COUNT = 10;

    private String backgroundName;


    private Background background;

    private String world;


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

    private boolean fullSpeedrun = false;

    GameEntity player = null;

    private TimedSound placeSound;


    public static final int SMALLER_GRID = 25;
    public static final int GRID_SIZE = 50;
    public HashMap<String, Replay> availableReplays = new HashMap<>();
    private ArrayList<String> availableSpeeds = new ArrayList<>();

    private double speedButtonGap = 85;
    private double replayButtonGap = 175;

    private int sizeX;
    private int sizeY;

    private String name;

    private static final int DAY_LENGTH = 5;//300;

    private int countingFactor = 2;

    private ArrayList<Integer[]> frames = new ArrayList<>();


    private ArrayList<BackgroundObject> backgroundObjects;

    private ArrayList<GameEntity> lighting;
    private ArrayList<GameEntity> nextLighting;


    private ArrayList<String[]> messages;

    public Map(int sizeX, int sizeY, String name, boolean isReplay, String world) {

        messages = new ArrayList<>();


        placeSound = new TimedSound(75);


        nextLighting = new ArrayList<>();
        lighting = new ArrayList<>();

        backgroundObjects = new ArrayList<>();

        background = new Background();
        backgroundName = "empty";
        Settings.put("speed", "1");
        finished = 0;

        this.world = world;


        this.isReplay = isReplay;
        this.name = name;
        this.sizeX = sizeX;
        this.sizeY = sizeY;

        if (!Menu.currentMenu.equals("editor")) {
            fullSpeedrun = (Settings.get("full speedrun") == 1);
        }

        frames.add(new Integer[]{Settings.get("fps")/countingFactor, 0});

        initialiseButtons();


    }





    public void setBackground(Background background, String backgroundName) {
        this.background = background;
        this.backgroundName = backgroundName;
    }

    public double getGravity() {
        if (trance) {
            return TRANCE_GRAVITY;
        } else {
            return GRAVITY;
        }
    }

    public void trance() {
        this.trance = !this.trance;

    }

    public void moveCamera(double dx, double dy) {
        cameraX+=Main.correctFPS(dx);
        cameraY+=Main.correctFPS(dy);
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
            backButton = new SwitchMenuButton(400, 500, 200, 100, "back", world + " levels");
            if (fullSpeedrun) {
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
        System.out.println("is pb: " + isPb);


        if (!isReplay) {


            if (fullSpeedrun) {
                if (Replay.canProgress(Main.currentFull, getName()) || getName().equals("1")) {

                    Main.currentFull.add(new Replay(frames, getName()));


                    if (name.equals(Integer.toString(UserFileHandler.getLastLevel(getWorld())))) {

                        if (UserFileHandler.getTime(world + "\\full", 1) > Replay.getTime(Main.currentFull) || UserFileHandler.getTime(world + "\\full", 1) == -1) {

                            Replay.saveReplays(Main.currentFull);

                        }
                        String data = LocalDate.now() + "|" + Main.getCurrentTime();

                        for (Replay replay : Main.currentFull) {
                            data = data + "|" + Main.formatTime(replay.getTime());
                        }

                        UserFileHandler.saveUserTime(world , "full", Replay.getTime(Main.currentFull), data);
                        Main.currentFull = new ArrayList<>();

                    }
                }
            }


            ReplaySave.saveReplay(frames, world + "\\" + "last\\" + name);

            if (isPb) {
                System.out.println("saving new best");
                saveReplay();
            }
            UserFileHandler.saveUserTime(world, name, getTime(), Main.getCurrentTime());
        }
        if (fullSpeedrun) {

            Menu.switchMenu( world + " levels");
        } else {
            String message = ((isPb) ? "New best time: " : "Map completed in: ") + Main.formatTime(getTime());
            Stats.add("total Finishes", 1);
            Main.victory(world + "\\" +name, new MenuText(475, 250, message, 50, "message"));
        }


    }

    public boolean isPb() {
        return UserFileHandler.getTime(getName(), 1) > getTime() || UserFileHandler.getTime(getName(), 1) == -1;
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
        Main.playMap(MapLoader.loadMap(world + "\\" +name, 1));

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

        if (entity instanceof Candle || entity instanceof Light) {
            lighting.add(entity);
        } else {
            entities.add(entity);
        }


    }


    public void addParticle(GameEntity entity) {
        particles.add(entity);
    }

    public void addEntityLive(GameEntity entity) {


        if (entity instanceof Candle || entity instanceof Light) {
            nextLighting.add(entity);
        } else {
            nextEntities.add(entity);
        }


    }


    public void addParticleLive(GameEntity entity) {
        if (nextParticles.size()+particles.size() > MAX_PARTICLES) {
            return;
        }
        nextParticles.add(entity);
    }


    public void resetTimer() {
        this.currentTick = 0;
        finished = 0;
    }

    public void saveReplay() {


        ReplaySave.saveReplay(frames, world + "\\" +name);


    }

    public double getCurrentTick() {

        return currentTick;
    }


    public void movePlayer(double x, double y) {
        player.setX(x);
        player.setY(y);
    }


    private GameEntity lastOverlay;
    private BackgroundObject lastBackground;

    private double mouseOriginX = 0;
    private double mouseOriginY = 0;

    private double lastX = 0;
    private double lastY = 0;

    private boolean mouseDragging = false;
    private ArrayList<GameEntity> overlayEntites = new ArrayList<>();

    private DimensionPortal lastPortal;


    private HashMap<InputAction, ArrayList<Square>> actions;

    private void editorControls() {


        String tool = Settings.getStr("editor tool");

        double speed = ((Main.isKeyDown(InputAction.Sprint)) ? 1000 : 500);

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


            }

            mouseDragging = true;
            lastOverlay = null;
            lastBackground = null;


            GameEntity toolDisplay = null;
            BackgroundObject backgroundDisplay = null;



            switch (tool) {
                case "wall", "gate", "stickyWall", "water", "lava", "corner", "sandTile", "gear", "gearSpeed", "pink","candleNot", "candle":
                    toolDisplay = placeTile();
                    break;
                case "background wall":
                    backgroundDisplay = placeBackgroundTile();
                    break;
                case "key":
                    toolDisplay = new Key(Main.reverseUnit(Main.mouseX) + cameraX, Main.reverseUnit(Main.mouseY) + cameraY, this, 1, currentCode);
                    break;
                case "shurikan":
                    Shurikan shurikan = new Shurikan(Main.reverseUnit(Main.mouseX) + cameraX, Main.reverseUnit(Main.mouseY) + cameraY, this);
                    shurikan.setVelY(7);
                    toolDisplay = shurikan;
                    break;
                case "portal":
                    lastPortal =  new DimensionPortal(Main.reverseUnit(Main.mouseX) + cameraX, Main.reverseUnit(Main.mouseY) + cameraY, this, Main.reverseUnit(Main.mouseX) + cameraX + 100,Main.reverseUnit(Main.mouseY) + cameraY);
                    toolDisplay = lastPortal;
                    break;
                case "plate":
                    toolDisplay = new Plate(Main.reverseUnit(Main.mouseX) + cameraX, Main.reverseUnit(Main.mouseY) + cameraY, this, 1, currentCode);
                    break;
                case "flag":
                    toolDisplay = new Flag(Main.reverseUnit(Main.mouseX) + cameraX, Main.reverseUnit(Main.mouseY) + cameraY, this);
                    break;
                case "eraser":
                    erase();
                    break;
                case "portalLocation":
                    if (lastPortal != null) {
                        lastPortal.setTpX(Main.reverseUnit(Main.mouseX) + cameraX);
                    }
                    if (lastPortal != null) {
                        lastPortal.setTpY(Main.reverseUnit(Main.mouseY) + cameraY);
                    }
                    break;
                case "player":
                    playerX = Main.reverseUnit(Main.mouseX) + cameraX;
                    playerY = Main.reverseUnit(Main.mouseY) + cameraY;
                    break;
            }


            if (toolDisplay != null) {
                overlayEntites.add(toolDisplay);
                lastOverlay = toolDisplay;
            }

            if (backgroundDisplay != null) {
                lastBackground = backgroundDisplay;

            }

        } else {
            if (mouseDragging) {
                for (GameEntity entity : entities) {
                    entity.loadHitbox();
                }
                mouseDragging = false;


                if (!(lastOverlay == null)) {
                    if (lastOverlay.getSizeX() < 1 || lastOverlay.getSizeY() < 1) {

                    } else {

                        if (lastOverlay instanceof Key || lastOverlay instanceof Plate) {
                            currentCode = getNextCode();
                            lastOverlay.setCode(currentCode);
                        }

                        addEntityLive(lastOverlay);
                        lastOverlay.placeAnimate();

                        placeSound.play(SoundLoader.fall, 1, 1);

                    }
                }


                if (!(lastBackground == null)) {
                    if (lastBackground.getSizeX() < 1 || lastBackground.getSizeY() < 1) {

                    } else {


                        backgroundObjects.add(lastBackground);
                        placeSound.play(SoundLoader.fall, 1, 1);

                    }
                }
                //activate shit
            }
        }


    }

    public String getWorld() {
        return world;
    }




    private BackgroundObject placeBackgroundTile() {

        double sizeX = GRID_SIZE + applyGrid((Main.reverseUnit(Main.mouseX)) + cameraX) - mouseOriginX;
        double sizeY = GRID_SIZE + applyGrid((Main.reverseUnit(Main.mouseY)) + cameraY) - mouseOriginY;


        BackgroundObject tile = new BackgroundObject(mouseOriginX,mouseOriginY, sizeX, sizeY, this, ImageLoader.wallTile, 1);


        if ((int) sizeX != (int) lastX) {
            SoundLoader.playSound(SoundLoader.stone, 1, 0, 1);

        } else if ((int) sizeY != (int) lastY) {
            SoundLoader.playSound(SoundLoader.stone, 1, 0, 1);

        }

        lastX = sizeX;
        lastY = sizeY;


        if (sizeX < 0) {

            if (sizeY < 0) {


                tile.setSizeX(-sizeX);
                tile.setSizeY(-sizeY);
                tile.setX(mouseOriginX + sizeX);
                tile.setY(mouseOriginY + sizeY);

            } else {



                tile.setSizeX(-sizeX);
                tile.setX(mouseOriginX + sizeX);
            }


        } else if (sizeY < 0) {

            tile.setSizeY(-sizeY);
            tile.setY(mouseOriginY + sizeY);

        }

        return tile;
        //  toolDisplay = maxWall((Map.Wall) toolDisplay);

    }





    private GameEntity placeTile() {


        GameEntity tile;

        String tool = Settings.getStr("editor tool");


        double sizeX = GRID_SIZE + applyGrid((Main.reverseUnit(Main.mouseX)) + cameraX) - mouseOriginX;
        double sizeY = GRID_SIZE + applyGrid((Main.reverseUnit(Main.mouseY)) + cameraY) - mouseOriginY;

        tile = switch (tool) {
            case "gate" -> new Gate(mouseOriginX, mouseOriginY, this, sizeX, sizeY, InputAction.Default, FillType.Tile, 1, currentCode);
            case "corner" -> new CornerWall(mouseOriginX, mouseOriginY, this, sizeX, sizeY, InputAction.Default, FillType.Tile, 1, 315);
            case "stickyWall" -> new StickyWall(mouseOriginX, mouseOriginY, this, sizeX, sizeY, InputAction.Default, FillType.Tile, 1);
            case "water" -> MapLoader.getWater(mouseOriginX, mouseOriginY, this, sizeX, sizeY);
            case "lava" -> MapLoader.getLava(mouseOriginX, mouseOriginY, this, sizeX, sizeY);
            case "gear" -> new Gear(mouseOriginX, mouseOriginY, sizeX, sizeX, this, 0, currentCode);
            case "gearSpeed" -> new Gear(mouseOriginX, mouseOriginY, sizeX, sizeX, this, 1.5, currentCode);
            case "candle" -> new Candle(mouseOriginX, mouseOriginY,sizeX, sizeX, this);
            case "candleNot" -> new Light(mouseOriginX, mouseOriginY,sizeX, sizeX, this);
            default -> new Wall(mouseOriginX, mouseOriginY, this, sizeX, sizeY, InputAction.Default, FillType.Tile, 1);

        };

        if (tool.equals("sandTile")) {
            ((Wall)tile).setType("sand");
            ((Wall)tile).setImage(ImageLoader.sandTile);
        } if (tool.equals("pink")) {
            ((Wall)tile).setType("pink");
            ((Wall)tile).setImage(ImageLoader.pinkWall);
        }


        if ((int) sizeX != (int) lastX) {
            SoundLoader.playSound(SoundLoader.stone, 1, 0, 1);

        } else if ((int) sizeY != (int) lastY) {
            SoundLoader.playSound(SoundLoader.stone, 1, 0, 1);

        }

        lastX = sizeX;
        lastY = sizeY;


        if (sizeX < 0) {

            if (sizeY < 0) {

                if (tile instanceof CornerWall) {
                    ((CornerWall) tile).setRotation(405);
                }

                tile.setSizeX(-sizeX);
                tile.setSizeY(-sizeY);
                tile.setX(mouseOriginX + sizeX);
                tile.setY(mouseOriginY + sizeY);

            } else {
                if (tile instanceof CornerWall) {
                    ((CornerWall) tile).setRotation(225);
                }


                tile.setSizeX(-sizeX);
                tile.setX(mouseOriginX + sizeX);
            }




        } else if (sizeY < 0) {
            if (tile instanceof CornerWall) {
                ((CornerWall) tile).setRotation(495);
            }

            tile.setSizeY(-sizeY);
            tile.setY(mouseOriginY + sizeY);

        }
       // tile.loadHitbox();

        return tile;
        //  toolDisplay = maxWall((Map.Wall) toolDisplay);

    }

    private int getNextCode() {
        int largestCode = 0;

        for (GameEntity entity : entities) {
            if (entity instanceof Key) {
                if (largestCode <= ((Key) entity).getCode()) {
                    largestCode = ((Key) entity).getCode() + 1;
                }
            } else if (entity instanceof Plate) {
                if (largestCode <= ((Plate) entity).getCode()) {
                    largestCode = ((Plate) entity).getCode() + 1;
                }
            }
        }
        return largestCode;
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
        while (getActions(wall).contains(InputAction.Left)) {
            collisions++;
            if (collisions > maxCollisions) {
                return null;
            }
            wall.setSizeX(wall.getSizeX() - 1);
        }

        wall.reset();


        return wall;
    }

    private void eraserEntities() {
        Square mouseIntesect = new Square(Main.reverseUnit(Main.mouseX) + cameraX, Main.reverseUnit(Main.mouseY) + cameraY, SMALLER_GRID, SMALLER_GRID, 1, InputAction.Default);

        ArrayList<GameEntity> removeEntites = new ArrayList<>();

        for (GameEntity entity : entities) {
            if (mouseIntesect.intersect(entity.getMainShape())) {
                removeEntites.add(entity);
            }

        }

        for (GameEntity entity : removeEntites) {
            placeSound.play(SoundLoader.suck, 0.5, 1);

            entities.remove(entity);

        }
        lastOverlay = null;
        lastBackground = null;
    }

    private void eraserWall() {
        Square mouseIntesect = new Square(Main.reverseUnit(Main.mouseX) + cameraX, Main.reverseUnit(Main.mouseY) + cameraY, 1, 1, 1, InputAction.Default);

        ArrayList<BackgroundObject> removeObjects = new ArrayList<>();

        for (BackgroundObject entity : backgroundObjects) {
            if (mouseIntesect.intersect(entity.getMainShape())) {
                removeObjects.add(entity);
            }

        }

        for (BackgroundObject entity: removeObjects) {
            placeSound.play(SoundLoader.suck, 0.5, 1);
            backgroundObjects.remove(entity);

        }
    }

    private void eraserLighting() {
        Square mouseIntesect = new Square(Main.reverseUnit(Main.mouseX) + cameraX, Main.reverseUnit(Main.mouseY) + cameraY, 1, 1, 1, InputAction.Default);

        ArrayList<GameEntity> removeObjects = new ArrayList<>();

        for (GameEntity entity : lighting) {
            if (mouseIntesect.intersect(entity.getMainShape())) {
                removeObjects.add(entity);
            }

        }

        for (GameEntity entity: removeObjects) {
            placeSound.play(SoundLoader.suck, 0.5, 1);
            lighting.remove(entity);

        }
    }




    private void erase() {
    eraserWall();
    eraserEntities();
   eraserLighting();
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
                    if (((int)(currentTick) % countingFactor == 0)) {
                        frames.add(new Integer[]{(int) player.getNonRenderX(), (int) player.getNonRenderY(), (int) player.getNonRenderSizeY()});
                    }
                } else {
                    for (int i = (frames.size()&~1)-1; i>=0; i-=2) {
                        if (i != 0) {
                            frames.remove(i);
                        }

                    }
                    countingFactor = countingFactor*2;
                    frames.set(0,new Integer[]{Settings.get("fps")/countingFactor, 0});

                }

            }


            for (GameEntity entity : entities) {
                entity.tick();
            }

            for (GameEntity entity : particles) {
                entity.tick();
            }
            for (GameEntity entity : lighting) {
                entity.tick();
            }

            entities.addAll(nextEntities);
            nextEntities = new ArrayList<>();

            particles.addAll(nextParticles);
            nextParticles = new ArrayList<>();

            lighting.addAll(nextLighting);
            nextLighting = new ArrayList<>();

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
        return world + "\\" + name;
    }


    public void addBackgroundObject(BackgroundObject object) {
        backgroundObjects.add(object);
    }

    public boolean screenInersect(Square square) {
        return square.intersect(new Square(cameraX, cameraY, Main.DEFAULT_WIDTH_MAP, Main.DEFAULT_HEIGHT_MAP, 1, InputAction.Default));
    }

    public void render(GraphicsContext g) {

        background.renderSky(g, cameraX, getTime());

        if (lastBackground != null) {
            lastBackground.render(g);
        }


        for (BackgroundObject backgroundObject : backgroundObjects) {
            if (screenInersect(backgroundObject.getMainShape())) {
                backgroundObject.render(g);
            }


        }


        for (GameEntity entity : particles) {
            if (screenInersect(entity.getMainShape())) {
                entity.render(g);
            }
        }
        for (GameEntity entity : entities) {
            if (screenInersect(entity.getMainShape())) {
                entity.render(g);
            }



        }

        for (GameEntity entity : overlayEntites) {
            entity.render(g);
        }

        for (GameEntity entity : lighting) {
            if (screenInersect(entity.getMainShape())) {
                entity.render(g);
            }
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
            g.setFill(Color.color(0, 1, 0, 0.2));
            g.drawImage(ImageLoader.player, Main.correctUnit(playerX - cameraX), Main.correctUnit(playerY - cameraY), Main.correctUnit(GRID_SIZE), Main.correctUnit(GRID_SIZE));
        } else if (isReplay) {

            g.setFill(Color.WHITE);
            g.setFont(new Font(Settings.FONT, Main.correctUnit(40)));
            g.fillText("x" + String.format("%.2f", actualSpeed), correctUnit(225), correctUnit(825));
        } else {
            for (int i = 0; i < ((Player)player).getShurikans(); i++) {
                g.drawImage(ImageLoader.shurikan, Main.correctUnit(25)+i*Main.correctUnit(25), Main.correctUnit(60), Main.correctUnit(55), Main.correctUnit(55));
            }
        }



        if (Settings.get("debug") > 0 || Menu.currentMenu.equals("editor")) {
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
        String lines = sizeX + " " + sizeY + " " + (int) playerX + " " + (int) playerY + "\n/";



        lines = lines + "\n" + backgroundName + " ;";
        for (BackgroundObject entity : backgroundObjects) {
            lines = lines + "\n" + entity.toString() + ";";
        }

        lines = lines + "\n/";

        for (GameEntity entity : entities) {
            lines = lines + "\n" + entity.toString() + ";";
        }
        for (GameEntity entity : lighting) {
            lines = lines + "\n" + entity.toString() + ";";
        }
        return lines;
    }

    public ArrayList<GameEntity> getEntities() {
        return entities;
    }


    public ArrayList<BackgroundObject> getBackgroundObjects() {
        return backgroundObjects;
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

    public void splash(GameEntity entity) {

        for (GameEntity entity1 : entities) {
            if (entity1 instanceof Liquid) {
                if (entity.getMainShape().intersect(entity1.getMainShape())) {
                    ((Liquid)(entity1)).splash(entity.getX(), entity);
                }
            }
        }

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



    public void addGate(int x, int y, int sizeX, int sizeY, double parallax, int code) {


        Gate wallImage = new Gate(x, y, this, sizeX, sizeY
                , InputAction.Default, FillType.Tile, parallax, code);

        addEntity(wallImage);


    }


    public ArrayList<InputAction> getActions(GameEntity entity) {
       // System.out.println(entity);
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
                    if (!InputAction.isUnactionable(currentEntity.getShape(entity.getMainShape()).getAction())) {
                        if (InputAction.isYType(currentEntity.getShape(entity.getMainShape()).getAction())) {

                            return currentEntity.getShape(entity.getMainShape());
                        } else {

                            returnEntity = currentEntity.getShape(entity.getMainShape());
                        }
                    }

                }
            }
        }


        return returnEntity;
    }


}
