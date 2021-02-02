package com.mygdx.auber.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.mygdx.auber.Config;
import com.mygdx.auber.Scenes.Hud;
import com.mygdx.auber.Screens.PlayScreen;

public class Player extends Sprite implements InputProcessor {
    public Vector2 velocity = new Vector2(0, 0);

    private final Collision collision;
    public final Array<TiledMapTileLayer> collisionLayer;
    public static float x, y;
    public boolean demo;

    private float health;
    float SPEED = 1.3f;


    public static boolean canHeal = true;
    public static float healStopTime;

    private boolean isWHeld;
    private boolean isAHeld;
    private boolean isSHeld;
    private boolean isDHeld;
    private boolean usingSpeedPowerUp;
    private boolean usingArrestPowerUp;
    private static boolean isUsingShield = false;
    private static boolean isUsingFreeze = false;
    private static boolean isUsingHighlight = false;

    private float alpha = 0;
    private float arrestRadius;
    private float screenx, screeny;
    Sprite arrow;

    private Vector2 infirmaryPosition = new Vector2();
    public Array<Vector2> teleporters = new Array<>();

    public Player(Sprite sprite, Array<TiledMapTileLayer> collisionLayer, boolean demo) {
        super(sprite);
        this.collisionLayer = collisionLayer;
        this.collision = new Collision();
        this.demo = demo;
        this.arrow = new Sprite(new Texture("arrow.png"));
        arrow.setOrigin(arrow.getWidth() / 2, 0);

        if (demo) {
            this.setAlpha(0.01f);
        }
        health = 100f;
    }

    /**
     * Used to draw the player to the screen
     * 
     * @param batch Batch for the player to be drawn in
     */
    public void draw(Batch batch) {
        super.draw(batch);
    }

    /**
     * Draws arrows pointing in the direction of key systems being destroyed
     * 
     * @param batch Batch for the arrow to be rendered in
     */
    public void drawArrow(Batch batch) {
        for (KeySystem keySystem : KeySystemManager.getBeingDestroyedKeySystems()) {

            Vector2 position = new Vector2(this.getX(), this.getY());
            double angle = Math.atan((keySystem.position.x - position.x) / (keySystem.position.y - position.y));
            ;

            angle = Math.toDegrees(angle);

            if (this.getY() > keySystem.position.y) {
                angle = angle - 180;
            }

            arrow.setRotation((float) -angle);
            arrow.setPosition(this.getX() + this.getWidth() / 2 - arrow.getWidth() / 2,
                    this.getY() + this.getHeight() / 2);
            arrow.draw(batch);
        }
    }

    /**
     * Draws the arrest radius for Auber
     * 
     * @param shapeRenderer Shape renderer to be used for drawing shapes
     */
    public void drawCircle(ShapeRenderer shapeRenderer) {
        if (Gdx.input.getX() != screenx || Gdx.input.getY() != screeny || this.getX() != x || this.getY() != y) {
            alpha += 0.01;
        } else {
            alpha -= .01f;
        } // If the player is moving, fade in the circle, else fade out

        screeny = Gdx.input.getY();
        screenx = Gdx.input.getX();

        alpha = Math.max(0, Math.min(.3f, alpha)); // Clamp the alpha between 0 and .3

        Gdx.gl.glLineWidth(3f);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(.2f, .2f, .2f, alpha);
        shapeRenderer.circle(this.getX() + this.getWidth() / 2, this.getY() + this.getHeight() / 2, arrestRadius, 900);
        shapeRenderer.end(); // Rendering the circle
    }

    /**
     * Finds the location of the infirmary on the map
     */
    public void findInfirmary(TiledMapTileLayer tileLayer) {
        for (int i = 0; i < tileLayer.getWidth(); i++) {
            for (int j = 0; j < tileLayer.getHeight(); j++) // Scans every tile
            {
                int x = (i * tileLayer.getTileWidth()) + tileLayer.getTileWidth() / 2;
                int y = (j * tileLayer.getTileHeight()) + tileLayer.getTileHeight() / 2; // x,y coord of the centre of
                                                                                         // the tile
                TiledMapTileLayer.Cell cell = tileLayer.getCell(i, j); // Returns the cell at the x,y coord
                if (cell != null && cell.getTile() != null && cell.getTile().getProperties().containsKey("healer")) {
                    // If matches key, and is not null
                    infirmaryPosition.x = x;
                    infirmaryPosition.y = y;
                }
            }
        }

    }

    /**
     * Used to update the player, move in direction, change scale, and check for
     * collision
     */
    public void update(float delta) {
        if (demo) {
            heal();
        }

        velocity.x = 0;
        velocity.y = 0;
        Player.x = getX();
        Player.y = getY(); // Set the velocity to 0 and set the current x/y to x and y

        if (!canHeal) {
            healStopTime += delta;
        } // If cant heal, add time to healStopTime
        if (healStopTime >= 15) {
            healStopTime = 0;
            canHeal = true;
        } // After 15 seconds the player can heal again

        if (isWHeld) {
            velocity.y += 1;
        }
        if (isSHeld) {
            velocity.y -= 1;
        } // Add or subtract speed from the y velocity depending on which key is held (if
          // both held velocity.y = 0)
        if (isAHeld) {
            velocity.x -= 1;
            this.setScale(-1, 1);
        }
        if (isDHeld) {
            velocity.x += 1;
            this.setScale(1, 1);
        } // Add or subtract speed from the x velocity depending on which key is held (if
          // both held velocity.x = 0) and set the scale to flip the sprite depending on
          // movement
          // We use velocity as 1 in order to be able to calculate the correct angle.
        double angle = Math.atan2(velocity.y, velocity.x);

        velocity = collision.checkForCollision(this, collisionLayer, velocity, collision); // Checks for collision in
                                                                                           // the direction of movement

        if (Vector2.dst(this.getX(), this.getY(), infirmaryPosition.x, infirmaryPosition.y) < 100 && canHeal) {
            heal(1);
        }

        float speed;
        if (!usingSpeedPowerUp) {
            speed = Config.NORMAL_PLAYER_SPEED;
        } else {
            speed = Config.FAST_PLAYER_SPEED;
        }

        if (!usingArrestPowerUp) {
            arrestRadius = Config.NORMAL_ARREST_RANGE;
        } else {
            arrestRadius = Config.EXTEND_ARREST_RANGE;
        }

        if (isAHeld || isDHeld || isWHeld || isSHeld) {
            setX((float) (getX() + Math.cos(angle) * speed * Math.abs(velocity.x) * delta));
            setY((float) (getY() + Math.sin(angle) * speed * Math.abs(velocity.y) * delta)); // Set the player position to current position +
                                                                      // velocity
        }
        // Make sure there's an input so weird things don't happen, as atan2(0,0) is
        // undefined
    }

    /**
     * When a key is pressed, this method is called
     * 
     * @param keycode Code of key that was pressed
     * @return true if successful
     */
    @Override
    public boolean keyDown(int keycode) {
        if (demo) {
            return false;
        }
        switch (keycode) {
            case Input.Keys.W:
                isWHeld = true;
                break;
            case Input.Keys.A:
                isAHeld = true;
                break;
            case Input.Keys.D:
                isDHeld = true;
                break;
            case Input.Keys.S:
                isSHeld = true;
                break;
            case Input.Keys.SPACE:
                for (int i = 0; i < teleporters.size; i++) {
                    if (teleporters.get(i).dst(this.getX(), this.getY()) < 50) {
                        // System.out.println("Teleported");
                        this.teleport();
                        break;
                    }
                }
                break;
        } // If key is pressed, set isKeyHeld to true
        return true;
    }

    /**
     * When a key is lifted, this method is called
     * 
     * @param keycode Code of key that was lifted
     * @return true if successful
     */
    @Override
    public boolean keyUp(int keycode) {
        if (demo) {
            return false;
        }
        switch (keycode) {
            case Input.Keys.W:
                isWHeld = false;
                break;
            case Input.Keys.S:
                isSHeld = false;
                break;
            case Input.Keys.A:
                isAHeld = false;
                break;
            case Input.Keys.D:
                isDHeld = false;
                break;
        } // Set key lifted to false
        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    /**
     * Called when a mouse left click is clicked
     * 
     * @param screenX X Screen coordinate of mouse press
     * @param screenY Y Screen coordinate of mouse press
     * @param pointer
     * @param button
     * @return True if successful
     */
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (demo) {
            return false;
        }
        Vector3 vec = new Vector3(screenX, screenY, 0);
        PlayScreen.getCamera().unproject(vec);
        Vector2 point = new Vector2(vec.x, vec.y); // Gets the x,y coordinate of mouse press and converts it to world
                                                   // coordinates

        for (Infiltrator infiltrator : NPCCreator.infiltrators) {
            if (infiltrator.getBoundingRectangle().contains(point)) {
                if (Vector2.dst(this.getX(), this.getY(), infiltrator.getX(), infiltrator.getY()) < arrestRadius) {
                    NPCCreator.removeInfiltrator(this, infiltrator.index);
                    Hud.incrementArrestedInfiltrators();
                    return true;
                }
            }
        } // If an infiltrator was clicked, remove it from the list

        for (CrewMembers crewMember : NPCCreator.crew) {
            if (crewMember.getBoundingRectangle().contains(point)) {
                if (Vector2.dst(this.getX(), this.getY(), crewMember.getX(), crewMember.getY()) < arrestRadius) {
                    NPCCreator.removeCrewmember(crewMember.index);
                    Hud.incrementIncorrectArrests();
                    return true;
                }
            }
        } // If an crewmember was clicked, remove it from the list
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return true;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

    /**
     * Heal Auber for a certain amount
     * 
     * @param amount Amount to heal by
     */
    public void heal(int amount) {
        if (canHeal) {
            health += amount;
            if (health > 100) {
                health = 100;
            }
        } // If he can heal, add health
        else {

        } // If he cant heal, check if time has passed, if it has set canHeal to true and
          // heal for the amount
    }

    /**
     * Heal Auber for the full amount
     */
    public void heal() {
        if (canHeal) {
            health = 100;
        } // If can heal, heal
        else {
            if (System.currentTimeMillis() - healStopTime > 20 * 100) {
                canHeal = true;
                heal();
            }
        } // If he cant heal, check if time has passed, if it has set canHeal to true and
          // heal
    }

    /**
     * Take damage for amount given.
     * 
     * @param amount Amount of damage to deal.
     */
    public void takeDamage(float amount) {

        if(!isUsingShield){
            health -= amount;
        }
    }

    /**
     * Teleport player to the other teleporter. There are only 2 so player is
     * teleported to the furthest one.
     */
    public void teleport() {
        // System.out.println("x: " + getX() + ", y: " + getY());
        Vector2 furthestTeleporter = new Vector2();
        for (Vector2 teleporter : this.teleporters) {
            if (furthestTeleporter.equals(Vector2.Zero)) {
                furthestTeleporter.set(teleporter);
                continue;
            }
            Vector2 currentPosition = new Vector2(this.getX(), this.getY());
            if (currentPosition.dst2(teleporter) > currentPosition.dst2(furthestTeleporter)) {
                furthestTeleporter.set(teleporter);
            }
        }
        // System.out.println(furthestTeleporter);
        setX(furthestTeleporter.x);
        setY(furthestTeleporter.y);
    }

    /**
     * Get the location of the teleporters on the map.
     * 
     * @param tileLayer Tile map layer containing the teleporters.
     * @return Array of the positions of teleporters.
     */
    public static Array<Vector2> getTeleporterLocations(TiledMapTileLayer tileLayer) {
        Array<Vector2> teleporters = new Array<>();

        for (int i = 0; i < tileLayer.getWidth(); i++) {
            // Scan every tile
            for (int j = 0; j < tileLayer.getHeight(); j++) {
                int x = (i * tileLayer.getTileWidth()) + tileLayer.getTileWidth() / 2;
                int y = (j * tileLayer.getTileHeight()) + tileLayer.getTileHeight() / 2; // x,y coord of the centre of
                                                                                         // the tile
                TiledMapTileLayer.Cell cell = tileLayer.getCell(i, j); // Returns the cell at the x,y coord
                if (cell != null && cell.getTile() != null && cell.getTile().getProperties().containsKey("teleporter")) // If
                                                                                                                        // ID
                                                                                                                        // matches
                                                                                                                        // teleporter
                                                                                                                        // tiles,
                                                                                                                        // and
                                                                                                                        // is
                                                                                                                        // not
                                                                                                                        // null
                {
                    Vector2 position = new Vector2(x, y);
                    teleporters.add(position);
                }
            }
        }
        return teleporters;
    }

    /**
     * @param inUse Whether the player is sped up by a powerup or not
     */
    public void speedUp(boolean inUse) {
        usingSpeedPowerUp = inUse;

    };

    public void shieldUp(boolean inUse){
        isUsingShield = inUse;
    }

    public void freezeUp(boolean inUse){
        isUsingFreeze = inUse;
    }

    public void highlightUp(boolean inUse){
        isUsingHighlight = inUse;
    }

    public static boolean getFreeze(){
        return isUsingFreeze;
    }

    public static boolean getHighlight(){
        return isUsingHighlight;
    }

    public float getHealth() {
        return health;
    }

    public void arrestUp(boolean inUse) {
        usingArrestPowerUp = inUse;
    };

    public void dispose() {
    }

    public Vector2 getPosition() {
        return new Vector2(x, y);
    }
}
