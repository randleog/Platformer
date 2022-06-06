import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class ReplaySave {


    public static ArrayList<Integer[]> getFrames(String mapName) {
        ArrayList<Integer[]> frames = new ArrayList<>();

        File file = new File("res\\replays\\" + mapName + ".txt");

        if (file.exists()) {


            try {
                Scanner scanner = new Scanner(file);

                while (scanner.hasNextLine()) {
                    String[] line = scanner.nextLine().split(" ");

                    frames.add(new Integer[]{Integer.parseInt(line[0]), Integer.parseInt(line[1])});

                }


                scanner.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        return frames;


    }


    public static Replay getReplay(String mapName) {
        return new Replay(getFrames(mapName), mapName);

    }

    public static void saveReplay(ArrayList<Integer[]> frames, String mapName) {
        try {




            FileWriter writer = new FileWriter("res\\replays\\"+ mapName + ".txt");
            String text = "";
            for (Integer[] frame :frames) {
                for (int i = 0; i < frame.length; i++) {
                    text = text + frame[i] + " ";
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
