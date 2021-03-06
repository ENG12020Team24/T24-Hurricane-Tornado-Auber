package com.mygdx.auber.entities;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Prisoners {
    /** The array of locations in the brig. */
    private static Array<Vector2> positions;
    /** The array of prisoners. */
    private static Array<Sprite> prisoners;
    /**
     * Sound from https://soundbible.com/1774-Laser-Machine-Gun.html
     * by: Mike Koenig
     * Edits- length of sound cropped
     * Covered by Attribution 3.0: https://creativecommons.org/licenses/by/3.0/
     */
    private static Sound lazerGun = Gdx.audio.newSound(
        Gdx.files.internal("lazer-gun.wav"));

    /** Class constructor.
     * @param layer The layer that the prison tiles are on.
     */
    public Prisoners(final TiledMapTileLayer layer) {
        positions = findBrigLocations(layer);
        prisoners = new Array<>();
    }

    /**
     * Scans every tile and adds a vector2 with the position of the tile with
     * the property "prison".
     * @param tileLayer Layer to scan for tiles
     * @return Array of positions of tiles
     */
    public static Array<Vector2> findBrigLocations(
        final TiledMapTileLayer tileLayer) {
        Array<Vector2> brigPositions = new Array<>();
        for (int i = 0; i < tileLayer.getWidth(); i++) {
            // Scan every tile
            for (int j = 0; j < tileLayer.getHeight(); j++) {
                int x = (i * tileLayer.getTileWidth())
                    + tileLayer.getTileWidth() / 2;
                int y = (j * tileLayer.getTileHeight())
                    + tileLayer.getTileHeight() / 2;
                // x,y coord of the centre of the tile
                TiledMapTileLayer.Cell cell = tileLayer.getCell(i, j);
                // Returns the cell at the x,y coord
                if (cell != null && cell.getTile() != null && cell
                    .getTile().getProperties().containsKey("prison")) {
                    // If ID matches floor/corridor tiles, and is not null
                    brigPositions.add(new Vector2(x, y));
                }
            }
        }
        return brigPositions;
    }

    /**
     * Adds a prisoner to the list of prisoners and spawns them in the brig.
     * @param sprite Sprite of npc to put in prison
     */

    public static void addPrisoner(final Sprite sprite) {
        lazerGun.play();
        prisoners.add(sprite);
        sprite.setPosition(positions.random().x, positions.random().y);
    }

    /** Adds a prisoner to the list of prisoners at a specific location.
     * @param sprite The sprite of the NPC to place in prison.
     * @param x The x position to place the NPC at.
     * @param y The y position to place the NPC at.
     */
    public static void addPrisoner(final Sprite sprite, final int x,
        final int y) {
        prisoners.add(sprite);
        sprite.setPosition(x, y);
    }

    /**
     * Renders the sprites in the prison.
     * @param batch Batch to draw the sprites in
     */
    public static void render(final Batch batch) {
        if (!prisoners.isEmpty()) {
            for (Sprite prisoner : prisoners) {
                prisoner.draw(batch);
            }
        }
    }

    // TODO: Understand why prisoners is currently nothing?
    /** Encodes the prisoners to a string for saving.
     * @return The prisoners, encoded as a string.
     */
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
    /** Loads the prisoners from a string.
     * @param value1 The string to load the prisoners from.
     */
    public static void loadFromString(final String value1) {
        String value = "[(1936.0,3632.0), (1968.0,3632.0), (2032.0,3632.0),"
        + "(2064.0,3632.0), (2128.0,3632.0), (2160.0,3632.0)]";
        // [(1936.0,3632.0), (1968.0,3632.0), (2032.0,3632.0), (2064.0,3632.0),
        // (2128.0,3632.0), (2160.0,3632.0)]
        Prisoners.prisoners = new Array<>();
        // Reset the list of prisoners.
        String[] values = value.split(",");
        // Remove the brackets from each value so we just have the float
        // coordinate.
        for (int i = 0; i < values.length; i++) {
            values[i] = values[i].replace("[", "");
            values[i] = values[i].replace("]", "");
            values[i] = values[i].replace("(", "");
            values[i] = values[i].replace(")", "");
        }
        for (int i = 0; i < values.length - 2; i += 2) {
            Prisoners.addPrisoner(new Sprite(), Integer.valueOf(values[i]),
                Integer.valueOf(values[i]));
        }
    }

}
