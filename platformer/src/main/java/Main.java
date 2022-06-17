import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.input.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;


import java.awt.*;


import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * the main class that launches the game
 *
 * @author William Randle
 * @version 0.0.9
 * @todo: leaderboard for full speedrun ranking, then stats page button launches menu for previous records in all the categories: slider to show them
 * @todo: save times for individual users (last, best[and overall too], speedrun[and overall too])
 * @todo: vanity slots to customise character design (multi layered player model with independantly moving parts)
 * @todo: water / lava
 * @todo: level editor
 */
public class Main extends Application {


    public static int totalScrolls = 0;


    public static boolean isLevelEditor = false;

    public static String mapName = "-";

    public static final String IS_INT_REGEX = "^([+-]?[0-9]\\d*|0)$";

    public static int deaths = 0;

    public static final String VERSION = "0.0.9";


    public static boolean mouseClicked = false;


    public static boolean isVictory = false;

    public static String lastKey = "";




    public static int monitorFPS = 0;

    public static final double EXPECTED_ASPECT_RATIO = 1.7778;

    public static Random random = new Random();

    private static WritableImage lastFrame;

    private static Canvas canvas;

    private static double fpstime = 2;
    public static Map lastMap = null;

    public static int possibleFps = 0;


    public static boolean mouseUsed = false;

    public static boolean mouseDragging = false;
    public static boolean mouseDraggingUsed = false;

    public static final double DEFAULT_WIDTH_MAP = 1800;
    public static final double DEFAULT_HEIGHT_MAP = 1100;

    private static final int GAME_UNIT_SETTING = 1000;
    public static double gameUnit = 1;


    public static HashMap<InputAction, Integer> hashMap = new HashMap<>();

    private static HashMap<KeyCode, InputAction> inputMap = new HashMap<>();

    private static HashMap<InputAction, ArrayList<KeyCode>> readInput = new HashMap<>();

    private static Timeline loop;

    public static Map currentMap;

    private static Stage primaryStage;

    public static boolean mouseDown = false;

    public static double mouseX = 0;
    public static double mouseY = 0;


    public static boolean loaded = false;

    public static Square screen;


    private static boolean exit = false;


    public static ArrayList<Replay> currentFull;



    public static boolean hasFinished = true;


    private static final double BASE_FPS = 144;

    /**
     * Load the menus for navigation, and launch the user selection menu.
     *
     * @param primaryStage Stage javafx shows things on.
     */
    public void start(Stage primaryStage) {

        monitorFPS = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[0].getDisplayMode().getRefreshRate();

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        canvas = new Canvas(screenSize.getWidth(), screenSize.getHeight());

        gameUnit = screenSize.getHeight() / GAME_UNIT_SETTING;

        renderLoadingStage(canvas.getGraphicsContext2D());

        currentFull = new ArrayList<>();

        Settings.initialiseValues();

        Stats.load();
        GridParser.parseAll();


        readKeyBind();


        BorderPane pane = new BorderPane();


        pane.setCenter(canvas);
        Scene scene = new Scene(pane, screenSize.getWidth() / 1.1, screenSize.getHeight() / 1.1);
        scene.addEventHandler(KeyEvent.KEY_PRESSED, (key) -> {

            keyDown(key.getCode());
        });


        primaryStage.setScene(scene);

        scene.addEventHandler(KeyEvent.KEY_RELEASED, (key) -> {

            if (key.getCode() == KeyCode.F11) {
                primaryStage.setFullScreen(!primaryStage.isFullScreen());
            } else {
                keyReleased(key.getCode());
            }
        });


        canvas.setOnMouseMoved(event -> {
            mouseX = event.getX();
            mouseY = event.getY();

        });

        canvas.setOnMouseDragged(event -> {

            mouseX = event.getX();
            mouseY = event.getY();
        });

        canvas.setOnMousePressed(event -> {
            mouseDown = true;

        });

        canvas.setOnMouseReleased(event -> {
            mouseDown = false;
            mouseClicked = true;


        });


        canvas.setOnScroll(scrollEvent -> {
            totalScrolls += scrollEvent.getDeltaY();

        });

        primaryStage.setFullScreen(true);
        primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);


        primaryStage.show();


        fpstime = 1000.0 / Settings.getD("fps");


        loop = new Timeline(new KeyFrame(Duration.millis(fpstime), (ActionEvent event) -> {
            tick();
            render(canvas.getGraphicsContext2D());
        }));

        loop.setCycleCount(Animation.INDEFINITE);

        loop.play();


        Menu.loadButtonsStart();


        Menu.switchMenu("main");


        Main.primaryStage = primaryStage;


        SoundLoader.playButtonPress();
        SoundLoader.playMusic(SoundLoader.music1, 1, 0);


        SoundLoader.adjustMusicVolume();
    }


    public static String getWorld(String name) {

        return name.split("\\\\")[0];
    }
    public static String getLevel(String name) {

        return name.split("\\\\")[1];
    }


    public static double getMemory() {
        return Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
    }


    public static WritableImage getCanvas() {
        return lastFrame;
    }



    public static String getCurrentTime() {
        return LocalDate.now() + "|" + LocalTime.now().getHour() + ";" + LocalTime.now().getMinute();
    }

    private static void renderLoadingStage(GraphicsContext g) {

        canvas.getGraphicsContext2D().fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        g.setFont(new Font(Settings.FONT, correctUnit(115)));
        g.setFill(Color.WHITE);
        g.fillText("Loading...", canvas.getWidth() / 2.8, canvas.getHeight() / 2.5);
    }




    private static void exit() {
        primaryStage.close();
    }

    public static void planExit() {
        exit = true;

    }

    public static void victory(String name, MenuElement button) {

        Settings.put("focus", Settings.LAST_REPLAY);

        Menu.switchMenu("victory", button);


        resetTimeline();


        hashMap.put(InputAction.Menu, 2);
        currentMap = MapLoader.loadMap(name, 2);
        Menu.currentlyMenu = false;

        isVictory = true;


    }


    public static void loadEditor() {
        resetTimeline();
        isLevelEditor = true;


        currentMap = MapLoader.loadMap("preset\\empty", 0);
        hashMap.put(InputAction.Menu, 2);
        Menu.currentlyMenu = false;

    }

    public static double getDistance(double x, double y, double x1, double y1) {
        return Math.sqrt(Math.pow(x-x1,2) + Math.pow(y-y1, 2));
    }


    public static boolean intersect(GameEntity entity, double x, double y, double sizeX, double sizeY) {


        double x2 = entity.getX();
        double y2 = entity.getY();
        double sizeX2 = entity.getSizeX();
        double sizeY2 = entity.getSizeY();
        return x + sizeX > x2 && x < x2 + sizeX2
                && y + sizeY > y2 && y < y2 + sizeY2;
    }


    private static void tick() {
        mouseUsed = false;


        gameUnit = primaryStage.getHeight() / GAME_UNIT_SETTING;
        if (!(currentMap == null)) {
            currentMap.tick();
        }

        if (exit) {
            exit();
        }




    }


    public static double correctFPS(double val) {
        return (val/Settings.get("fps"))*BASE_FPS;
    }

    public static WritableImage getScreenshot() {

        return canvas.snapshot(new SnapshotParameters(), null);
    }


    private static int totalTime = 0;
    private static int count = 0;

    public static void resetTimeline() {
        loop.stop();


        fpstime = 1000.0 / Settings.getD("fps");


        loop = new Timeline(new KeyFrame(Duration.millis(fpstime), (ActionEvent event) -> {
            long time = System.nanoTime();

            tick();
            render(canvas.getGraphicsContext2D());

            totalTime += (int) ((1000000000.0 / (System.nanoTime() - time)));
            count++;
            if (count > Settings.get("fps")) {

                possibleFps = totalTime / count;
                count = 0;
                totalTime = 0;
            }
        }));

        loop.setCycleCount(Animation.INDEFINITE);

        loop.play();

    }

    public static String formatTime(double time) {


        if (time < 60) {
            return String.format("%.2f", (time % 60));
        } else if (time < 3600) {
            return String.format("%02d:%.2f", (int) ((time % 3600) / 60)
                    , (time % 60));


        } else {
            return String.format("%d:%02d:%.2f"
                    , (int) (time / 3600), (int) ((time % 3600) / 60)
                    , (time % 60));
        }


    }


    public static void playMap(Map newMap) {
        mapName = newMap.getName();
        Settings.put("focus", Settings.BEST_REPLAY);
        isVictory = false;

        deaths = 0;
        resetTimeline();


        if (Settings.get("full speedrun") == 1) {

            hasFinished = false;
            if (newMap.getName().equals("1")) {
                currentFull = new ArrayList<>();
            }


        }


        lastMap = null;
        hashMap.put(InputAction.Menu, 2);
        currentMap = newMap;
        Menu.currentlyMenu = false;


        Menu.setCurrentMenu("");


    }

    public static void playDimension(Map newMap) {
        Main.lastMap = Main.currentMap;
        hashMap.put(InputAction.Menu, 2);
        currentMap = newMap;
        Menu.currentlyMenu = false;
        Menu.setCurrentMenu("");
    }


    public static void deactivateKey(InputAction action) {
        hashMap.replace(action, -1);


    }

    /**
     * Interpolate between two values.
     *
     * @param x1 Double floor value.
     * @param x2 Double ceiling value.
     * @return Double a value between the two parsed values.
     */
    public static double interpolate(double x1, double x2, double max, double current) {
        return x1 + ((x2 - x1) / max) * current;
    }

    public static Integer getKey(InputAction action) {
        return hashMap.get(action);


    }

    private static void render(GraphicsContext g) {

        g.drawImage(ImageLoader.sky1, 0, 0, canvas.getWidth(), canvas.getHeight());
        if (!(currentMap == null)) {
            currentMap.render(g);
        }

        for (MenuElement button : Menu.getCurrentMenu()) {
            button.tick();
            if (button instanceof MenuText) {
                MenuText menuText = (MenuText) button;

                if (menuText.getUpdateTag().equals("FPS")) {
                    menuText.setText("FPS: " + Settings.get("fps"));
                }
            }


            button.render(canvas.getGraphicsContext2D());
        }


            mouseClicked = false;


        g.setFill(Color.BLACK);
        canvas.getGraphicsContext2D().fillRect(primaryStage.getHeight() * EXPECTED_ASPECT_RATIO, 0, primaryStage.getWidth(), primaryStage.getHeight());




    }


    public static double correctUnit(double input) {
        return input * gameUnit;
    }
    public static double reverseUnit(double input) {
        return input / gameUnit;
    }

    private static void readKeyBind() {
        inputMap = new HashMap<>();

        inputMap.put(KeyCode.W, InputAction.Up);
        inputMap.put(KeyCode.A, InputAction.Left);
        inputMap.put(KeyCode.S, InputAction.Down);
        inputMap.put(KeyCode.D, InputAction.Right);
        inputMap.put(KeyCode.SPACE, InputAction.Up);
        inputMap.put(KeyCode.SHIFT, InputAction.Sprint);
        inputMap.put(KeyCode.ESCAPE, InputAction.Menu);
        inputMap.put(KeyCode.F11, InputAction.FullScreen);
        inputMap.put(KeyCode.BACK_SPACE, InputAction.Reset);
        inputMap.put(KeyCode.CONTROL, InputAction.Control);


        inputMap.put(KeyCode.R, InputAction.Hook);

        inputMap.put(KeyCode.UP, InputAction.Up);
        inputMap.put(KeyCode.LEFT, InputAction.Left);
        inputMap.put(KeyCode.DOWN, InputAction.Down);
        inputMap.put(KeyCode.RIGHT, InputAction.Right);

        hashMap.put(InputAction.Reset, 0);

        hashMap.put(InputAction.FullScreen, 0);
        hashMap.put(InputAction.Menu, 2);
        hashMap.put(InputAction.Up, 0);
        hashMap.put(InputAction.Left, 0);
        hashMap.put(InputAction.Down, 0);
        hashMap.put(InputAction.Control, 0);
        hashMap.put(InputAction.Right, 0);
        hashMap.put(InputAction.Sprint, 0);
        hashMap.put(InputAction.Default, 0);
        hashMap.put(InputAction.Hook, 0);


        readDisplayInputs();
    }

    public static ArrayList<KeyCode> getDisplayOptions(InputAction action) {
        return readInput.get(action);
    }


    private static void readDisplayInputs() {


        for (KeyCode keyCode : inputMap.keySet()) {
            if (readInput.get(inputMap.get(keyCode)) == null) {
                ArrayList<KeyCode> keys = new ArrayList<>();
                keys.add(keyCode);
                readInput.put(inputMap.get(keyCode), keys);
            } else {
                ArrayList<KeyCode> keys = readInput.get(inputMap.get(keyCode));
                keys.add(keyCode);
                readInput.put(inputMap.get(keyCode), keys);
            }

        }
    }


    public static boolean isKeyDown(InputAction action) {
        return hashMap.get(action) > 0;
    }


    private static void keyDown(KeyCode code) {
        if (code == KeyCode.ESCAPE) {
            if (!Menu.currentlyMenu && !isVictory) {
                if (hashMap.get(inputMap.getOrDefault(code, InputAction.Default)) == 2) {
                    hashMap.put(inputMap.getOrDefault(code, InputAction.Default), 0);
                } else if (hashMap.get(inputMap.getOrDefault(code, InputAction.Default)) == 0) {
                    hashMap.put(inputMap.getOrDefault(code, InputAction.Default), 1);
                }
            }
        } else {
            if (hashMap.get(inputMap.getOrDefault(code, InputAction.Default)) > -1) {
                hashMap.put(inputMap.getOrDefault(code, InputAction.Default), 1);


            }
        }

        hashMap.put(InputAction.Default, 1);
        lastKey = code.getName();
    }

    private static void keyReleased(KeyCode code) {
        if (code == KeyCode.ESCAPE) {
            if (!Menu.currentlyMenu && !isVictory) {
                if (hashMap.get(inputMap.getOrDefault(code, InputAction.Default)) == 1) {
                    hashMap.put(inputMap.getOrDefault(code, InputAction.Default), 2);
                }
            } else {
                Menu.switchMenu(Menu.getBranching());
            }
        } else {
            hashMap.put(inputMap.getOrDefault(code, InputAction.Default), 0);
        }
    }

}