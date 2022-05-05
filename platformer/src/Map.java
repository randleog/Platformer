import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.ArrayList;

public class Map {

    private ArrayList<GameEntity> entities = new ArrayList<>();
    private ArrayList<GameEntity> nextEntities = new ArrayList<>();

    private ArrayList<GameEntity> particles = new ArrayList<>();
    private ArrayList<GameEntity> nextParticles = new ArrayList<>();

    public static final double GRAVITY = 15;

    private static final double WALL_CORNER_SIZE = 10.0;

    public static final double AIR_DRAG = 0.4;
    public static final double GROUND_DRAG = 0.0001;

    private static final int CRASH_PARTICLE_COUNT = 10;


    public static final double BASE_DRAG_Y = 0.5;


    protected double cameraX = 0;
    protected double cameraY = 0;

    protected boolean cameraMap;


    public Map(boolean cameraMap) {
        this.cameraMap = cameraMap;
    }

    public void removeEntity(GameEntity entity) {
        entity.setFlagRemoval();
        removeFlagged();
    }


    public Hookable getHookable() {
        for (GameEntity entity : entities) {
            if (entity instanceof Hookable) {
                return (Hookable) entity;
            }
        }
        return null;
    }

    public void removeParticle(GameEntity entity) {
        entity.setFlagRemoval();
        removeFlagged();
    }

    private void removeFlagged() {
        ArrayList<GameEntity> bufferEntities = new ArrayList<>();
        bufferEntities.addAll(entities);
        entities = new ArrayList<>();
        for (GameEntity entity : bufferEntities) {
            if (!entity.isFlagRemoval()) {
                entities.add(entity);
            }
        }


        bufferEntities = new ArrayList<>();
        bufferEntities.addAll(particles);
        particles = new ArrayList<>();
        for (GameEntity entity : bufferEntities) {
            if (!entity.isFlagRemoval()) {
                particles.add(entity);
            }
        }

    }

    public double correctUnit(double input) {
        return input / Main.gameUnit;
    }

    public double correctUnitX(double input) {
        return correctUnit(input) - correctUnit(cameraX);
    }

    public double correctUnitY(double input) {
        return correctUnit(input) - correctUnit(cameraY);
    }

    public void addEntity(GameEntity entity) {
        entities.add(entity);
    }

    public void addParticle(GameEntity entity) {
        particles.add(entity);
    }

    public void addEntityLive(GameEntity entity) {
        nextEntities.add(entity);
    }


    public void addParticleLive(GameEntity entity) {
        nextParticles.add(entity);
    }

    public void tick() {
        if (Main.getKey(InputAction.Menu) > 0) {
            for (GameEntity entity : entities) {
                entity.tick();
            }

            for (GameEntity entity : particles) {
                entity.tick();
            }

            entities.addAll(nextEntities);
            nextEntities = new ArrayList<>();

            particles.addAll(nextParticles);
            nextParticles = new ArrayList<>();

        }
    }


    public void render(GraphicsContext g) {
        for (GameEntity entity : particles) {
            entity.render(g);
        }
        for (GameEntity entity : entities) {
            entity.render(g);
        }

        if (!(Main.getKey(InputAction.Menu) > 0)) {
            g.setFill(Color.color(0,0,0,0.4));
            g.fillRect(0,0,g.getCanvas().getWidth(),g.getCanvas().getHeight());

            g.setFill(Color.color(1,1,1));
            g.setFont(new Font(40));
            g.fillText("Paused", 100, 200);
            g.setFont(new Font(25));
            String currentAction = "";
            currentAction = currentAction + getStringKey(InputAction.Up);
            currentAction = currentAction + getStringKey(InputAction.Sprint);
            currentAction = currentAction + getStringKey(InputAction.Hook);
            currentAction = currentAction + getStringKey(InputAction.Left);
            currentAction = currentAction + getStringKey(InputAction.Right);
            currentAction = currentAction + getStringKey(InputAction.Down);
            g.fillText(currentAction, 100, 400);
        }


    }

    public ArrayList<GameEntity> getEntities() {
        return entities;
    }



    private String getStringKey(InputAction action) {
        String currentAction = "";
        int current = 0;
        ArrayList<KeyCode> codes = Main.getDisplayOptions(action);

        for (KeyCode code : codes) {
            currentAction = currentAction + code.getName();
            if (current < codes.size()-1) {
                currentAction = currentAction + " | ";
            }
            current++;
        }
        currentAction = currentAction+" to: " + action + "\n";

        return currentAction;
    }


    public InputAction isIntersect(GameEntity entity) {

        InputAction actualAction = InputAction.Default;
        for (GameEntity currentEntity : entities) {
            if (currentEntity.getParallax() == entity.getParallax()) {
                if (!entity.equals(currentEntity)) {
                    if (currentEntity.intersect(entity)) {
                        if (InputAction.isYType(currentEntity.getAction())) {
                            return currentEntity.getAction();
                        } else {
                            if (InputAction.isXType(currentEntity.getAction())) {
                                actualAction = currentEntity.getAction();
                            }
                        }
                    }
                }


            }
        }

        return actualAction;
    }


    public void crashParticle(double x, double y) {
        for (int i = 0; i < CRASH_PARTICLE_COUNT; i++) {
            Particle particle = new Particle(x, y, this, 10, 10, ImageLoader.particle, true, 1, 1);
            particle.setVelX(Main.random.nextDouble(Particle.PARTICLE_SPEED) - Particle.PARTICLE_SPEED / 2);
            particle.setVelY(Main.random.nextDouble(Particle.PARTICLE_SPEED) - Particle.PARTICLE_SPEED);

            addParticleLive(particle);
        }

    }

    public void addWallCloseDown(int x, int y, int sizeX, int sizeY, double parallax) {

        addStandardWallSegments(x,y,sizeX,sizeY,parallax);

        //downward wall
        Wall wallDownLeft = new Wall(x + WALL_CORNER_SIZE, y + sizeY - 1
                , this, sizeX/2.0 - WALL_CORNER_SIZE * 2, 1, InputAction.Left, FillType.Nothing, parallax);
        //downward wall
        Wall wallDownRight = new Wall(x + WALL_CORNER_SIZE+sizeX/2.0, y + sizeY - 1
                , this, sizeX/2.0 - WALL_CORNER_SIZE * 2, 1, InputAction.Right, FillType.Nothing, parallax);


        addEntity(wallDownLeft);
        addEntity(wallDownRight);

    }

    private void addStandardWallSegments(int x, int y, int sizeX, int sizeY, double parallax) {
        Wall wallUp = new Wall(x + WALL_CORNER_SIZE, y, this, sizeX - WALL_CORNER_SIZE * 2, 1, InputAction.Up, FillType.Nothing, parallax);
        Wall wallRight = new Wall(x + sizeX, y + WALL_CORNER_SIZE, this, 1, sizeY - WALL_CORNER_SIZE * 2, InputAction.Right, FillType.Nothing, parallax);
        Wall wallLeft = new Wall(x, y + WALL_CORNER_SIZE, this, 1, sizeY - WALL_CORNER_SIZE * 2, InputAction.Left, FillType.Nothing, parallax);
        Wall wallImage = new Wall(x, y, this, sizeX, sizeY, InputAction.Default, FillType.Tile, parallax);

        addEntity(wallUp);
        addEntity(wallRight);
        addEntity(wallLeft);
        addEntity(wallImage);
    }


    public void addWall(int x, int y, int sizeX, int sizeY, double parallax) {

        addStandardWallSegments(x,y,sizeX,sizeY,parallax);
        Wall wallDown = new Wall(x + WALL_CORNER_SIZE, y + sizeY - 1, this, sizeX - WALL_CORNER_SIZE * 2, 1, InputAction.Down, FillType.Nothing, parallax);


        addEntity(wallDown);


    }

    public GameEntity intersectAction(GameEntity entity) {
        GameEntity returnEntity = entity;
        for (GameEntity currentEntity : entities) {
            if (currentEntity.getParallax() == entity.getParallax()) {
                if (!entity.equals(currentEntity)) {
                    if (currentEntity.intersect(entity)) {
                        if (InputAction.isYType(currentEntity.getAction())) {
                            return currentEntity;
                        } else {
                            if (InputAction.isXType(currentEntity.getAction())) {
                                returnEntity = currentEntity;
                            }
                        }
                    }
                }


            }
        }

        return returnEntity;
    }


}
