import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class GridParser {

    public static final int MAP_SCALE = 100;


    public static void parseAll() {
        File directory = new File("res\\maps\\format\\");
        File[] levels = directory.listFiles();
        int fileCount = directory.list().length;

        for (int i = 0; i < fileCount; i++) {
            if (!levels[i].isDirectory()) {
                loadLevel(levels[i].getName().replace(".txt",""));
            }
        }
    }


    public static void loadLevel(String name) {

        File file = new File("res\\maps\\format\\" + name + ".txt");
        ArrayList<String[]> formatMap = new ArrayList<>();
        try {
            Scanner in = new Scanner(file);




            while (in.hasNextLine()) {
                String line = in.nextLine();
                formatMap.add(line.split(" "));

            }





            in.close();
        } catch (FileNotFoundException e) {
            System.out.println("file " + file.getPath() + " not found");
            e.printStackTrace();
        }


        ArrayList<GameEntity> walls = new ArrayList<>();

        try {
            FileWriter writer = new FileWriter("res\\maps\\" + name + ".txt");

            int width = formatMap.get(0).length*MAP_SCALE*2;
            int height = formatMap.size()*MAP_SCALE*2;

            int playerX = 0;
            int playerY = 0;


            for (int i = 0; i < formatMap.size(); i++) {
                for (int j = 0; j < formatMap.get(i).length; j++) {
                    int x = j*MAP_SCALE;
                    int y = i*MAP_SCALE;
                    if (formatMap.get(i)[j].equals("1")) {
                        if (j > 0) {
                            if (formatMap.get(i)[j - 1].equals("1")) {
                                walls.get(walls.size()-1).setSizeX(walls.get(walls.size()-1).getSizeX()+MAP_SCALE);
                            } else {
                                walls.add(new Wall(x, y, null, MAP_SCALE, MAP_SCALE, InputAction.Default, FillType.Nothing, 1));
                            }
                        } else {
                            walls.add(new Wall(x, y, null, MAP_SCALE, MAP_SCALE, InputAction.Default, FillType.Nothing, 1));


                        }
                    } else if (formatMap.get(i)[j].equals("-1")) {
                        playerX = x;
                        playerY = y+15;
                    }else if (formatMap.get(i)[j].equals("-2")) {
                        walls.add(new Flag(x, y, null));
                    }
                }
            }



            String lines = width + " " + height + " " + playerX + " " + playerY + "\n/\n/\n";
            for (GameEntity wall : walls) {
                lines = lines + wall.toString() + ";\n";
            }
            writer.write(lines);

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}
