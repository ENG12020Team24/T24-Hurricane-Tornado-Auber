package com.mygdx.auber.Powerups;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.auber.Config;
import com.mygdx.auber.entities.Player;

public class SpeedUp extends PowerUp {
    /** Used to store the current amount of time this PowerUp has been
     * active for.
     */
    private float timer = 0;

    public SpeedUp(Vector2 position) {

        super(position);
        r = Config.DEFAULT_SPEEDUP_RED;
        g = Config.DEFAULT_SPEEDUP_GREEN;
        b = Config.DEFAULT_SPEEDUP_BLUE;
    }

    /**
     * Used to update the status of this powerup every frame.
     * @param Player The Player whose position will be used for collision
     * and who the powerup will be applied to.
     */
    @Override
    public void update(final Player player) {
        if (playerCollision(player.getX(), player.getY(), player.getWidth(),
            player.getHeight())) {
            taken = true;
            r = Config.COLLISION_SPEEDUP_RED;
            g = Config.COLLISION_SPEEDUP_GREEN;
            b = Config.COLLISION_SPEEDUP_BLUE;
        }
        if (taken && !used) {
            position.x = player.getX();
            position.y = player.getY();
            timer += Gdx.graphics.getDeltaTime();

            if (timer < Config.SPEEDUP_TIME) {
                player.speedUp(true);
            } else {
                player.speedUp(false);
                used = true;
            }
        }
    }

    /**
     * Calls the superclass render method with the specified colour.
     * @param shapeRenderer The ShapeRenderer used to draw the powerup.
     */
    public void render(final ShapeRenderer shapeRenderer) {
        super.render(shapeRenderer);
    }
}
