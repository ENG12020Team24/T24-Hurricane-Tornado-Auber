package com.mygdx.auber.Powerups;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.auber.entities.Player;
import com.mygdx.auber.Config;

public abstract class PowerUp extends Sprite {
    /** The position of this powerup within the game world. */
    protected Vector2 position = new Vector2(0, 0);
    /** Used to store if the user has picked up this PowerUp. */
    protected boolean taken;
    /** Used to store if the user has finished using this PowerUp. */
    protected boolean used;

    /** The red component of this powerup's colour. */
    protected float r;
    /** The green component of this powerup's colour. */
    protected float g;
    /** The blue component of this powerup's colour. */
    protected float b;

    /**
     * Constructor used to instantiate the class.
     * @param position A Vector2 containing the position of the PowerUp.
     */
    public PowerUp(final Vector2 position) {
        this.position = position;
        this.taken = false;
        this.used = false;
    }

    /**
     * An abstract method used to update the PowerUp every frame.
     * @param player The Player, used to update the PowerUp.
     */
    public abstract void update(Player player);

    /**
     * Determines if the player is touching this powerup.
     * @param playerX The x-coordinate of the player.
     * @param playerY The y-coordinate of the player.
     * @param playerWidth The width of the player.
     * @param playerHeight The height of the player.
     * @return True if the player is touching this, false otherwise
     */
    public boolean playerCollision(final float playerX, final float playerY,
        final float playerWidth, final float playerHeight) {
        if (playerX >= position.x + Config.POWERUP_DIAMETER
            || playerX + playerWidth <= position.x) {
            return false;
        }

        if (playerY >= position.y + Config.POWERUP_DIAMETER
            || playerY + playerHeight <= position.y) {
            return false;
        }
        return true;
    }

    /**
     * Draws this powerup using the specified shapeRenderer.
     * @param shapeRenderer The ShapeRenderer used to draw the powerup.
     */
    public void render(ShapeRenderer shapeRenderer) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(r, g, b, 1);
        shapeRenderer.circle(position.x + Config.POWERUP_DIAMETER / 2,
            position.y + Config.POWERUP_DIAMETER / 2,
            Config.POWERUP_DIAMETER / 2);
        shapeRenderer.end();
    }

    /**
     * Used to determine if the powerup has been used and is no longer
     * affecting the player.
     * @return A boolean containing true if the powerup has been used and is no
     * longer affecting the player, false otherwise.
     */
    public boolean isUsed() {
        return used;
    }
}
