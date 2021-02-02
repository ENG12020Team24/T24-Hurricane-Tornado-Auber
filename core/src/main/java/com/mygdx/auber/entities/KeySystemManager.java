package com.mygdx.auber.entities;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class KeySystemManager {
    public static Array<KeySystem> keySystems = new Array<>();
    private final TiledMapTileLayer keySystemLayer;

    public KeySystemManager(TiledMapTileLayer tileLayer) {
        this.keySystemLayer = tileLayer;
        loadKeySystems(tileLayer);
    }

    /**
     * Scans the tile layer and adds a KeySystem to keySystens upon finding a tile
     * with they property "keysystem"
     * 
     * @param tileLayer layer to scan for key systems
     */
    private static void loadKeySystems(TiledMapTileLayer tileLayer) {
        for (int i = 0; i < tileLayer.getWidth(); i++) {
            // Scan every tile
            for (int j = 0; j < tileLayer.getHeight(); j++) {
                int x = (i * tileLayer.getTileWidth()) + tileLayer.getTileWidth() / 2;
                int y = (j * tileLayer.getTileHeight()) + tileLayer.getTileHeight() / 2; // x,y coord of the centre of
                                                                                         // the tile
                TiledMapTileLayer.Cell cell = tileLayer.getCell(i, j); // Returns the cell at the x,y coord
                if (cell != null && cell.getTile() != null && cell.getTile().getProperties().containsKey("keysystem")) // If
                                                                                                                       // ID
                                                                                                                       // matches
                                                                                                                       // floor/corridor
                                                                                                                       // tiles,
                                                                                                                       // and
                                                                                                                       // is
                                                                                                                       // not
                                                                                                                       // null
                {
                    // System.out.print("i: ");
                    // System.out.print(i);
                    // System.out.print(", j: ");
                    // System.out.println(j);
                    String name = (String) cell.getTile().getProperties().get("name");
                    Vector2 position = new Vector2(x, y);
                    KeySystem keySystem = new KeySystem(cell, name, position);
                    keySystems.add(keySystem);
                }
            }
        }
    }

    /**
     * 
     * @return The number of key systems that have both not been destroyed and are
     *         not being destroyed.
     */
    public static int safeKeySystemsCount() {
        int remaining = 0;

        for (KeySystem keySystem : keySystems) {
            if (keySystem.isSafe()) {
                remaining += 1;
            }
        }
        return remaining;
    }

    /**
     * 
     * @return The number of key systems that are currently being destroyed.
     */
    public static int beingDestroyedKeySystemsCount() {
        int beingDestroyed = 0;

        for (KeySystem keySystem : keySystems) {
            if (keySystem.isBeingDestroyed()) {
                beingDestroyed += 1;
            }
        }
        return beingDestroyed;
    }

    /**
     * 
     * @return The number of key systems that have been destroyed.
     */
    public static int destroyedKeySystemsCount() {
        int destroyed = 0;

        for (KeySystem keySystem : keySystems) {
            if (keySystem.isDestroyed()) {
                destroyed += 1;
            }
        }
        return destroyed;
    }

    /**
     * Returns the closest KeySystem
     * 
     * @param x x coord
     * @param y y coord
     * @return Closest KeySystem
     */
    public static KeySystem getClosestKeySystem(float x, float y) {
        KeySystem closest = null;
        for (KeySystem keySystem : keySystems) {
            if (closest == null) {
                closest = keySystem;
                continue;
            }
            if (keySystem.position.dst(x, y) < closest.position.dst(x, y)) {
                closest = keySystem;
            }
        }
        return closest;
    }

    /**
     *
     * @return An array of KeySystems being destroyed
     */
    public static Array<KeySystem> getBeingDestroyedKeySystems() {
        Array<KeySystem> keySystemsList = new Array<>();
        for (KeySystem keySystem : keySystems) {
            if (keySystem.isBeingDestroyed()) {
                keySystemsList.add(keySystem);
            }
        }
        return keySystemsList;
    }

    /**
     * 
     * @return The number of key systems, no matter if they have been destroyed or
     *         not.
     */
    public static int keySystemsCount() {
        return keySystems.size;
    }

    public static void dispose() {
        keySystems.clear();
    }
}
