package auber.com.mygdx.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
import auber.com.mygdx.Auber;
import auber.com.mygdx.ScrollingBackground;


public class ChooseDifficultyScreen implements Screen{
    private final Stage stage;
    TextButton easyButton, normalButton, hardButton, backButton; 
    TextButton.TextButtonStyle textButtonStyle;
    BitmapFont font;
    Skin skin;
    Texture background;
    TextureAtlas buttonAtlas;

    /**
     * Lets the player choose dificulty based on button press.
     * Calls PlayScreen with a number based on the difficulty:
     *      0 - Easy
     *      1 - Normal
     *      2 - Hard
     */
    public ChooseDifficultyScreen(final Auber game){
        Viewport viewport = new ExtendViewport(Auber.VirtualWidth, Auber.VirtualHeight, new OrthographicCamera());
        stage = new Stage(viewport, game.batch);
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

        easyButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new PlayScreen(game, false, 0));
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                easyButton.setChecked(true);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                easyButton.setChecked(false);
            }
        });

        normalButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new PlayScreen(game, false, 1));
            }
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                normalButton.setChecked(true);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                normalButton.setChecked(false);
            }
        });

        hardButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new PlayScreen(game, false, 2));
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                hardButton.setChecked(true);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                hardButton.setChecked(false);
            }
        });

        backButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenuScreen(game));
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                backButton.setChecked(true);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                backButton.setChecked(false);
            }
        });

        Table difficultyTable = new Table();
        difficultyTable.center();
        difficultyTable.setFillParent(true);
        difficultyTable.setTouchable(Touchable.enabled);

        // difficultyTable.add(CHOOSE DIFFILCULTY TEXT).padBottom(20);
        // difficultyTable.row();

        difficultyTable.add(easyButton).padBottom(20);
        difficultyTable.row();
        difficultyTable.add(normalButton).padBottom(20);
        difficultyTable.row();
        difficultyTable.add(hardButton).padBottom(50);
        difficultyTable.row();
        difficultyTable.add(backButton).padBottom(20);

        stage.addActor(difficultyTable);

    }

    @Override
    public void show() {
        // TODO Auto-generated method stub

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.getBatch().begin();
        stage.getBatch().draw(background, -100f, 0f);
        stage.getBatch().end();
        stage.draw();
        stage.act();
    }

    @Override
    public void resize(int width, int height) {
        // TODO Auto-generated method stub

    }

    @Override
    public void pause() {
        // TODO Auto-generated method stub

    }

    @Override
    public void resume() {
        // TODO Auto-generated method stub

    }

    @Override
    public void hide() {
        // TODO Auto-generated method stub

    }

    @Override
    public void dispose() {
        stage.dispose();
        font.dispose();
        skin.dispose();
        buttonAtlas.dispose();
        background.dispose();
    }
    
}
