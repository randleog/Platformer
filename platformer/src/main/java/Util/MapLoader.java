package Util;

import Map.Map;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import Map.Map;
import Map.Player;
import Map.ReplayPlayer;
import Map.*;
import javafx.scene.paint.Color;


import Main.Main;

public class MapLoader {





    /**
     * @param mapName
     * @param type    0 = empty, 1 = with player, 2 = replay player
     * @return
     */
    public static Map loadMap(String mapName, int type) {


        File file = new File("res\\maps\\" + mapName + ".txt");
        if (file.exists()) {

            String[] text = getFileString(file).split("/");

            Scanner header = new Scanner(text[0]);
            int width = header.nextInt();
            int height = header.nextInt();

            int playerX = header.nextInt();
            int playerY = header.nextInt();
            header.close();

            Map map;
            if (type == 2) {
                map = new Map(width, height, mapName.split("\\\\")[1], true, mapName.split("\\\\")[0]);
            } else {
                map = new Map(width, height, mapName.split("\\\\")[1], false, mapName.split("\\\\")[0]);
            }

            if (type == 1) {
                map.addEntity(new Player(playerX, playerY, map));
            }

                Scanner background = new Scanner(text[1]);
            String backgroundName = "";

                background.useDelimiter(";");
                if (background.hasNext()) {
                    backgroundName =background.next();
                    map.setBackground(BackgroundLoader.loadBackground(backgroundName), backgroundName);
                }
                while (background.hasNext()) {
                    String[] line = background.next().split(" ");
                    map.addBackgroundObject(new BackgroundObject(Integer.parseInt(line[0]),Integer.parseInt(line[1]), Integer.parseInt(line[2]), Integer.parseInt(line[3]), map,ImageLoader.wallTile, 1));
                }

                background.close();



            Scanner entities = new Scanner(text[2]);
            entities.useDelimiter(";");

            while (entities.hasNext()) {

                String[] line = entities.next().split(" ");

                String name = line[0];

                switch (name) {
                    case "stickyWall" -> {
                        int x = Integer.parseInt(line[1]);
                        int y = Integer.parseInt(line[2]);
                        int wallWidth = Integer.parseInt(line[3]);
                        int wallHeight = Integer.parseInt(line[4]);
                        map.addEntity(new StickyWall(x, y, map, wallWidth, wallHeight, InputAction.Default, FillType.Tile, 1));
                        break;
                    }
                    case "water" -> {
                        int x = Integer.parseInt(line[1]);
                        int y = Integer.parseInt(line[2]);
                        int wallWidth = Integer.parseInt(line[3]);
                        int wallHeight = Integer.parseInt(line[4]);
                        map.addEntity(getWater(x,y,map,wallWidth,wallHeight));
                        break;
                    }
                    case "lava" -> {
                        int x = Integer.parseInt(line[1]);
                        int y = Integer.parseInt(line[2]);
                        int wallWidth = Integer.parseInt(line[3]);
                        int wallHeight = Integer.parseInt(line[4]);
                        map.addEntity(getLava(x,y,map,wallWidth,wallHeight));

                        break;
                    }
                    case "gate" -> {
                        int x = Integer.parseInt(line[1]);
                        int y = Integer.parseInt(line[2]);

                        int wallWidth = Integer.parseInt(line[3]);
                        int wallHeight = Integer.parseInt(line[4]);
                        int code = Integer.parseInt(line[5]);
                        map.addGate(x, y, wallWidth, wallHeight, 1, code);
                        break;
                    }
                    case "key" -> {
                        int x = Integer.parseInt(line[1]);
                        int y = Integer.parseInt(line[2]);
                        int code = Integer.parseInt(line[3]);
                        map.addEntity(new Key(x, y, map, 1, code));
                        break;
                    }
                    case "moving" -> {
                        int x = Integer.parseInt(line[1]);
                        int y = Integer.parseInt(line[2]);
                        int wallWidth = Integer.parseInt(line[3]);
                        int wallHeight = Integer.parseInt(line[4]);
                        int velx = Integer.parseInt(line[5]);
                        int vely = Integer.parseInt(line[6]);

                        map.addMovingWall(x, y, wallWidth, wallHeight, vely, velx);
                        break;
                    }
                    case "basicEnemy" -> {
                        int x = Integer.parseInt(line[1]);
                        int y = Integer.parseInt(line[2]);
                        boolean runner = Boolean.parseBoolean(line[3]);
                        boolean jumper = Boolean.parseBoolean(line[4]);
                        map.addEntity(new BasicEnemy(x, y, map, runner, jumper));
                        break;
                    }
                    case "hookable" -> {
                        int x = Integer.parseInt(line[1]);
                        int y = Integer.parseInt(line[2]);

                        int radius = Integer.parseInt(line[3]);


                        map.addEntity(new Hookable(x, y, map, radius));
                        break;
                    }
                    case "flag" -> {
                        int x = Integer.parseInt(line[1]);
                        int y = Integer.parseInt(line[2]);
                        map.addEntity(new Flag(x, y, map));
                        break;
                    }
                    case "dimension" -> {
                        int x = Integer.parseInt(line[1]);
                        int y = Integer.parseInt(line[2]);
                        int tpx = Integer.parseInt(line[3]);
                        int tpy = Integer.parseInt(line[4]);
                        map.addEntity(new DimensionPortal(x, y, map, tpx, tpy));
                        break;
                    }
                    case "corner" -> {
                        int x = Integer.parseInt(line[1]);
                        int y = Integer.parseInt(line[2]);
                        int wallWidth = Integer.parseInt(line[3]);
                        int wallHeight = Integer.parseInt(line[4]);
                        double rotation = Double.parseDouble(line[5]);
                        map.addCornerWall(x, y, wallWidth, wallHeight, 1, rotation);
                        break;
                    }
                    case "trophy" -> {
                        int x = Integer.parseInt(line[1]);
                        int y = Integer.parseInt(line[2]);
                        String trophyName = line[3];

                        map.addEntity(new Trophy(x, y, map, trophyName));
                        break;
                    }
                    case "candle" -> {
                        int x = Integer.parseInt(line[1]);
                        int y = Integer.parseInt(line[2]);

                        map.addEntity(new Candle(x, y, map));
                        break;
                    }
                    case "gear" -> {
                        int x = Integer.parseInt(line[1]);
                        int y = Integer.parseInt(line[2]);

                        int wallWidth = Integer.parseInt(line[3]);
                        int wallHeight = Integer.parseInt(line[4]);
                        double startingSpeed = Double.parseDouble(line[5]);
                        int code = Integer.parseInt(line[6]);


                        map.addEntity(new Gear(x, y,wallWidth, wallHeight, map, startingSpeed, code));
                        break;
                    }
                    case "plate" -> {
                        int x = Integer.parseInt(line[1]);
                        int y = Integer.parseInt(line[2]);
                        int code = Integer.parseInt(line[3]);
                        map.addEntity(new Plate(x, y, map, 1, code));
                        break;
                    }
                    default -> {
                        String wallType = line[1];
                        int x = Integer.parseInt(line[2]);
                        int y = Integer.parseInt(line[3]);

                        int wallWidth = Integer.parseInt(line[4]);
                        int wallHeight = Integer.parseInt(line[5]);
                        Wall wall = new Wall(x, y, map, wallWidth, wallHeight, InputAction.Default, FillType.Tile, 1);

                        wall.setType(wallType);
                        wall.setImage(ImageLoader.getImage(name + "Tile"));

                        map.addEntity(wall);
                        break;
                    }
                }


            }

            entities.close();
            boolean isReplay = type == 2;

            String world = mapName.split("\\\\")[0];
            String level = mapName.split("\\\\")[1];

            //deal with the replays

            if ((new File("res\\replays\\" + world+ "\\gold\\" + level + ".txt").exists())) {
                if ((new File("res\\replays\\"+ mapName + ".txt").exists())) {



                    if (UserFileHandler.getTime(mapName, 1) < (ReplaySave.getReplay(world + "\\gold\\" + level).getTime())) {
                        System.out.println("?");
                        if ((new File("res\\replays\\" + world+ "\\author\\" + level + ".txt").exists())) {

                            System.out.println("shouldAdd");
                            map.addEntity(new ReplayPlayer(playerX, playerY, map, ReplaySave.getReplay(world +"\\author\\" + level), isReplay,  Settings.AUTHOR_REPLAY));

                            if (UserFileHandler.getTime(mapName, 1) < (ReplaySave.getReplay(world +"\\author\\" + level).getTime())) {
                                map.addEntity(new ReplayPlayer(playerX, playerY, map, ReplaySave.getReplay(world +"\\gold\\" + level), isReplay,  Settings.GOLD_REPLAY));
                            }
                        }
                    } else {


                        map.addEntity(new ReplayPlayer(playerX, playerY, map, ReplaySave.getReplay(world +"\\gold\\" + level), isReplay,  Settings.GOLD_REPLAY));

                    }
                } else {
                    map.addEntity(new ReplayPlayer(playerX, playerY, map, ReplaySave.getReplay(world +"\\gold\\" +level), isReplay, Settings.GOLD_REPLAY));
                }
            }

            if ((new File("res\\replays\\" +world+"\\full\\" + level + ".txt").exists())) {


                    map.addEntity(new ReplayPlayer(playerX, playerY, map, ReplaySave.getReplay(world+"\\full\\" + level), isReplay, Settings.SPEEDRUN_REPLAY));


            }


            if ((new File("res\\replays\\" + world + "\\last\\" + level + ".txt").exists())) {


                map.addEntity(new ReplayPlayer(playerX, playerY, map, ReplaySave.getReplay(world + "\\last\\" + level), isReplay,  Settings.LAST_REPLAY));


            }

            if ((new File("res\\replays\\" + mapName + ".txt").exists())) {

                map.addEntity(new ReplayPlayer(playerX, playerY, map, ReplaySave.getReplay(mapName), isReplay, Settings.BEST_REPLAY));
            }



            map.setStartEntities();
            return map;

        } else {
            System.out.println("no map found: " +"res\\maps\\" + mapName + ".txt");
            return null;
        }
    }




    public static final Liquid getWater(double x, double y, Map map, double sizeX, double sizeY) {
        return new Liquid(x, y, map, sizeX, sizeY, InputAction.Swim, Color.color(0.4, 0.8, 1, 0.3), 50, 0.0015, 0.01, 0.03, "water");
    }


    public static final Liquid getLava(double x, double y, Map map, double sizeX, double sizeY) {
        return new Liquid(x, y, map, sizeX, sizeY, InputAction.Lava, Color.color(1, 0.3, 0, 0.8), 50, 0.003, 0.05, 0.15, "lava");
    }


    public static void saveMap(Map map, boolean keepName) {
        String name = "";
        if (!keepName) {
            int i = 1;
            while ((new File("res\\maps\\custom\\" + i  + ".txt")).exists()) {
                i++;
            }
            name = i + "";
        }

        File file = new File ("res\\maps\\custom\\" + name + ".txt");
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(map.toString());


            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private static String getFileString(File file) {
        String text = "";
        try {
            Scanner in = new Scanner(file);

            while (in.hasNextLine()) {
                text = text + in.nextLine();
            }


            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return text;
    }
}
