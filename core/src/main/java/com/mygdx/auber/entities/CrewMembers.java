package com.mygdx.auber.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Array;
import com.mygdx.auber.Config;
import com.mygdx.auber.Pathfinding.GraphCreator;
import com.mygdx.auber.Pathfinding.MapGraph;
import com.mygdx.auber.Pathfinding.Node;

public class CrewMembers extends NPC {
    public double timeToWait = Math.random() * 15;
    public static Array<Sprite> crewSprites = new Array<>();

    public CrewMembers(Sprite sprite, Node node, MapGraph mapGraph) {
        super(sprite, node, mapGraph, Config.CREW_MEMBER_SPEED);
        this.setPosition(node.getX(), node.getY());
    }

    /**
     * Step needs to be called in the update method, makes the NPC move and check if
     * it has reached its next node
     */
    public void step(float delta) {
        this.moveNPC(delta);

        this.elapsedTime += delta;
        this.checkCollision(Config.CREW_MEMBER_SPEED);

        // this.collision.checkForCollision(this, layer, this.velocity, this.collision);
        // //This line enables collision, need to give same layers as player though,
        // wouldn't recommend

        if ((this.elapsedTime >= timeToWait) && this.pathQueue.isEmpty()) { // If wait time has elapsed and no where
                                                                            // else to go in path
            this.elapsedTime = 0;
            reachDestination();
        }
    }

    /**
     * Called when the path queue is empty
     */
    @Override
    public void reachDestination() {
        this.velocity.x = 0;
        this.velocity.y = 0;
        timeToWait = Math.random() * 15;

        double chance = Math.random();

        if (chance < 0.2) {
            setGoal(GraphCreator.getKeySystemNodes().random(), Config.CREW_MEMBER_SPEED);
        } // 1/5 chance of going to a key system
        else {
            Node newGoal;
            do {
                newGoal = MapGraph.getNodes().random();
            } while (newGoal == previousNode);
            {
                setGoal(newGoal, Config.CREW_MEMBER_SPEED);

            } // 4/5 chance of going to a random node
        }
    }

    /**
     * Generates the list of crewmate sprites the crewmates can be
     */
    public static void createCrewSprites() {
        CrewMembers.crewSprites.add(new Sprite(new Texture("AlienStand.png")));
        CrewMembers.crewSprites.add(new Sprite(new Texture("HumanStand.png")));
        CrewMembers.crewSprites.add(new Sprite(new Texture("Luffy.png")));
        CrewMembers.crewSprites.add(new Sprite(new Texture("Sagiri.png")));
    }

    /**
     * Returns a crew member sprite, low chance of anime
     * 
     * @return A Sprite
     */
    public static Sprite selectSprite() {
        double chance = Math.random() * 20;
        if (chance < 1) {
            return crewSprites.get(3);
        }
        if (chance < 2) {
            return crewSprites.get(2);
        }
        if (chance < 13) {
            return crewSprites.get(1);
        } else {
            return crewSprites.get(0);
        }
    } // Low chance of anime sprites (Always innocent) and high chance of construction
      // worker or alien

    public void setIndex(int index) {
        this.index = index;
    }

    public void dispose() {
        crewSprites.clear();
    }
}
