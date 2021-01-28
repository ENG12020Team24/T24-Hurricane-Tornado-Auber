package com.mygdx.auber;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class ScrollingBackground {

    Texture image;
    float y1, y2;
    float x;
    public static int SPEED = 30; // In pixels per second
    float imageScale;

    public ScrollingBackground() {
        image = new Texture("background.png");

        y1 = 2000;
        y2 = y1 + image.getHeight() - 40;
        x = 200;
        imageScale = 1;
    }

    /**
     * Used to update and render the background
     * @param delta The time in seconds since the previous frame
     * @param batch
     */
    public void updateRender(float delta, SpriteBatch batch) {
        y1 -= SPEED * delta;
        y2 -= SPEED * delta;

        if (y1 + image.getHeight() * imageScale <= 2000) {
            y1 = y2 + image.getHeight() * imageScale;
        }
        if (y2 + image.getHeight() * imageScale <= 2000) {
            y2 = y1 + image.getHeight() * imageScale;
        }

        batch.draw(image, x, y1, image.getWidth(), image.getHeight());
        batch.draw(image, x, y2, image.getWidth(), image.getHeight());
    }

    /**
     * Resets the size of the background
     * @param width
     * @param height
     */
    public void resize(int width, int height) {
        imageScale = 1;
    }
}
