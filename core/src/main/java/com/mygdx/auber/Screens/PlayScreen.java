package com.mygdx.auber.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.auber.Auber;
import com.mygdx.auber.Config;
import com.mygdx.auber.Pathfinding.GraphCreator;
import com.mygdx.auber.Pathfinding.MapGraph;
import com.mygdx.auber.Powerups.ArrestUp;
import com.mygdx.auber.Powerups.PowerUp;
import com.mygdx.auber.Powerups.FreezeUp;
import com.mygdx.auber.Powerups.SpeedUp;
import com.mygdx.auber.Powerups.ShieldUp;
import com.mygdx.auber.Powerups.HighlightUp;
import com.mygdx.auber.Scenes.Hud;
import com.mygdx.auber.ScrollingBackground;
import com.mygdx.auber.entities.KeySystemManager;
import com.mygdx.auber.entities.Prisoners;
import com.mygdx.auber.entities.Player;
import com.mygdx.auber.entities.Infiltrator;
import com.mygdx.auber.entities.CrewMembers;
import com.mygdx.auber.entities.NPC;
import com.mygdx.auber.entities.NPCCreator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileNameExtensionFilter;

public class PlayScreen implements Screen {
    /** The instance of the game that is running. */
    private final Auber game;
    /** The viewport used to draw the game. */
    private final Viewport viewport;
    /** The HUD displayed to the user. */
    private final Hud hud;
    /** The map loader used to load the map. */
    private final TmxMapLoader mapLoader;
    /** The game map. */
    private final TiledMap map;
    /** The renderer used to render the map. */
    private final OrthogonalTiledMapRenderer renderer;
    /** The GraphCreator used for pathfinding. */
    private final GraphCreator graphCreator;
    /** The game's background. */
    private final ScrollingBackground scrollingBackground;
    /** Used to manage the key systems that can be destroyed. */
    private final KeySystemManager keySystemManager;
    /** The collection of prisoners in the game world. */
    private Prisoners prisoners;
    /** The shape renderer used to render powerups. */
    private final ShapeRenderer shapeRenderer;
    /** The camera used to control the rendering. */
    private static OrthographicCamera camera;
    /** The current iteration of the player. */
    private Player player;
    /** The number of infiltrators in the game. */
    private static final int NUMBER_OF_INFILTRATORS = 8;
    /** The number of crew existing in the game world. */
    private int numberOfCrew;
    /** The number of incorrect arrests Auber can make before failing. */
    private int maxIncorrectArrests;
    /** Whether the game is running in demo mode or not. */
    private static boolean demo;
    /** The difficulty of the game. */
    private int difficulty;
    /** The list of powerups in the game. */
    private ArrayList<PowerUp> powerUps;
    /** The list of powerups to remove from the game on the next frame. */
    private ArrayList<PowerUp> powerUpsToRemove;
    /** The list of powerups to add to the game world. */
    private ArrayList<PowerUp> powerUpsToAdd;
    // /** The siren sound from https://soundbible.com/1577-Siren-Noise.html 
    //  * Under a creative common's licence
    // */
    // private Sound sirenNoise;
    // /** */
    // private boolean isSirenRunning = false;

    private boolean forcePause = false;

    /**
     * Class constructor.
     * @param currentGame    The currently running instance of the game.
     * @param isDemo         Whether the game is running in demo mode or not.
     * @param gameDifficulty The difficulty of the game.
     */
    public PlayScreen(
        final Auber currentGame, final boolean isDemo,
            final int gameDifficulty) {
        this.game = currentGame;
        this.demo = isDemo;
        this.difficulty = gameDifficulty;
        this.numberOfCrew = Config.CREW_COUNT_DIFFICULTY_MULTIPLIER
            * (gameDifficulty + 1);
        this.maxIncorrectArrests = Config.INCORRECT_ARREST_DIFFICULTY_MULTIPLIER
            * (Config.INCORRECT_ARREST_DIFFICULTY_MULTIPLIER - gameDifficulty);
        camera = new OrthographicCamera();
        viewport = new ExtendViewport(
            Auber.VIRTUAL_WIDTH, Auber.VIRTUAL_HEIGHT, camera);
        shapeRenderer = new ShapeRenderer();
        scrollingBackground = new ScrollingBackground();
        // Creating a new camera, viewport, hud and scrolling background,
        // setting the viewport to camera and virtual height/width
        //sirenNoise = Gdx.audio.newSound(Gdx.files.internal("Siren_Noise.wav"));

        mapLoader = new TmxMapLoader();

        map = mapLoader.load("AuberMap.tmx");
        // Creates a new map loader and loads the map into map

        Infiltrator.createInfiltratorSprites();
        CrewMembers.createCrewSprites();
        // Generates the infiltrator and crewmember sprites

        graphCreator = new GraphCreator(
            (TiledMapTileLayer) map.getLayers().get("Tile Layer 1"));
        // Generates all the nodes and paths for the given map layer
        keySystemManager = new KeySystemManager(
            (TiledMapTileLayer) map.getLayers().get("Systems"));
        // Generates key systems
        prisoners = new Prisoners(
            (TiledMapTileLayer) map.getLayers().get("OutsideWalls+Lining"));

        MapGraph mapGraph = graphCreator.getMapGraph();

        powerUps = new ArrayList<PowerUp>();
        powerUpsToRemove = new ArrayList<PowerUp>();
        powerUpsToAdd = new ArrayList<PowerUp>();

        powerUpsToAdd.add(new ArrestUp(
            new Vector2(Config.POWERUP_START_X, Config.POWERUP_START_Y)));
        powerUpsToAdd.add(new SpeedUp(
            new Vector2(Config.POWERUP_START_X, Config.POWERUP_START_Y)));
        powerUpsToAdd.add(new FreezeUp(
            new Vector2(Config.POWERUP_START_X, Config.POWERUP_START_Y)));
        powerUpsToAdd.add(new ShieldUp(
            new Vector2(Config.POWERUP_START_X, Config.POWERUP_START_Y)));
        powerUpsToAdd.add(new HighlightUp(
            new Vector2(Config.POWERUP_START_X, Config.POWERUP_START_Y)));

        for (int i = 0; i < NUMBER_OF_INFILTRATORS; i++) {
            // System.out.println("Infiltrator created!");
            if (i == NUMBER_OF_INFILTRATORS - 1) {
                NPCCreator.createInfiltrator(
                    Infiltrator.getHardSprites().random(),
                    mapGraph.getRandomNode(),
                    graphCreator.getMapGraph());
                break;
            }
            NPCCreator.createInfiltrator(
                Infiltrator.getEasySprites().random(), mapGraph.getRandomNode(),
                    graphCreator.getMapGraph());
        } // Creates numberOfInfiltrators infiltrators, gives them a random
          // hard or easy sprite

        if (isDemo) {
            NPCCreator.createCrew(new Sprite(
                new Texture("AuberStand.png")), mapGraph.getRandomNode(),
                    graphCreator.getMapGraph());
        }

        for (int i = 0; i < numberOfCrew; i++) {
            NPCCreator.createCrew(
                CrewMembers.selectSprite(), mapGraph.getRandomNode(),
                graphCreator.getMapGraph());
        } // Creates numberOfCrew crewmembers, gives them a random sprite

        Array<TiledMapTileLayer> playerCollisionLayers = new Array<>();
        playerCollisionLayers.add(
            (TiledMapTileLayer) map.getLayers().get("Tile Layer 1"));
        playerCollisionLayers.add((TiledMapTileLayer) map.getLayers().get(2));
        // The layers on which the player will collide

        player = new Player(new Sprite(
            new Texture("AuberStand.png")), playerCollisionLayers, isDemo);
        player.setPosition(Config.PLAYER_START_X, Config.PLAYER_START_Y);
        // Creates a player and sets him to the given position
        player.findInfirmary(
            (TiledMapTileLayer) map.getLayers().get("Systems"));
        // Finds infirmary
        player.setTeleporters(Player.getTeleporterLocations(
            (TiledMapTileLayer) map.getLayers().get("Systems")));

        renderer = new OrthogonalTiledMapRenderer(map);
        // Creates a new renderer with the given map

        camera.position.set(player.getX(), player.getY(), 0);
        // Sets the camera position to the player

        Gdx.input.setInputProcessor(player);
        // Sets the input to be handled by the player class
        hud = new Hud(currentGame.getBatch(), this, player);

    }

   /**
     * Class constructor.
     * @param currentGame    The currently running instance of the game.
     * @param isDemo         Whether the game is running in demo mode or not.
     * @param path The file path of the game file to be loaded.
     */
    public PlayScreen(
        final Auber currentGame, final boolean isDemo,
            final String path) {
        this.game = currentGame;
        this.demo = isDemo;


        String encodedPlayer = "", diff = "", e1 = "", e2 = "", e3 = "",
            e4 = "", e5 = "", e6 = "", e7 = "", e8 = "";
        try {
            File file = new File(path);
            Scanner reader = new Scanner(file);
            encodedPlayer = reader.nextLine();
            diff = reader.nextLine();
            e1 = reader.nextLine(); // Infiltrator locations.
            e2 = reader.nextLine(); // Infiltrator is destroying
            e3 = reader.nextLine(); // Infiltrator is invisible.
            e4 = reader.nextLine(); // Infiltratpr time invisible.
            e5 = reader.nextLine(); // Infiltrator is hard sprite.
            e6 = reader.nextLine(); // Last infiltrator index.
            e7 = reader.nextLine(); // Locations of all crew members.
            e8 = reader.nextLine(); // Last crew index.
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
              System.out.println("Unrecognisable file type.");
        }

        this.difficulty = Integer.valueOf(diff);
        this.numberOfCrew = Config.CREW_COUNT_DIFFICULTY_MULTIPLIER
            * (this.difficulty + 1);
        this.maxIncorrectArrests = Config.INCORRECT_ARREST_DIFFICULTY_MULTIPLIER
            * (Config.INCORRECT_ARREST_DIFFICULTY_MULTIPLIER - this.difficulty);
        camera = new OrthographicCamera();
        viewport = new ExtendViewport(
            Auber.VIRTUAL_WIDTH, Auber.VIRTUAL_HEIGHT, camera);
        shapeRenderer = new ShapeRenderer();
        scrollingBackground = new ScrollingBackground();
        // Creating a new camera, viewport, hud and scrolling background,
        // setting the viewport to camera and virtual height/width

        mapLoader = new TmxMapLoader();

        map = mapLoader.load("AuberMap.tmx");
        // Creates a new map loader and loads the map into map

        Infiltrator.createInfiltratorSprites();
        CrewMembers.createCrewSprites();
        // Generates the infiltrator and crewmember sprites

        graphCreator = new GraphCreator(
            (TiledMapTileLayer) map.getLayers().get("Tile Layer 1"));
        // Generates all the nodes and paths for the given map layer
        keySystemManager = new KeySystemManager(
            (TiledMapTileLayer) map.getLayers().get("Systems"));
        // Generates key systems
        prisoners = new Prisoners(
            (TiledMapTileLayer) map.getLayers().get("OutsideWalls+Lining"));

        powerUps = new ArrayList<PowerUp>();
        powerUpsToRemove = new ArrayList<PowerUp>();
        powerUpsToAdd = new ArrayList<PowerUp>();

        powerUpsToAdd.add(new ArrestUp(
            new Vector2(Config.POWERUP_START_X, Config.POWERUP_START_Y)));
        powerUpsToAdd.add(new SpeedUp(
            new Vector2(Config.POWERUP_START_X, Config.POWERUP_START_Y)));
        powerUpsToAdd.add(new FreezeUp(
            new Vector2(Config.POWERUP_START_X, Config.POWERUP_START_Y)));
        powerUpsToAdd.add(new ShieldUp(
            new Vector2(Config.POWERUP_START_X, Config.POWERUP_START_Y)));
        powerUpsToAdd.add(new HighlightUp(
            new Vector2(Config.POWERUP_START_X, Config.POWERUP_START_Y)));

       NPCCreator.loadInfiltratorsFromEncoding(e1, e2, e3, e4, e5, e6, graphCreator.getMapGraph());

       MapGraph mapGraph = graphCreator.getMapGraph();

        if (isDemo) {
            NPCCreator.createCrew(new Sprite(
                new Texture("AuberStand.png")), mapGraph.getRandomNode(),
                    graphCreator.getMapGraph());
        }


        NPCCreator.LoadCrewFromEncoding(e7, e8, graphCreator.getMapGraph());

        Array<TiledMapTileLayer> playerCollisionLayers = new Array<>();
        playerCollisionLayers.add(
            (TiledMapTileLayer) map.getLayers().get("Tile Layer 1"));
        playerCollisionLayers.add((TiledMapTileLayer) map.getLayers().get(2));
        // The layers on which the player will collide


        String[] splitPlayer = encodedPlayer.split(",");

        splitPlayer[0] = splitPlayer[0].replace("[", "");
        splitPlayer[splitPlayer.length - 1]
            = splitPlayer[splitPlayer.length - 1].replace("]", "");

        player = new Player(new Sprite(
            new Texture("AuberStand.png")), playerCollisionLayers, isDemo);
        player.setPosition(Float.valueOf(splitPlayer[0]),
            Float.valueOf(splitPlayer[1]));
        player.setHealth(Float.valueOf(splitPlayer[2]));
        player.setCanHeal(Boolean.valueOf(splitPlayer[3]));
        player.setHealStopTime(Float.valueOf(splitPlayer[4]));
        player.setUsingArrestPowerUp(Boolean.valueOf(splitPlayer[5]));
        player.setUsingSpeedPowerUp(Boolean.valueOf(splitPlayer[6]));
        // Creates a player and sets him to the given position
        player.findInfirmary(
            (TiledMapTileLayer) map.getLayers().get("Systems"));
        // Finds infirmary
        player.setTeleporters(Player.getTeleporterLocations(
            (TiledMapTileLayer) map.getLayers().get("Systems")));

        renderer = new OrthogonalTiledMapRenderer(map);
        // Creates a new renderer with the given map

        camera.position.set(player.getX(), player.getY(), 0);
        // Sets the camera position to the player

        Gdx.input.setInputProcessor(player);
        // Sets the input to be handled by the player class
        hud = new Hud(currentGame.getBatch(), this, player);

    }
    /**
     * Method implemented from abstract superclass.
     */
    @Override
    public void show() {

    }

    /**
     * If any of the game over conditions are true, returns true.
     * @return Boolean if the game is over or not
     */
    public boolean gameOver() {
        return player.getHealth() <= 0
            || Hud.getIncorrectArrestCount() >= maxIncorrectArrests
            || KeySystemManager.destroyedKeySystemsCount()
            >= Config.MAX_LOST_SYSTEMS;
    }

    /**
     * If any of the win conditions are true, returns true.
     * @return Boolean If the game is won or not.
     */
    public boolean gameWin() {
        return !NPCCreator.hasInfiltrators();
    }

    /**
     * Called every frame, call update methods in here.
     * @param time Time between last frame and this frame.
     */
    public void update(final float time) {

        if (player.getRequestedSave()) {
            // If the player has requested for the game to be saved.
            // First we have to force pause the game, otherwise we have issues.
            this.forcePause = true;

            JFileChooser chooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Game Files", "gme");
            chooser.setFileFilter(filter);
            JFrame f = new JFrame();
            f.setVisible(true);
            f.toFront();
            f.setVisible(false);
            int res = chooser.showSaveDialog(f);
            f.dispose();
            if (res == JFileChooser.APPROVE_OPTION) {
                this.saveGame(chooser.getSelectedFile().toString() + ".gme");
            }

            player.handledSave();
            // Tell the player that saving has been handled so that the flag is
            // reset.
        } else {
            if (player.getRequestedPause() || player.getRequestedSave()) {
                // If the player has requested for the game to be paused we
                // need to update differently.
                renderer.setView(camera);
                /**
                *   TODO: render a pause screen instead of the game world, with
                * resume game, save game, and exit buttons. Currently the game
                * just freezes and user inputs
                * are all ignored (except for escape clicks) until the game is
                * unpaused.
                */
            } else {
                NPC.updateNPC(player, time);
                player.update(time);
                hud.update(player);
                camera.update();
                // Updating everything that needs to be updated

                // debugText();

                renderer.setView(camera); // Needed for some reason

                if (powerUps.size() == 0 && powerUpsToAdd.size() > 0) {
                    powerUps.add(powerUpsToAdd.remove(0));
                }

                if (gameOver()) {
                    System.out.println("Lose");
                    game.setScreen(new GameOverScreen(game, false));
                    return;
                }
                // If game over, show game over screen and dispose of all assets
                if (gameWin()) {
                    System.out.println("Win");
                    game.setScreen(new GameOverScreen(game, true));
                    return;
                } // If game won, show game win screen and dispose of all assets
            }
        }



    }

    // TODO: Save the list of power ups in the game, the power ups to remove,
    // and the power ups to add.
    // Also need to save the state of the Infiltrator and CrewMembers
    // singleton classes, unsure as to why these are singleton?
    /** Saves the game.
     * @param path The file to save the game to.
     */
    private void saveGame(final String path) {
        File gameFile = new File(path);
        try {
            if (gameFile.createNewFile()) {
                FileWriter writer = new FileWriter(path);
                // write file now
                writer.write(this.player.encode());
                writer.write(System.lineSeparator());
                writer.write(String.valueOf(this.difficulty));
                writer.write(System.lineSeparator());
                writer.write(NPCCreator.encode());
                // Saves encoded data of all the NPCs in the game.
                // end of writing file

                writer.close();
                System.out.print("");
            }
        } catch (IOException e) {
            System.out.print("Error creating file.");
            e.printStackTrace();
        }

    }

    /** The value to use for the colour when clearing the background. */
    private static final float BACKGROUND_CLEAR_VALUE = 0.09f;

    /**
     * Called every frame, call render methods in here.
     * @param delta Time between last frame and this frame.
     */
    @Override
    public void render(final float delta) {
        Gdx.gl.glClearColor(BACKGROUND_CLEAR_VALUE,
            BACKGROUND_CLEAR_VALUE, BACKGROUND_CLEAR_VALUE, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        // Clears the screen and sets it to the colour light blue or whatever
        // colour it is

        // if (Infiltrator.isAlarm && !isSirenRunning){
        //     if (KeySystemManager.beingDestroyedKeySystemsCount() !=0){
        //         sirenNoise.loop();}
        //     else{
        //         Infiltrator.isAlarm=false;
        //     }
        // } else {
        //     sirenNoise.stop();
        // }

        if (!demo) {
            camera.position.set(
                player.getX() + player.getWidth() / 2,
                player.getY() + player.getHeight() / 2, 0);
            // Sets camera to centre of player position
        } else {
            CrewMembers crew = NPCCreator.getCrew().get(0);
            camera.position.set(
                crew.getX() + crew.getWidth() / 2,
                crew.getY() + crew.getHeight() / 2, 0);
        }

        game.getBatch().setProjectionMatrix(camera.combined);
        // Ensures everything is rendered properly,
        // only renders things in viewport
        shapeRenderer.setProjectionMatrix(camera.combined);
        // Ensures the shape renderer renders thing properly
        renderer.getBatch().begin(); // Start the sprite batch
        /* Render sprites/textures below this line */

        scrollingBackground.updateRender(
            delta, (SpriteBatch) renderer.getBatch());
        // Renders the background
        /*renderer.renderTileLayer((TiledMapTileLayer) map.getLayers().get(0));
        // Renders the bottom layer of the map
        Prisoners.render(renderer.getBatch());
        renderer.renderTileLayer((TiledMapTileLayer) map.getLayers().get(1));
        renderer.renderTileLayer((TiledMapTileLayer) map.getLayers().get(2));
        renderer.renderTileLayer((TiledMapTileLayer) map.getLayers().get(3));*/
        for (MapLayer l : map.getLayers()) {
            renderer.renderTileLayer((TiledMapTileLayer) l);
        }

        NPC.render(renderer.getBatch()); // Renders all NPCs
        if (!demo) {
            player.draw(renderer.getBatch()); // Renders the player
            player.drawArrow(renderer.getBatch());
            // Renders arrows towards key systems
        }

        update(delta); // Updates the game camera and NPCs
        hud.draw(); // Draws the HUD on the game

        /* Render sprites/textures above this line */
        renderer.getBatch().end(); // Finishes the sprite batch
        /* Render shapes below this line */

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        // Allows for alpha changes in shapes

        if (!demo) {
            player.drawCircle(shapeRenderer);
        }
        // graphCreator.shapeRenderer.setProjectionMatrix(camera.combined);
        // Ensures shapes are rendered properly
        // graphCreator.render(); //Debugging shows nodes and paths

        /* Render shapes above this line */
        Gdx.gl.glDisable(GL20.GL_BLEND);

        for (PowerUp pu : powerUps) {
            pu.render(shapeRenderer);
            pu.update(player);
            if (pu.isUsed()) {
                powerUpsToRemove.add(pu);
            }
        }
        for (PowerUp pu : powerUpsToRemove) {
            powerUps.remove(pu);
        }

    }

    /**
     * Called upon window being resized, and at the beginning.
     * @param width  Width of the window.
     * @param height Height of the window.
     */
    @Override
    public void resize(final int width, final int height) {
        viewport.update(width, height);
        camera.viewportWidth = width / 2f;
        camera.viewportHeight = height / 2f;
        camera.update();
        scrollingBackground.resize(width, height);
    }

    /**
     * Method implemented from abstract superclass.
     */
    @Override
    public void pause() {

    }

    /**
     * Method implemented from abstract superclass.
     */
    @Override
    public void resume() {

    }

    /**
     * Method implemented from abstract superclass.
     */
    @Override
    public void hide() {
        graphCreator.dispose();
        NPC.disposeNPC();
        KeySystemManager.dispose();
        player.dispose();
    }

    /**
     * Called when the screen is closed, need to call dispose methods of
     * classes to ensure no memory leaks.
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

    /**
     * Prints debug text, not used.
     */
    public void debugText() {
        System.out.println("KeySystems:");
        System.out.format(
            " Safe: %d\n", KeySystemManager.safeKeySystemsCount());
        System.out.format(
            " BeingDestroyed: %d\n",
            KeySystemManager.beingDestroyedKeySystemsCount());
        System.out.format(
            " Destroyed: %d\n", KeySystemManager.destroyedKeySystemsCount());
        System.out.println();
    }

    /**
     * Gets the max incorrect arrests.
     * @return An int containing the max incorrect arrests.
     */
    public int getMaxIncorrectArrests() {
        return maxIncorrectArrests;
    }

    /**
     * Gets the number of infiltrators.
     * @return An int containing the number of infiltrators.
     */
    public static int getNumberOfInfiltrators() {
        return NUMBER_OF_INFILTRATORS;
    }

    /**
     * The camera used to render the game.
     * @return The camera used to render the game.
     */
    public static OrthographicCamera getCamera() {
        return camera;
    }

}
