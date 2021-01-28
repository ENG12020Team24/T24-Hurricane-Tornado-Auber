package com.mygdx.auber;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.auber.Screens.MainMenuScreen;

public final class Auber extends Game {
    /** The virtual width of the game. */
    public static final int VIRTUAL_WIDTH = 800;
    /** The virtual height of the game. */
    public static final int VIRTUAL_HEIGHT = 480;
    /** The SpriteBatch used to render sprites during the game. */
    private SpriteBatch batch;

    /**
     * Called when this is created.
     */
    @Override
    public void create() {
        batch = new SpriteBatch();
        setScreen(new MainMenuScreen(this));
    }

    /**
     * Called when a new frame is rendered.
     */
    @Override
    public void render() {
        super.render(); // delegates render method
                        // to current active screen
    }

    /**
     *
     * @return This Auber's sprite batch.
     */
    public SpriteBatch getBatch() {
        return batch;
    }
}
