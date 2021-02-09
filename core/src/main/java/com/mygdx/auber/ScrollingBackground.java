package com.mygdx.auber;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class ScrollingBackground {
    /** The background texture. */
    private Texture image;
    /** The first y value used in rendering. */
    private float y1;
    /** The second y value used in rendering. */
    private float y2;
    /** The x value used in rendering. */
    private float x;
    /** The amount to scale the background by. */
    private float imageScale;
    /** The initial value for x. */
    private static final float INITIAL_X = 200;
    /** The initial value for y1. */
    private static final float INITIAL_Y = 2000;
    /** The initial offset for y2. */
    private static final float INITIAL_Y2 = 40;

    /**
     * Class constructor.
     */
    public ScrollingBackground() {
        image = new Texture("background.png");
        y1 = INITIAL_Y;
        y2 = y1 + image.getHeight() - INITIAL_Y2;
        x = INITIAL_X;
        imageScale = 1;
    }

    /**
     * Used to update and render the background.
     * @param delta The time in seconds since the previous frame.
     * @param batch The batch used to draw the background.
     */
    public void updateRender(final float delta, final SpriteBatch batch) {
        y1 -= Config.BACKGROUND_SCROLL_SPEED * delta;
        y2 -= Config.BACKGROUND_SCROLL_SPEED * delta;

        if (y1 + image.getHeight() * imageScale <= INITIAL_Y) {
            y1 = y2 + image.getHeight() * imageScale;
        }
        if (y2 + image.getHeight() * imageScale <= INITIAL_Y) {
            y2 = y1 + image.getHeight() * imageScale;
        }

        batch.draw(image, x, y1, image.getWidth(), image.getHeight());
        batch.draw(image, x, y2, image.getWidth(), image.getHeight());
    }

    /**
     * Resets the size of the background.
     * @param width The new width of this background.
     * @param height The new height of this background.
     */
    public void resize(final int width, final int height) {
        imageScale = 1;
    }
}
