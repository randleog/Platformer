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
import java.util.HashMap;

public class Main extends Application {


    private static Canvas canvas;

    public static final int FPS = 144;


    private static final int GAME_UNIT_SETTING = 1000;
    public static double gameUnit = 1;

    public static HashMap<InputAction, Integer> hashMap = new HashMap<>();

    private static HashMap<KeyCode, InputAction> inputMap = new HashMap<>();

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


        currentMap = new Map(true);
        currentMap.addWall(0,1000,1000,50);
        currentMap.addWall(1000,500,100,1000);
        currentMap.addWall(700,0,100,800);
        currentMap.addWall(0,800,100,1000);
      //  currentMap.addEntity(new Wall(0, 995, currentMap, 1000, 500, InputAction.Up));
        currentMap.addEntity(new Player(700, 500, currentMap));
    }

    private static void tick(){
        currentMap.tick();
    }


    public static void deactivateKey(InputAction action) {
        hashMap.replace(action,-1);


    }

    public static Integer getKey(InputAction action) {
        return hashMap.get(action);


    }

    private static void render(GraphicsContext g){
        canvas.getGraphicsContext2D().setFill(Color.color(0,0,0));
        g.fillRect(0,0,canvas.getWidth(),canvas.getHeight());
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

        inputMap.put(KeyCode.UP, InputAction.Up);
        inputMap.put(KeyCode.LEFT, InputAction.Left);
        inputMap.put(KeyCode.DOWN, InputAction.Down);
        inputMap.put(KeyCode.RIGHT, InputAction.Right);

        hashMap.put(InputAction.Up, 0);
        hashMap.put(InputAction.Left, 0);
        hashMap.put(InputAction.Down, 0);
        hashMap.put(InputAction.Right, 0);
        hashMap.put(InputAction.Sprint, 0);
        hashMap.put(InputAction.Default, 0);

    }


    public static boolean isKeyDown(InputAction action) {
        return hashMap.get(action) > 0;
    }



    private static void keyDown(KeyCode code) {
        if (hashMap.get(inputMap.getOrDefault(code, InputAction.Default)) > -1) {
            hashMap.put(inputMap.getOrDefault(code, InputAction.Default), 1);
        }
    }

    private static void keyReleased(KeyCode code) {
        hashMap.put(inputMap.getOrDefault(code, InputAction.Default), 0);
    }

}