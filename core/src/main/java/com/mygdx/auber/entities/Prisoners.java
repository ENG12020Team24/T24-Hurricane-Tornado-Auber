package com.mygdx.auber.entities;

import java.util.Arrays;

import javax.print.event.PrintEvent;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Prisoners {
    private static Array<Vector2> positions;
    private static Array<Sprite> prisoners;

    public Prisoners(TiledMapTileLayer layer) {
        positions = findBrigLocations(layer);
        prisoners = new Array<>();
    }

    /**
     * Scans every tile and adds a vector 2 with the position of the tile with the
     * property "prison"
     * 
     * @param tileLayer Layer to scan for tiles
     * @return Array of positions of tiles
     */
    public static Array<Vector2> findBrigLocations(TiledMapTileLayer tileLayer) {
        Array<Vector2> positions = new Array<>();
        for (int i = 0; i < tileLayer.getWidth(); i++) {
            // Scan every tile
            for (int j = 0; j < tileLayer.getHeight(); j++) {
                int x = (i * tileLayer.getTileWidth()) + tileLayer.getTileWidth() / 2;
                int y = (j * tileLayer.getTileHeight()) + tileLayer.getTileHeight() / 2; // x,y coord of the centre of
                                                                                         // the tile
                TiledMapTileLayer.Cell cell = tileLayer.getCell(i, j); // Returns the cell at the x,y coord
                if (cell != null && cell.getTile() != null && cell.getTile().getProperties().containsKey("prison")) {
                    // If ID matches floor/corridor tiles, and is not null
                    positions.add(new Vector2(x, y));
                }
            }
        }
        return positions;
    }

    /**
     * Adds a prisoner to the list of prisoners and spawns them in the brig
     * 
     * @param sprite Sprite of npc to put in prison
     */
    public static void addPrisoner(Sprite sprite) {
        prisoners.add(sprite);
        sprite.setPosition(positions.random().x, positions.random().y);
    }

    public static void addPrisoner(Sprite sprite, int x, int y) {
        prisoners.add(sprite);
        sprite.setPosition(x, y);
    }

    /**
     * Renders the sprites in the prison
     * 
     * @param batch Batch to draw the sprites in
     */
    public static void render(Batch batch) {
        if (!prisoners.isEmpty()) {
            for (Sprite prisoner : prisoners) {
                prisoner.draw(batch);
            }
        }
    }

    // TODO: Understand why prisoners is currently nothing?
    public static String encode() {
        Array<Vector2> prisonerLocations = new Array<>();
        System.out.print("Encoding prisoners.");
        System.out.print(Prisoners.prisoners.size);

        for (Sprite sprite : Prisoners.prisoners) {
            prisonerLocations.add(new Vector2(sprite.getX(), sprite.getY()));
        }
        return prisonerLocations.toString();
    }

    // TODO: Needs testing.
    public static void LoadFromString(String value1) {
        String value = "[(1936.0,3632.0), (1968.0,3632.0), (2032.0,3632.0), (2064.0,3632.0), (2128.0,3632.0), (2160.0,3632.0)]";
        // [(1936.0,3632.0), (1968.0,3632.0), (2032.0,3632.0), (2064.0,3632.0), (2128.0,3632.0), (2160.0,3632.0)]
        Prisoners.prisoners = new Array<>();    // Reset the list of prisoners.
        String[] values = value.split(",");
        // Remove the brackets from each value so we just have the float coordinate.
        for (int i = 0; i < values.length; i++) {
            values[i] = values[i].replace("[", "");
            values[i] = values[i].replace("]", "");
            values[i] = values[i].replace("(", "");
            values[i] = values[i].replace(")", "");
        }
        for (int i = 0; i < values.length - 2; i+=2) {
            Prisoners.addPrisoner(new Sprite(), Integer.valueOf(values[i]), Integer.valueOf(values[i]));
        }
    }

}
