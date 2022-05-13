import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class GridParser {

    //public static final int MAP_SCALE = 200;


    public static void parseAll() {
        File directory = new File("res\\maps\\format\\");
        File[] levels = directory.listFiles();
        int fileCount = directory.list().length;

        for (int i = 0; i < fileCount; i++) {
            if (!levels[i].isDirectory()) {
                loadLevel(levels[i].getName().replace(".txt", ""));
            }
        }
    }


    public static void loadLevel(String name) {

        File file = new File("res\\maps\\format\\" + name + ".txt");
        ArrayList<String[]> formatMap = new ArrayList<>();
        int mapScale = 100;  //should be multiple of 50. anything lower than 50 definately not
        try {
            Scanner in = new Scanner(file);
            mapScale = in.nextInt();

            in.nextLine();


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
            System.out.println(mapScale);

            int width = formatMap.get(0).length * mapScale * 4;
            int height = formatMap.size() * mapScale * 4;

            int playerX = 0;
            int playerY = 0;


            for (int i = 0; i < formatMap.size(); i++) {
                for (int j = 0; j < formatMap.get(i).length; j++) {
                    int x = j * mapScale;
                    int y = i * mapScale;
                    switch (formatMap.get(i)[j]) {
                        case "1":
                            if (j > 0) {
                                if (formatMap.get(i)[j - 1].equals("1")) {
                                    walls.get(walls.size() - 1).setSizeX(walls.get(walls.size() - 1).getSizeX() + mapScale);
                                } else {
                                    walls.add(new Wall(x, y, null, mapScale, mapScale, InputAction.Default, FillType.Nothing, 1));
                                }
                            } else {
                                walls.add(new Wall(x, y, null, mapScale, mapScale, InputAction.Default, FillType.Nothing, 1));


                            }
                            break;
                        case "-1":
                            playerX = x;
                            playerY = y + 15;
                            break;
                        case "-2":
                            walls.add(new Flag(x, y, null));
                            break;

                        default:
                            if (formatMap.get(i)[j].contains("C")) {
                                double rotation = Double.parseDouble(formatMap.get(i)[j].split(":")[1]);
                                walls.add(new CornerWall(x, y, null,mapScale*3, mapScale, InputAction.Default, FillType.Nothing, 1, rotation));
                            }
                            break;
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
