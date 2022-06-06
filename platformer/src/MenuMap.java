import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.paint.Color;

public class MenuMap extends MenuButton {

    private Map map;

    private String mapName;


    public MenuMap(String mapName) {
        super(0,0,100,100, mapName, TextType.normal);
        this.mapName = mapName;

    }

    public void loadMap() {
        map = MapLoader.loadMap(mapName, 0);
    }



    public void tick() {
        map.tick();

    }





    @Override
    public void render(GraphicsContext g) {


        map.render(g);
       // g.setFill(Color.color(0,0,0,0.5));
       // g.fillRect(0,0,g.getCanvas().getWidth(),g.getCanvas().getHeight());
        g.save();
        g.setGlobalAlpha(0.4);
        g.drawImage(ImageLoader.sky1, 0,0,g.getCanvas().getWidth(),g.getCanvas().getHeight());
        g.restore();

    }
}
