import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

public class ReplaySave {

    public static final int DATA_POINTS = 4;


    public static Replay getReplay(String mapName) {
        ArrayList<Integer[]> frames = new ArrayList<>();

        File file = new File("res\\replays\\" + mapName + ".txt");

        String date = "";

        if (file.exists()) {


            try {
                Scanner scanner = new Scanner(file);

                String[] line = scanner.nextLine().split(" ");
                frames.add(new Integer[]{Integer.parseInt(line[0]), 0});
                date = line[1];


                while (scanner.hasNextLine()) {
                    line = scanner.nextLine().split(" ");

                    Integer[] frame = new Integer[line.length];
                    for (int i = 0; i < line.length; i++) {
                        frame[i]=Integer.parseInt(line[i]);

                    }

                    frames.add(frame);

                }


                scanner.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        String name = "";

        if (mapName.contains("author")) {
            name = Settings.AUTHOR_REPLAY;
        } else if (mapName.contains("gold")) {
            name = Settings.GOLD_REPLAY;
        } else if (mapName.contains("full")) {
            name = Settings.SPEEDRUN_REPLAY;
        }else if (mapName.contains("last")) {
            name = Settings.LAST_REPLAY;
        }else if (mapName.contains("saves")) {
            name = Settings.SAVE_REPLAY;
        } else {
            name = Settings.BEST_REPLAY;
        }

        return new Replay(frames, mapName, date, name);


    }





    public static void saveReplay(ArrayList<Integer[]> frames, String mapName) {

        try {

            if (!(new File("res\\replays\\"+ mapName + ".txt")).exists()) {
                try {
                    (new File("res\\replays\\"+ mapName + ".txt")).createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            FileWriter writer = new FileWriter("res\\replays\\"+ mapName + ".txt");
            String text = frames.get(0)[0] + " " +  LocalDate.now() + "\n";
            for (int i = 1; i < frames.size(); i++) {
                for (int j = 0; j < frames.get(i).length; j++) {
                    text = text + frames.get(i)[j] + " ";
                }
                text = text + "\n";
            }
            writer.write(text);

            writer.close();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
