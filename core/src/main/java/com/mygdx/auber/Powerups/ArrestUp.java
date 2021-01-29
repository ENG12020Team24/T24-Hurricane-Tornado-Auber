package com.mygdx.auber.Powerups;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.auber.Config;
import com.mygdx.auber.entities.Player;

public class ArrestUp extends PowerUp {
    private float timer = 0;

    public ArrestUp(Vector2 position) {

        super(position);
        r = Config.DEFAULT_ARRESTUP_RED;
        g = Config.DEFAULT_ARRESTUP_GREEN;
        b = Config.DEFAULT_ARRESTUP_BLUE;
    }

    /**
     * Used to update the status of this powerup every frame.
     *
     * @param player The Player whose position will be used for collision
     *               and who the powerup will be applied to.
     */
    @Override
    public void update(final Player player) {
        if (playerCollision(player.getX(), player.getY(), player.getWidth(),
                player.getHeight())) {
            taken = true;
        }
        if (taken && !used) {
            position.x = player.getX();
            position.y = player.getY();
            timer += Gdx.graphics.getDeltaTime();

            if (timer < Config.ARRESTUP_TIME) {
                player.arrestUp(true);
            } else {
                player.arrestUp(false);
                used = true;
            }
        }
    }

    /**
     * Calls the superclass render method with the specified colour.
     *
     * @param shapeRenderer The ShapeRenderer used to draw the powerup.
     */
    public void render(final ShapeRenderer shapeRenderer) {
        if (!taken) {
            super.render(shapeRenderer);
        }
    }
}
