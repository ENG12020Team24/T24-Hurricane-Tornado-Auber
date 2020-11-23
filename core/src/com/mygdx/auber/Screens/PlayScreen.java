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
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.auber.Auber;
import com.mygdx.auber.Pathfinding.GraphCreator;
import com.mygdx.auber.Pathfinding.MapGraph;
import com.mygdx.auber.Scenes.Hud;
import com.mygdx.auber.ScrollingBackground;
import com.mygdx.auber.entities.*;

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
    public static OrthographicCamera camera;
    public Player player;

    public static final int numberOfInfiltrators = 80;
    public static final int numberOfCrew = 120;
    public static final int maxIncorrectArrests = 3;

    public PlayScreen(Auber game, boolean demo){
        this.game = game;

        camera = new OrthographicCamera();
        viewport = new ExtendViewport(Auber.VirtualWidth, Auber.VirtualHeight, camera);
        hud = new Hud(game.batch);
        scrollingBackground = new ScrollingBackground(); //Creating a new camera, viewport, hud and scrolling background, setting the viewport to camera and virtual height/width

        mapLoader = new TmxMapLoader();

        map = mapLoader.load("AuberMap4.0.tmx"); //Creates a new map loader and loads the map into map

        Infiltrator.createInfiltratorSprites();
        CrewMembers.createCrewSprites(); //Generates the infiltrator and crewmember sprites

        graphCreator = new GraphCreator((TiledMapTileLayer)map.getLayers().get(0)); //Generates all the nodes and paths for the given map layer
        keySystemManager = new KeySystemManager((TiledMapTileLayer)map.getLayers().get(1));

        for (int i = 0; i < numberOfInfiltrators; i++) {
            //System.out.println("Infiltrator created!");
            NPCCreator.createInfiltrator(Infiltrator.easySprites.random(), MapGraph.getRandomNode(), graphCreator.mapGraph);
        } //Creates numberOfInfiltrators infiltrators, gives them a random hard or easy sprite

        for(int i = 0; i < numberOfCrew; i++)
        {
            //System.out.println("Crewmember created!");
            NPCCreator.createCrew(CrewMembers.selectSprite(), MapGraph.getRandomNode(), graphCreator.mapGraph);
        } //Creates numberOfCrew crewmembers, gives them a random sprite

        player = new Player(new Sprite(new Texture("AuberStand.png")),(TiledMapTileLayer)map.getLayers().get(0), demo);
        player.setPosition(600, 1000); //Creates a player and sets him to the given position

        renderer = new OrthogonalTiledMapRenderer(map); //Creates a new renderer with the given map

        camera.position.set(player.getX(),player.getY(),0); //Sets the camera position to the player

        Gdx.input.setInputProcessor(player); //Sets the input to be handled by the player class
    }

    @Override
    public void show() {


    }

    /**
     * If any of the game over conditions are true, returns true
     * @return Boolean if the game is over or not
     */
    public boolean gameOver() {
        return Player.health <= 0 || Hud.CrewmateCount >= 3 || KeySystemManager.destroyedKeySystemsCount() >= 15 ;
    }

    /**
     * If any of the win conditions are true, returns true
     * @return Boolean If the game is won or not
     */
    public boolean gameWin()
    {
        return NPCCreator.infiltrators.isEmpty();
    }

    /**
     * Called every frame, call update methods in here
     * @param time Time between last frame and this frame
     */
    public void update(float time){
        NPC.updateNPC(time);
        player.update(time);
        hud.update();
        camera.update(); //Updating everything that needs to be updated

        //debugText();

        renderer.setView(camera); //Needed for some reason

        if(gameOver()){
            game.setScreen(new GameOverScreen(game, false));
        } //If game over, show game over screen and dispose of all assets
        if(gameWin())
        {
            game.setScreen(new GameOverScreen(game, true));
        } //If game won, show game win screen and dispose of all assets
    }

    /**
     * Called every frame, call render methods in here
     * @param delta Time between last frame and this frame
     */
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.09f, 0.09f, 0.09f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);// Clears the screen and sets it to the colour light blue or whatever colour it is

        camera.position.set(player.getX() + player.getWidth()/2,player.getY() + player.getHeight()/2,0); //Sets camera to centre of player position
        game.batch.setProjectionMatrix(camera.combined); //Ensures everything is rendered properly, only renders things in viewport

        renderer.getBatch().begin();  //Start the sprite batch

        scrollingBackground.updateRender(delta, (SpriteBatch) renderer.getBatch());//Renders the background
        renderer.renderTileLayer((TiledMapTileLayer) map.getLayers().get(0)); //Renders the bottom layer of the map
        renderer.renderTileLayer((TiledMapTileLayer) map.getLayers().get(1));

        NPC.render(renderer.getBatch()); //Renders all NPCs
        player.draw(renderer.getBatch()); //Renders the player
        player.drawArrow(renderer.getBatch()); //Renders arrows towards key systems

        update(delta); //Updates the game camera and NPCs
        hud.stage.draw(); //Draws the HUD on the game

        renderer.getBatch().end(); //Finishes the sprite batch

        //graphCreator.shapeRenderer.setProjectionMatrix(camera.combined); //Ensures shapes are rendered properly
        //graphCreator.render(); //Debugging shows nodes and paths
    }

    /**
     * Called upon window being resized, and at the beginning
     * @param width Width of the window
     * @param height Height of the window
     */
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        camera.viewportWidth = width/3f;
        camera.viewportHeight = height/3f;
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
     * Called when the screen is closed, need to call dispose methods of classes to ensure no memory leaks
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

    public void debugText()
    {
        System.out.println("KeySystems:");
        System.out.format(" Safe: %d\n", KeySystemManager.safeKeySystemsCount());
        System.out.format(" BeingDestroyed: %d\n", KeySystemManager.beingDestroyedKeySystemsCount());
        System.out.format(" Destroyed: %d\n", KeySystemManager.destroyedKeySystemsCount());
        System.out.println();
    }
}
