import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class UserFileHandler {

    private static final int MAX_LEADERBOARD = 50;

    private static final int MAX_LEADERBOARD_REPLAYS = 50;



    public static double getTime(String mapName, int number) {
        File file = new File("res\\userData\\levelTimes.txt");
        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String[] line = scanner.nextLine().split(" ");

                if (line[0].equals(mapName)) {
                    if (line.length-1 < number) {
                        return -1;
                    } else {
                        return Double.parseDouble(line[number].split(":")[0]);
                    }
                }
            }


            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return -1;

    }

    public static String getUserTime(String mapName, int number) {
        File file = new File("res\\userData\\levelTimes.txt");
        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String[] line = scanner.nextLine().split(" ");

                if (line[0].equals(mapName)) {
                    if (line.length-1 < number) {
                        return "";
                    } else {
                        return line[number];
                    }
                }
            }


            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return "";

    }

    public static int getLeaderboardSize(String mapName) {

        File file = new File("res\\userData\\levelTimes.txt");
        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String[] line = scanner.nextLine().split(" ");

                if (line[0].equals(mapName)) {
                    return line.length-1;
                }
            }


            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return 0;

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

    public static String getStat(String name, String stat) {
        String data = "";
        File file = new File("res\\userData\\" + name  + ".txt");
        checkFileExists(file);
        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String[] line = scanner.nextLine().split(" ");

                if (line[0].equals(stat.replaceAll(" ", "_"))) {
                    data = line[1];
                }
            }


            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        data = data.replaceAll("_", " ");

        if (data.equals("")) {
            data = "-";
        }
        return data;

    }

    private static void checkFileExists(File file) {
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch( IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void saveStat(String name, String stat, String data)  {

        stat = stat.replaceAll(" ", "_");
        data = data.replaceAll(" ", "_");

        if (data.equals("")) {
            data = data + "-";
        }

        File file = new File("res\\userData\\" + name + ".txt");
        checkFileExists(file);
        String text = "";
        try {
            Scanner scanner = new Scanner(file);
            boolean added = false;
            while (scanner.hasNextLine()) {
                String lineText = scanner.nextLine();
                String[] line = lineText.split(" ");

                if (line[0].equals(stat)) {


                    text = text + line[0]+ " " + data+ "\n";

                    added = true;

                } else {
                    text = text + lineText + "\n";
                }
                if (!scanner.hasNextLine()) {
                    text = text.substring(0,text.length()-1);
                }
            }
            if (!added) {
                text = text + "\n" + stat + " " + data;
            }

            if (text.charAt(0) == '\n') {
                text = text.substring(1);
            }

            FileWriter myWriter = new FileWriter("res\\userData\\" + name + ".txt");

            myWriter.write(text);
            myWriter.close();

            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    public static double getCumulativeBestTimes() {
        double total = 0;
        boolean notAll = false;

        for (int i = 1; i < Main.lastLevel+1; i++) {
            if (getTime(Integer.toString(i), 1) == -1) {
                notAll = true;
            }
            total = total + getTime(Integer.toString(i), 1);
        }

        if (notAll) {
            return -1;
        }
        return total;
    }






/*
    private static void saveUserTimeJson(String mapName, double time, String data) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("key", "solve");


        jsonObject.put("solves", solvesJ);

        try {
            FileWriter file = new FileWriter("solves.json");
            file.write(jsonObject.toJSONString());
            file.close();
        } catch (IOException e) {

            e.printStackTrace();
        }
    }
    
 */

    public static void saveUserTime(String mapName, double time, String data) {
        File file = new File("res\\userData\\levelTimes.txt");
        String text = "";
        try {
            Scanner scanner = new Scanner(file);

            while (scanner.hasNextLine()) {
                String lineText = scanner.nextLine();
                String[] line = lineText.split(" ");

                if (line[0].equals(mapName)) {


                    text = text + line[0]+ " ";
                    ArrayList<LeaderboardTime> values = new ArrayList<>();
                    for (int i = 1; i < line.length; i++) {


                        values.add(new LeaderboardTime(Double.parseDouble(line[i].split(":")[0]),line[i].split(":")[1],line[i].split(":")[2]));
                    }
                    values.add(new LeaderboardTime(time, Settings.getStr("name"), data));


                    Collections.sort(values);

                    int i = 0;
                    for (LeaderboardTime currentTime : values) {

                        if (i< MAX_LEADERBOARD) {
                            text = text + currentTime.getTime() + ":" + currentTime.getName() + ":" + currentTime.getData()+ " ";
                        }
                        i++;
                    }



                    text = text + "\n";
                } else {
                    text = text + lineText + "\n";
                }
                if (!scanner.hasNextLine()) {
                    text = text.substring(0,text.length()-1);
                }
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
