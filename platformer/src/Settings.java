import java.util.ArrayList;
import java.util.HashMap;

public class Settings {

    private static HashMap<String, Integer> settings = new HashMap<>();

    private static HashMap<String, String> settingsString = new HashMap<>();


    public static final String BEST_REPLAY = "best";
    public static final String SPEEDRUN_REPLAY = "speedrun";
    public static final String LAST_REPLAY = "last";
    public static final String AUTHOR_REPLAY = "author";
    public static final String GOLD_REPLAY = "gold";

    public static final String ANONYMOUS_NAME = "-";


    public static final int MAX_NAME_LENGTH = 21;

    public static final String FONT = "system";

    public static void put(String key, String input) {
        if (key.equals("fps")) {
            try {
                throw new Exception();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        settingsString.put(key, input);
    }

    public static void put(String key, Integer input) {
        settings.put(key, input);
    }


    public static String getStr(String key) {
        return settingsString.get(key);
    }

    public static double getStrD(String key) {

        return Double.parseDouble(settingsString.get(key));
    }


    public static Integer get(String key) {
        return settings.get(key);
    }

    public static double getD(String key) {
        return settings.get(key)*1.0;
    }

    public static void save() {
        for (String key : settingsString.keySet()) {
            saveSetting(key);
        }
        for (String key : settings.keySet()) {
            saveSetting(key);
        }

    }

    public static void load() {
        loadSetting("debug");
        loadSetting("fps");
        loadSetting("full speedrun");
        loadSetting("name");
    }

    private static void loadSetting(String name) {
        String data = UserFileHandler.getStat("settings", name);
        if (data.matches(Main.IS_INT_REGEX)) {
            put(name, Integer.parseInt(data));
        } else {
            if (settingsString.containsKey(name)) {
                put(name, data);
            }
        }

    }

    private static void saveSetting(String name) {
        if (settingsString.containsKey(name)) {
            UserFileHandler.saveStat("settings", name, getStr(name));

        } else {

            UserFileHandler.saveStat("settings", name, Integer.toString(get(name)));
        }

    }


    public static void initialiseValues() {

        put("playback speed", -1);

        put("speed", "1");

        put("debug", -1);
        put("full speedrun", -1);

        put("fps", Main.monitorFPS);

        put("replay speed", 10);

        put("name", ANONYMOUS_NAME);


        put("focus", BEST_REPLAY);

        put("show " + AUTHOR_REPLAY, 1);
        put("show " + GOLD_REPLAY, 1);
        put("show " + BEST_REPLAY, 1);
        put("show " + SPEEDRUN_REPLAY, 1);
        put("show " + LAST_REPLAY, 1);
        load();


    }
}
