

public class LevelButton extends MenuButton {


    private String name;

    public LevelButton(int x, int y, int width, int height, String name) {
        super(x,y,width,height, "level: " + name);
        this.name = name;
        double time = UserFileHandler.getUserTime(name, 1);
        if (time == -1) {
            text = text+"\nbest time: N/A";
        } else {
            text = text+"\nbest time: " + String.format("%.2f",time);
        }

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
