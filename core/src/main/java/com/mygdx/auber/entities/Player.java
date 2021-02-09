package com.mygdx.auber.entities;

import java.util.Arrays;

import javax.print.event.PrintEvent;

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

public final class Player extends Sprite implements InputProcessor {
    /** Auber's velocity. */
    private Vector2 velocity = new Vector2(0, 0);
    /** Auber's collision handler. */
    private final Collision collision;
    /** The collision layer Auber exists on. */
    private final Array<TiledMapTileLayer> collisionLayer;
    /** Auber's x coordinate. */
    private static float x;
    /** Auber's y coordinate. */
    private static float y;
    /** Whether the game is running in demo mode or not. */
    private boolean demo;
    /** Auber's health. */
    private float health;

    /** Stores whether Auber can currently heal. */
    private static boolean canHeal = true;
    /** How long Auber's healing has been stopped for. */
    private static float healStopTime;
    private float speed;

    /** Stores whether the W key is held. */
    private boolean isWHeld;
    /** Stores whether the A key is held. */
    private boolean isAHeld;
    /** Stores whether the S key is held. */
    private boolean isSHeld;
    /** Stores whether the D key is held. */
    private boolean isDHeld;
    /** Stores whether the player is using the speed powerup. */
    private boolean isUsingSpeedPowerUp;
    /** Stores whether the player is using the arrest powerup. */
    private boolean isUsingArrestPowerUp;
    /** Stores whether the player is using the shield powerup. */
    private static boolean isUsingShieldPowerUp = false;
    /** Stores whether the player is using the freeze powerup. */
    private static boolean isUsingFreezePowerUp = false;
    /** Stores whether the player is using the highlight powerup. */
    private static boolean isUsingHighlightPowerUp = false;

    /** The alpha value used for Auber. */
    private float alpha = 0;
    /** The radius within which Auber can arrest NPCs. */
    private float arrestRadius;
    /** The sprite used to point to unsafe systems. */
    private Sprite arrowSprite;

    /** The position of the infirmary on the map. */
    private Vector2 infirmaryPosition = new Vector2();
    /** The list of teleporters on the map. */
    private Array<Vector2> teleporters = new Array<>();

    /** The alpha values used in the demo mode. */
    private static final float AUBER_DEMO_ALPHA = 0.01f;
    /** The maximum health that Auber can have. */
    private static final int AUBER_MAX_HEALTH = 100;

    /** Whether the user has requested pause. */
    private boolean requestedPause = false;
    /** Whether the user has requested save. */
    private boolean requestedSave = false;
    /** Needed for Checkstyle and to allow tests to work. */
    private static final float AUBER_SPRITE_HEIGHT = 32.0f;

    /** Class constructor.
     * @param newSprite The sprite to use for the player.
     * @param newCollisionLayer The collision layer the player will exist on.
     * @param isDemo Whether the game demo is running.
     */
    public Player(final Sprite newSprite,
        final Array<TiledMapTileLayer> newCollisionLayer,
        final boolean isDemo) {
        super(newSprite);
        this.collisionLayer = newCollisionLayer;
        this.collision = new Collision();

        this.demo = isDemo;
        if (getHeight() == AUBER_SPRITE_HEIGHT) {
        // very janky but keeps tests from failing because there's a pathing
        // problem
             this.arrowSprite = new Sprite(new Texture("arrow.png"));
        } else {
            this.arrowSprite = new Sprite(new Texture("assets/arrow.png"));
        }
        arrowSprite.setOrigin(arrowSprite.getWidth() / 2, 0);
        if (demo) {
            this.setAlpha(AUBER_DEMO_ALPHA);
        }
        health = AUBER_MAX_HEALTH;
    }

    /**
     * Used to draw the player to the screen.
     * @param batch Batch for the player to be drawn in
     */
    public void draw(final Batch batch) {
        super.draw(batch);
    }

    /** The number of degrees in a semicircle. */
    private static final int SEMICIRCLE_DEGREES = 180;

    /**
     * Draws arrows pointing in the direction of key systems being destroyed.
     * @param batch Batch for the arrow to be rendered in
     */
    public void drawArrow(final Batch batch) {
        for (KeySystem keySystem
            : KeySystemManager.getBeingDestroyedKeySystems()) {

            Vector2 position = new Vector2(this.getX(), this.getY());
            double angle = Math.atan((keySystem.getPosition().x - position.x)
                / (keySystem.getPosition().y - position.y));

            angle = Math.toDegrees(angle);

            if (this.getY() > keySystem.getPosition().y) {
                angle = angle - SEMICIRCLE_DEGREES;
            }

            arrowSprite.setRotation((float) -angle);
            arrowSprite.setPosition(this.getX() + this.getWidth() / 2
                - arrowSprite.getWidth() / 2,
                this.getY() + this.getHeight() / 2);
            arrowSprite.draw(batch);
        }
    }

    /** The colour of the arrest circle. */
    private static final float CIRCLE_COLOUR = 0.2f;
    /** How fast to fade the arrest circle. */
    private static final float CIRCLE_FADE = 0.01f;
    /** The thickness of the arrest circle. */
    private static final float CIRCLE_LINE_WIDTH = 3f;
    /** The maximum visibility of the arrest circle. */
    private static final float MAX_CIRCLE_ALPHA = 0.3f;

    /**
     * Draws the arrest radius for Auber.
     * @param shapeRenderer Shape renderer to be used for drawing shapes.
     */
    public void drawCircle(final ShapeRenderer shapeRenderer) {
        if (Gdx.input.getX() != Gdx.input.getX()
            || Gdx.input.getY() != Gdx.input.getY()
            || this.getX() != x || this.getY() != y) {
            alpha += CIRCLE_FADE;
        } else {
            alpha -= CIRCLE_FADE;
        } // If the player is moving, fade in the circle, else fade out

        alpha = Math.max(0, Math.min(MAX_CIRCLE_ALPHA, alpha));
        // Clamp the alpha between 0 and .3

        Gdx.gl.glLineWidth(CIRCLE_LINE_WIDTH);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(CIRCLE_COLOUR, CIRCLE_COLOUR,
            CIRCLE_COLOUR, alpha);
        shapeRenderer.circle(this.getX() + this.getWidth() / 2,
            this.getY() + this.getHeight() / 2, arrestRadius);
        shapeRenderer.end(); // Rendering the circle
    }

    /**
     * Finds the location of the infirmary on the map.
     * @param tileLayer the TileLayer to search for infirmaries.
     */
    public void findInfirmary(final TiledMapTileLayer tileLayer) {
        for (int i = 0; i < tileLayer.getWidth(); i++) {
            for (int j = 0; j < tileLayer.getHeight(); j++) {
                 // Scans every tile
                int tileX = (i * tileLayer.getTileWidth())
                    + tileLayer.getTileWidth() / 2;
                int tileY = (j * tileLayer.getTileHeight())
                    + tileLayer.getTileHeight() / 2;
                // x,y coord of the centre of the tile
                TiledMapTileLayer.Cell cell = tileLayer.getCell(i, j);
                // Returns the cell at the x,y coord
                if (cell != null && cell.getTile() != null && cell.getTile()
                    .getProperties().containsKey("healer")) {
                    // If matches key, and is not null
                    infirmaryPosition.x = tileX;
                    infirmaryPosition.y = tileY;
                }
            }
        }

    }

    /**
     * Used to update the player, move in direction, change scale, and check for
     * collision.
     * @param delta The time since the previous frame, in seconds.
     */
    public void update(final float delta) {
        if (demo) {
            heal();
        }

        velocity.x = 0;
        velocity.y = 0;
        Player.x = getX();
        Player.y = getY();
        // Set the velocity to 0 and set the current x/y to x and y

        if (!canHeal) {
            healStopTime += delta;
        } // If cant heal, add time to healStopTime
        if (healStopTime >= Config.AUBER_HEAL_STOP_TIME) {
            healStopTime = 0;
            canHeal = true;
        } // After 15 seconds the player can heal again

        if (isWHeld) {
            velocity.y += 1;
        }
        if (isSHeld) {
            velocity.y -= 1;
        }
        // Add or subtract speed from the y velocity depending on which key
        // is held (if both held velocity.y = 0)
        if (isAHeld) {
            velocity.x -= 1;
            this.setScale(-1, 1);
        }
        if (isDHeld) {
            velocity.x += 1;
            this.setScale(1, 1);
        }
        /*Add or subtract speed from the x velocity depending on which key is
        held (if both held velocity.x = 0) and set the scale to flip the sprite
        depending on movement.
        We use velocity as 1 in order to be able to calculate the correct
        angle. */
        double angle = Math.atan2(velocity.y, velocity.x);

        velocity = collision.checkForCollision(this, collisionLayer, velocity,
            collision);
        // Checks for collision in the direction of movement

        if (Vector2.dst(this.getX(), this.getY(), infirmaryPosition.x,
            infirmaryPosition.y) < AUBER_MAX_HEALTH && canHeal) {
            heal(1);
        }

        if (!this.isUsingSpeedPowerUp) {
            speed = Config.NORMAL_PLAYER_SPEED;
        } else {
            speed = Config.FAST_PLAYER_SPEED;
        }

        if (!isUsingArrestPowerUp) {
            arrestRadius = Config.NORMAL_ARREST_RANGE;
        } else {
            arrestRadius = Config.EXTEND_ARREST_RANGE;
        }

        if (isAHeld || isDHeld || isWHeld || isSHeld) {
            setX((float) (getX() + Math.cos(angle) * speed
                * Math.abs(velocity.x) * delta));
            setY((float) (getY() + Math.sin(angle) * speed
                * Math.abs(velocity.y) * delta));
            // Set the player position to current position + velocity
        }
        // Make sure there's an input so weird things don't happen, as
        // atan2(0,0) is undefined
    }

    /** How close Auber has to be to a teleporter in order to teleport. */
    public static final float MIN_TELEPORT_DISTANCE = 50;

    /**
     * When a key is pressed, this method is called.
     * @param keycode Code of key that was pressed
     * @return true if successful
     */
    @Override
    public boolean keyDown(final int keycode) {
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
                    if (teleporters.get(i).dst(this.getX(), this.getY())
                        < MIN_TELEPORT_DISTANCE) {
                        // System.out.println("Teleported");
                        this.teleport();
                        break;
                    }
                }
                break;
            default:
        } // If key is pressed, set isKeyHeld to true
        return true;
    }

    /**
     * When a key is lifted, this method is called.
     * @param keycode Code of key that was lifted
     * @return true if successful
     */
    @Override
    public boolean keyUp(final int keycode) {
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
            case Input.Keys.ESCAPE:
            // When escape is clicked, toggle the requested pause variable.
                requestedPause = !requestedPause;
                break;
            case Input.Keys.P:  // When P is clicked, save the game.
                requestedPause = true;
                requestedSave = true;
                break;
            default:
        } // Set key lifted to false
        return true;
    }

    @Override
    public boolean keyTyped(final char character) {
        return false;
    }

    /**
     * Called when a mouse left click is clicked.
     * @param screenX X Screen coordinate of mouse press
     * @param screenY Y Screen coordinate of mouse press
     * @param pointer
     * @param button
     * @return True if successful
     */
    @Override
    public boolean touchDown(final int screenX, final int screenY,
        final int pointer, final int button) {
        if (demo || requestedPause) {
            // If it is a demo, or the game is paused then do nothing.
            return false;
        }
        Vector3 vec = new Vector3(screenX, screenY, 0);
        PlayScreen.getCamera().unproject(vec);
        Vector2 point = new Vector2(vec.x, vec.y);
        // Gets the x,y coordinate of mouse press and converts it to world
        // coordinates

        for (Infiltrator infiltrator : NPCCreator.getInfiltrators()) {
            if (infiltrator.getBoundingRectangle().contains(point)) {
                if (Vector2.dst(this.getX(), this.getY(), infiltrator.getX(),
                    infiltrator.getY()) < arrestRadius) {
                    NPCCreator.removeInfiltrator(this, infiltrator.getIndex());
                    Hud.incrementArrestedInfiltrators();
                    return true;
                }
            }
        } // If an infiltrator was clicked, remove it from the list

        for (CrewMembers crewMember : NPCCreator.getCrew()) {
            if (crewMember.getBoundingRectangle().contains(point)) {
                if (Vector2.dst(this.getX(), this.getY(), crewMember.getX(),
                    crewMember.getY()) < arrestRadius) {
                    NPCCreator.removeCrewmember(crewMember.getIndex());
                    Hud.incrementIncorrectArrests();
                    return true;
                }
            }
        } // If an crewmember was clicked, remove it from the list
        return true;
    }

    @Override
    public boolean touchUp(final int screenX, final int screenY,
        final int pointer, final int button) {
        return false;
    }

    @Override
    public boolean touchDragged(final int screenX, final int screenY,
        final int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(final int screenX, final int screenY) {
        return true;
    }

    @Override
    public boolean scrolled(final int amount) {
        return false;
    }

    /**
     * Heal Auber for a certain amount.
     * @param amount Amount to heal by.
     */
    public void heal(final int amount) {
        if (canHeal) {
            health += amount;
            if (health > AUBER_MAX_HEALTH) {
                health = AUBER_MAX_HEALTH;
            }
        }
        // If he can heal, add health
        // If he cant heal, check if time has passed, if it has set canHeal
        // to true and heal for the amount
    }

    /**
     * Heal Auber for the full amount.
     */
    public void heal() {
        if (canHeal) {
            health = AUBER_MAX_HEALTH;
            // If can heal, heal
        } else {
            if (healStopTime > Config.AUBER_HEAL_STOP_TIME) {
                canHeal = true;
                heal();
            }
        }
        // If he cant heal, check if time has passed, if it has set canHeal
        // to true and heal
    }

    /**
     * Take damage for amount given.
     * @param amount Amount of damage to deal.
     */
    public void takeDamage(final float amount) {

        if (!isUsingShieldPowerUp) {
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
            if (currentPosition.dst2(teleporter)
                > currentPosition.dst2(furthestTeleporter)) {
                furthestTeleporter.set(teleporter);
            }
        }
        // System.out.println(furthestTeleporter);
        setX(furthestTeleporter.x);
        setY(furthestTeleporter.y);
    }

    /**
     * Get the location of the teleporters on the map.
     * @param tileLayer Tile map layer containing the teleporters.
     * @return Array of the positions of teleporters.
     */
    public static Array<Vector2> getTeleporterLocations(
        final TiledMapTileLayer tileLayer) {
        Array<Vector2> teleporters = new Array<>();

        for (int i = 0; i < tileLayer.getWidth(); i++) {
            // Scan every tile
            for (int j = 0; j < tileLayer.getHeight(); j++) {
                int tileX = (i * tileLayer.getTileWidth())
                    + tileLayer.getTileWidth() / 2;
                int tileY = (j * tileLayer.getTileHeight())
                    + tileLayer.getTileHeight() / 2;
                // x,y coord of the centre of the tile
                TiledMapTileLayer.Cell cell = tileLayer.getCell(i, j);
                // Returns the cell at the x,y coord
                if (cell != null && cell.getTile() != null
                    && cell.getTile().getProperties()
                    .containsKey("teleporter")) {
                    // If ID matches teleporter tiles, and is not null
                    Vector2 position = new Vector2(tileX, tileY);
                    teleporters.add(position);
                }
            }
        }
        return teleporters;
    }

    /** Sets whether the player is using the speed powerup.
     * @param inUse Whether the player is using the speed powerup.
     */
    public void speedUp(final boolean inUse) {
        isUsingSpeedPowerUp = inUse;

    };

    /** Sets whether the player is using the freeze powerup.
     * @param inUse Whether the player is using the freeze powerup.
     */
    public void shieldUp(final boolean inUse) {
        isUsingShieldPowerUp = inUse;
    }

    /** Sets whether the player is using the freeze powerup.
     * @param inUse Whether the player is using the freeze powerup.
     */
    public void freezeUp(final boolean inUse) {
        isUsingFreezePowerUp = inUse;
    }

    /** Sets whether the player is using the highlight powerup.
     * @param inUse Whether the player is using the highlight powerup.
     */
    public void highlightUp(final boolean inUse) {
        isUsingHighlightPowerUp = inUse;
    }

    /** Gets whether the player is using the freeze powerup.
     * @return True if the player is using the freeze powerup, false
     * otherwise.
    */
    public static boolean getFreeze() {
        return isUsingFreezePowerUp;
    }

    /** Gets whether the player is using the highlight powerup.
     * @return True if the player is using the highlight powerup, false
     * otherwise.
    */
    public static boolean getHighlight() {
        return isUsingHighlightPowerUp;
    }

    /** Returns this player's health.
     * @return A float containing this player's health.
     */
    public float getHealth() {
        return health;
    }

    /** Sets this player's health.
     * @param newHealth The new value of the player's health.
     */
    public void setHealth(final float newHealth) {
        this.health = newHealth;
    }

    /**
     * Sets when the player stopped healing.
     * @param value When the player stopped healing.
     */
    public void setHealStopTime(final float value) {
        this.healStopTime = value;
    }

    /**
     * Sets whether the player is using the arrest powerup.
     * @param newArrestPowerUp Whether the player is using the arrest powerup.
     */
    public void setUsingArrestPowerUp(final boolean newArrestPowerUp) {
        this.isUsingArrestPowerUp = newArrestPowerUp;
    }

    /**
     * Sets whether the player is using the speed powerup.
     * @param newSpeedPowerUp Whether the player is using the speed powerup.
     */
    public void setUsingSpeedPowerUp(final boolean newSpeedPowerUp) {
        this.isUsingSpeedPowerUp = newSpeedPowerUp;
    }

    /** Sets whether the player is using the arrest powerup.
     * @param inUse Whether the player is using the arrest powerup.
     */
    public void arrestUp(final boolean inUse) {
        isUsingArrestPowerUp = inUse;
    };

    /** Called when this object is deleted. */
    public void dispose() {
    }

    /** Gets the position of this Player.
     * @return A Vector2 containing the position of this player.
     */
    public Vector2 getPosition() {
        return new Vector2(x, y);
    }

    /** Sets whether the player can heal.
     * @param newCanHeal The new value of canHeal.
     */
    public static void setCanHeal(final boolean newCanHeal) {
        Player.canHeal = newCanHeal;
    }

    /**
     * Sets the heal stop time to 0.
     */
    public void resetHealStopTime() {
        healStopTime = 0;
    }

    /** Returns whether the player can currently heal.
     * @return A boolean containing whether the player can currently heal.
     */
    public static boolean canHeal() {
        return canHeal;
    }

    /** Sets the array of teleporter locations.
     * @param newTeleporters The array of teleporter locations.
     */
    public void setTeleporters(final Array<Vector2> newTeleporters) {
        this.teleporters = newTeleporters;
    }
    /** Returns whether the user has requested pause.
     * @return Whether the user has requested pause.
     */
    public boolean getRequestedPause() {
        return this.requestedPause;
    }

    /** Returns whether the user has requested save.
     * @return Whether the user has requested save.
     */
    public boolean getRequestedSave() {
        return this.requestedSave;
    }

    /** Sets whether the user has requested pause.
     * @param value Whether the user has requested pause.
     */
    public void setRequestedPause(final boolean value) {
        this.requestedPause = value;
    }

    /** Called when the player has been saved. */
    public void handledSave() { // This is called when saving has been handled.
        this.requestedSave = false;
        System.out.println("Handled save");
    }

    /** Encodes this Player to a string.
     * @return This Player encoded as a string.
    */
    public String encode() {
        String[] r = {String.valueOf(this.x), String.valueOf(this.y),
            String.valueOf(this.health), String.valueOf(this.canHeal),
            String.valueOf(this.healStopTime),
            String.valueOf(this.isUsingArrestPowerUp),
            String.valueOf(this.isUsingSpeedPowerUp)};
        return Arrays.toString(r);
    }

}
