package com.mygdx.auber.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.auber.Auber;

public class MainMenuScreen implements Screen {
    /** The Viewport used for the camera. */
    private Viewport viewport;
    /** The Stage used to manage the buttons. */
    private Stage stage;
    /** The button used to start the game. */
    private TextButton playButton;
    /** The button used to exit the game. */
    private TextButton exitButton;
    /** The button used to start the demo. */
    private TextButton demoButton;
    /** The button used to start the tutorial. */
    private TextButton tutorialButton;
    /** The button used to load a game. */
    private TextButton loadButton;
    /** The style of the buttons. */
    private TextButton.TextButtonStyle textButtonStyle;
    /** The font of the text on the buttons. */
    private BitmapFont font;
    /** The skin used for the buttons. */
    private Skin skin;
    /** The atlas containing the textures for the sprites. */
    private TextureAtlas buttonAtlas;
    /** The texture of the title. */
    private Texture title;
    /** The image of the title. */
    private Image titleCard;
    /** The background texture. */
    private Texture background;
    /** The instance of the game that is running. */
    private Auber game;
    /** The amount of padding to place beneath buttons. */
    private static final float BUTTON_PADDING = 10;
    /** The difficulty used for the demo. */
    private static final int DEMO_DIFFICULTY = 42;

    /**
     * Class constructor.
     * @param currentGame The instance of Auber that is running.
     */
    public MainMenuScreen(final Auber currentGame) {
        this.game = currentGame;

        viewport = new ExtendViewport(Auber.VIRTUAL_WIDTH,
            Auber.VIRTUAL_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, ((Auber) currentGame).getBatch());
        Gdx.input.setInputProcessor(stage);

        font = new BitmapFont();
        skin = new Skin();
        title = new Texture("TitleCard.png");
        buttonAtlas = new TextureAtlas("buttonAtlas.atlas");
        background = new Texture("background.png");
        skin.addRegions(buttonAtlas);
        textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = font;
        textButtonStyle.up = skin.getDrawable("up-button");
        textButtonStyle.down = skin.getDrawable("down-button");
        textButtonStyle.checked = skin.getDrawable("checked-button");
        playButton = new TextButton("PLAY", textButtonStyle);
        demoButton = new TextButton("DEMO", textButtonStyle);
        exitButton = new TextButton("EXIT", textButtonStyle);
        tutorialButton = new TextButton("TUTORIAL", textButtonStyle);
        loadButton = new TextButton("LOAD", textButtonStyle);
        titleCard = new Image(title);

        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(final InputEvent event, final float x,
                final float y) {
                currentGame.setScreen(new ChooseDifficultyScreen(currentGame));
            }

            @Override
            public void enter(final InputEvent event, final float x,
                final float y, final int pointer, final Actor fromActor) {
                playButton.setChecked(true);
            }

            @Override
            public void exit(final InputEvent event, final float x,
                final float y, final int pointer, final Actor toActor) {
                playButton.setChecked(false);
            }
        });
        loadButton.addListener(new ClickListener() {
            @Override
            public void clicked(final InputEvent event, final float x,
                final float y) {
                //KYLE THIS IS YOURS NOW
            }

            @Override
            public void enter(final InputEvent event, final float x,
                final float y, final int pointer, final Actor fromActor) {
                loadButton.setChecked(true);
            }

            @Override
            public void exit(final InputEvent event, final float x,
                final float y, final int pointer, final Actor toActor) {
                loadButton.setChecked(false);
            }
        });
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(final InputEvent event, final float x,
                final float y) {
                Gdx.app.exit();
            }

            @Override
            public void enter(final InputEvent event, final float x,
                final float y, final int pointer, final Actor fromActor) {
                exitButton.setChecked(true);
            }

            @Override
            public void exit(final InputEvent event, final float x,
                final float y, final int pointer, final Actor toActor) {
                exitButton.setChecked(false);
            }
        });
        demoButton.addListener(new ClickListener() {
            @Override
            public void clicked(final InputEvent event, final float x,
                final float y) {
                currentGame.setScreen(new PlayScreen(currentGame, true,
                    DEMO_DIFFICULTY));
            }

            @Override
            public void enter(final InputEvent event, final float x,
                final float y, final int pointer, final Actor fromActor) {
                demoButton.setChecked(true);
            }

            @Override
            public void exit(final InputEvent event, final float x,
                final float y, final int pointer, final Actor toActor) {
                demoButton.setChecked(false);
            }
        });
        tutorialButton.addListener(new ClickListener() {
            @Override
            public void clicked(final InputEvent event, final float x,
                final float y) {
                currentGame.setScreen(new TutorialScreen(currentGame));
            }

            @Override
            public void enter(final InputEvent event, final float x,
                final float y, final int pointer, final Actor fromActor) {
                tutorialButton.setChecked(true);
            }

            @Override
            public void exit(final InputEvent event, final float x,
                final float y, final int pointer, final Actor toActor) {
                tutorialButton.setChecked(false);
            }
        });

        Table menuTable = new Table();
        menuTable.setTouchable(Touchable.enabled);
        menuTable.setFillParent(true);
        menuTable.add(titleCard);
        menuTable.row();
        menuTable.add(playButton).padBottom(BUTTON_PADDING);
        menuTable.row();
        menuTable.add(loadButton).padBottom(BUTTON_PADDING);
        menuTable.row();
        menuTable.add(demoButton).padBottom(BUTTON_PADDING);
        menuTable.row();
        menuTable.add(tutorialButton).padBottom(BUTTON_PADDING);
        menuTable.row();
        menuTable.add(exitButton);
        // menuTable.debug();

        stage.addActor(menuTable);
    }

    /**
     * Method implemented from abstract superclass.
     */
    @Override
    public void show() {

    }

    /** The x location to draw the background at. */
    private static final float BACKGROUND_X_LOCATION = -100f;
    /** The y location to draw the background at. */
    private static final float BACKGROUND_Y_LOCATION = 0f;

    /**
     * Called every frame to render this screen.
     * @param delta The time in seconds between the previous frame and this
     * one.
     */
    @Override
    public void render(final float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.getBatch().begin();
        stage.getBatch().draw(background, BACKGROUND_X_LOCATION,
            BACKGROUND_Y_LOCATION);
        stage.getBatch().end();
        stage.draw();
        stage.act();
    }

    /**
     * Method implemented from abstract superclass.
     */
    @Override
    public void resize(final int width, final int height) {
        viewport.update(width, height);
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
    }

    /**
     * Method called when this screen is deleted.
     */
    @Override
    public void dispose() {
        stage.dispose();
        font.dispose();
        skin.dispose();
        buttonAtlas.dispose();
        background.dispose();
    }
}
