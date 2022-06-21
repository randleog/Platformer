package Menu;

import Map.Map;
import Menu.MenuElement;
import Util.ImageLoader;
import Util.MapLoader;
import javafx.scene.canvas.GraphicsContext;
import Main.Main;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.paint.Color;

public class MenuMap extends MenuElement {

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
   //     map.moveCamera(-1,0);

    }





    @Override
    public void render(GraphicsContext g) {

        map.render(g);
       // g.setFill(Color.color(0,0,0,0.5));
       // g.fillRect(0,0,g.getCanvas().getWidth(),g.getCanvas().getHeight());
        g.setFill(Color.color(0,0,0,0.4));
         g.fillRect(0,0,g.getCanvas().getWidth(),g.getCanvas().getHeight());

    }
}
