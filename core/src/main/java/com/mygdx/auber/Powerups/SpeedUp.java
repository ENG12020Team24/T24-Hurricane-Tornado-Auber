package com.mygdx.auber.Powerups;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.auber.entities.Player;

public class SpeedUp extends PowerUp {
    /** Used to store the current amount of time this PowerUp has been
     * active for.
     */
    public float timer = 0;

    /**
     * Constructor used to instantiate the class.
     * @param position A Vector2 containing the position of the PowerUp.
     */
    public SpeedUp(Vector2 position) {
        super(position);
    }

    /**
     * Used to update the status of this powerup every frame.
     * @param Player The Player whose position will be used for collision
     * and who the powerup will be applied to.
     */
    @Override
    public void update(Player player) {
        if (playerCollision(player.getX(), player.getY(), player.getWidth(),
            player.getHeight())) {
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
     * Calls the superclass render method with the specified colour.
     * @param shapeRenderer The ShapeRenderer used to draw the powerup.
     */
    public void render(ShapeRenderer shapeRenderer) {
        super.render(shapeRenderer, 0, 1, 0);
    }
}
