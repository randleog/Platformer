package Map;


import javafx.scene.image.Image;

import java.util.ArrayList;

public class Gear extends BackgroundObject{

    double gearFactor;


    ArrayList<Gear> neibhbours;


    public Gear(double x, double y, double sizeX, double sizeY, Map map, Image image, double parallax) {
        super(x, y, sizeX, sizeY, map, image, parallax);

        neibhbours = new ArrayList<>();
    }


    @Override
    public void tick() {

    }


    public void scan() {
        //use map to scan map for neighboring gears and add them to the collection, this is then progressively worked through, using their direction of motion to factor in. if even, 0.
    }
}
