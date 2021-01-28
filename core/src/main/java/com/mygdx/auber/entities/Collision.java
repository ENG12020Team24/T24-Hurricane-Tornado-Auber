package com.mygdx.auber.entities;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

class Collision {

    public boolean collideX, collideY;

    /**
     * Scans the blocks directly right of the sprite, for the height of the sprite,
     * and returns a bool based on if they are blocked or not
     *
     * @param sprite         A Sprite object of the character
     * @param collisionLayer Collision layer in the Tiled map
     * @return True if there is a on the right of the sprite that contains
     *         "blocked", else returns false
     */
    public boolean collidesRight(Sprite sprite, TiledMapTileLayer collisionLayer) {
        // Iterate across the amount of tiles tall the sprite is
        for (float step = collisionLayer.getTileHeight() / 2f; step < sprite
                .getHeight(); step += collisionLayer.getTileHeight() / 2f) {
            // Check if cell contains blocked property
            boolean collides = isCellBlocked(collisionLayer, sprite.getX() + sprite.getWidth(), sprite.getY() + step);
            if (collides)
                return true;
        }
        return false;
    }

    /**
     * Scans the blocks directly left of the sprite, for the height of the sprite,
     * and returns a bool based on if they are blocked or not
     *
     * @param sprite         A Sprite object of the character
     * @param collisionLayer Collision layer in the Tiled map
     * @return True if there is a on the left of the sprite that contains "blocked",
     *         else returns false
     */
    public boolean collidesLeft(Sprite sprite, TiledMapTileLayer collisionLayer) {
        // Iterate across the amount of tiles wide the sprite is
        for (float step = collisionLayer.getTileHeight() / 2f; step < sprite
                .getHeight(); step += collisionLayer.getTileHeight() / 2f) {
            // Check if cell contains blocked property
            boolean collides = isCellBlocked(collisionLayer, sprite.getX(), sprite.getY() + step);
            if (collides)
                return true;
        }
        return false;
    }

    /**
     * Scans the blocks directly top of the sprite, for the height of the sprite,
     * and returns a bool based on if they are blocked or not
     *
     * @param sprite         A Sprite object of the character
     * @param collisionLayer Collision layer in the Tiled map
     * @return True if there is a on the top of the sprite that contains "blocked",
     *         else returns false
     */
    public boolean collidesTop(Sprite sprite, TiledMapTileLayer collisionLayer) {
        // Iterate across the amount of tiles wide the sprite is
        for (float step = collisionLayer.getTileWidth() / 2f; step < sprite
                .getWidth(); step += collisionLayer.getTileWidth() / 2f) {
            // Check if cell contains blocked property
            boolean collides = isCellBlocked(collisionLayer, sprite.getX() + step, sprite.getY() + sprite.getHeight());
            if (collides)
                return true;
        }
        return false;
    }

    /**
     * Scans the blocks directly bottom of the sprite, for the height of the sprite,
     * and returns a bool based on if they are blocked or not
     *
     * @param sprite         A Sprite object of the character
     * @param collisionLayer Collision layer in the Tiled map
     * @return True if there is a on the bottom of the sprite that contains
     *         "blocked", else returns false
     */
    public boolean collidesBottom(Sprite sprite, TiledMapTileLayer collisionLayer) {
        // Iterate across the amount of tiles tall the sprite is
        for (float step = collisionLayer.getTileWidth() / 2f; step < sprite
                .getWidth(); step += collisionLayer.getTileWidth() / 2f) {
            // Check if cell contains blocked property
            boolean collides = isCellBlocked(collisionLayer, sprite.getX() + step, sprite.getY());
            if (collides)
                return true;
        }
        return false;
    }

    /**
     * "Checks a cell and returns a bool for whether the cell contains the key
     * "blocked" or not
     *
     * @param collisionLayer Collision layer in the Tiled map
     * @param x              X coordinate of the cell to check
     * @param y              Y coordinate of the cell to check
     * @return True if cell contains "blocked", else false
     */
    private boolean isCellBlocked(TiledMapTileLayer collisionLayer, float x, float y) {
        TiledMapTileLayer.Cell cell = collisionLayer.getCell((int) (x / collisionLayer.getTileWidth()),
                (int) (y / collisionLayer.getTileHeight())); // Set variable cell to the cell at specified x,y
                                                             // coordinate
        return cell != null && cell.getTile() != null && cell.getTile().getProperties().containsKey("blocked"); // If
                                                                                                                // cell
                                                                                                                // is
                                                                                                                // not
                                                                                                                // null,
                                                                                                                // and
                                                                                                                // the
                                                                                                                // cell
                                                                                                                // contains
                                                                                                                // "blocked",
                                                                                                                // return
                                                                                                                // true,
                                                                                                                // else
                                                                                                                // false
    }

    /**
     * Checks for collision in the direction of movement
     * 
     * @param sprite          Sprite being used by object
     * @param collisionLayers TiledMapTileLayer to search for tiles to collide with
     *                        on
     * @param velocity        Vector2 velocity of the object
     * @param collision       Collision object
     * @return Returns a vector with x/y changed to account for collision
     */
    public Vector2 checkForCollision(Sprite sprite, Array<TiledMapTileLayer> collisionLayers, Vector2 velocity,
            Collision collision) {
        float oldX = sprite.getX(), oldY = sprite.getY();
        collideX = false;
        collideY = false; // Recording the old x,y coords and setting the bool values to false

        for (TiledMapTileLayer collisionLayer : // Checks across all layers in the array of layers given as an argument
        collisionLayers) {
            // Move on x
            if (velocity.x < 0) {
                collideX = collision.collidesLeft(sprite, collisionLayer);
            } else if (velocity.x > 0) {
                collideX = collision.collidesRight(sprite, collisionLayer);
            }
            // React to x
            if (collideX) {
                sprite.setX(oldX);
                velocity.x = 0;
            }

            // Move on y
            if (velocity.y < 0) {
                collideY = collision.collidesBottom(sprite, collisionLayer);
            } else if (velocity.y > 0) {
                collideY = collision.collidesTop(sprite, collisionLayer);
            }
            // React to y
            if (collideY) {
                sprite.setY(oldY);
                velocity.y = 0;
            }
        }
        return velocity;
    }
}
