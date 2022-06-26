package Util;

import Main.Main;

import java.util.ArrayList;

public class Replay {

    public static final String DEFAULT_DATE = "5500 BCE";



    private ArrayList<Integer[]> frames = new ArrayList<>();

    private String mapName;

    private String date;

    private String name;

    public Replay(ArrayList<Integer[]> frames, String mapName) {

        this.frames = frames;
        this.mapName = mapName;
        this.date = "5500 BC";

        this.name = "replay";

    }

    public Replay(ArrayList<Integer[]> frames, String mapName, String date, String name) {

        this.frames = frames;

        this.name = name;
        this.mapName = mapName;
        this.date = date;

        if (date.equals("0")) {
            this.date = "5500 BC";
        }

    }

    public String getMapName() {
        return mapName;
    }

    public ArrayList<Integer[]> getFrames() {
       return new ArrayList<>(frames.subList(1,frames.size()-1));
    }

    public ArrayList<Integer[]> getAllFrames() {
        return frames;
    }



    public String getName() {
        return name;
    }


    public int getFps() {
        return frames.get(0)[0];
    }

    public String getDate() {
        return date;
    }

    public double getTime() {

        if (frames.size() == 0) {
            return 0;
        }

        return ((frames.size()-1)*1.0)/frames.get(0)[0];
    }


    public static boolean canProgress(ArrayList<Replay> actual, String next) {
        if (next.contains("custom")) {
            return false;
        }
        if (next.split("\\\\").length < 2) {
            return false;
        }

        String level = next.split("\\\\")[1];

        if (!(level .matches(Main.IS_INT_REGEX))) {
            return false;
        }

        if (actual.size() ==0) {
            if (level.equals("1")) {
                return true;
            }
        }


        for (Replay replay : actual) {
            if (replay.getMapName().equals(next)) {
                return false;
            }
        }

        boolean can = true;
        for (int i = 2; i < Integer.parseInt(level)+1; i++) {
            if (!(hasLast(actual, i))) {
                can = false;
            }
        }



        return can;

    }

    public static void saveReplays(ArrayList<Replay> actual) {

        for (Replay replay : actual) {
            ReplaySave.saveReplay(replay.getAllFrames(),  Main.getWorld(replay.getMapName()) + "\\full\\" + Main.getLevel( replay.getMapName()));
        }
    }

    public static double getTime(ArrayList<Replay> actual) {

        double time = 0;



        for (Replay replay : actual) {

            time = time + replay.getTime();
        }


        return time;


    }


    private static boolean hasLast(ArrayList<Replay> actual, int next) {

        boolean can = false;
        for (Replay replay : actual) {
            if (Main.getLevel(replay.getMapName()).equals(Integer.toString(next-1))) {
                can = true;
            }
        }


        return can;

    }
}
