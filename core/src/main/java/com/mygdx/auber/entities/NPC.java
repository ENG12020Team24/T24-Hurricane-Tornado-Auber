package com.mygdx.auber.entities;

import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Queue;
import com.mygdx.auber.Pathfinding.MapGraph;
import com.mygdx.auber.Pathfinding.Node;

/**
 * NPCs use nodes to walk around and A* to navigate between them, the nodes are
 * generated and stored in GraphCreator, while MapGraph is used to search and
 * manipulate the node graph. Generally, NPCs are updated through updateNPC,
 * which calls each npcs step method, which makes them moves, sets their sprite
 * scale, checks if they have reached the next node etc Crewmember is very
 * simple, they walk around randomly and wait for a random amount of time
 * Infiltrator is more complex, they sometimes go to destroy keysystems, and
 * can use abilities
 */
public abstract class NPC extends Sprite {
    /** Velocity vector. */
    private Vector2 velocity = new Vector2(0, 0);
    /** Index of the NPC in its respective list. */
    private int index;
    /** Time elapsed since NPC last moved. */
    private float elapsedTime = 0f;
    /** Mapgraph for the NPC to reference. */
    public MapGraph mapGraph;
    /** Previous node the NPC visited. */
    private Node previousNode;
    /** pathQueue the NPC is currently traversing. */
    private Queue<Node> pathQueue = new Queue<>();
    /** Used to help calculate collisions. */
    private Collision collision;

    /**
     * Constructor for NPC.
     * @param sprite   Sprite to be used for the NPC
     * @param start    Node for the NPC to start at
     * @param newMapGraph mapGraph for the NPC to reference
     * @param speed The speed of this NPC.
     */
    public NPC(final Sprite sprite, final Node start,
        final MapGraph newMapGraph, final float speed) {
        super(sprite);

        this.mapGraph = newMapGraph;
        this.previousNode = start;
        this.setPosition(start.getX(), start.getY());
        this.setGoal(mapGraph.getRandomNode(), speed);
        this.collision = new Collision();
    }

    public NPC(final Sprite sprite, final float x, final float y,
    final MapGraph newMapGraph, final float speed) {
    super(sprite);

    this.mapGraph = newMapGraph;
    this.previousNode = mapGraph.getRandomNode();
    this.setPosition(x, y);
    this.setGoal(mapGraph.getRandomNode(), speed);
    this.collision = new Collision();
}


    /**
     * Updates every NPC, to be called in a screens update method.
     * @param p The current instance of the player.
     * @param delta Float of time between last and current frame, used for
     * movement
     */
    public static void updateNPC(final Player p, final float delta) {
        if (NPCCreator.hasCrew()) {
            for (CrewMembers crewMember : NPCCreator.getCrew()) {
                crewMember.step(delta);
            }
        }

        if (NPCCreator.hasInfiltrators()) {
            for (Infiltrator infiltrator : NPCCreator.getInfiltrators()) {
                infiltrator.step(p, delta);
            }
        }
    }

    /**
     * Sets the goal node and calculates the path to take there.
     * @param goal Node to move NPC to.
     * @param speed The speed of this NPC.
     */
    public void setGoal(final Node goal, final float speed) {
        GraphPath<Node> graphPath = mapGraph.findPath(previousNode, goal);
        //System.out.println(graphPath.getCount());
        // System.out.println(goal);
        for (int i = 1; i < graphPath.getCount(); i++) {
            // System.out.println("aaa");
            this.pathQueue.addLast(graphPath.get(i));
        }

        setSpeedToNextNode(speed);
    }

    /** How close the NPC must be to a node to be classed as having reached
     * it. */
    private static final float NODE_PROXIMITY_VALUE = 5;

    /**
     * Checks whether the NPC has made it to the next node.
     * @param speed The speed of this NPC.
     */
    public void checkCollision(final float speed) {
        if (this.pathQueue.size > 0) {
            Node targetNode = this.pathQueue.first();
            if (Vector2.dst(this.getX(), this.getY(), targetNode.getX(),
                targetNode.getY()) <= NODE_PROXIMITY_VALUE) {
                reachNextNode(speed);
                // If the sprite is within 5 pixels of the node, it has
                // reached the node
            }
        }
    }

    /**
     * Called when NPC has reached a node, sets the next node to be moved to,
     * or if the path queue is empty, destination is reached.
     * @param speed The speed of this NPC.
     */
    public void reachNextNode(final float speed) {
        this.velocity.x = 0;
        this.velocity.y = 0;

        this.previousNode = this.pathQueue.first();
        // System.out.println(pathQueue.size);
        this.pathQueue.removeFirst();

        if (this.pathQueue.size != 0) {
            this.setSpeedToNextNode(speed);
            // If there are items in the queue, set the velocity towards the
            // next node
        }
    }

    /**
     * Sets the velocity towards the next node.
     * @param speed The speed of this NPC.
     */
    public void setSpeedToNextNode(final float speed) {
        this.velocity.x = 0;
        this.velocity.y = 0;

        if (pathQueue.isEmpty()) {
            this.reachDestination();
            // this.setGoal(MapGraph.getRandomNode());
            return;
        }

        Node nextNode = this.pathQueue.first();
        double angle = MathUtils.atan2(
            this.getY() - nextNode.getY(), this.getX() - nextNode.getX());
        this.velocity.x -= (MathUtils.cos((float) angle) * speed);
        this.velocity.y -= (MathUtils.sin((float) angle) * speed);
    }

    /**
     * Moves the NPC based on their movement vector, and sets their sprite in
     * the direction of movement.
     * @param deltaTime The time since the previous frame in seconds.
     */
    public void moveNPC(final float deltaTime) {

        this.setX(this.getX() + this.velocity.x * deltaTime);
        this.setY(this.getY() + this.velocity.y * deltaTime);

        if (this.velocity.x < 0) {
            this.setScale(-1, 1);
        } else if (this.velocity.x > 0) {
            this.setScale(1, 1);
        }
    }

    /**
     * Render method for rendering all NPCs.
     * @param batch Batch for the NPCs to render in.
     */
    public static void render(final Batch batch) {
        for (Infiltrator infiltrator : NPCCreator.getInfiltrators()) {
            infiltrator.draw(batch);
            if (Infiltrator.isHighlighted()) {
                infiltrator.setColor(Color.RED);
            } else {
                infiltrator.setColor(Color.WHITE);
            }
        }

        for (CrewMembers crewMember : NPCCreator.getCrew()) {
            crewMember.draw(batch);
        }
    }

    /**
     * Dispose method to be called in dispose method of screen.
     */
    public static void disposeNPC() {
        for (Infiltrator infiltrator : NPCCreator.getInfiltrators()) {
            infiltrator.dispose();

        }
        for (CrewMembers crewMember : NPCCreator.getCrew()) {
            crewMember.dispose();
        }

        NPCCreator.dispose();
    }

    /**
     * A placeholder function to be superceded by subclasses own
     * reachDestination().
     */
    public abstract void reachDestination();

    /** Gets the index of this NPC.
     * @return the index of this NPC.
     */
    public int getIndex() {
        return index;
    }

    /** Sets the index of this NPC.
     * @param newIndex The new index for this NPC.
     */
    public void setIndex(final int newIndex) {
        this.index = newIndex;
    }

    /** Gets this NPC's velocity.
     * @return this NPC's velocity.
     */
    public Vector2 getVelocity() {
        return velocity;
    }

    /** Sets this NPC's velocity.
     * @param newVelocity The new velocity for this NPC.
     */
    public void setVelocity(final Vector2 newVelocity) {
        this.velocity = newVelocity;
    }

    /**
     * Gets the previous node that this NPC visited.
     * @return The previous node that this NPC visited.
     */
    public Node getPreviousNode() {
        return previousNode;
    }

    /** Returns how long this NPC has been waiting for.
     * @return the elapsedTime variable.
     */
    public float getElapsedTime() {
        return elapsedTime;
    }

    /** Sets the elapsed time to 0. */
    public void resetElapsedTime() {
        this.elapsedTime = 0;
    }

    /** Returns the path this NPC is following.
     * @return The path that this NPC is following.
     */
    public Queue<Node> getPathQueue() {
        return pathQueue;
    }

    /** Increases the elapsed time by the given value.
     * @param delta The amount to increase the elapsed time by.
     */
    public void incrementElapsedTime(final float delta) {
        this.elapsedTime += delta;
    }

    /** Clears this NPC's pathQueue. */
    public void clearPathQueue() {
        this.pathQueue.clear();
    }

}
