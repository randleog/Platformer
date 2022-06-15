import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class Menu {
    private static final int BUTTON_WIDTH = 230;
    private static final int BUTTON_HEIGHT = 100;

    private static final int BUTTON_GAP = 58;


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
        Menu.loadButtonsStart();

        currentlyMenu = true;
        setCurrentMenu(newMenu);

        Main.currentMap = null;

        updateTransition(currentMenu);

        Main.loaded = true;
        Main.resetTimeline();

        Main.isVictory = false;


        SoundLoader.adjustMusicVolume();


        if (newMenu.equals("editor")) {
            Main.loadEditor();
        }

    }

    private static void updateTransition(String menu) {


        for (MenuElement button : menus.get(menu).getbuttons()) {
            if (button instanceof MenuTransition) {
                ((MenuTransition) button).loadImage(Main.getScreenshot());
            } else if (button instanceof MenuZoomTransition) {
                ((MenuZoomTransition) button).loadImage(Main.getScreenshot());
            } else if (button instanceof MenuMap) {

                ((MenuMap) button).loadMap();
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

    private static void loadMain() {
        ArrayList<MenuElement> mainMenu = new ArrayList<>();
        mainMenu.add(new MenuMap("main\\2"));
        mainMenu.add(new MenuText(BUTTON_GAP, 100, "Platformer v" + Main.VERSION, 55, "Title"));


        mainMenu.add(new SwitchMenuButton(BUTTON_GAP, 200, BUTTON_WIDTH * 2 + BUTTON_GAP, (BUTTON_HEIGHT), "levels", "levels"));

        mainMenu.add(new SwitchMenuButton(BUTTON_GAP, 350, BUTTON_WIDTH, BUTTON_HEIGHT, "replays", "replays"));
        mainMenu.add(new SwitchMenuButton(BUTTON_GAP + BUTTON_WIDTH + BUTTON_GAP, 350, BUTTON_WIDTH, BUTTON_HEIGHT, "level editor", "editor").getAnimateRight(true));


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

        elements.add(new MenuSlider(BUTTON_GAP, 100, 500, 100, "fps", "fps", 300, 15, false, Main.monitorFPS));


        elements.add(new SwitchMenuButton(BUTTON_GAP, 550, BUTTON_WIDTH, BUTTON_HEIGHT, "replay settings", "replay settings"));
        elements.add(new SwitchMenuButton(BUTTON_GAP * 2 + BUTTON_WIDTH, 550, BUTTON_WIDTH, BUTTON_HEIGHT
                , "sound settings", "sound settings").getAnimateRight(true));

        elements.add(new ToggleButton(BUTTON_GAP, 250, BUTTON_WIDTH, BUTTON_HEIGHT, "enable debug", "disable debug", "debug"));

        elements.add(new ToggleButton(BUTTON_GAP * 2 + BUTTON_WIDTH, 250, BUTTON_WIDTH, BUTTON_HEIGHT, "enable reduced motion", "disable reduced motion", "reduced motion").getAnimateRight(true));

        elements.add(new ToggleButton(BUTTON_GAP, 400, BUTTON_WIDTH, BUTTON_HEIGHT, "enable full speedrun", "disable full speedrun", "full speedrun"));

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
        File directory = new File("res\\replays");
        File[] levels = directory.listFiles();
        int fileCount = directory.list().length;

        int directories = 0;
        for (int i = 0; i < fileCount; i++) {
            if (!levels[i].isDirectory()) {
                int x = i - directories;

                double width = Main.DEFAULT_WIDTH_MAP - BUTTON_GAP * 2;

                int xFactor = (int) (x % (width
                        / (BUTTON_WIDTH + BUTTON_GAP)));

                int yFactor = (int) (x / (width
                        / (BUTTON_WIDTH + BUTTON_GAP)));


                elements.add(new ReplayButton(xFactor * BUTTON_WIDTH + xFactor * BUTTON_GAP + BUTTON_GAP
                        , yFactor * BUTTON_HEIGHT + yFactor * BUTTON_GAP + BUTTON_GAP * 2
                        , BUTTON_WIDTH, BUTTON_HEIGHT, levels[i].getName().replace(".txt", "")));
            } else {
                directories++;
            }
        }
        elements.add(new SwitchMenuButton(BUTTON_GAP, 800, BUTTON_WIDTH, BUTTON_HEIGHT, "back", "main"));

        elements.add(new MenuText(900, 100, "Replay Menu: ", 55, "Title"));
        elements.add(new MenuTransition(NORMAL_TRANSITION_TIME));
        menus.put("replays", new Menu(elements, "main"));

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
        elements.add(new MenuText(475, 55, "Levels Menu: ", 50, "Title"));
        elements.add(new MenuTransition(NORMAL_TRANSITION_TIME));
        menus.put("levels", new Menu(elements, "main"));

    }


    private static void loadLevelBoard(String name) {
        ArrayList<MenuElement> elements = new ArrayList<>();





        elements.add(new MenuMap("main" + "\\5"));


        elements.add(new MenuText(475, 55, "Levels Menu: ", 50, "Title"));

        elements.add(new ScrollMenu(BUTTON_GAP, BUTTON_GAP * 3, (int) Main.DEFAULT_WIDTH_MAP - BUTTON_GAP * 5, (int) (BUTTON_HEIGHT * 1.5), new Menu(getLevelButtons(Main.getWorld(name)), "main"), "level scroll"));




        elements.add(new MenuText(BUTTON_GAP, BUTTON_GAP, "full speedrun best time:  " + String.format("%.2f", UserFileHandler.getTime(Main.getWorld(name) + "\\full", 1)), 20, "full"));


        elements.add(new MenuText(BUTTON_GAP, BUTTON_GAP+25, "cumulative best times:    " + String.format("%.2f", UserFileHandler.getCumulativeBestTimes(Main.getWorld(name))), 20, "full"));


        elements.add(new SwitchMenuButton(BUTTON_GAP, 800, BUTTON_WIDTH, BUTTON_HEIGHT, "back", "levels"));
        elements.add(new MenuTransition(NORMAL_TRANSITION_TIME));


        menus.put(name + " levels", new Menu(elements, "levels"));

    }

    private static ArrayList<MenuElement> getLevelButtons(String folder) {
        ArrayList<MenuElement> mainLevels = new ArrayList<>();

        mainLevels.add(new MenuText(0, 0, folder, 20));

        File directory = new File("res\\maps\\" + folder);
        File[] levels = directory.listFiles();
        int fileCount = directory.list().length;

        int directories = 0;

        for (int i = 0; i < fileCount; i++) {


            if (!levels[i].isDirectory()) {
                int x = i - directories;
                double width = Main.DEFAULT_WIDTH_MAP - BUTTON_GAP * 10;

                int xFactor = (int) (x % (width
                        / (BUTTON_WIDTH + BUTTON_GAP)));

                int yFactor = (int) (x / (width
                        / (BUTTON_WIDTH + BUTTON_GAP)));


                String levelName = levels[i].getName().replace(".txt", "");
                if (folder.length() > 0) {
                    levelName = folder + "\\" + levels[i].getName().replace(".txt", "");
                }

                mainLevels.add(new LevelButton(xFactor * BUTTON_WIDTH + xFactor * BUTTON_GAP
                        , yFactor * BUTTON_HEIGHT + yFactor * BUTTON_GAP
                        , BUTTON_WIDTH, BUTTON_HEIGHT, levelName));


            } else {
                directories++;
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
        items.add(new SettingButton(BUTTON_GAP * 6 + BUTTON_HEIGHT * 6, 0, BUTTON_HEIGHT, BUTTON_HEIGHT, "editor tool", "corner", MenuElement.TextType.hide));

        elements.add(new ScrollMenu(BUTTON_GAP * 3 + BUTTON_WIDTH, 800, BUTTON_WIDTH * 4, BUTTON_HEIGHT, new Menu(items, "editor"), "item scroll"));


        elements.add(new SettingButton(BUTTON_GAP * 3 + BUTTON_WIDTH * 5, 800, BUTTON_HEIGHT, BUTTON_HEIGHT, "editor tool", "player", MenuElement.TextType.hide));
        elements.add(new SettingButton(BUTTON_GAP * 4 + BUTTON_WIDTH * 5 + BUTTON_HEIGHT, 800, BUTTON_HEIGHT, BUTTON_HEIGHT, "editor tool", "cursor", MenuElement.TextType.hide));
        elements.add(new SettingButton(BUTTON_GAP * 5 + BUTTON_WIDTH * 5 + BUTTON_HEIGHT * 2, 800, BUTTON_HEIGHT, BUTTON_HEIGHT, "editor tool", "eraser", MenuElement.TextType.hide));

        elements.add(new SettingButtonSwitchMenu(BUTTON_GAP, 800, BUTTON_WIDTH, BUTTON_HEIGHT, "back", "exit", "main", MenuElement.TextType.choice));
        elements.add(new SettingButtonSwitchMenu(BUTTON_GAP, 900, BUTTON_WIDTH, BUTTON_HEIGHT, "back", "save+exit", "main", MenuElement.TextType.choice));


        menus.put("editor", new Menu(elements, "main"));
    }


}
