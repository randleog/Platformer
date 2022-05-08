import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Hookable extends GameEntity {

    private double hookRadius;
    private static final double HOOK_FACTOR = 8;

    public Hookable(double x, double y, Map map, double hookRadius) {
        super(x, y, map, InputAction.Default, FillType.Nothing, 1);
        this.hookRadius = hookRadius;
        this.sizeX = 35;
        this.sizeY = 35;
        this.color = Color.color(0.65, 0, 0.7, 0.7);
        this.image = ImageLoader.wallTile;
    }


    public void tick() {

        if (Main.isKeyDown(InputAction.Hook)) {
            for (GameEntity entity : map.getEntities()) {
                if (entity instanceof Player) {
                    Player player = (Player) entity;

                    if (player.isHooking()) {
                        if (Math.sqrt(Math.pow(Math.abs(entity.getX() - getX()), 2) + Math.pow(Math.abs(entity.getY() - getY()), 2)) < hookRadius) {
                            hook(player);
                        }
                    }
                }
            }
        }

    }

    private void hook(Player player) {


        player.setVelY(player.getVelY() + ((this.y - player.getY()))/Main.fps / HOOK_FACTOR);
        player.setVelX(player.getVelX() + ((this.x - player.getX()))/Main.fps / HOOK_FACTOR);


    }

    public void render(GraphicsContext g) {
        boolean canShow = false;


        if (map.isRadius(this.x, this.y, map.playerX, map.playerY, hookRadius)) {
            g.setFill(color);
            g.fillOval(getRenderX(), getRenderY(), map.correctUnit(getSizeX()), map.correctUnit(getSizeY()));

            if (Main.isKeyDown(InputAction.Hook)) {
                g.setStroke(Color.color(0.5, 0.5, 0.5));
                double x = getRenderX();
                double y = getRenderY();
                double hookX = map.player.getRenderX();
                double hookY = map.player.getRenderY();
                g.setLineWidth(4);

                g.strokeLine(x, y, hookX, hookY);
            }

        }


        //   renderSquare(g);
    }
}
