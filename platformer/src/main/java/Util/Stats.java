package Util;

import Util.UserFileHandler;

import java.util.ArrayList;
import java.util.HashMap;

public class Stats {

    public static ArrayList<String> expectedStats = new ArrayList<>();
    public static ArrayList<String> doubleStats = new ArrayList<>();


    public static final int DOUBLE_CONVERSION = 100;

    private static HashMap<String, Integer> stats = new HashMap<>();


    public static void put(String key, Integer input) {
        stats.put(key, input);
    }

    public static double getD(String key) {

        return stats.get(key)/(DOUBLE_CONVERSION*1.0);
    }


    public static int get(String key) {
        return stats.get(key);
    }

    public static void add(String key, int amount) {
        stats.put(key, stats.get(key)+amount);
    }

    public static void add(String key, double amount) {
        stats.put(key, stats.get(key)+(int)(amount*DOUBLE_CONVERSION));
    }

    public static void save() {

        for (String key : stats.keySet()) {
            saveStat(key);
        }

    }

    public static ArrayList<String> getExpectedStats() {
        return expectedStats;
    }

    public static void load() {
        expectedStats = new ArrayList<>();
        expectedStats.add("total Deaths");
        expectedStats.add("total Finishes");
        expectedStats.add("total Resets");
        expectedStats.add("total Jumps");
        expectedStats.add("total Time");
        doubleStats.add("total Time");


        for (String stat : expectedStats) {
            loadStat(stat);
        }



    }

    private static void loadStat(String name) {
        String result = UserFileHandler.getStat("stats", name);
        if (result.equals("")) {
            put(name, 0);
        } else {
            int data = Integer.parseInt(result);


            put(name, data);
        }

    }

    private static void saveStat(String name) {
        UserFileHandler.saveStat("stats", name, stats.get(name)+"");

    }




}
