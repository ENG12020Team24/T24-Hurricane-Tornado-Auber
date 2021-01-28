package com.mygdx.auber.Powerups;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.auber.entities.Player;
import com.mygdx.auber.Config;

public abstract class PowerUp extends Sprite {
    public Vector2 position = new Vector2(0, 0);
    public static int type;
    public boolean taken;
    public boolean used;

    public PowerUp(int type, Vector2 position) {
        this.type = type;
        this.position = position;
        this.taken = false;
        this.used = false;
    }

    public abstract void update(Player player);

    public boolean playerCollision(float playerX, float playerY, float playerWidth, float playerHeight) {
        if (playerX >= position.x + Config.POWERUP_DIAMETER || playerX + playerWidth <= position.x) {
            return false;
        }

        if (playerY >= position.y + Config.POWERUP_DIAMETER || playerY + playerHeight <= position.y) {
            return false;
        }
        return true;
    }

    public void render(ShapeRenderer shapeRenderer, int r, int g, int b) {

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(r, g, b, 1);
        shapeRenderer.circle(position.x + Config.POWERUP_DIAMETER / 2, position.y + Config.POWERUP_DIAMETER / 2,
                Config.POWERUP_DIAMETER / 2);
        shapeRenderer.end();

    }
}
