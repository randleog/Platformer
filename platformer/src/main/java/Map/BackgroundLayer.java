package Map;

import Main.Main;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;

public class BackgroundLayer {

    private double parallax;
    private Image image;


public BackgroundLayer(double parallax, Image image) {
    this.parallax = parallax;
    this.image = image;
}


public void render(GraphicsContext g, double x) {
    g.setFill(new ImagePattern(image, -Main.correctUnit(x)*parallax, 0, Main.correctUnit(Main.DEFAULT_WIDTH_MAP), Main.correctUnit(Main.DEFAULT_HEIGHT_MAP), false));
    g.fillRect(0,0, Main.correctUnit(Main.DEFAULT_WIDTH_MAP), Main.correctUnit(Main.DEFAULT_HEIGHT_MAP));
}

}
