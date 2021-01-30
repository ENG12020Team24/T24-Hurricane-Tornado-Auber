package com.mygdx.auber.Pathfinding;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class Path implements Connection<Node> {
    /** The origin Node of this Path. */
    private Node fromNode;
    /** The destination Node of this Path. */
    private Node toNode;
    /** The cost for this Path. */
    private float cost;

    /**
     * Class constructor.
     * @param newFromNode The origin node for this path.
     * @param newToNode The destination node for this path.
     */
    public Path(final Node newFromNode, final Node newToNode) {
        this.fromNode = newFromNode;
        this.toNode = newToNode;
        cost = Vector2.dst(newFromNode.getX(), newFromNode.getY(),
            newToNode.getX(), newToNode.getY());
    }

    /** The width of the line drawn for debugging purposes. */
    private static final float LINE_WIDTH = 4;

    /**
     * Render method for rendering the paths between nodes, used for
     * debugging purposes.
     * @param shapeRenderer The shape renderer being used.
     */
    public void render(final ShapeRenderer shapeRenderer) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, 1);
        shapeRenderer.rectLine(fromNode.getX(), fromNode.getY(),
            toNode.getX(), toNode.getY(), LINE_WIDTH);
        shapeRenderer.end();
    }

    /**
     * Returns the cost of the path.
     * @return A float containing the cost.
     */
    @Override
    public float getCost() {
        return cost;
    }

    /**
     * Returns the node the path comes from.
     * @return The origin node.
     */
    @Override
    public Node getFromNode() {
        return fromNode;
    }

    /**
     * Returns the node the path goes to.
     * @return The destination node.
     */
    @Override
    public Node getToNode() {
        return toNode;
    }
}
