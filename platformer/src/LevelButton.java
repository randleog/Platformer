

public class LevelButton extends MenuButton {

    private static final int TICK_REFRESH = 200;


    private String name;

    private int currentTick = 0;

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

    public void refreshTime() {

        double time = UserFileHandler.getUserTime(name, 1);
        if (time == -1) {
            text = "level: " + name+"\nbest time: N/A";
        } else {
            text = "level: " + name+"\nbest time: " + String.format("%.2f",time);
        }
    }



    public void tick() {

        currentTick++;
        if (currentTick == TICK_REFRESH) {
            refreshTime();
        }
        updateMouse();

        click();

    }



    private void click() {
        if (Main.mouseDown && mouseOver) {
            Main.mouseDown = false;
            Main.playMap(MapLoader.loadMap(name, true));
        }
    }
}
