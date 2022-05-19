import java.awt.event.ItemEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class UserFileHandler {

    private static final int MAX_LEADERBOARD = 100;


    public static double getUserTime(String mapName, int number) {
        File file = new File("res\\userData\\levelTimes.txt");
        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String[] line = scanner.nextLine().split(" ");

                if (line[0].equals(mapName)) {
                    if (line.length-1 < number) {
                        return -1;
                    } else {
                        return Double.parseDouble(line[number]);
                    }
                }
            }


            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return -1;

    }

    public static ArrayList<String> getTrophies(String mapName) {
        ArrayList<String> trophies = new ArrayList<>();
        File file = new File("res\\userData\\trophies.txt");
        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String[] line = scanner.nextLine().split(" ");

                if (line[0].equals(mapName)) {
                    for (int i = 1; i < line.length; i++) {
                        trophies.add(line[i]);
                    }
                }
            }


            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return trophies;

    }


    public static void saveUserTrophy(String mapName, String trophy) {
        File file = new File("res\\userData\\trophies.txt");
        String text = "";
        try {
            Scanner scanner = new Scanner(file);
            boolean added = false;
            while (scanner.hasNextLine()) {
                String lineText = scanner.nextLine();
                String[] line = lineText.split(" ");

                if (line[0].equals(mapName)) {


                    text = text + line[0]+ " ";
                    ArrayList<String> values = new ArrayList<>();
                    for (int i = 1; i < line.length; i++) {
                        values.add(line[i]);
                    }
                    values.add(trophy);


                    for (String currenTime : values) {

                        text = text + currenTime + " ";
                    }

                    added = true;

                    text = text + "\n";
                } else {
                    text = text + lineText + "\n";
                }
                if (!scanner.hasNextLine()) {
                    text = text.substring(0,text.length()-1);
                }
            }
            if (!added) {
                text = text + "\n" + mapName + " " + trophy;
            }

            if (text.charAt(0) == '\n') {
                text = text.substring(1);
            }

            FileWriter myWriter = new FileWriter("res\\userData\\trophies.txt");

            myWriter.write(text);
            myWriter.close();

            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }



    }


    public static void saveUserTime(String mapName, double time) {
        File file = new File("res\\userData\\levelTimes.txt");
        String text = "";
        try {
            Scanner scanner = new Scanner(file);
            boolean added = false;
            while (scanner.hasNextLine()) {
                String lineText = scanner.nextLine();
                String[] line = lineText.split(" ");

                if (line[0].equals(mapName)) {


                    text = text + line[0]+ " ";
                    ArrayList<Double> values = new ArrayList<>();
                    for (int i = 1; i < line.length; i++) {
                        values.add(Double.parseDouble(line[i]));
                    }
                    values.add(time);
                    Collections.sort(values);

                    int i = 0;
                    for (Double currenTime : values) {

                        if (i< MAX_LEADERBOARD) {
                            text = text + currenTime + " ";
                        }
                        i++;
                    }

                    added = true;

                    text = text + "\n";
                } else {
                    text = text + lineText + "\n";
                }
                if (!scanner.hasNextLine()) {
                    text = text.substring(0,text.length()-1);
                }
            }
            if (!added) {
                text = text + "\n" + mapName + " " + time;
            }

            if (text.charAt(0) == '\n') {
                text = text.substring(1);
            }

            FileWriter myWriter = new FileWriter("res\\userData\\levelTimes.txt");

            myWriter.write(text);
            myWriter.close();

            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }



    }


}
