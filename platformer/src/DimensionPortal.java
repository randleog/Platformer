import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class DimensionPortal extends GameEntity {

    private String mapName;

    public DimensionPortal(double x, double y, Map map, String mapName) {
        super(x,y,map,InputAction.Default, FillType.Image, 1);
        this.sizeX = 75;
        this.sizeY = 75;
        this.mapName = mapName;
        this.color = Color.color(1,0.5,0);
        this.image = ImageLoader.dimension;

    }


    public void tick() {

        if (this.intersect(map.player)) {
            map.removeEntity(this);
            if (Main.lastMap == null) {

                Main.playDimension(MapLoader.loadMap(mapName, true));
            } else {

                Main.playDimension(Main.lastMap);
            }


        }
    }

    public void render(GraphicsContext g) {
        renderSquare(g);
    }
}
