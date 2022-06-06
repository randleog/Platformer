import java.util.ArrayList;

public class Replay {

    private ArrayList<Integer[]> frames = new ArrayList<>();

    private String name;

    public Replay(ArrayList<Integer[]> frames, String name) {

        this.frames = frames;
        this.name = name;

    }


    public String getName() {
        return name;
    }

    public ArrayList<Integer[]> getFrames() {
        return frames;
    }


    public static void saveReplays(ArrayList<Replay> actual) {

        for (Replay replay : actual) {
            ReplaySave.saveReplay(replay.getFrames(), "full\\" + replay.getName());
        }
    }

    public static double getTime(ArrayList<Replay> actual) {

        double time = 0;



        for (Replay replay : actual) {
            int fps = replay.getFrames().get(0)[0];

            time = time + ((replay.getFrames().size()-1)*1.0)/fps;
        }


        return time;


    }


    public static boolean canProgress(ArrayList<Replay> actual, String next) {


        if (next.equals("1")) {
            return true;
        }


        for (Replay replay : actual) {
            if (replay.getName().equals(next)) {
                return false;
            }
        }

        boolean can = true;
        for (int i = 2; i < Integer.parseInt(next); i++) {
            if (!(hasLast(actual, i))) {
                can = false;
            }
        }



        return can;

    }

    public double getTime() {

        return ((frames.size()-1)*1.0)/frames.get(0)[0];
    }

    private static boolean hasLast(ArrayList<Replay> actual, int next) {

        boolean can = false;
        for (Replay replay : actual) {
            if (replay.getName().equals(Integer.toString(next-1))) {
                can = true;
            }
        }


        return can;

    }
}
