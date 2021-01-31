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
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.auber.Auber;

public final class ChooseDifficultyScreen implements Screen {
    /** Used to control the different buttons. */
    private final Stage stage;
    /** The button used to select Easy difficulty. */
    private TextButton easyButton;
    /** The button used to select Normal difficulty. */
    private TextButton normalButton;
    /** The button used to select Hard difficulty. */
    private TextButton hardButton;
    /** The button used to return to the main menu. */
    private TextButton backButton;
    /** The style of the buttons. */
    private TextButton.TextButtonStyle textButtonStyle;
    /** The font used to draw text on the buttons. */
    private BitmapFont font;
    /** The skin used for the buttons. */
    private Skin skin;
    /** The background of this screen. */
    private Texture background;
    /** The atlas containing the textures for the sprites. */
    private TextureAtlas buttonAtlas;
    /** The amount of padding to place between buttons in the same group. */
    private static final float IN_GROUP_BOTTOM_PADDING = 20;
    /** The amount of padding between groups of buttons. */
    private static final float BETWEEN_GROUP_BOTTOM_PADDING = 50;

    /**
     * Lets the player choose dificulty based on button press. Calls PlayScreen
     * with a number based on the difficulty: 0 - Easy 1 - Normal 2 - Hard.
     * @param game The instance of the game currently running.
     */
    public ChooseDifficultyScreen(final Auber game) {
        Viewport viewport = new ExtendViewport(Auber.VIRTUAL_WIDTH,
            Auber.VIRTUAL_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, game.getBatch());
        Gdx.input.setInputProcessor(stage);

        background = new Texture("background.png");

        skin = new Skin();
        buttonAtlas = new TextureAtlas("buttonAtlas.atlas");
        skin.addRegions(buttonAtlas);

        font = new BitmapFont();
        textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = font;
        textButtonStyle.up = skin.getDrawable("up-button");
        textButtonStyle.down = skin.getDrawable("down-button");
        textButtonStyle.checked = skin.getDrawable("checked-button");

        easyButton = new TextButton("Easy", textButtonStyle);
        normalButton = new TextButton("Normal", textButtonStyle);
        hardButton = new TextButton("Hard", textButtonStyle);
        backButton = new TextButton("Back", textButtonStyle);

        easyButton.addListener(new ClickListener() {
            @Override
            public void clicked(final InputEvent event, final float x,
                final float y) {
                game.setScreen(new PlayScreen(game, false, 0));
            }

            @Override
            public void enter(final InputEvent event, final float x,
                final float y, final int pointer, final Actor fromActor) {
                easyButton.setChecked(true);
            }

            @Override
            public void exit(final InputEvent event, final float x,
                final float y, final int pointer, final Actor toActor) {
                easyButton.setChecked(false);
            }
        });

        normalButton.addListener(new ClickListener() {
            @Override
            public void clicked(final InputEvent event, final float x,
                final float y) {
                game.setScreen(new PlayScreen(game, false, 1));
            }

            @Override
            public void enter(final InputEvent event, final float x,
                final float y, final int pointer, final Actor fromActor) {
                normalButton.setChecked(true);
            }

            @Override
            public void exit(final InputEvent event, final float x,
                final float y, final int pointer, final Actor toActor) {
                normalButton.setChecked(false);
            }
        });

        hardButton.addListener(new ClickListener() {
            @Override
            public void clicked(final InputEvent event,
                final float x, final float y) {
                game.setScreen(new PlayScreen(game, false, 2));
            }

            @Override
            public void enter(final InputEvent event, final float x,
                final float y, final int pointer, final Actor fromActor) {
                hardButton.setChecked(true);
            }

            @Override
            public void exit(final InputEvent event, final float x,
                final float y, final int pointer, final Actor toActor) {
                hardButton.setChecked(false);
            }
        });

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(final InputEvent event, final float x,
                final float y) {
                game.setScreen(new MainMenuScreen(game));
            }

            @Override
            public void enter(final InputEvent event, final float x,
                final float y, final int pointer, final Actor fromActor) {
                backButton.setChecked(true);
            }

            @Override
            public void exit(final InputEvent event, final float x,
                final float y, final int pointer, final Actor toActor) {
                backButton.setChecked(false);
            }
        });

        Table difficultyTable = new Table();
        difficultyTable.center();
        difficultyTable.setFillParent(true);
        difficultyTable.setTouchable(Touchable.enabled);

        difficultyTable.add(easyButton).padBottom(IN_GROUP_BOTTOM_PADDING);
        difficultyTable.row();
        difficultyTable.add(normalButton).padBottom(IN_GROUP_BOTTOM_PADDING);
        difficultyTable.row();
        difficultyTable.add(hardButton).padBottom(
            BETWEEN_GROUP_BOTTOM_PADDING);
        difficultyTable.row();
        difficultyTable.add(backButton).padBottom(IN_GROUP_BOTTOM_PADDING);

        stage.addActor(difficultyTable);

    }

    /**
     * Method implemented from abstract superclass.
     */
    @Override
    public void show() {
        // TODO Auto-generated method stub

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
        // TODO Auto-generated method stub

    }

    /**
     * Method implemented from abstract superclass.
     */
    @Override
    public void pause() {
        // TODO Auto-generated method stub

    }

    /**
     * Method implemented from abstract superclass.
     */
    @Override
    public void resume() {
        // TODO Auto-generated method stub

    }

    /**
     * Method implemented from abstract superclass.
     */
    @Override
    public void hide() {
        // TODO Auto-generated method stub
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
