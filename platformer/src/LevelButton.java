

public class LevelButton extends MenuButton {


    private String name;

    public LevelButton(int x, int y, int width, int height, String name) {
        super(x,y,width,height, "level: " + name);
        this.name = name;
    }



    public void tick() {

        updateMouse();

        click();

    }



    private void click() {
        if (Main.mouseDown && mouseOver) {
            Main.mouseDown = false;
            Main.playMap(MapLoader.loadMap(name));
        }
    }
}
