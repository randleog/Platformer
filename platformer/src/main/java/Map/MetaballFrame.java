package Map;

import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;


public class MetaballFrame extends GameEntity {


    private static final int PARTICLE_RESOLUTION = 2;

    private ArrayList<ControlledParticle> particles = new ArrayList<>();




    public MetaballFrame(double x, double y, Map map, double sizeX, double sizeY, double parallax) {
        super(x, y, map, InputAction.Swim, FillType.Color, parallax);


    }


    @Override
    public void tick() {

    }

    public void render(GraphicsContext g) {



    }





}
