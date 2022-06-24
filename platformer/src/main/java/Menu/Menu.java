package Menu;

import Main.Main;
import Map.Background;
import Map.InputAction;
import Util.MapLoader;
import Util.Settings;
import Util.SoundLoader;
import Util.UserFileHandler;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import Util.Stats;
import Util.LeaderboardTime;

public class Menu {
    private static final int BUTTON_WIDTH = 230;
    private static final int BUTTON_HEIGHT = 100;


    private static final int BUTTON_GAP = 58;


    public static final int GRAPHICS_MAX = 5;


    public static String currentMenu = "main";

    private static final double NORMAL_TRANSITION_TIME = 0.25;
    private static final double LOADING_TRANSITION_TIME = 1.5;


    private static HashMap<String, Menu> menus = new HashMap<>();

    public static boolean currentlyMenu = true;


    private String branch;
    private ArrayList<MenuElement> buttons;


    public Menu(ArrayList<MenuElement> buttons, String branch) {
        this.branch = branch;
        this.buttons = buttons;
    }


    public String getBranch() {
        return branch;
    }


    public void setButtons(ArrayList<MenuElement> buttons) {
        this.buttons = buttons;
    }


    public ArrayList<MenuElement> getbuttons() {
        return buttons;
    }


    public static String getBranching() {
        return menus.get(currentMenu).getBranch();
    }

    public static void setCurrentMenu(String newMenu) {


        //check the users name is filled
        if (newMenu.equals("main")) {

            if (Settings.getStr("name").equals(Settings.ANONYMOUS_NAME)) {
                currentMenu = "name";
                return;
            }
        }

        currentMenu = newMenu;
    }

    public static void switchMenu(String newMenu, MenuElement message) {


        Main.hashMap.put(InputAction.Menu, 2);
        Settings.save();
        Stats.save();


        Main.lastMap = null;
        loadMenu(newMenu);

        currentlyMenu = true;
        setCurrentMenu(newMenu);

        Main.currentMap = null;

        updateTransition(currentMenu);

        updateMessage(message);

        Main.loaded = true;
        Main.resetTimeline();

        Main.isVictory = false;

    }


    private static void updateMessage(MenuElement message) {
        ArrayList<MenuElement> buttons = menus.get(currentMenu).getbuttons();
        buttons.add(message);
        menus.get(currentMenu).setButtons(buttons);

    }

    public static void switchMenu(String newMenu) {



        if (currentMenu.equals("editor") && Settings.getStr("back").equals("save+exit")) {
            MapLoader.saveMap(Main.currentMap, false);
        }


        if (!Main.hasFinished) {

            Main.currentFull = new ArrayList<>();
        }


        Main.hashMap.put(InputAction.Menu, 2);
        Settings.save();
        Stats.save();


        Main.lastMap = null;
    //    Menu.loadButtonsStart();

        currentlyMenu = true;
        setCurrentMenu(newMenu);

        Main.currentMap = null;

        updateTransition(currentMenu);

        Main.loaded = true;
        Main.resetTimeline();

        Main.isVictory = false;


        SoundLoader.adjustMusicVolume();


        if (newMenu.equals("editor")) {
            Main.loadEditor(Settings.getStr("map choice"));
            System.out.println(Settings.getStr("map choice"));
        }

    }

    private static void updateTransition(String menu) {
        if(!menus.containsKey(menu)) {
            System.out.println("invalid menu!");
            return;
        }

        for (MenuElement button : menus.get(menu).getbuttons()) {
            if (button instanceof MenuTransition) {
                ((MenuTransition) button).loadImage(Main.getScreenshot());
            } else if (button instanceof MenuZoomTransition) {
                ((MenuZoomTransition) button).loadImage(Main.getScreenshot());
            } else if (button instanceof MenuMap) {

                ((MenuMap) button).loadMap();
            }else if (button instanceof MenuBackground) {

                ((MenuBackground) button).loadBackground();
            }
        }
    }

    public static ArrayList<MenuElement> getCurrentMenu() {
        if (currentMenu.equals("")) {
            return new ArrayList<>();
        }

        if (menus.containsKey(currentMenu)) {
            return menus.get(currentMenu).getbuttons();
        } else {
            ArrayList<MenuElement> errorScreen = new ArrayList<>();
            errorScreen.add(new MenuText(600, 575, "invalid menu screen", 40, "main menu"));
            errorScreen.add(new SwitchMenuButton(600, 600, 400, 100, "Return to main menu", "main"));
            return errorScreen;
        }


    }







    private static void loadPreEditor() {
        ArrayList<MenuElement> elements = new ArrayList<>();

        elements.add(new ScrollMenu(BUTTON_GAP, BUTTON_GAP+BUTTON_HEIGHT,BUTTON_WIDTH*5+BUTTON_GAP*6, BUTTON_HEIGHT*3, new Menu(getEditorOptions("custom"), "main"), "editor cutstom options"));


        elements.add(new SwitchMenuButton(BUTTON_GAP, 800, BUTTON_WIDTH, BUTTON_HEIGHT, "back", "main"));

        menus.put("pre editor", new Menu(elements, "main"));
    }







    private static ArrayList<MenuElement> getEditorOptions(String folder) {
        ArrayList<MenuElement> mainLevels = new ArrayList<>();

        File directory = new File("res\\maps\\" + folder);
        File[] levels = directory.listFiles();
        int fileCount = directory.list().length;


int nonIntLevels = 0;

        int maxLevel = 0;
        for (int i = 0; i < fileCount; i++) {
            if ((levels[i].getName().replace(".txt", "").matches(Main.IS_INT_REGEX))) {
                if (Integer.parseInt(levels[i].getName().replace(".txt", "")) > maxLevel) {
                    maxLevel = Integer.parseInt(levels[i].getName().replace(".txt", ""));
                }
            }else {
                if (!(levels[i].isDirectory())) {
                    double x = BUTTON_GAP + (BUTTON_WIDTH+BUTTON_GAP)*((nonIntLevels)%maxColums);
                    double y = (BUTTON_HEIGHT+BUTTON_GAP)*((int)(((nonIntLevels)*1.0)/maxColums));
                    mainLevels.add(new SettingButtonSwitchMenu((int)x, (int)y, BUTTON_WIDTH, BUTTON_HEIGHT, "map choice", folder + "\\" + levels[i].getName().replace(".txt", ""), "editor", MenuElement.TextType.choice));
                    nonIntLevels++;
                }
            }
        }


        int maxColums = 5;

        for (int i = 1+nonIntLevels; i < maxLevel+1+nonIntLevels; i++) {
            double x = BUTTON_GAP + (BUTTON_WIDTH+BUTTON_GAP)*((i-1)%maxColums);
            double y = (BUTTON_HEIGHT+BUTTON_GAP)*((int)(((i-1)*1.0)/maxColums));
            if ((new File("res\\maps\\" + folder + "\\" + i + ".txt").exists())) {

                String levelName = folder + "\\" + i;

                mainLevels.add(new SettingButtonSwitchMenu((int)x, (int)y, BUTTON_WIDTH, BUTTON_HEIGHT, "map choice", levelName, "editor", MenuElement.TextType.choice));
            } else {
                mainLevels.add(new InactiveButton((int)x, (int)y, BUTTON_WIDTH, BUTTON_HEIGHT, "level missing"));

            }


        }

        return mainLevels;
    }












    private static void loadMain() {
        ArrayList<MenuElement> mainMenu = new ArrayList<>();
        mainMenu.add(new MenuBackground("desert", 2,0));
        mainMenu.add(new MenuText(BUTTON_GAP, 100, "Platformer v" + Main.VERSION, 55, "Title"));


        mainMenu.add(new SwitchMenuButton(BUTTON_GAP, 200, BUTTON_WIDTH * 2 + BUTTON_GAP, (BUTTON_HEIGHT), "levels", "levels"));

        mainMenu.add(new SwitchMenuButton(BUTTON_GAP, 350, BUTTON_WIDTH, BUTTON_HEIGHT, "replays", "replays"));
        mainMenu.add(new SwitchMenuButton(BUTTON_GAP + BUTTON_WIDTH + BUTTON_GAP, 350, BUTTON_WIDTH, BUTTON_HEIGHT, "level editor", "pre editor").getAnimateRight(true));


        mainMenu.add(new SwitchMenuButton(BUTTON_GAP, 500, BUTTON_WIDTH, BUTTON_HEIGHT, "stats", "stats"));
        mainMenu.add(new SwitchMenuButton(BUTTON_GAP + BUTTON_WIDTH + BUTTON_GAP, 500, BUTTON_WIDTH, BUTTON_HEIGHT, "settings", "settings").getAnimateRight(true));


        mainMenu.add(new SwitchMenuButton(BUTTON_GAP, 710, BUTTON_WIDTH / 2, BUTTON_HEIGHT / 2, "change", "name"));
        mainMenu.add(new MenuText(BUTTON_GAP * 2 + BUTTON_WIDTH / 2, 742, Settings.getStr("name"), 35, "name", "Monospaced Regular"));
        mainMenu.add(new ExitButton(BUTTON_GAP, 800, BUTTON_WIDTH, BUTTON_HEIGHT, "exit"));


        mainMenu.add(new MenuTransition((Main.loaded) ? NORMAL_TRANSITION_TIME : LOADING_TRANSITION_TIME));
        menus.put("main", new Menu(mainMenu, "main"));
    }


    public static void loadButtonsStart() {
        loadMain();


        loadLevelMenu();
        loadReplayMenu();
        loadStatsMenu();
        loadSettingsMenu();
        loadVictoryMenu();
        loadName();
        loadReplaySettingsMenu();

        loadSoundSettingsMenu();

        loadEditorMenu();

        loadPreEditor();

        loadLevelBoard("main");
        loadLevelBoard("custom");

        loadLeaderboard("main\\full");
        loadLeaderboard("main\\1");
        loadLeaderboard("main\\2");
        loadLeaderboard("main\\3");
        loadLeaderboard("main\\4");
        loadLeaderboard("main\\5");


    }

    private static void loadMenu(String name) {
        loadLevelMenu();

        switch (name) {
            case "main":
                loadMain();
                break;
            case "name":
                loadName();
                break;
            case "settings":
                loadSettingsMenu();
                break;
            case "stats":
                loadStatsMenu();
                break;
            case "replays":
                loadReplayMenu();
                break;
            case "levels":
                loadLevelMenu();
                break;
            case "victory":
                loadVictoryMenu();
                break;
            case "replay settings":
                loadReplaySettingsMenu();
                break;
            case "sound settings":
                loadSoundSettingsMenu();
                break;
            case "pre editor":
                loadPreEditor();
                break;
            default:
                if (name.contains("leaderboard")) {
                    loadLeaderboard(name.replace(" leaderboard", ""));
                } else if (name.contains("levels")) {
                    loadLevelBoard(name.replace(" levels", ""));
                }

                break;
        }
    }


    private static void loadSettingsMenu() {
        ArrayList<MenuElement> elements = new ArrayList<>();
        elements.add(new MenuMap("main\\3"));

        elements.add(new MenuSlider(BUTTON_GAP, 100, 500, 100, "fps", "fps", 300, 60, false, Main.monitorFPS));


        elements.add(new SwitchMenuButton(BUTTON_GAP, 550, BUTTON_WIDTH, BUTTON_HEIGHT, "replay settings", "replay settings"));
        elements.add(new SwitchMenuButton(BUTTON_GAP * 2 + BUTTON_WIDTH, 550, BUTTON_WIDTH, BUTTON_HEIGHT
                , "sound settings", "sound settings").getAnimateRight(true));

        elements.add(new ToggleButton(BUTTON_GAP, 250, BUTTON_WIDTH, BUTTON_HEIGHT, "enable debug", "disable debug", "debug"));

        elements.add(new ToggleButton(BUTTON_GAP * 2 + BUTTON_WIDTH, 250, BUTTON_WIDTH, BUTTON_HEIGHT, "enable reduced motion", "disable reduced motion", "reduced motion").getAnimateRight(true));

        elements.add(new ToggleButton(BUTTON_GAP, 400, BUTTON_WIDTH, BUTTON_HEIGHT, "enable full speedrun", "disable full speedrun", "full speedrun"));

        elements.add(new ToggleButton(BUTTON_GAP*2+BUTTON_WIDTH, 400, BUTTON_WIDTH, BUTTON_HEIGHT, "enable image smoothing", "disable image smoothing", "image smoothing"));

        elements.add(new MenuSlider(BUTTON_GAP+550, 100, 500, 100, "graphics", "graphics", GRAPHICS_MAX, 0, false, Main.monitorFPS));

        elements.add(new SwitchMenuButton(BUTTON_GAP, 800, BUTTON_WIDTH, BUTTON_HEIGHT, "back", "main"));
        elements.add(new MenuText(900, 100, "settings: ", 55, "Title"));
        elements.add(new MenuTransition(NORMAL_TRANSITION_TIME));

        menus.put("settings", new Menu(elements, "main"));
    }

    private static void loadSoundSettingsMenu() {
        ArrayList<MenuElement> elements = new ArrayList<>();
        elements.add(new MenuMap("main\\3"));

        elements.add(new MenuSlider(BUTTON_GAP, 100, 500, 100, "sfx volume", "volume", 100, 0, false, false));

        elements.add(new MenuSlider(BUTTON_GAP, 300, 500, 100, "music volume", "music volume", 100, 0, false, false));

        elements.add(new SwitchMenuButton(BUTTON_GAP, 800, BUTTON_WIDTH, BUTTON_HEIGHT, "back", "settings"));
        elements.add(new MenuText(900, 100, "sound settings: ", 55, "Title"));
        elements.add(new MenuTransition(NORMAL_TRANSITION_TIME));

        menus.put("sound settings", new Menu(elements, "settings"));
    }


    private static void loadReplaySettingsMenu() {
        ArrayList<MenuElement> elements = new ArrayList<>();
        elements.add(new MenuMap("main\\3"));


        ArrayList<MenuElement> hideReplays = new ArrayList<>();

        int i = 0;
        for (String replay : Settings.getReplays()) {
            hideReplays.add(new ToggleButton(0, i * 150
                    , 200, 100, "show " + replay, "hide " + replay, "show " + replay, Settings.getReplayColor(replay)));
            i++;
        }

        elements.add(new ScrollMenu(1400, 175, 300, 500, new Menu(hideReplays, "main"), "leaderboard scroll"));


        elements.add(new SwitchMenuButton(BUTTON_GAP, 800, BUTTON_WIDTH, BUTTON_HEIGHT, "back", "settings"));
        elements.add(new MenuText(900, 100, "replay settings: ", 55, "Title"));
        elements.add(new MenuTransition(NORMAL_TRANSITION_TIME));

        menus.put("replay settings", new Menu(elements, "settings"));
    }

    private static void loadReplayMenu() {
        ArrayList<MenuElement> elements = new ArrayList<>();
        elements.add(new MenuMap("main\\4"));

        elements.add(new ScrollMenu(BUTTON_GAP, BUTTON_GAP * 3, (int) Main.DEFAULT_WIDTH_MAP - BUTTON_GAP * 5, (int) (BUTTON_HEIGHT * 1.5), new Menu(getReplayButtons("main"), "main"), "main replay scroll"));

        elements.add(new ScrollMenu(BUTTON_GAP, BUTTON_GAP * 3+BUTTON_HEIGHT * 3, (int) Main.DEFAULT_WIDTH_MAP - BUTTON_GAP * 5, (int) (BUTTON_HEIGHT * 1.5), new Menu(getReplayButtons("custom"), "main"), "custom replay scroll"));




        elements.add(new SwitchMenuButton(BUTTON_GAP, 800, BUTTON_WIDTH, BUTTON_HEIGHT, "back", "main"));

        elements.add(new MenuText(900, 100, "Replay Menu: ", 55, "Title"));
        elements.add(new MenuTransition(NORMAL_TRANSITION_TIME));
        menus.put("replays", new Menu(elements, "main"));

    }

    private static ArrayList<MenuElement> getReplayButtons(String folder) {

        ArrayList<MenuElement> mainLevels = new ArrayList<>();

        File directory = new File("res\\replays\\" + folder);
        File[] levels = directory.listFiles();
        int fileCount = directory.list().length;



        int maxLevel = 0;
        for (int i = 0; i < fileCount; i++) {
            if ((levels[i].getName().replace(".txt", "").matches(Main.IS_INT_REGEX))) {
                if (Integer.parseInt(levels[i].getName().replace(".txt", "")) > maxLevel) {
                    maxLevel = Integer.parseInt(levels[i].getName().replace(".txt", ""));
                }
            }
        }


        int maxColums = 5;

        for (int i = 1; i < maxLevel+1; i++) {

            double x = BUTTON_GAP + (BUTTON_WIDTH+BUTTON_GAP)*((i-1)%maxColums);
            double y = (BUTTON_HEIGHT+BUTTON_GAP)*((int)(((i-1)*1.0)/maxColums));
            if ((new File("res\\replays\\" + folder + "\\" + i + ".txt").exists())) {

                String levelName = folder + "\\" + i;

                mainLevels.add(new ReplayButton((int)x, (int)y, BUTTON_WIDTH, BUTTON_HEIGHT, levelName));

            } else {

                mainLevels.add(new InactiveButton((int)x, (int)y, BUTTON_WIDTH, BUTTON_HEIGHT, "level missing"));

            }


        }

        return mainLevels;




    }

    private static void loadName() {
        ArrayList<MenuElement> elements = new ArrayList<>();
        elements.add(new MenuText(300, 200
                , "enter name:", 60, "name"));

        elements.add(new MenuTextField(300, 300
                , "", 50, "name", MenuElement.TextType.normal));

        elements.add(new MenuTransition(NORMAL_TRANSITION_TIME));


        menus.put("name", new Menu(elements, "main"));

    }

    private static void loadLevelMenu() {


        ArrayList<MenuElement> elements = new ArrayList<>();

        elements.add(new SwitchMenuButton(BUTTON_GAP, BUTTON_GAP * 2, BUTTON_WIDTH, BUTTON_HEIGHT, "main", "main levels"));

        elements.add(new SwitchMenuButton(BUTTON_GAP * 2 + BUTTON_WIDTH, BUTTON_GAP * 2, BUTTON_WIDTH, BUTTON_HEIGHT, "custom", "custom levels"));


        elements.add(new SwitchMenuButton(BUTTON_GAP, 800, BUTTON_WIDTH, BUTTON_HEIGHT, "back", "main"));
        elements.add(new MenuText(475, 55, "world Menu: ", 50, "Title"));
        elements.add(new MenuTransition(NORMAL_TRANSITION_TIME));
        menus.put("levels", new Menu(elements, "main"));

    }


    private static void loadLevelBoard(String name) {
        ArrayList<MenuElement> elements = new ArrayList<>();





        elements.add(new MenuMap("main" + "\\5"));


        elements.add(new MenuText(475, 55, name + " levels: ", 50, "Title"));

        elements.add(new ScrollMenu(BUTTON_GAP, BUTTON_GAP * 3, (int) Main.DEFAULT_WIDTH_MAP - BUTTON_GAP * 5, (int) (BUTTON_HEIGHT * 3.5), new Menu(getLevelButtons(Main.getWorld(name)), "main"), "level scroll"));




        elements.add(new MenuText(BUTTON_GAP, BUTTON_GAP, "full speedrun best time:  " + String.format("%.2f", UserFileHandler.getTime(Main.getWorld(name) + "\\full", 1)), 20, "full"));


        elements.add(new MenuText(BUTTON_GAP, BUTTON_GAP+25, "cumulative best times:    " + String.format("%.2f", UserFileHandler.getCumulativeBestTimes(Main.getWorld(name))), 20, "full"));


        elements.add(new SwitchMenuButton(BUTTON_GAP, 800, BUTTON_WIDTH, BUTTON_HEIGHT, "back", "levels"));
        elements.add(new MenuTransition(NORMAL_TRANSITION_TIME));


        menus.put(name + " levels", new Menu(elements, "levels"));

    }

    private static final int maxColums = 5;

    private static ArrayList<MenuElement> getLevelButtons(String folder) {
        ArrayList<MenuElement> mainLevels = new ArrayList<>();

        File directory = new File("res\\maps\\" + folder);
        File[] levels = directory.listFiles();
        int fileCount = directory.list().length;


        int nonIntLevels = 0;

        int maxLevel = 0;
        for (int i = 0; i < fileCount; i++) {
            if ((levels[i].getName().replace(".txt", "").matches(Main.IS_INT_REGEX))) {
                if (Integer.parseInt(levels[i].getName().replace(".txt", "")) > maxLevel) {
                    maxLevel = Integer.parseInt(levels[i].getName().replace(".txt", ""));
                }
            } else {
                if (!(levels[i].isDirectory())) {
                    double x = BUTTON_GAP + (BUTTON_WIDTH+BUTTON_GAP)*((nonIntLevels)%maxColums);
                    double y = (BUTTON_HEIGHT+BUTTON_GAP)*((int)(((nonIntLevels)*1.0)/maxColums));
                    mainLevels.add(new LevelButton((int)x, (int)y, BUTTON_WIDTH, BUTTON_HEIGHT, levels[i].getName().replace(".txt", "")));
                    nonIntLevels++;
                }
            }
        }




        for (int i = 1+nonIntLevels; i < maxLevel+1+nonIntLevels; i++) {
            double x = BUTTON_GAP + (BUTTON_WIDTH+BUTTON_GAP)*((i-1)%maxColums);
            double y = (BUTTON_HEIGHT+BUTTON_GAP)*((int)(((i-1)*1.0)/maxColums));
            if ((new File("res\\maps\\" + folder + "\\" + i + ".txt").exists())) {

                String levelName = folder + "\\" + i;

                mainLevels.add(new LevelButton((int)x, (int)y, BUTTON_WIDTH, BUTTON_HEIGHT, levelName));
            } else {
                mainLevels.add(new InactiveButton((int)x, (int)y, BUTTON_WIDTH, BUTTON_HEIGHT, "level missing"));

            }


        }

        return mainLevels;
    }

    private static void loadStatsMenu() {
        ArrayList<MenuElement> stats = new ArrayList<>();
        stats.add(new MenuMap("main\\1"));
        stats.add(new SwitchMenuButton(BUTTON_GAP, 800, BUTTON_WIDTH, BUTTON_HEIGHT, "back", "main"));
        stats.add(new MenuText(50, 75, "Stats:", 45, "deaths"));

        int i = 0;
        for (String stat : Stats.getExpectedStats()) {
            if ((Stats.doubleStats.contains(stat))) {
                stats.add(new MenuText(50, 200 + i * BUTTON_GAP
                        , stat + ": " + Main.formatTime(Stats.getD(stat)), 30, stat));

            } else {
                stats.add(new MenuText(50, 200 + i * BUTTON_GAP
                        , stat + ": " + Stats.get(stat), 30, stat));
            }
            i++;
        }


        Menu speedrun = getLeaderboardMenu();

        stats.add(new MenuText(1400, 75, "Leaderboards:", 45, "deaths"));

        stats.add(new ScrollMenu(1400, 175, 300, 500, speedrun, "leaderboard scroll"));


        stats.add(new MenuTransition(NORMAL_TRANSITION_TIME));


        menus.put("stats", new Menu(stats, "main"));

    }

    private static void loadLeaderboard(String name) {
        ArrayList<MenuElement> elements = new ArrayList<>();

        elements.add(new SwitchMenuButton(BUTTON_GAP, 800, BUTTON_WIDTH, BUTTON_HEIGHT, "back", "stats"));
        elements.add(new MenuText(5, 75, name.replace("full", "speedrun") + " leaderboard: ", 50));


        elements.add(new ScrollMenu(50, 150, 700, 500, loadLeaderboardTimes(name), name + " leaderboard scroll"));


        menus.put(name + " leaderboard", new Menu(elements, "stats"));
    }

    private static Menu loadLeaderboardTimes(String name) {
        ArrayList<MenuElement> elements = new ArrayList<>();


        ArrayList<LeaderboardTime> leaderboardTimes = UserFileHandler.getLeaderboard(name);

        for (int i = 0; i < leaderboardTimes.size(); i++) {
            elements.add(new MenuText(5, 0 + i * 40, (i + 1) + ": " + leaderboardTimes.get(i).getFormatString(), 30, "d", "Monospaced Regular"));
        }


        return new Menu(elements, "stats");
    }


    private static Menu getLeaderboardMenu() {
        ArrayList<MenuElement> elements = new ArrayList<>();



        elements.add(new SwitchMenuButton(0, 0, 200, 100, "full speedrun", "main\\full leaderboard"));


        int i = 0;
        for (String level : UserFileHandler.getLevels("main")) {
            i++;
            elements.add(new SwitchMenuButton(0, 125*i, 200, 100, "level " + level, level + " leaderboard"));

        }


        return new Menu(elements, "stats");
    }


    private static void loadVictoryMenu() {
        ArrayList<MenuElement> stats = new ArrayList<>();
        stats.add(new SwitchMenuButton(350, 775, BUTTON_WIDTH, BUTTON_HEIGHT, "back", "main"));
        stats.add(new LevelButton("play again", 700, 775, BUTTON_WIDTH, BUTTON_HEIGHT, Main.mapName));
        stats.add(new MenuTransition(NORMAL_TRANSITION_TIME));


        menus.put("victory", new Menu(stats, "levels"));

    }


    private static void loadEditorMenu() {
        ArrayList<MenuElement> elements = new ArrayList<>();

        ArrayList<MenuElement> items = new ArrayList<>();


        items.add(new SettingButton(0, 0, BUTTON_HEIGHT, BUTTON_HEIGHT, "editor tool", "stickyWall", MenuElement.TextType.hide));
        items.add(new SettingButton(BUTTON_GAP + BUTTON_HEIGHT, 0, BUTTON_HEIGHT, BUTTON_HEIGHT, "editor tool", "wall", MenuElement.TextType.hide));


        items.add(new SettingButton(BUTTON_GAP * 2 + BUTTON_HEIGHT * 2, 0, BUTTON_HEIGHT, BUTTON_HEIGHT, "editor tool", "flag", MenuElement.TextType.hide));

        items.add(new SettingButton(BUTTON_GAP * 3 + BUTTON_HEIGHT * 3, 0, BUTTON_HEIGHT, BUTTON_HEIGHT, "editor tool", "key", MenuElement.TextType.hide));
        items.add(new SettingButton(BUTTON_GAP * 4 + BUTTON_HEIGHT * 4, 0, BUTTON_HEIGHT, BUTTON_HEIGHT, "editor tool", "gate", MenuElement.TextType.hide));

        items.add(new SettingButton(BUTTON_GAP * 5 + BUTTON_HEIGHT * 5, 0, BUTTON_HEIGHT, BUTTON_HEIGHT, "editor tool", "water", MenuElement.TextType.hide));
        items.add(new SettingButton(BUTTON_GAP * 6 + BUTTON_HEIGHT * 6, 0, BUTTON_HEIGHT, BUTTON_HEIGHT, "editor tool", "lava", MenuElement.TextType.hide));
        items.add(new SettingButton(BUTTON_GAP * 7 + BUTTON_HEIGHT * 7, 0, BUTTON_HEIGHT, BUTTON_HEIGHT, "editor tool", "corner", MenuElement.TextType.hide));
        items.add(new SettingButton(BUTTON_GAP * 8 + BUTTON_HEIGHT * 8, 0, BUTTON_HEIGHT, BUTTON_HEIGHT, "editor tool", "background wall", MenuElement.TextType.hide));
        items.add(new SettingButton(BUTTON_GAP * 9 + BUTTON_HEIGHT * 9, 0, BUTTON_HEIGHT, BUTTON_HEIGHT, "editor tool", "sandTile", MenuElement.TextType.hide));
        items.add(new SettingButton(BUTTON_GAP * 10 + BUTTON_HEIGHT * 10, 0, BUTTON_HEIGHT, BUTTON_HEIGHT, "editor tool", "candle", MenuElement.TextType.hide));

        items.add(new SettingButton(BUTTON_GAP * 11 + BUTTON_HEIGHT * 11, 0, BUTTON_HEIGHT, BUTTON_HEIGHT, "editor tool", "gear", MenuElement.TextType.hide));
        items.add(new SettingButton(BUTTON_GAP * 12 + BUTTON_HEIGHT * 12, 0, BUTTON_HEIGHT, BUTTON_HEIGHT, "editor tool", "gearSpeed", MenuElement.TextType.hide));

        elements.add(new ScrollMenu(BUTTON_GAP * 3 + BUTTON_WIDTH, 800, BUTTON_WIDTH * 3, BUTTON_HEIGHT, new Menu(items, "editor"), "item scroll"));


        elements.add(new SettingButton(BUTTON_GAP * 3 + BUTTON_WIDTH * 5, 800, BUTTON_HEIGHT, BUTTON_HEIGHT, "editor tool", "player", MenuElement.TextType.hide));
        elements.add(new SettingButton(BUTTON_GAP * 4 + BUTTON_WIDTH * 5 + BUTTON_HEIGHT, 800, BUTTON_HEIGHT, BUTTON_HEIGHT, "editor tool", "cursor", MenuElement.TextType.hide));
        elements.add(new SettingButton(BUTTON_GAP * 5 + BUTTON_WIDTH * 5 + BUTTON_HEIGHT * 2, 800, BUTTON_HEIGHT, BUTTON_HEIGHT, "editor tool", "eraser", MenuElement.TextType.hide));

        elements.add(new SettingButtonSwitchMenu(BUTTON_GAP, 800, BUTTON_WIDTH, BUTTON_HEIGHT, "back", "exit", "main", MenuElement.TextType.choice));
        elements.add(new SettingButtonSwitchMenu(BUTTON_GAP, 900, BUTTON_WIDTH, BUTTON_HEIGHT, "back", "save+exit", "main", MenuElement.TextType.choice));


        menus.put("editor", new Menu(elements, "main"));
    }


}
