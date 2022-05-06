import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class MapLoader {


    /**
     *
     * @param mapName
     * @param type 0 = empty, 1 = with player, 2 = replay player
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
                map = new Map(width, height, mapName, true);
            } else {
                map = new Map(width, height, mapName, false);
            }


            Scanner background = new Scanner(text[1]);
          //  System.out.println("wtf");
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
                System.out.println(name);
                if (name.equals("wall")) {
                    int x = Integer.parseInt(line[1]);
                    int y = Integer.parseInt(line[2]);
                    int wallWidth = Integer.parseInt(line[3]);
                    int wallHeight = Integer.parseInt(line[4]);
                    map.addWall(x, y, wallWidth, wallHeight, 1);
                } else if (name.equals("gate")) {
                    int x = Integer.parseInt(line[1]);
                    int y = Integer.parseInt(line[2]);
                    int wallWidth = Integer.parseInt(line[3]);
                    int wallHeight = Integer.parseInt(line[4]);
                    int code = Integer.parseInt(line[5]);
                    map.addGate(x, y, wallWidth, wallHeight, 1, code);
                } else if (name.equals("key")) {
                    int x = Integer.parseInt(line[1]);
                    int y = Integer.parseInt(line[2]);
                    int code = Integer.parseInt(line[3]);
                    map.addEntity(new Key(x, y, map, 1, code));
                } else if (name.equals("moving")) {
                    int x = Integer.parseInt(line[1]);
                    int y = Integer.parseInt(line[2]);
                    int wallWidth = Integer.parseInt(line[3]);
                    int wallHeight = Integer.parseInt(line[4]);
                    int velx = Integer.parseInt(line[5]);
                    int vely = Integer.parseInt(line[6]);

                    map.addMovingWall(x, y, wallWidth, wallHeight, vely, velx);
                } else if (name.equals("basicEnemy")) {
                    int x = Integer.parseInt(line[1]);
                    int y = Integer.parseInt(line[2]);
                    boolean runner = Boolean.parseBoolean(line[3]);
                    boolean jumper = Boolean.parseBoolean(line[4]);
                    map.addEntity(new BasicEnemy(x, y, map, runner, jumper));
                } else if (name.equals("hookable")) {
                    int x = Integer.parseInt(line[1]);
                    int y = Integer.parseInt(line[2]);
                    int radius = Integer.parseInt(line[3]);


                    map.addEntity(new Hookable(x, y, map, radius));
                }else if (name.equals("flag")) {
                    int x = Integer.parseInt(line[1]);
                    int y = Integer.parseInt(line[2]);

                    map.addEntity(new Flag(x, y, map));
                }else if (name.equals("dimension")) {
                    int x = Integer.parseInt(line[1]);
                    int y = Integer.parseInt(line[2]);
                    String dimension = line[3];
                    map.addEntity(new DimensionPortal(x, y, map, dimension));
                }


            }

            entities.close();

            if ((new File("res\\replays\\" + mapName + ".txt").exists())) {
                boolean isReplay = type == 2;
                map.addEntity(new ReplayPlayer(playerX, playerY, map, ReplaySave.getFrames(mapName), isReplay));
            }

            if (type ==1) {
                map.addEntity(new Player(playerX, playerY, map));
            }
            map.setStartEntities();
            return map;

        } else {
            System.out.println("no map found");
            return null;
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
