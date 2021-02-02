package com.mygdx.auber.entities;

import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Queue;
import com.mygdx.auber.Pathfinding.MapGraph;
import com.mygdx.auber.Pathfinding.Node;

/**
 * NPCs use nodes to walk around and A* to navigate between them, the nodes are
 * generated and stored in GraphCreator, while MapGraph is used to search and
 * manipulate the node graph Generally, NPCs are updated through updateNPC,
 * which calls each npcs step method, which makes them moves, sets their sprite
 * scale, checks if they have reached the next node etc Crewmember is very
 * simple, they walk around randomly and wait for a random amount of time
 * Infiltrator is more complex, they sometimes go to destroy keysystems, and can
 * use abilities
 */
public class NPC extends Sprite {
    public Vector2 velocity = new Vector2(0, 0); // Velocity vector
    public int index; // Index of the NPC in its respective list
    float elapsedTime = 0f; // Time elapsed since NPC last moved

    public MapGraph mapGraph; // Mapgraph for the NPC to reference
    Node previousNode; // Previous node the NPC visited
    public Queue<Node> pathQueue = new Queue<>(); // pathQueue the NPC is currently traversing

    public Collision collision;

    /**
     * Constructor for NPC
     * 
     * @param sprite   Sprite to be used for the NPC
     * @param start    Node for the NPC to start at
     * @param mapGraph mapGraph for the NPC to reference
     */
    public NPC(Sprite sprite, Node start, MapGraph mapGraph, float speed) {
        super(sprite);

        this.mapGraph = mapGraph;
        this.previousNode = start;
        this.setPosition(start.getX(), start.getY());
        this.setGoal(MapGraph.getRandomNode(), speed);
        this.collision = new Collision();
    }

    /**
     * Updates every NPC, to be called in a screens update method
     * 
     * @param delta Float of time between last and current frame, used for movement
     */
    public static void updateNPC(Player p, float delta) {
        if (NPCCreator.crew.notEmpty()) {
            for (CrewMembers crewMember : NPCCreator.crew) {
                crewMember.step(delta);
            }
        }

        if (NPCCreator.infiltrators.notEmpty()) {
            for (Infiltrator infiltrator : NPCCreator.infiltrators) {
                infiltrator.step(p, delta);
            }
        }
    }

    /**
     * Sets the goal node and calculates the path to take there
     * 
     * @param goal Node to move NPC to
     */
    public void setGoal(Node goal, float speed) {
        GraphPath<Node> graphPath = mapGraph.findPath(previousNode, goal);

        for (int i = 1; i < graphPath.getCount(); i++) {
            this.pathQueue.addLast(graphPath.get(i));
        }

        setSpeedToNextNode(speed);
    }

    /**
     * Checks whether the NPC has made it to the next node
     */
    public void checkCollision(float speed) {
        if (this.pathQueue.size > 0) {
            Node targetNode = this.pathQueue.first();
            if (Vector2.dst(this.getX(), this.getY(), targetNode.getX(), targetNode.getY()) <= 5) {
                reachNextNode(speed); // If the sprite is within 5 pixels of the node, it has reached the node
            }
        }
    }

    /**
     * Called when NPC has reached a node, sets the next node to be moved to, or if
     * the path queue is empty, destination is reached
     */
    public void reachNextNode(float speed) {
        this.velocity.x = 0;
        this.velocity.y = 0;

        this.previousNode = this.pathQueue.first();
        this.pathQueue.removeFirst();

        if (this.pathQueue.size != 0) {
            this.setSpeedToNextNode(speed); // If there are items in the queue, set the velocity towards the next node
        }
    }

    /**
     * Sets the velocity towards the next node
     */
    public void setSpeedToNextNode(float speed) {
        this.velocity.x = 0;
        this.velocity.y = 0;

        if (pathQueue.isEmpty()) {
            this.reachDestination();
            // this.setGoal(MapGraph.getRandomNode());
            return;
        }

        Node nextNode = this.pathQueue.first();
        double angle = MathUtils.atan2(this.getY() - nextNode.getY(), this.getX() - nextNode.getX());
        this.velocity.x -= (MathUtils.cos((float) angle) * speed);
        this.velocity.y -= (MathUtils.sin((float) angle) * speed);
    }

    /**
     * Moves the NPC based on their movement vector, and sets their sprite in the
     * direction of movement
     */
    public void moveNPC(float deltaTime) {

        this.setX(this.getX() + this.velocity.x * deltaTime);
        this.setY(this.getY() + this.velocity.y * deltaTime);

        if (this.velocity.x < 0) {
            this.setScale(-1, 1);
        } else if (this.velocity.x > 0) {
            this.setScale(1, 1);
        }
    }

    /**
     * Render method for rendering all NPCs
     * 
     * @param batch Batch for the NPCs to render in
     */
    public static void render(Batch batch) {
        for (Infiltrator infiltrator : NPCCreator.infiltrators) {
            infiltrator.draw(batch);
            if (infiltrator.isHighlighted){
                infiltrator.setColor(Color.RED);
            } else {
                infiltrator.setColor(Color.WHITE);
            }
        }

        for (CrewMembers crewMember : NPCCreator.crew) {
            crewMember.draw(batch);
        }
    }

    /**
     * Dispose method to be called in dispose method of screen
     */
    public static void disposeNPC() {
        for (Infiltrator infiltrator : NPCCreator.infiltrators) {
            infiltrator.dispose();

        }
        for (CrewMembers crewMember : NPCCreator.crew) {
            crewMember.dispose();
        }

        NPCCreator.dispose();
    }

    /**
     * A placeholder function to be superceded by subclasses own reachDesintation()
     */
    public void reachDestination() {
    }

}
