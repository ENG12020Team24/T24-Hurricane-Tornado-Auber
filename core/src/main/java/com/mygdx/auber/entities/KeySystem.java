package com.mygdx.auber.entities;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.auber.Config;

public class KeySystem {
    /** The name of this key system. */
    private String name;
    /** The cell this key system is located on. */
    private final TiledMapTileLayer.Cell cell;
    /** The time that the system's destruction started at. */
    private Long destructionStartTime;
    /** The position of this key system. */
    private Vector2 position;

    /**
     * Class constructor.
     * @param newCell The cell this system is located on.
     * @param newName The name of this system.
     * @param newPosition The position of this system.
     */
    public KeySystem(final TiledMapTileLayer.Cell newCell,
        final String newName, final Vector2 newPosition) {
        this.cell = newCell;
        this.name = newName;
        this.position = newPosition;
        //System.out.println(position);
    }

    /**
     * Called when a system begins to be destroyed.
     */
    public void startDestroy() {
        destructionStartTime = System.currentTimeMillis();
    }

    /**
     * Called when an Infiltrator stops destroying a system.
     */
    public void stopDestroy() {
        if (!isDestroyed()) {
            destructionStartTime = null;
        }
    }

    /**
     * Calculates time remaining for the system to be destroyed. Note: System is
     * destroyed in 60 seconds.
     * @return Null if system isn't being/hasn't been destroyed. Otherwise,
     * time remaining in milliseconds.
     */
    public Long timeRemaining() {
        if (destructionStartTime == null) {
            // System isn't being destroyed
            return null;
        }
        long timeElapsed = System.currentTimeMillis() - destructionStartTime;
        if (timeElapsed <= Config.SYSTEM_DESTRUCTION_TIME) {
            // System is being destroyed. Less than 60 seconds remaining.
            /*if (timeElapsed == Math.ceil(timeElapsed)) {
                Player.takeDamage(0.005f);
            } // Deals damage whilst the key system is being destroyed*/
            // unneeded due to badly writted check
            return timeElapsed;
        }
        // System has been destroyed
        return null;
    }

    // timeRemaining == null implies that the system is not currently being
    // destroyed.
    // destructionStartTime == null implies that the system is not being
    // destroyed.

    /**
     * @return True if the system has not been destroyed and is not currently
     * being destroyed. False otherwise.
     */
    public boolean isSafe() {
        return timeRemaining() == null && destructionStartTime == null;
    }

    /**
     * @return True if the system is currently being destroyed, but has not been
     *         destroyed yet. False otherwise.
     */
    public boolean isBeingDestroyed() {
        return timeRemaining() != null;
    }

    /**
     * @return True if the system has been destroyed, false otherwise
     */
    public boolean isDestroyed() {
        return timeRemaining() == null && destructionStartTime != null;
    }

    /** Gets the position of this KeySystem.
     * @return A Vector2 containing this KeySystem's position.
     */
    public Vector2 getPosition() {
        return position;
    }
}
