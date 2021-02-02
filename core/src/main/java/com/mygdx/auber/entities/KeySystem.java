package com.mygdx.auber.entities;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;

public class KeySystem {
    public String name;
    final TiledMapTileLayer.Cell cell;
    private Long destructionStartTime;
    public static float destructionTime = 30000; // milliseconds
    public Vector2 position;

    public KeySystem(TiledMapTileLayer.Cell cell, String name, Vector2 position) {
        this.cell = cell;
        this.name = name;
        this.position = position;
    }

    /**
     * Called when a system begins to be destroyed
     */
    public void startDestroy() {
        destructionStartTime = System.currentTimeMillis();
    }

    /**
     * Called when an Infiltrator stops destroying a system
     */
    public void stopDestroy() {
        if (!isDestroyed()) {
            destructionStartTime = null;
        }
    }

    /**
     * Calculates time remaining for the system to be destroyed. Note: System is
     * destroyed in 60 seconds.
     * 
     * @return Null if system isn't being/hasn't been destroyed. Time remaining in
     *         milliseconds.
     */
    public Long timeRemaining() {
        if (destructionStartTime == null) {
            // System isn't being destroyed
            return null;
        }
        long timeElapsed = System.currentTimeMillis() - destructionStartTime;
        if (timeElapsed <= destructionTime) {
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
    // destructionStartTime == null implies that the system is not being destroyed.

    /**
     * 
     * @return True if the system has not been destroyed and is not currently being
     *         destroyed. False otherwise.
     */
    public boolean isSafe() {
        return timeRemaining() == null && destructionStartTime == null;
    }

    /**
     * 
     * @return True if the system is currently being destroyed, but has not been
     *         destroyed yet. False otherwise.
     */
    public boolean isBeingDestroyed() {
        return timeRemaining() != null;
    }

    /**
     * 
     * @return True if the system has been destroyed, false otherwise
     */
    public boolean isDestroyed() {
        return timeRemaining() == null && destructionStartTime != null;
    }
}
