package main.java.com.mygdx.auber.Pathfinding;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
public class Path implements Connection<Node> {

    public Node fromNode,toNode;
    public float cost;

    public Path(Node fromNode, Node toNode)
    {
         this.fromNode = fromNode;
         this.toNode = toNode;
         cost = Vector2.dst(fromNode.x,fromNode.y,toNode.x,toNode.y);
    }

    /**
     * Render method for rendering the paths between nodes, used for debugging purposes
     * @param shapeRenderer The shape renderer being used
     */
    public void render(ShapeRenderer shapeRenderer)
    {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, 1);
        shapeRenderer.rectLine(fromNode.x, fromNode.y, toNode.x, toNode.y, 4);
        shapeRenderer.end();
    }

    /**
     * Returns the cost of the path
     * @return An Int, the cost
     */
    @Override
    public float getCost() {
        return cost;
    }

    /**
     * Returns the node the path comes from
     * @return A Node, the from node
     */
    @Override
    public Node getFromNode() {
        return fromNode;
    }

    /**
     * Returns the node the path goes to
     * @return A Node, the to node
     */
    @Override
    public Node getToNode() {
        return toNode;
    }
}
