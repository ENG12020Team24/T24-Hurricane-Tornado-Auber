package com.mygdx.auber.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.auber.Auber;
import com.mygdx.auber.Pathfinding.GraphCreator;
import com.mygdx.auber.Pathfinding.MapGraph;
import com.mygdx.auber.Powerups.PowerUp;
import com.mygdx.auber.Powerups.SpeedUp;
import com.mygdx.auber.Scenes.Hud;
import com.mygdx.auber.ScrollingBackground;
import com.mygdx.auber.entities.*;

import java.util.ArrayList;

public class PlayScreen implements Screen {
    private final Auber game;
    private final Viewport viewport;
    private final Hud hud;
    private final TmxMapLoader mapLoader;
    public final TiledMap map;
    private final OrthogonalTiledMapRenderer renderer;
    private final GraphCreator graphCreator;
    private final ScrollingBackground scrollingBackground;
    private final KeySystemManager keySystemManager;
    private Prisoners prisoners;
    private final ShapeRenderer shapeRenderer;
    public static OrthographicCamera camera;
    public Player player;

    public static final int numberOfInfiltrators = 8;
    public static int numberOfCrew;
    public static int maxIncorrectArrests;

    private static boolean demo;
    private int difficulty;

    ArrayList<PowerUp> powerUps;
    ArrayList<PowerUp> powerUpsToRemove;

    public PlayScreen(Auber game, boolean demo, int difficulty) {
        this.game = game;
        this.demo = demo;
        this.difficulty = difficulty;
        this.numberOfCrew = 40 * (difficulty + 1);
        this.maxIncorrectArrests = 3 * (3 - difficulty);

        camera = new OrthographicCamera();
        viewport = new ExtendViewport(Auber.VirtualWidth, Auber.VirtualHeight, camera);
        hud = new Hud(game.batch);
        shapeRenderer = new ShapeRenderer();
        scrollingBackground = new ScrollingBackground(); // Creating a new camera, viewport, hud and scrolling
                                                         // background, setting the viewport to camera and virtual
                                                         // height/width

        mapLoader = new TmxMapLoader();

        map = mapLoader.load("AuberMap.tmx"); // Creates a new map loader and loads the map into map

        Infiltrator.createInfiltratorSprites();
        CrewMembers.createCrewSprites(); // Generates the infiltrator and crewmember sprites

        graphCreator = new GraphCreator((TiledMapTileLayer) map.getLayers().get("Tile Layer 1")); // Generates all the
                                                                                                  // nodes and paths for
                                                                                                  // the given map layer
        keySystemManager = new KeySystemManager((TiledMapTileLayer) map.getLayers().get("Systems")); // Generates key
                                                                                                     // systems
        prisoners = new Prisoners((TiledMapTileLayer) map.getLayers().get("OutsideWalls+Lining"));

        powerUps = new ArrayList<PowerUp>();
        powerUpsToRemove = new ArrayList<PowerUp>();

        powerUps.add(new SpeedUp(new Vector2(1700,2800)));

        for (int i = 0; i < numberOfInfiltrators; i++) {
            // System.out.println("Infiltrator created!");
            if (i == numberOfInfiltrators - 1) {
                NPCCreator.createInfiltrator(Infiltrator.hardSprites.random(), MapGraph.getRandomNode(),
                        graphCreator.mapGraph);
                break;
            }
            NPCCreator.createInfiltrator(Infiltrator.easySprites.random(), MapGraph.getRandomNode(),
                    graphCreator.mapGraph);
        } // Creates numberOfInfiltrators infiltrators, gives them a random hard or easy
          // sprite

        if (demo) {
            NPCCreator.createCrew(new Sprite(new Texture("AuberStand.png")), MapGraph.getRandomNode(),
                    graphCreator.mapGraph);
        }

        for (int i = 0; i < numberOfCrew; i++) {
            NPCCreator.createCrew(CrewMembers.selectSprite(), MapGraph.getRandomNode(), graphCreator.mapGraph);
        } // Creates numberOfCrew crewmembers, gives them a random sprite

        Array<TiledMapTileLayer> playerCollisionLayers = new Array<>();
        playerCollisionLayers.add((TiledMapTileLayer) map.getLayers().get("Tile Layer 1"));
        playerCollisionLayers.add((TiledMapTileLayer) map.getLayers().get(2)); // The layers on which the player will
                                                                               // collide

        player = new Player(new Sprite(new Texture("AuberStand.png")), playerCollisionLayers, demo);
        player.setPosition(1700, 3000); // Creates a player and sets him to the given position
        player.findHealers((TiledMapTileLayer) map.getLayers().get("Systems")); // Finds infirmary
        player.teleporters = player.getTeleporterLocations((TiledMapTileLayer) map.getLayers().get("Systems")); // Finds
                                                                                                                // the
                                                                                                                // teleporters

        renderer = new OrthogonalTiledMapRenderer(map); // Creates a new renderer with the given map

        camera.position.set(player.getX(), player.getY(), 0); // Sets the camera position to the player

        Gdx.input.setInputProcessor(player); // Sets the input to be handled by the player class
    }

    @Override
    public void show() {

    }

    /**
     * If any of the game over conditions are true, returns true
     * 
     * @return Boolean if the game is over or not
     */
    public boolean gameOver() {
        return Player.health <= 0 || Hud.CrewmateCount >= maxIncorrectArrests
                || KeySystemManager.destroyedKeySystemsCount() >= 15;
    }

    /**
     * If any of the win conditions are true, returns true
     * 
     * @return Boolean If the game is won or not
     */
    public boolean gameWin() {
        return NPCCreator.infiltrators.isEmpty();
    }

    /**
     * Called every frame, call update methods in here
     * 
     * @param time Time between last frame and this frame
     */
    public void update(float time) {
        NPC.updateNPC(time);
        player.update(time);
        hud.update();
        camera.update(); // Updating everything that needs to be updated

        // debugText();

        renderer.setView(camera); // Needed for some reason

        if (gameOver()) {
            System.out.println("Win");
            game.setScreen(new GameOverScreen(game, false));
            return;
        } // If game over, show game over screen and dispose of all assets
        if (gameWin()) {
            System.out.println("Lose");
            game.setScreen(new GameOverScreen(game, true));
            return;
        } // If game won, show game win screen and dispose of all assets
    }

    /**
     * Called every frame, call render methods in here
     * 
     * @param delta Time between last frame and this frame
     */
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.09f, 0.09f, 0.09f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);// Clears the screen and sets it to the colour light blue or whatever
                                                 // colour it is

        if (!demo) {
            camera.position.set(player.getX() + player.getWidth() / 2, player.getY() + player.getHeight() / 2, 0); // Sets
                                                                                                                   // camera
                                                                                                                   // to
                                                                                                                   // centre
                                                                                                                   // of
                                                                                                                   // player
                                                                                                                   // position
        } else {
            CrewMembers crew = NPCCreator.crew.get(0);
            camera.position.set(crew.getX() + crew.getWidth() / 2, crew.getY() + crew.getHeight() / 2, 0);
        }

        game.batch.setProjectionMatrix(camera.combined); // Ensures everything is rendered properly, only renders things
                                                         // in viewport
        shapeRenderer.setProjectionMatrix(camera.combined); // Ensures the shape renderer renders thing properly

        renderer.getBatch().begin(); // Start the sprite batch
        /* Render sprites/textures below this line */

        scrollingBackground.updateRender(delta, (SpriteBatch) renderer.getBatch());// Renders the background
        renderer.renderTileLayer((TiledMapTileLayer) map.getLayers().get(0)); // Renders the bottom layer of the map
        Prisoners.render(renderer.getBatch());
        renderer.renderTileLayer((TiledMapTileLayer) map.getLayers().get(1));
        renderer.renderTileLayer((TiledMapTileLayer) map.getLayers().get(2));
        renderer.renderTileLayer((TiledMapTileLayer) map.getLayers().get(3));

        NPC.render(renderer.getBatch()); // Renders all NPCs
        if (!demo) {
            player.draw(renderer.getBatch()); // Renders the player
            player.drawArrow(renderer.getBatch()); // Renders arrows towards key systems
        }

        update(delta); // Updates the game camera and NPCs
        hud.stage.draw(); // Draws the HUD on the game

        /* Render sprites/textures above this line */
        renderer.getBatch().end(); // Finishes the sprite batch
        /* Render shapes below this line */

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA); // Allows for alpha changes in shapes

        if (!demo) {
            player.drawCircle(shapeRenderer);
        }
        // graphCreator.shapeRenderer.setProjectionMatrix(camera.combined); //Ensures
        // shapes are rendered properly
        // graphCreator.render(); //Debugging shows nodes and paths

        /* Render shapes above this line */
        Gdx.gl.glDisable(GL20.GL_BLEND);

        for (PowerUp pu : powerUps){
            pu.render(shapeRenderer);
            pu.update(player);
            if (pu.used){
                powerUpsToRemove.add(pu);
            }
        }
        for (PowerUp pu : powerUpsToRemove){
            powerUps.remove(pu);
        }

    }

    /**
     * Called upon window being resized, and at the beginning
     * 
     * @param width  Width of the window
     * @param height Height of the window
     */
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        camera.viewportWidth = width / 2f;
        camera.viewportHeight = height / 2f;
        camera.update();
        scrollingBackground.resize(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        graphCreator.dispose();
        NPC.disposeNPC();
        KeySystemManager.dispose();
        player.dispose();
    }

    /**
     * Called when the screen is closed, need to call dispose methods of classes to
     * ensure no memory leaks
     */
    @Override
    public void dispose() {
        graphCreator.dispose();
        NPC.disposeNPC();
        KeySystemManager.dispose();
        player.dispose();
        map.dispose();
        game.dispose();
        renderer.dispose();
    }

    public void debugText() {
        System.out.println("KeySystems:");
        System.out.format(" Safe: %d\n", KeySystemManager.safeKeySystemsCount());
        System.out.format(" BeingDestroyed: %d\n", KeySystemManager.beingDestroyedKeySystemsCount());
        System.out.format(" Destroyed: %d\n", KeySystemManager.destroyedKeySystemsCount());
        System.out.println();
    }
}
