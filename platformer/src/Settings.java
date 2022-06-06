import java.util.ArrayList;
import java.util.HashMap;

public class Settings {

    private static HashMap<String, Integer> settings = new HashMap<>();

    private static HashMap<String, String> settingsString = new HashMap<>();




    public static void put(String key, String input) {
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
        loadSetting("full speedrun");
        loadSetting("fps");
        loadSetting("full speedrun");
    }

    private static void loadSetting(String name) {
        String data = UserFileHandler.getStat("settings", name);
        if (data.matches(Main.IS_INT_REGEX)) {
            put(name, Integer.parseInt(data));
        } else {
            put(name, data);
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

        put("fps", 144);

        put("replay speed", 10);

        put("focus", "player");

        put("show author", 1);
        put("show gold", 1);
        put("show player", 1);
        put("show full speedrun", 1);

        load();


    }
}
