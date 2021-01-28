package com.mygdx.auber.Powerups;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.auber.entities.Player;

public class SpeedUp extends PowerUp {

    public float timer = 0;

    public SpeedUp(int type, Vector2 position) {
        super(1, position);
    }

    /**
     * Used to update the status of this powerup every frame
     */
    @Override
    public void update(Player player) {
        if (playerCollision(player.getX(), player.getY(), player.getWidth(), player.getHeight())) {
            taken = true;
        }
        if (taken && !used) {
            timer += Gdx.graphics.getDeltaTime();

            if (timer < 10) {
                player.speedUp(true);
            } else {
                player.speedUp(false);
                used = true;
            }
        }
    }

    /**
     * Calls the superclass render method with the specified colour
     */
    public void render(ShapeRenderer shapeRenderer) {
        super.render(shapeRenderer, 0, 1, 0);
    }
}
