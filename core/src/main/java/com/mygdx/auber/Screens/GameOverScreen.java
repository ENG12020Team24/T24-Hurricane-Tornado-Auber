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

public class GameOverScreen implements Screen {
    private final Stage stage;
    TextButton menuButton;
    TextButton.TextButtonStyle textButtonStyle;

    public GameOverScreen(final Auber game, boolean win) {
        Viewport viewport = new ExtendViewport(Auber.VIRTUAL_WIDTH, Auber.VIRTUAL_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, game.getBatch());
        Gdx.input.setInputProcessor(stage);

        // Table setup
        Table table = new Table();
        table.center();
        table.setFillParent(true);
        table.setTouchable(Touchable.enabled);

        // Win title setup
        Label.LabelStyle titleFont = new Label.LabelStyle(new BitmapFont(), Color.WHITE);
        String endStatus;
        if (win) {
            endStatus = "YOU WIN";
        } else {
            endStatus = "YOU LOSE";
        }
        Label gameOverLabel = new Label(endStatus, titleFont);
        table.add(gameOverLabel).padBottom(20);
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
            public void clicked(InputEvent event, float x, float y) {
                // System.out.println("Main menu");
                game.setScreen(new MainMenuScreen(game));
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                // System.out.println("Hovered");
                menuButton.setChecked(true);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                // System.out.println("Exited");
                menuButton.setChecked(false);
            }
        });
        table.add(menuButton);

        stage.addActor(table);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.draw();
        stage.act();
    }

    @Override
    public void resize(int width, int height) {

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

    }
}
