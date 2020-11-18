package com.mygdx.auber.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
    private Auber game;
    public static OrthographicCamera camera;
    private Viewport viewport;
    private Hud hud;
    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    private GraphCreator graphCreator;
    public Player player;
    private int numberOfInfiltrators = 1;
    private int numberOfCrew = 50;
    private ScrollingBackground scrollingBackground;

    public PlayScreen(Auber game){
        this.game = game;

        camera = new OrthographicCamera();
        viewport = new ExtendViewport(Auber.VirtualWidth, Auber.VirtualHeight, camera);
        hud = new Hud(game.batch);
        this.scrollingBackground = new ScrollingBackground();

        mapLoader = new TmxMapLoader();
        map = mapLoader.load("testmap2.tmx");

        graphCreator = new GraphCreator((TiledMapTileLayer)map.getLayers().get(0));
        for (int i = 0; i < numberOfInfiltrators; i++) {
            System.out.println("Infiltrator created!");
            NPCCreator.createInfiltrator(new Sprite(new Texture("HumanInfiltratorStand.png")), MapGraph.getRandomNode(), graphCreator.mapGraph);
        }
        for(int i = 0; i < numberOfCrew; i++)
        {
            System.out.println("Crewmember created!");
            NPCCreator.createCrew(new Sprite(new Texture("AlienInfiltratorStand.png")), MapGraph.getRandomNode(), graphCreator.mapGraph);
        }

        player = new Player(new Sprite(new Texture("AuberStand.png")),(TiledMapTileLayer)map.getLayers().get(0));
        player.setPosition(600, 1000);

        renderer = new OrthogonalTiledMapRenderer(map);
        camera.position.set(player.getX(),player.getY(),0);

        Gdx.input.setInputProcessor(player);
    }

    @Override
    public void show() {


    }

    public boolean gameOver() {
        return Player.health <= 0 || hud.CrewmateCount >= 3 || KeySystemManager.destroyedKeySystemsCount() >= 15;
    }

    public boolean gameWin()
    {
        return NPCCreator.infiltrators.isEmpty();
    }

    public void update(float time){
        NPC.updateNPC(time);
        player.update();
        hud.update();
        camera.update();
        renderer.setView(camera);

        if(gameOver()){
            game.setScreen(new GameOverScreen(game));
            dispose();
        } //If game over, show game over screen and dispose of all assets
        if(gameWin())
        {
            game.setScreen(new GameOverScreen(game));
            dispose();
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.09f, 0.09f, 0.09f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);// Clears the screen and sets it to the colour light blue or whatever colour it is

        camera.position.set(player.getX() + player.getWidth()/2,player.getY() + player.getHeight()/2,0); //Sets camera to centre of player position
        game.batch.setProjectionMatrix(camera.combined); //Ensures everything is rendered properly, only renders things in viewport

        renderer.getBatch().begin();  //Start the sprite batch

        scrollingBackground.updateRender(delta, (SpriteBatch) renderer.getBatch());//Renders the background
        renderer.renderTileLayer((TiledMapTileLayer) map.getLayers().get(0)); //Renders the bottom layer of the map

        NPC.render(renderer.getBatch()); //Renders all NPCs
        player.draw(renderer.getBatch()); //Renders the player

        update(delta); //Updates the game camera and NPCs
        hud.stage.draw(); //Draws the HUD on the game

        renderer.getBatch().end(); //Finishes the sprite batch

        //graphCreator.shapeRenderer.setProjectionMatrix(camera.combined); //Ensures nodes are rendered properly
        //graphCreator.render(); //Debugging shows nodes and paths
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        camera.viewportWidth = width/2f;
        camera.viewportHeight = height/2f;
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

    }

    @Override
    public void dispose() {
        game.dispose();
        map.dispose();
        renderer.dispose();
        graphCreator.dispose();
        NPC.dispose();
    }
}
