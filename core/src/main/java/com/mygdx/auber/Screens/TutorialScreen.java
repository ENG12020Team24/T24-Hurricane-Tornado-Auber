package com.mygdx.auber.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.auber.Auber;

public class TutorialScreen implements Screen {
    /** The viewport used to draw the game. */
    private Viewport viewport;
    /** The Stage used to manage the buttons. */
    private Stage stage;
    /** The tutorial images to show the user. */
    private Array<Image> images = new Array<>();
    // Create array of images to be iterated through when moving through
    // tutorial
    /** The currently displayed tutorial image. */
    private int tutorialPage = 0;
    // Used for tracking current tutorial image

    /** The currently running instance of Auber. */
    private Auber game;

    /**
     * Class constructor.
     * @param currentGame The currently running instance of the game.
     */
    public TutorialScreen(final Auber currentGame) {
        this.game = currentGame;

        viewport = new ExtendViewport(
            Auber.VIRTUAL_WIDTH, Auber.VIRTUAL_HEIGHT,
                new OrthographicCamera());
        stage = new Stage(viewport, ((Auber) currentGame).getBatch());
        Gdx.input.setInputProcessor(stage);

        images.add(new Image(new Texture("Tutorial1.png")));
        // Adds each image to the images array
        images.add(new Image(new Texture("Tutorial2.png")));
        // New parts of the tutorial can be easily added
        images.add(new Image(new Texture("Tutorial3.png")));
        images.add(new Image(new Texture("Tutorial4.png")));
        images.add(new Image(new Texture("Tutorial5.png")));
        images.add(new Image(new Texture("Tutorial6.png")));

        final Table tutTable = new Table();
        // Table for adding each image to so that it can be displayed
        stage.addActor(tutTable);
        tutTable.setFillParent(true);
        // Fills stage with the table to make it the correct dimensions
        //  and to resize with the screen
        tutTable.add(images.get(tutorialPage));
        stage.addListener(new ClickListener() {
            @Override
            public void clicked(final InputEvent event, final float x,
                final float y) {
                if (tutorialPage == images.size - 1) {
                    // Change i to number of tutorial images - 1
                    currentGame.setScreen(new MainMenuScreen(currentGame));
                    // Change screen to main menu if on the final tutorial
                    // image
                } else {
                    tutTable.removeActor(images.get(tutorialPage));
                    // Removes current image from the table
                    tutorialPage++;
                    // Increases i to get the new image
                    tutTable.add(images.get(tutorialPage));
                    // Gets the next tutorial image from the array

                }

            }

        });

        // stage.addActor(images.get(0));

    }

    /**
     * Method implemented from abstract superclass.
     */
    @Override
    public void show() {

    }

    /**
     * Called to draw the tutorial screen.
     * @param delta The time since the previous frame in seconds.
     */
    @Override
    public void render(final float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        // Clear stage of previous renders
        stage.draw();
        // Draws the new stage

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
