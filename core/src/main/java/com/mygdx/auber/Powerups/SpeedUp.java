package com.mygdx.auber.Powerups;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.auber.entities.Player;

public class SpeedUp extends PowerUp {

    public float timer = 0;

    public SpeedUp(Vector2 position) {

        super(1, position);
        r = 0;
        g = 0.5f;
        b = 0.4f;
    }

    @Override
    public void update(Player player) {

        if (playerCollision(player.getX(), player.getY(), player.getWidth(), player.getHeight())) {
            taken = true;
            b = 1f;
            g = 0.2f;
            r = 0.2f;
        }
        if (taken && !used) {
            position.x = player.getX();
            position.y = player.getY();
            timer += Gdx.graphics.getDeltaTime();

            if (timer < 10) {
                player.speedUp(true);
            } else {
                player.speedUp(false);
                used = true;
            }
        }
    }

    public void render(ShapeRenderer shapeRenderer) {
        super.render(shapeRenderer);
    }
}
