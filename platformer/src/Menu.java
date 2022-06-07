import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class Menu {
    private static final int BUTTON_WIDTH = 292;
    private static final int BUTTON_HEIGHT = 100;

    private static final int BUTTON_GAP = 50;


    private static String currentMenu = "main";

    private static final double NORMAL_TRANSITION_TIME = 0.25;
    private static final double LOADING_TRANSITION_TIME = 1.5;


    private static HashMap<String, ArrayList<MenuButton>> menus = new HashMap<>();

    public static boolean currentlyMenu = true;

    public static void setCurrentMenu(String newMenu) {

       if (newMenu.equals("main")) {
           System.out.println(Settings.getStr("name"));
           if (Settings.getStr("name").equals(Settings.ANONYMOUS_NAME)) {
               currentMenu = "name";
               return;
           }
       }

        currentMenu = newMenu;
    }

    public static void switchMenu(String newMenu, MenuButton message) {


        Main.hashMap.put(InputAction.Menu, 2);
        Settings.save();
        Stats.save();


        Main.lastMap = null;
        Menu.loadbuttons();

        currentlyMenu = true;
        setCurrentMenu(newMenu);

        Main.currentMap = null;

        updateTransition(currentMenu);

        updateMessage(message);

        Main.loaded = true;
        Main.resetTimeline();

        Main.isVictory = false;

    }


    private static void updateMessage (MenuButton message) {
        ArrayList<MenuButton> buttons = menus.get(currentMenu);
        buttons.add(message);
    }

    public static void switchMenu(String newMenu) {


        Main.hashMap.put(InputAction.Menu, 2);
        Settings.save();
        Stats.save();


        Main.lastMap = null;
        Menu.loadbuttons();

        currentlyMenu = true;
        setCurrentMenu(newMenu);

        Main.currentMap = null;

        updateTransition(currentMenu);

        Main.loaded = true;
        Main.resetTimeline();

        Main.isVictory = false;

    }

    private static void updateTransition(String menu) {
        for (MenuButton button : menus.get(menu)) {
            if (button instanceof MenuTransition) {
                ((MenuTransition) button).loadImage(Main.getScreenshot());
            }else if (button instanceof  MenuZoomTransition) {
                ((MenuZoomTransition) button).loadImage(Main.getScreenshot());
            } else if (button instanceof MenuMap) {
                System.out.println("loading background map");
                ((MenuMap) button).loadMap();
            }
        }
    }

    public static ArrayList<MenuButton> getCurrentMenu() {


        if (currentMenu.equals("")) {
            return new ArrayList<>();
        }

        if (menus.containsKey(currentMenu)) {
            return menus.get(currentMenu);
        } else {
            ArrayList<MenuButton> errorScreen = new ArrayList<>();
            errorScreen.add(new MenuText(600,575,"invalid menu screen",40,"main menu"));
            errorScreen.add(new SwitchMenuButton(600,600,400,100,"Return to main menu", "main"));
            return errorScreen;
        }




    }



    public static void loadbuttons() {
        ArrayList<MenuButton> mainMenu = new ArrayList<>();
        mainMenu.add(new MenuMap("2"));
        mainMenu.add(new ExitButton(BUTTON_GAP, 800, BUTTON_WIDTH*2, BUTTON_HEIGHT, "exit"));
        mainMenu.add(new SwitchMenuButton(BUTTON_GAP, 200, BUTTON_WIDTH*2, BUTTON_HEIGHT, "levels", "levels"));
        mainMenu.add(new SwitchMenuButton(BUTTON_GAP,450,BUTTON_WIDTH*2,BUTTON_HEIGHT, "replays", "replays"));
        mainMenu.add(new SwitchMenuButton(BUTTON_GAP,575,BUTTON_WIDTH*2,BUTTON_HEIGHT, "settings", "settings"));

        mainMenu.add(new SwitchMenuButton(BUTTON_GAP,325,BUTTON_WIDTH*2,BUTTON_HEIGHT, "stats", "stats"));



        mainMenu.add(new MenuText(185,742,Settings.getStr("name"), 35, "name", "Monospaced Regular"));
        mainMenu.add(new SwitchMenuButton(50,710,120,50, "change", "name"));

        mainMenu.add(new MenuText(BUTTON_GAP,100,"Platformer v" + Main.VERSION, 55, "Title"));
        mainMenu.add(new MenuTransition((Main.loaded) ? NORMAL_TRANSITION_TIME : LOADING_TRANSITION_TIME ));
        menus.put("main", mainMenu);

        loadLevelMenu();
        loadReplayMenu();
        loadSettingsMenu();
        loadStatsMenu();
        loadVictoryMenu();
        loadName();




    }


    private static void loadSettingsMenu() {
        ArrayList<MenuButton> settingsMenu = new ArrayList<>();
        settingsMenu.add(new MenuMap("3"));

        settingsMenu.add(new MenuSlider(BUTTON_GAP,100,500,100,"fps", "fps", 300, 10, Main.monitorFPS));


        settingsMenu.add(new ToggleButton(BUTTON_GAP, 250, 200,100, "enable debug", "disable debug", "debug"));
        settingsMenu.add(new ToggleButton(BUTTON_GAP, 400, 200,100, "enable full speedrun", "disable full speedrun", "full speedrun"));

        settingsMenu.add(new SwitchMenuButton(BUTTON_GAP, 800, BUTTON_WIDTH*2, BUTTON_HEIGHT, "back", "main"));
        settingsMenu.add(new MenuText(900,100,"settings: ", 55, "Title"));
        settingsMenu.add(new MenuTransition(NORMAL_TRANSITION_TIME));

        menus.put("settings", settingsMenu);
    }


    private static void loadReplayMenu() {
        ArrayList<MenuButton> replayMenu = new ArrayList<>();
        replayMenu.add(new MenuMap("4"));
        File directory = new File("res\\replays");
        File[] levels = directory.listFiles();
        int fileCount = directory.list().length;

        int directories = 0;
        for (int i = 0; i < fileCount; i++) {
            if (!levels[i].isDirectory()) {
                int x = i-directories;

                double width = Main.DEFAULT_WIDTH_MAP - BUTTON_GAP * 2;

                int xFactor = (int) (x % (width
                        / (BUTTON_WIDTH + BUTTON_GAP)));

                int yFactor = (int) (x / (width
                        / (BUTTON_WIDTH + BUTTON_GAP)));






                replayMenu.add(new ReplayButton(xFactor * BUTTON_WIDTH + xFactor * BUTTON_GAP + BUTTON_GAP
                        , yFactor * BUTTON_HEIGHT + yFactor * BUTTON_GAP + BUTTON_GAP * 2
                        , BUTTON_WIDTH, BUTTON_HEIGHT, levels[i].getName().replace(".txt", "")));
            } else {
                directories++;
            }
        }
        replayMenu.add(new SwitchMenuButton(BUTTON_GAP, 800, BUTTON_WIDTH*2, BUTTON_HEIGHT, "back", "main"));
        replayMenu.add(new MenuText(900,100,"Replay Menu: ", 55, "Title"));
        replayMenu.add(new MenuTransition(NORMAL_TRANSITION_TIME));
        menus.put("replays", replayMenu);

    }


    private static void loadStatsMenu() {
        ArrayList<MenuButton> stats = new ArrayList<>();
        stats.add(new MenuMap("1"));
        stats.add(new SwitchMenuButton(BUTTON_GAP, 800, BUTTON_WIDTH*2, BUTTON_HEIGHT, "back", "main"));
        stats.add(new MenuText(50, 75, "Stats:", 45, "deaths"));

        int i = 0;
        for (String stat : Stats.getExpectedStats()) {
            if ((Stats.doubleStats.contains(stat))) {
                stats.add(new MenuText(50, 200+i*BUTTON_GAP
                        , stat + ": " + Main.formatTime(Stats.getD(stat)), 30, stat));

            } else {
                stats.add(new MenuText(50, 200 + i * BUTTON_GAP
                        , stat + ": " + Stats.get(stat), 30, stat));
            }
            i++;
        }

        stats.add(new MenuTransition(NORMAL_TRANSITION_TIME));


        menus.put("stats", stats);

    }

    private static void loadVictoryMenu() {
        ArrayList<MenuButton> stats = new ArrayList<>();
        stats.add(new SwitchMenuButton(350, 775, BUTTON_WIDTH, BUTTON_HEIGHT, "back", "main"));

        stats.add(new MenuTransition(NORMAL_TRANSITION_TIME));


        menus.put("victory", stats);

    }


    private static void loadName() {
        ArrayList<MenuButton> stats = new ArrayList<>();
        stats.add(new MenuText(300, 200
                , "enter name:", 60, "name"));

        stats.add(new MenuTextField(300, 300
                , "", 50, "name", MenuButton.TextType.normal));

        stats.add(new MenuTransition(NORMAL_TRANSITION_TIME));


        menus.put("name", stats);

    }

    private static void loadLevelMenu() {
        ArrayList<MenuButton> levelMenu = new ArrayList<>();
        levelMenu.add(new MenuMap("5"));
        File directory = new File("res\\maps");
        File[] levels = directory.listFiles();
        int fileCount = directory.list().length;

        int directories = 0;

        for (int i = 0; i < fileCount; i++) {


            if (!levels[i].isDirectory()) {
                int x = i-directories;
                double width = Main.DEFAULT_WIDTH_MAP - BUTTON_GAP * 2;

                int xFactor = (int) (x % (width
                        / (BUTTON_WIDTH + BUTTON_GAP)));

                int yFactor = (int) (x / (width
                        / (BUTTON_WIDTH + BUTTON_GAP)));





                String levelName = levels[i].getName().replace(".txt", "");
                if (levelName.matches(Main.IS_INT_REGEX)) {
                    if (Integer.parseInt(levelName) > Main.lastLevel) {
                        Main.lastLevel = Integer.parseInt(levelName);
                    }
                }
                levelMenu.add(new LevelButton(xFactor * BUTTON_WIDTH + xFactor * BUTTON_GAP + BUTTON_GAP
                        , yFactor * BUTTON_HEIGHT + yFactor * BUTTON_GAP + BUTTON_GAP * 2
                        , BUTTON_WIDTH, BUTTON_HEIGHT, levelName));

            } else {
                directories++;
            }
        }


        if (UserFileHandler.getUserTime("full", 1) > 0) {
            levelMenu.add(new MenuText(BUTTON_GAP,25, "full speedrun best time:  " + String.format("%.2f",UserFileHandler.getUserTime("full", 1)), 20, "full"));
        }
        if (UserFileHandler.getCumulativeBestTimes() > 0) {
            levelMenu.add(new MenuText(BUTTON_GAP,55, "cumulative best times:    " + String.format("%.2f",UserFileHandler.getCumulativeBestTimes()), 20, "full"));
        }

        levelMenu.add(new SwitchMenuButton(BUTTON_GAP, 800, BUTTON_WIDTH*2, BUTTON_HEIGHT, "back", "main"));
        levelMenu.add(new MenuText(475,55,"Levels Menu: ", 50, "Title"));
        levelMenu.add(new MenuTransition(NORMAL_TRANSITION_TIME));
        menus.put("levels", levelMenu);

    }

}
