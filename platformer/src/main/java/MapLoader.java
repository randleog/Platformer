import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

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


            Scanner background = new Scanner(text[1]);

            background.useDelimiter(";");
            while (background.hasNext()) {

                String[] line = background.next().split(" ");
                String name = line[0];

                if (name.equals("wall")) {
                    double parallax = Double.parseDouble(line[1]);
                    int x = Integer.parseInt(line[2]);
                    int y = Integer.parseInt(line[3]);
                    int wallWidth = Integer.parseInt(line[4]);
                    int wallHeight = Integer.parseInt(line[5]);
                    map.addWall(x, y, wallWidth, wallHeight, parallax);
                }


            }

            background.close();
            // System.out.println("wtf");

            Scanner entities = new Scanner(text[2]);
            entities.useDelimiter(";");

            while (entities.hasNext()) {

                String[] line = entities.next().split(" ");

                String name = line[0];

                int x = Integer.parseInt(line[1]);
                int y = Integer.parseInt(line[2]);
                switch (name) {
                    case "wall" -> {
                        int wallWidth = Integer.parseInt(line[3]);
                        int wallHeight = Integer.parseInt(line[4]);
                        map.addWall(x, y, wallWidth, wallHeight, 1);
                        break;
                    }
                    case "stickyWall" -> {
                        int wallWidth = Integer.parseInt(line[3]);
                        int wallHeight = Integer.parseInt(line[4]);
                        map.addEntity(new StickyWall(x, y, map, wallWidth, wallHeight, InputAction.Default, FillType.Tile, 1));
                        break;
                    }
                    case "water" -> {
                        int wallWidth = Integer.parseInt(line[3]);
                        int wallHeight = Integer.parseInt(line[4]);
                        map.addEntity(new Water(x, y, map, wallWidth, wallHeight, 1));
                        break;
                    }
                    case "gate" -> {
                        int wallWidth = Integer.parseInt(line[3]);
                        int wallHeight = Integer.parseInt(line[4]);
                        int code = Integer.parseInt(line[5]);
                        map.addGate(x, y, wallWidth, wallHeight, 1, code);
                        break;
                    }
                    case "key" -> {
                        int code = Integer.parseInt(line[3]);
                        map.addEntity(new Key(x, y, map, 1, code));
                        break;
                    }
                    case "moving" -> {
                        int wallWidth = Integer.parseInt(line[3]);
                        int wallHeight = Integer.parseInt(line[4]);
                        int velx = Integer.parseInt(line[5]);
                        int vely = Integer.parseInt(line[6]);

                        map.addMovingWall(x, y, wallWidth, wallHeight, vely, velx);
                        break;
                    }
                    case "basicEnemy" -> {
                        boolean runner = Boolean.parseBoolean(line[3]);
                        boolean jumper = Boolean.parseBoolean(line[4]);
                        map.addEntity(new BasicEnemy(x, y, map, runner, jumper));
                        break;
                    }
                    case "hookable" -> {
                        int radius = Integer.parseInt(line[3]);


                        map.addEntity(new Hookable(x, y, map, radius));
                        break;
                    }
                    case "flag" -> {

                        map.addEntity(new Flag(x, y, map));
                        break;
                    }
                    case "dimension" -> {
                        String dimension = line[3];
                        map.addEntity(new DimensionPortal(x, y, map, dimension));
                        break;
                    }
                    case "corner" -> {
                        int wallWidth = Integer.parseInt(line[3]);
                        int wallHeight = Integer.parseInt(line[4]);
                        double rotation = Double.parseDouble(line[5]);
                        map.addCornerWall(x, y, wallWidth, wallHeight, 1, rotation);
                        break;
                    }
                    case "trophy" -> {
                        String trophyName = line[3];

                        map.addEntity(new Trophy(x, y, map, trophyName));
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


            if (type == 1) {
                map.addEntity(new Player(playerX, playerY, map));
            }
            map.setStartEntities();
            return map;

        } else {
            System.out.println("no map found: " +"res\\maps\\" + mapName + ".txt");
            return null;
        }
    }





    public static void saveMap(Map map, boolean keepName) {

        File file = new File ("res\\maps\\custom\\" + ((keepName) ? map.getName() :(new File("res\\maps\\custom\\").listFiles().length+1)) + ".txt");
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
