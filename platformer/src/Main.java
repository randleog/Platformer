import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.InputMethodTextRun;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Main extends Application {


    public static int deaths = 0;


    public static Random random = new Random();

    private static Canvas canvas;

    public static final int FPS = 144;


    private static final int GAME_UNIT_SETTING = 1000;
    public static double gameUnit = 1;

    public static HashMap<InputAction, Integer> hashMap = new HashMap<>();

    private static HashMap<KeyCode, InputAction> inputMap = new HashMap<>();

    private static HashMap<InputAction, ArrayList<KeyCode>> readInput = new HashMap<>();

    private static Timeline loop;

    private static Map currentMap;


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

        scene.addEventHandler(KeyEvent.KEY_RELEASED, (key) -> {

            keyReleased(key.getCode());
        });


        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);


        primaryStage.show();


        int fpstime = 1000 / FPS;


        loop = new Timeline(new KeyFrame(Duration.millis(fpstime), (ActionEvent event) -> {
            tick();
            render(canvas.getGraphicsContext2D());
        }));

        loop.setCycleCount(Animation.INDEFINITE);

        loop.play();


        currentMap = new Map(true, 3000,2000);
        currentMap.addParticle(new Wall(0, 700, currentMap, 1000, 600, InputAction.Default, FillType.Tile, 0.4));
        currentMap.addParticle(new Wall(0, 1000, currentMap, 1000, 200, InputAction.Default, FillType.Tile, 0.6));



        currentMap.addWall(0,1000,3000,50, 1);
        currentMap.addWall(1000,500,100,1000, 1);
        currentMap.addWall(700,0,100,800, 1);
        currentMap.addWallCloseDown(1400,-1000,100,1960, 1);
        currentMap.addWall(0,800,100,1000, 1);
        currentMap.addWall(1100,0,1000,100, 1);
        currentMap.addMovingWall(1100,500,100,50,1,0);
        currentMap.addMovingWall(1100,500,100,50,0,1);

        currentMap.addEntity(new Player(700, 500, currentMap));
        currentMap.addEntity(new Hookable(700, 500, currentMap, 400));
    }

    private static void tick(){

        currentMap.tick();
    }


    public static void deactivateKey(InputAction action) {
        hashMap.replace(action,-1);


    }

    /**
     * Interpolate between two values.
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

    private static void render(GraphicsContext g){

        g.drawImage(ImageLoader.sky1,0,0,canvas.getWidth(),canvas.getHeight());
        currentMap.render(g);
    }


    public static double correctUnit(double input) {
        return input/gameUnit;
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

        inputMap.put(KeyCode.R, InputAction.Hook);

        inputMap.put(KeyCode.UP, InputAction.Up);
        inputMap.put(KeyCode.LEFT, InputAction.Left);
        inputMap.put(KeyCode.DOWN, InputAction.Down);
        inputMap.put(KeyCode.RIGHT, InputAction.Right);

        hashMap.put(InputAction.Menu, 1);
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