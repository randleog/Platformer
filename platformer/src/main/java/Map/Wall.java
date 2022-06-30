package Map;

import GameControl.Square;
import Main.Main;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import Util.ImageLoader;
import javafx.scene.paint.ImagePattern;

import java.util.ArrayList;

public class Wall extends GameEntity {



    private ArrayList<int[]> centers = new ArrayList<>();

    private String type = "wall";



    public Wall(double x, double y, Map map, double sizeX, double sizeY, InputAction side, FillType fillType, double parallax) {
        super(x,y,map,side, fillType, parallax);
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.color = Color.color(1,0.5,0);
        this.image = ImageLoader.wallTile;




        loadHitbox();

        loadCenters();


        if (!(this.map == null) && this.sizeY == this.sizeX) {
            scan();
        }



    }

    public void reset() {

        loadHitbox();

        loadCenters();
    }


    @Override
    protected void loadHitbox() {
        hitbox = new ArrayList<>();
        hitbox.add(new Square(x + WALL_CORNER_SIZE, y, sizeX - WALL_CORNER_SIZE * 2, 1, parallax, InputAction.Up));
        hitbox.add(new Square(x + WALL_CORNER_SIZE, y + sizeY - 1, sizeX - WALL_CORNER_SIZE * 2, 1, parallax, InputAction.Down));
        if (sizeY > 25) {
            hitbox.add(new Square(x + sizeX - 1, y + WALL_CORNER_SIZE, 1, sizeY - WALL_CORNER_SIZE * 2, parallax, InputAction.Right));
            hitbox.add(new Square(x, y + WALL_CORNER_SIZE, 1, sizeY - WALL_CORNER_SIZE * 2, parallax, InputAction.Left));
        }



    }
    /**
     * removes hitboxes not needed
     */
    private void scan() {

        y-=10;
        if (map.getActions(this).contains(InputAction.Down)) {
            Square removeSquare = null;
            for (Square square : hitbox) {
                if (square.getAction() == InputAction.Up) {
                    removeSquare = square;
                }
            }

            hitbox.remove(removeSquare);

        }

        y+=10;

    }


    public void setType(String type){
        this.type = type;
        centers = new ArrayList<>();
    }




    @Override
    public boolean isWall() {
        return true;
    }

    private void loadCenters() {

        centers = new ArrayList<>();


        if (!type.equals("sand")) {
            return;
        }

        int y = (int)(sizeY/150);
        int x = (int)(sizeX/150);

        for (int i = 0; i < x; i++ ) {
            for (int j = 0; j < y; j++) {
                centers.add(new int[]{i,j});
            }
        }





    }


    public void tick() {


    }

    public void render(GraphicsContext g) {
        double x = getRenderX();
        double y = getRenderY();
        double sizeX = getRenderSizeX();
        double sizeY = getRenderSizeY();

        g.setFill(new ImagePattern(image, x, y, map.correctUnit(tileSize) * parallax, map.correctUnit(tileSize) * parallax, false));

        ImagePattern sideBricksRight = new ImagePattern(ImageLoader.wallTileRight, x, y, map.correctUnit(tileSize) * parallax, map.correctUnit(tileSize) * parallax, false);
        ImagePattern sideBricksLeft = new ImagePattern(ImageLoader.wallTileLeft, x, y, map.correctUnit(tileSize) * parallax, map.correctUnit(tileSize) * parallax, false);
        if (x < 0) {
            sizeX = sizeX+x;
            x = 0;
        }
        if (x + sizeX > Main.correctUnit(Main.DEFAULT_WIDTH_MAP)) {
            sizeX = Main.correctUnit(Main.DEFAULT_WIDTH_MAP)-x;
        }

        g.fillRect(x, y, (int)sizeX+1, (int)sizeY+1);

/*
        if (type.equals("wall")) {
            g.setFill(sideBricksRight);
            g.fillRect(x - Main.correctUnit(tileSize), y, Main.correctUnit(tileSize), (int) sizeY + 1);

            g.setFill(sideBricksLeft);
            g.fillRect(x + sizeX, y, Main.correctUnit(tileSize), (int) sizeY + 1);
        }

 */



        boolean hasDrawn = false;

        int startX = 0;
        int startY = 0;
        for (int[] center : centers) {
            if (!hasDrawn) {
                startY = center[1];
                startX = center[0];
            }
            hasDrawn = true;



            if (!(center[0] == startX)) {
                g.drawImage(ImageLoader.wallTile3, getRenderX() + map.correctUnit((center[0] - 0.3333) * 150) + map.correctUnit(50)
                        , getRenderY() + map.correctUnit(center[1] * 150) + map.correctUnit(50), map.correctUnit(50), map.correctUnit(50));

                g.drawImage(ImageLoader.wallTile3, getRenderX() + map.correctUnit((center[0] - 0.66666) * 150) + map.correctUnit(50)
                        , getRenderY() + map.correctUnit(center[1] * 150) + map.correctUnit(50), map.correctUnit(50), map.correctUnit(50));


            }
            if (!(center[1] == startY)) {
                g.drawImage(ImageLoader.wallTile4, getRenderX()+map.correctUnit((center[0])*150)+map.correctUnit(50)
                        ,  getRenderY()+map.correctUnit((center[1]-0.33333)*150)+map.correctUnit(50), map.correctUnit(50), map.correctUnit(50));
                g.drawImage(ImageLoader.wallTile4, getRenderX()+map.correctUnit((center[0])*150)+map.correctUnit(50)
                        ,  getRenderY()+map.correctUnit((center[1]+0.33333)*150)+map.correctUnit(50), map.correctUnit(50), map.correctUnit(50));



            }

            g.drawImage(ImageLoader.wallTile2, getRenderX()+map.correctUnit(center[0]*150)+map.correctUnit(50)
                    ,  getRenderY()+map.correctUnit(center[1]*150)+map.correctUnit(50), map.correctUnit(50), map.correctUnit(50));
        }

        for (Square shape : hitbox) {
            shape.render(g, map.cameraX, map.cameraY, (Player) map.player);
        }


    }



    public String toString() {
        String line ="wall " +  type + " " + (int)x + " " + (int)y + " " + (int)sizeX + " " + (int)sizeY;

        return line;
    }
}
