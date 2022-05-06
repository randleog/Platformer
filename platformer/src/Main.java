import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Main extends Application {


    public static int deaths = 0;

    private static final int BUTTON_WIDTH = 292;
    private static final int BUTTON_HEIGHT = 100;

    private static final int BUTTON_GAP = 100;
    public static Random random = new Random();

    private static Canvas canvas;

    public static final int FPS = 144;
    public static Map lastMap = null;


    public static final double DEFAULT_WIDTH_MAP = 1925;
    public static final double DEFAULT_HEIGHT_MAP = 1100;

    private static final int GAME_UNIT_SETTING = 1000;
    public static double gameUnit = 1;

    private static boolean menu = true;

    public static HashMap<InputAction, Integer> hashMap = new HashMap<>();

    private static HashMap<KeyCode, InputAction> inputMap = new HashMap<>();

    private static HashMap<InputAction, ArrayList<KeyCode>> readInput = new HashMap<>();

    private static Timeline loop;

    private static Map currentMap;

    private static Stage primaryStage;


    public static boolean mouseDown = false;

    public static double mouseX = 0;
    public static double mouseY = 0;

    public static ArrayList<MenuButton> levelMenu = new ArrayList<>();


    public static ArrayList<MenuButton> mainMenu = new ArrayList<>();


    private static ArrayList<MenuButton> currentMenu = new ArrayList<>();


    /**
     * Load the menus for navigation, and launch the user selection menu.
     *
     * @param primaryStage Stage javafx shows things on.
     */
    public void start(Stage primaryStage) {
        readKeyBind();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        canvas = new Canvas(screenSize.getWidth(), screenSize.getHeight());
        gameUnit = screenSize.getHeight() / GAME_UNIT_SETTING;


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

        });

        primaryStage.setFullScreen(true);
        primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);


        primaryStage.show();


        int fpstime = 1000 / FPS;


        loop = new Timeline(new KeyFrame(Duration.millis(fpstime), (ActionEvent event) -> {
            tick();
            render(canvas.getGraphicsContext2D());
        }));

        loop.setCycleCount(Animation.INDEFINITE);

        loop.play();





        addbuttons();

        switchMenu(mainMenu);

        Main.primaryStage = primaryStage;


    }


    public static void exit() {
        primaryStage.close();
    }


    private static void addbuttons() {
        mainMenu = new ArrayList<>();
        mainMenu.add(new ExitButton(100, 800, BUTTON_WIDTH*2, BUTTON_HEIGHT, "exit"));
        mainMenu.add(new LevelsMenuButton(100, BUTTON_GAP*2, BUTTON_WIDTH*2, BUTTON_HEIGHT));
        mainMenu.add(new MenuText(900,100,"Platformer", 55));
        loadLevelMenu();


    }


    private static void loadLevelMenu() {
        levelMenu = new ArrayList<>();

        File directory = new File("res\\maps");
        File[] levels = directory.listFiles();
        int fileCount = directory.list().length;

        for (int i = 0; i < fileCount; i++) {
            if (!levels[i].isDirectory()) {
                double width = canvas.getWidth() - BUTTON_GAP * 2;

                int xFactor = (int) (i % (width
                        / (BUTTON_WIDTH + BUTTON_GAP)));

                int yFactor = (int) (i / (width
                        / (BUTTON_WIDTH + BUTTON_GAP)));

                System.out.println(yFactor);


                levelMenu.add(new LevelButton(xFactor * BUTTON_WIDTH + xFactor * BUTTON_GAP + BUTTON_GAP
                        , yFactor * BUTTON_HEIGHT + yFactor * BUTTON_GAP + BUTTON_GAP * 2
                        , BUTTON_WIDTH, BUTTON_HEIGHT, levels[i].getName().replace(".txt", "")));

            }
        }

        levelMenu.add(new MainMenuButton(100, 800, BUTTON_WIDTH*2, BUTTON_HEIGHT, "back"));
        levelMenu.add(new MenuText(900,100,"Levels Menu: ", 55));

    }


    private static void tick() {

        if (!(currentMap == null)) {
            currentMap.tick();
        }


    }

    public static void playMap(Map newMap) {


        lastMap = null;
        hashMap.put(InputAction.Menu, 2);
        currentMap = newMap;
        menu = false;
        currentMenu = new ArrayList<>();

    }

    public static void playDimension(Map newMap) {
        Main.lastMap = Main.currentMap;
        hashMap.put(InputAction.Menu, 2);
        currentMap = newMap;
        menu = false;
        currentMenu = new ArrayList<>();
    }


    public static void switchMenu(ArrayList<MenuButton> newMenu) {

        lastMap = null;
        addbuttons();
        menu = true;
        currentMenu = newMenu;

        currentMap = null;
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

        for (MenuButton button : currentMenu) {
            button.tick();


            button.render(canvas.getGraphicsContext2D());
        }
    }


    public static double correctUnit(double input) {
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

        inputMap.put(KeyCode.R, InputAction.Hook);

        inputMap.put(KeyCode.UP, InputAction.Up);
        inputMap.put(KeyCode.LEFT, InputAction.Left);
        inputMap.put(KeyCode.DOWN, InputAction.Down);
        inputMap.put(KeyCode.RIGHT, InputAction.Right);

        hashMap.put(InputAction.FullScreen, 0);
        hashMap.put(InputAction.Menu, 2);
        hashMap.put(InputAction.Up, 0);
        hashMap.put(InputAction.Left, 0);
        hashMap.put(InputAction.Down, 0);
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
            if (hashMap.get(inputMap.getOrDefault(code, InputAction.Default)) == 2) {
                hashMap.put(inputMap.getOrDefault(code, InputAction.Default), 0);
            } else if (hashMap.get(inputMap.getOrDefault(code, InputAction.Default)) == 0) {
                hashMap.put(inputMap.getOrDefault(code, InputAction.Default), 1);
            }
        } else {
            if (hashMap.get(inputMap.getOrDefault(code, InputAction.Default)) > -1) {
                hashMap.put(inputMap.getOrDefault(code, InputAction.Default), 1);
            }
        }
    }

    private static void keyReleased(KeyCode code) {
        if (code == KeyCode.ESCAPE) {
            if (hashMap.get(inputMap.getOrDefault(code, InputAction.Default)) == 1) {
                hashMap.put(inputMap.getOrDefault(code, InputAction.Default), 2);
            }
        } else {
            hashMap.put(inputMap.getOrDefault(code, InputAction.Default), 0);
        }
    }

}