

public class ReplayButton extends MenuElement {

    private static final int TICK_REFRESH = 200;


    private String name;

    private int currentTick = 0;

    public ReplayButton(int x, int y, int width, int height, String name) {
        super(x,y,width,height, "level: " + name, TextType.normal);
        this.name = name;
        double time = UserFileHandler.getTime(name, 1);
        if (time == -1) {
            text = text+"\ntime: N/A";
        } else {
            text = text+"\ntime: " + String.format("%.2f",time);
        }

    }

    public void refreshTime() {

        double time = UserFileHandler.getTime(name, 1);
        if (time == -1) {
            text = "level: " + name+"\ntime: N/A";
        } else {
            text = "level: " + name+"\ntime: " + String.format("%.2f",time);
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
            Main.playMap(MapLoader.loadMap(name, 2));
        }
    }
}
