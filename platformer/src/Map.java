import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.ArrayList;

public class Map {

    private ArrayList<GameEntity> entities = new ArrayList<>();
    private ArrayList<GameEntity> nextEntities = new ArrayList<>();

    private ArrayList<GameEntity> particles = new ArrayList<>();
    private ArrayList<GameEntity> nextParticles = new ArrayList<>();

    public static final double GRAVITY = 15;

    public static final double AIR_DRAG= 0.007;
    public static final double GROUND_DRAG= 0.05;

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
        return input/Main.gameUnit;
    }

    public double correctUnitX(double input) {
        return correctUnit(input)-correctUnit(cameraX);
    }

    public double correctUnitY(double input) {
        return correctUnit(input)-correctUnit(cameraY);
    }

    public void addEntity(GameEntity entity) {
        entities.add(entity);
    }

    public void addEntityLive(GameEntity entity) {
        nextEntities.add(entity);
    }


    public void addParticleLive(GameEntity entity) {
        nextParticles.add(entity);
    }

    public void tick() {
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


    public void render(GraphicsContext g) {
        for (GameEntity entity : particles) {
            entity.render(g);
        }
        for (GameEntity entity : entities) {
            entity.render(g);
        }


    }



    public InputAction isIntersect( GameEntity entity) {

        InputAction actualAction = InputAction.Default;
        for (GameEntity currentEntity : entities) {
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

        return  actualAction;
    }




    public void crashParticle(double x, double y) {
        for (int i = 0; i < CRASH_PARTICLE_COUNT; i++) {
            Particle particle = new Particle(x,y,this,10,10, ImageLoader.particle, true, 1, 1);
            particle.setVelX(Main.random.nextDouble(Particle.PARTICLE_SPEED)-Particle.PARTICLE_SPEED/2);
            particle.setVelV(Main.random.nextDouble(Particle.PARTICLE_SPEED)-Particle.PARTICLE_SPEED);

            addParticleLive(particle);
        }

    }


    public void addWall(int x, int y, int sizeX, int sizeY, double parallax) {
        Wall wallUp = new Wall(x+10,y, this, sizeX-20, 1, InputAction.Up, parallax);

        Wall wallRight = new Wall(x+sizeX,y+10, this, 1, sizeY-20, InputAction.Right, parallax);
        Wall wallLeft = new Wall(x,y+10, this, 1, sizeY-20, InputAction.Left,parallax);
        Wall wallDown = new Wall(x+10,y+sizeY-1, this, sizeX-20, 1, InputAction.Down, parallax );
        Wall wallImage = new Wall(x,y, this, sizeX, sizeY, InputAction.Default, parallax );

        addEntity(wallUp);
        addEntity(wallRight);
        addEntity(wallLeft);

        addEntity(wallDown);

        addEntity(wallImage);
    }

    public GameEntity intersectAction(GameEntity entity) {
        GameEntity returnEntity = entity;
        for (GameEntity currentEntity : entities) {
            if (currentEntity.getParallax() == 1) {
            if (!entity.equals(currentEntity)) {
                if (currentEntity.intersect(entity)) {
                    if (!InputAction.isXType(currentEntity.getAction())) {
                        return currentEntity;
                    } else {
                        returnEntity = currentEntity;
                    }
                }
            }


            }
        }

        return returnEntity;
    }



}
