package com.mygdx.auber.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.auber.Pathfinding.GraphCreator;
import com.mygdx.auber.Pathfinding.MapGraph;
import com.mygdx.auber.Config;
import com.mygdx.auber.Pathfinding.*;

public class Infiltrator extends NPC {
    public boolean isDestroying = false;
    public double timeToWait = Math.random() * 5;

    private float timeInvisible;
    private boolean isInvisible = false;

    public static Array<Sprite> easySprites = new Array<>();
    public static Array<Sprite> hardSprites = new Array<>();

    public static boolean isHighlighted;

    public Infiltrator(Sprite sprite, Node node, MapGraph mapGraph) {
        super(sprite, node, mapGraph, Config.INFILTRATOR_SPEED);
        this.setPosition(node.getX(), node.getY());
    }

    /**
     * Step needs to be called in the update method, makes the NPC move and check if
     * it has reached its next node
     */
    public void step(Player p, float delta) {
        this.moveNPC(delta); // Moves the npc and sets their scale

        if (isDestroying) {
            KeySystem keySystem = KeySystemManager.getClosestKeySystem(previousNode.getX(), previousNode.getY());

            if (keySystem.isDestroyed()) {
                this.isDestroying = false;
                this.pathQueue.clear();
                this.setGoal(MapGraph.getRandomNode(), Config.INFILTRATOR_SPEED);
            }

            if (Vector2.dst(p.x, p.y, this.getX(), this.getY()) < 250) {
                keySystem.stopDestroy();
                this.useAbility(p);
                this.isDestroying = false;
            }
        } // If isDestroying, if the distance to the player is less than 250, use ability
          // and stop destroying, else keep adding time

        if (isInvisible) {
            timeInvisible += delta;
            if (timeInvisible > 10) {
                this.isInvisible = false;
            }
        } else {
            timeInvisible = 0;
            this.setAlpha(.99f);
        } // If isInvisible, keep adding time to timeInvisible, and if its longer than 10
          // seconds set isInvisible to false. If not timeInvisible, set the alpha to 1
          // and time to 0

        this.elapsedTime += delta;
        this.checkCollision(Config.INFILTRATOR_SPEED); // Add elapsed time and check collisions

        // this.collision.checkForCollision(this, layer, this.velocity, collision);

        // System.out.println(elapsedTime + " - " + timeToWait);
        if ((this.elapsedTime >= timeToWait) && this.pathQueue.isEmpty()) {
            this.elapsedTime = 0;
            reachDestination();
        } // If there is no queue and elapsed time is greater than time to wait, reach
          // destination
        if (p.getFreeze()){
            setSpeedToNextNode(0);
        } else {
            setSpeedToNextNode(Config.INFILTRATOR_SPEED);
        }

        if (p.getHighlight()){
            isHighlighted = true;
        } else {
            isHighlighted = false;
        }
    }

    /**
     * Called when the path queue is empty
     */
    @Override
    public void reachDestination() {
        this.velocity.x = 0;
        this.velocity.y = 0;
        timeToWait = Math.random() * 5;
        double chance = 1 / (double) NPCCreator.infiltrators.size;

        if ((Math.random() - 0.2f < chance) && !this.isDestroying && !this.isInvisible
                && KeySystemManager.safeKeySystemsCount() != 0) // 1/10 chance of infiltrator deciding to destroy a
                                                                // keysystem
        {
            this.destroyKeySystem();
            return;
        } // If not invisible or currently destroying a key system, random chance to go
          // destroying a key system

        if (pathQueue.size == 0 && GraphCreator.getKeySystemNodes().contains(this.previousNode, true)) {

            KeySystem keySystem = KeySystemManager.getClosestKeySystem(previousNode.getX(), previousNode.getY());
            if (keySystem == null) {
                this.isDestroying = false;
                setGoal(MapGraph.getRandomNode(), Config.INFILTRATOR_SPEED);
                return;
            }
            if (keySystem.isSafe()) {
                this.isDestroying = true;
                keySystem.startDestroy();
                timeToWait = KeySystem.destructionTime / 1000;
                return;
            }
        } // If no queue, and the last node in queue was a key systems node, start
          // destroying

        Node newGoal;
        do {
            newGoal = MapGraph.getNodes().random();
        } while (newGoal == previousNode);
        {
            setGoal(newGoal, Config.INFILTRATOR_SPEED);
        } // Set a new goal node and start moving towards it

    }

    /**
     * Starts destroying a random keySystem, moves towards it, sets isDestroying to
     * true
     */
    public void destroyKeySystem() {
        this.pathQueue.clear();
        Node keySystemNode = GraphCreator.getKeySystemNodes().random();
        KeySystem keySystem = KeySystemManager.getClosestKeySystem(keySystemNode.getX(), keySystemNode.getY());

        if ((keySystem.isDestroyed() || keySystem.isBeingDestroyed()) && KeySystemManager.safeKeySystemsCount() != 0) {
            destroyKeySystem();
        } else {
            this.setGoal(keySystemNode, Config.INFILTRATOR_SPEED);
        } // If Key system is being destroyed or is already destroyed, select a new key
          // system
    }

    /**
     * Causes the infiltrator to use a random ability
     */
    public void useAbility(Player p) {
        double chance = Math.random() * 3;

        if (!this.isDestroying) {
            return;
        }
        if (chance < 1) {
            this.goInvisible();
        } else if (chance >= 1 && chance < 2) {
            this.damageAuber(p, 15);
        } else {
            this.stopAuberHealing(p);
        } // 1/3 chance of using each ability

        this.pathQueue.clear();
        this.setGoal(MapGraph.getRandomNode(), Config.INFILTRATOR_SPEED); // After using an ability, go somewhere random
    }

    /**
     * Sets the sprite alpha to 0, records the time the invisibility started, sets
     * isInvisible to true
     */
    private void goInvisible() {
        this.isInvisible = true;
        this.isDestroying = false;
        this.timeInvisible = 0;
        this.setAlpha(0.05f);
    }

    /**
     * Damages Auber by an amount.
     * @param p The player to damage.
     * @param amount Int amount of damage to deal.
     */
    private void damageAuber(final Player p, final int amount) {
        p.takeDamage(amount);
    }

    /**
     * Sets canHeal to false in player, records the time at which he stopped being
     * able to heal
     */
    private void stopAuberHealing(final Player p) {
        // System.out.println("Stopped healing");
        p.canHeal = false;
        p.healStopTime = 0;
    }

    /**
     * Fills out the array of sprites available for the infiltrators to take
     */
    public static void createInfiltratorSprites() {
        Infiltrator.easySprites.add(new Sprite(new Texture("Doctor.png")));
        Infiltrator.easySprites.add(new Sprite(new Texture("InfiltratorEngineer.png")));
        Infiltrator.easySprites.add(new Sprite(new Texture("InfiltratorAlien.png")));

        Infiltrator.hardSprites.add(new Sprite(new Texture("AlienStand.png")));
        Infiltrator.hardSprites.add(new Sprite(new Texture("HumanStand.png")));
    }

    /**
     * Sets the index for this Infiltrator
     * @param index
     */
    public void setIndex(int index) {
        this.index = index;
    }

    public void setHighlight(ShapeRenderer shapeRenderer) { //for some reason this was breaking NPC collision?

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(1,0,0,1);
        shapeRenderer.circle(getX() - getWidth()/2,getY() - getHeight()/2,getHeight());
        shapeRenderer.end();

    }

    public void dispose() {
        easySprites.clear();
        // hardSprites.clear();
    }
}
