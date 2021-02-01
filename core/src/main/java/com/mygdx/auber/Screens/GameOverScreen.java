package com.mygdx.auber.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.auber.Auber;

public final class GameOverScreen implements Screen {
    /** The Stage used to manage the buttons. */
    private final Stage stage;
    /** The button to return to the main menu. */
    private TextButton menuButton;
    /** The style used for the button. */
    private TextButton.TextButtonStyle textButtonStyle;
    /** The amount of padding to place below the game over button. */
    private static final float GAME_OVER_BUTTON_PADDING = 20;

    /** Class constructor.
     * @param game The instance of the game that is running.
     * @param win Whether the player won or not.
     */
    public GameOverScreen(final Auber game, final boolean win) {
        Viewport viewport = new ExtendViewport(Auber.VIRTUAL_WIDTH,
            Auber.VIRTUAL_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, game.getBatch());
        Gdx.input.setInputProcessor(stage);

        // Table setup
        Table table = new Table();
        table.center();
        table.setFillParent(true);
        table.setTouchable(Touchable.enabled);

        // Win title setup
        Label.LabelStyle titleFont = new Label.LabelStyle(new BitmapFont(),
            Color.WHITE);
        String endStatus;
        if (win) {
            endStatus = "YOU WIN";
        } else {
            endStatus = "YOU LOSE";
        }
        Label gameOverLabel = new Label(endStatus, titleFont);
        table.add(gameOverLabel).padBottom(GAME_OVER_BUTTON_PADDING);
        table.row();

        // Main menu button setup
        BitmapFont font = new BitmapFont();
        textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = font;
        TextureAtlas buttonAtlas = new TextureAtlas("buttonAtlas.atlas");
        Skin skin = new Skin();
        skin.addRegions(buttonAtlas);
        textButtonStyle.up = skin.getDrawable("up-button");
        textButtonStyle.down = skin.getDrawable("down-button");
        textButtonStyle.checked = skin.getDrawable("checked-button");
        menuButton = new TextButton("MAIN MENU", textButtonStyle);
        menuButton.addListener(new ClickListener() {
            @Override
            public void clicked(final InputEvent event, final float x,
                final float y) {
                game.setScreen(new MainMenuScreen(game));
            }

            @Override
            public void enter(final InputEvent event, final float x,
                final float y, final int pointer, final Actor fromActor) {
                menuButton.setChecked(true);
            }

            @Override
            public void exit(final InputEvent event, final float x,
                final float y, final int pointer, final Actor toActor) {
                menuButton.setChecked(false);
            }
        });
        table.add(menuButton);
        stage.addActor(table);
    }

    /**
     * Method implemented from abstract superclass.
     */
    @Override
    public void show() {

    }

    /**
     * Called every frame to render this screen.
     * @param delta The time in seconds between the previous frame and this
     * one.
     */
    public void render(final float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.draw();
        stage.act();
    }

    /**
     * Method implemented from abstract superclass.
     */
    @Override
    public void resize(final int width, final int height) {

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
     * Method implemented from abstract superclass.
     */
    @Override
    public void dispose() {

    }
}
