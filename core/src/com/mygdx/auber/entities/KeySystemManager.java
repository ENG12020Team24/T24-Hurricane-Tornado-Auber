package com.mygdx.auber.entities;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class KeySystemManager {
    public static Array<KeySystem> keySystems = new Array<>();

    private final TiledMapTileLayer keySystemLayer;

    KeySystemManager(TiledMapTileLayer tileLayer) {
        this.keySystemLayer = tileLayer;
        loadKeySystems(tileLayer);
    }

    private static void loadKeySystems(TiledMapTileLayer tileLayer) {
        for (int i = 0; i < tileLayer.getWidth(); i++) {
            //Scan every tile
            for (int j = 0; j < tileLayer.getHeight(); j++) {
                int x = (i * tileLayer.getTileWidth()) + tileLayer.getTileWidth()/2;
                int y = (j * tileLayer.getTileHeight()) + tileLayer.getTileHeight()/2; //x,y coord of the centre of the tile
                TiledMapTileLayer.Cell cell = tileLayer.getCell(i, j); //Returns the cell at the x,y coord
                if(cell != null && cell.getTile() != null && !(cell.getTile().getProperties().containsKey("nodeless"))) //If ID matches floor/corridor tiles, and is not null
                {
                    if(cell.getTile().getProperties().containsKey("keysystemnode"))
                    {
                        String name = (String) cell.getTile().getProperties().get("name");
                        Vector2 position = new Vector2(x, y);
                        KeySystem keySystem = new KeySystem(cell, name, position);
                        keySystems.add(keySystem);
                    }
                }
            }
        }
    }

    public static int safeKeySystemsCount() {
        int remaining = 0;

        for (KeySystem keySystem : keySystems) {
            if (keySystem.isSafe()) {
                remaining += 1;
            }
        }
        return remaining;
    }

    public static int beingDestroyedKeySystemsCount() {
        int beingDestroyed = 0;

        for (KeySystem keySystem : keySystems) {
            if (keySystem.isBeingDestroyed()) {
                beingDestroyed += 1;
            }
        }
        return beingDestroyed;
    }

    public static int destroyedKeySystemsCount() {
        int destroyed = 0;

        for (KeySystem keySystem : keySystems) {
            if (keySystem.isDestroyed()) {
                destroyed += 1;
            }
        }
        return destroyed;
    }

    public KeySystem getClosestKeySystem(float x, float y, int range) {
        KeySystem closest = null;
        for (KeySystem keySystem : keySystems) {
            if (closest == null) {
                closest = keySystem;
                continue;
            }
            if (keySystem.position.dst2(x, y) < closest.position.dst2(x, y)) {
                closest = keySystem;
            }
        }
        return closest;
    }
}
