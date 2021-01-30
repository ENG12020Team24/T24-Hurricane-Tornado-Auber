package com.mygdx.auber.Pathfinding;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Node {
    /** The x coordinate of this Node. */
    private float x;
    /** The y coordinate of this Node. */
    private float y;
    /** The index of this Node. */
    private int index;

    /**
     * The class constructor.
     * @param nodeX The x coordinate for this Node.
     * @param nodeY The y coordinate for this Node.
     */
    public Node(final float nodeX, final float nodeY) {
        this.x = nodeX;
        this.y = nodeY;
    }

    /**
     * Setter for the index of the node, used for A* Indexed search.
     * @param newIndex The index to assign the node.
     */
    public void setIndex(final int newIndex) {
        this.index = newIndex;
    }

    /** The red value for rendering if this node is in the path. */
    private static final float IN_PATH_RED = .57f;
    /** The green value for rendering if this node is in the path. */
    private static final float IN_PATH_GREEN = .76f;
    /** The blue value for rendering if this node is in the path. */
    private static final float IN_PATH_BLUE = .48f;

    /** The red value for rendering if this node is not in the path. */
    private static final float NOT_IN_PATH_RED = .8f;
    /** The red value for rendering if this node is not in the path. */
    private static final float NOT_IN_PATH_GREEN = .88f;
    /** The red value for rendering if this node is not in the path. */
    private static final float NOT_IN_PATH_BLUE = .95f;

    /** The radius of the circle used for debug rendering. */
    private static final float CIRCLE_RADIUS = 5;

    /**
     * Renderer for the nodes, used for debugging purposes.
     * @param shapeRenderer The shape renderer being used.
     * @param batch         The sprite batch being used.
     * @param font          The font to use.
     * @param inPath        Whether this node is in the path.
     */
    public void render(final ShapeRenderer shapeRenderer,
        final SpriteBatch batch, final BitmapFont font, final boolean inPath) {
        // Drawing the Circle which holds the node
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        if (inPath) {
            // green
            shapeRenderer.setColor(IN_PATH_RED, IN_PATH_BLUE, IN_PATH_GREEN,
                1);
        } else {
            // blue
            shapeRenderer.setColor(NOT_IN_PATH_RED, NOT_IN_PATH_GREEN,
                NOT_IN_PATH_BLUE, 1);
        }
        shapeRenderer.circle(x, y, CIRCLE_RADIUS);
        shapeRenderer.end();

        // Drawing the outline of the circle
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(0, 0, 0, 1);
        shapeRenderer.circle(x, y, CIRCLE_RADIUS);
        shapeRenderer.end();
    }

    /**
     * Gets the index of this node.
     * @return An int containing this node's index.
     */
    public int getIndex() {
        return index;
    }

    /**
     * Gets the x coordinate of this node.
     * @return An int containing this node's x coordinate.
     */
    public float getX() {
        return x;
    }

    /**
     * Gets the y coordinate of this node.
     * @return An int containing this node's y coordinate.
     */
    public float getY() {
        return y;
    }
}
