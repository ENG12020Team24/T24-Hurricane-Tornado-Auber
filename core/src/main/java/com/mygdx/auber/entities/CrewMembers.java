package com.mygdx.auber.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.auber.Config;
import com.mygdx.auber.Pathfinding.GraphCreator;
import com.mygdx.auber.Pathfinding.MapGraph;
import com.mygdx.auber.Pathfinding.Node;

public final class CrewMembers extends NPC {
    /** The maximum time to wait in seconds. */
    private static final float MAX_WAIT_TIME = 15;
    /** How long to wait for in seconds between movements. */
    private double timeToWait = Math.random() * MAX_WAIT_TIME;
    /** The array of Sprites Crewmates can be. */
    private static Array<Sprite> crewSprites = new Array<>();

    /**
     * Class constructor.
     * @param sprite The sprite for this crew member.
     * @param node The node that this crew member starts on.
     * @param mapGraph The mapGraph that this crew member uses to navigate.
     */
    public CrewMembers(final Sprite sprite, final Node node,
        final MapGraph mapGraph) {
        super(sprite, node, mapGraph, Config.CREW_MEMBER_SPEED);
        this.setPosition(node.getX(), node.getY());
    }

    /**
     * Step needs to be called in the update method, makes the NPC move and
     * check if it has reached its next node.
     * @param delta the time between the previous frame and this frame in
     * seconds.
     */
    public void step(final float delta) {
        this.moveNPC(delta);

        this.incrementElapsedTime(delta);
        this.checkCollision(Config.CREW_MEMBER_SPEED);

        // this.collision.checkForCollision(this, layer, this.velocity,
            // this.collision);
        // //This line enables collision, need to give same layers as player
        // though, wouldn't recommend

        if ((this.getElapsedTime() >= timeToWait)
            && this.getPathQueue().isEmpty()) {
            // If wait time has elapsed and no where else to go in path
            this.resetElapsedTime();
            reachDestination();
        }
    }

    /** The chance that the crew member will path to a key system vs any valid
     * node. */
    private static final float KEY_SYSTEM_VISIT_CHANCE = 0.2f;
    /**
     * Called when the path queue is empty.
     */
    @Override
    public void reachDestination() {
        this.setVelocity(new Vector2(0, 0));
        timeToWait = Math.random() * MAX_WAIT_TIME;

        double chance = Math.random();

        if (chance < KEY_SYSTEM_VISIT_CHANCE) {
            setGoal(GraphCreator.getKeySystemNodes().random(),
                Config.CREW_MEMBER_SPEED);
            // 1/5 chance of going to a key system
        } else {
            Node newGoal;
            do {
                newGoal = MapGraph.getNodes().random();
            } while (newGoal == getPreviousNode());
            setGoal(newGoal, Config.CREW_MEMBER_SPEED);
            // 4/5 chance of going to a random node
        }
    }

    /**
     * Generates the list of crewmate sprites the crewmates can be.
     */
    public static void createCrewSprites() {
        CrewMembers.crewSprites.add(new Sprite(new Texture("AlienStand.png")));
        CrewMembers.crewSprites.add(new Sprite(new Texture("HumanStand.png")));
        CrewMembers.crewSprites.add(new Sprite(new Texture("Luffy.png")));
        CrewMembers.crewSprites.add(new Sprite(new Texture("Sagiri.png")));
    }

    /** Crewmate roll chances are scaled relative to this. */
    private static final float CREWMATE_CHANCES = 20;
    /** The chance of getting a given innocent sprite. */
    private static final float INNOCENT_CHANCE = 1;
    /** The chance of rolling a human worker sprite. */
    private static final float WORKER_CHANCE = 13;
    /** Index of the second innocent sprite. */
    private static final int SECOND_INNOCENT_INDEX = 3;

    /**
     * Returns a crew member sprite, low chance of anime.
     * @return A randomly selected Sprite.
     */
    public static Sprite selectSprite() {
        double chance = Math.random() * CREWMATE_CHANCES;
        if (chance < INNOCENT_CHANCE) {
            return crewSprites.get(SECOND_INNOCENT_INDEX);
        }
        if (chance < INNOCENT_CHANCE * 2) {
            return crewSprites.get(2);
        }
        if (chance < WORKER_CHANCE) {
            return crewSprites.get(1);
        } else {
            return crewSprites.get(0);
        }
    } // Low chance of anime sprites (Always innocent) and high chance of
      // construction worker or alien

    /**
     * Method implemented from abstract superclass.
     */
    public void dispose() {
        crewSprites.clear();
    }


     /**
     * Encodes data of all crew members into a recognisable string.
     * @param crew the set of crew members to encode.
     * @return the encoded data of the given array of sprites.
     */
    public static String encode(final Array<CrewMembers> crew) {
        String r = "";

        Array<Vector2> locations = new Array<>();
        for (CrewMembers i : crew) {
            locations.add(new Vector2(i.getX(), i.getY()));
        }

        r += locations.toString();
        return r;
    }

}
