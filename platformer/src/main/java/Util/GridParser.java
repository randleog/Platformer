package Util;

import Map.*;

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
                        case "2":
                            if (j > 0) {
                                if (formatMap.get(i)[j - 1].equals("2")) {
                                    walls.get(walls.size() - 1).setSizeX(walls.get(walls.size() - 1).getSizeX() + mapScale);
                                } else {
                                    walls.add(new Wall(x, y, null, mapScale, mapScale+(mapScale*1.0)/1.539, InputAction.Default, FillType.Nothing, 1));
                                }
                            } else {
                                walls.add(new Wall(x, y, null, mapScale, mapScale+(mapScale*1.0)/1.539, InputAction.Default, FillType.Nothing, 1));


                            }
                            break;
                        case "3":
                            if (j > 0) {
                                if (formatMap.get(i)[j - 1].equals("3")) {
                                    walls.get(walls.size() - 1).setSizeX(walls.get(walls.size() - 1).getSizeX() + mapScale);
                                } else {
                                    walls.add(new Wall(x, y-(mapScale*1.0)/2, null, mapScale, mapScale+(mapScale*1.0)/1.539, InputAction.Default, FillType.Nothing, 1));
                                }
                            } else {
                                walls.add(new Wall(x, y-(mapScale*1.0)/2, null, mapScale, mapScale+(mapScale*1.0)/1.539, InputAction.Default, FillType.Nothing, 1));


                            }
                            break;
                        case "4":
                            if (j > 0) {
                                if (formatMap.get(i)[j - 1].equals("4")) {
                                    walls.get(walls.size() - 1).setSizeX(walls.get(walls.size() - 1).getSizeX() + mapScale);
                                } else {
                                    walls.add(new Wall(x, y, null, mapScale, mapScale+(mapScale*1.0)/6.667, InputAction.Default, FillType.Nothing, 1));
                                }
                            } else {
                                walls.add(new Wall(x, y, null, mapScale, mapScale+(mapScale*1.0)/6.667, InputAction.Default, FillType.Nothing, 1));


                            }
                            break;
                        case "-1":
                            playerX = x;
                            playerY = y + 15;
                            break;
                        case "-2":
                            walls.add(new Flag(x, y, null));
                            break;
                        case "E":
                            walls.add(new BasicEnemy(x, y, null, false, false));
                            break;

                        default:
                            if (formatMap.get(i)[j].contains("C")) {
                                double rotation = Double.parseDouble(formatMap.get(i)[j].split(":")[1]);
                                walls.add(new CornerWall(x, y, null,mapScale, mapScale, InputAction.Default, FillType.Nothing, 1, rotation));
                            } else   if (formatMap.get(i)[j].contains("G")) {
                                int code = Integer.parseInt(formatMap.get(i)[j].split(":")[1]);
                                walls.add(new Gate(x, y, null,mapScale, mapScale, InputAction.Default, FillType.Nothing, 1, code));
                            }else   if (formatMap.get(i)[j].contains("K")) {
                                int code = Integer.parseInt(formatMap.get(i)[j].split(":")[1]);
                                walls.add(new Key(x, y, null, 1, code));
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
