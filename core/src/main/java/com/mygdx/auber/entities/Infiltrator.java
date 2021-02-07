package com.mygdx.auber.entities;

import javax.swing.Painter;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.auber.Pathfinding.GraphCreator;
import com.mygdx.auber.Pathfinding.MapGraph;
import com.mygdx.auber.Pathfinding.Node;
import com.mygdx.auber.Config;

public final class Infiltrator extends NPC {
    /** Whether this infiltrator is currently destroying a system. */
    private boolean isDestroying = false;
    /** How long to wait for between routes. */
    private double timeToWait = Math.random() * MAX_SECONDS_BETWEEN_MOVEMENTS;
    /** How long this infiltrator has been invisible for. */
    private float timeInvisible;
    /** Whether this Infiltrator is currently invisible. */
    private boolean isInvisible = false;
    /** The list of easier sprites to spot. */
    private static Array<Sprite> easySprites = new Array<>();
    /** The list of harder sprites to spot. */
    private static Array<Sprite> hardSprites = new Array<>();
    /** Whether the infiltrators are currently highlighted. */
    private static boolean isHighlighted;

    /**
     * Class constructor.
     * @param sprite The sprite used to draw this Infiltrator.
     * @param node The Node this infiltrator spawns on.
     * @param mapGraph The MapGraph this infiltrator uses to navigate.
     */
    public Infiltrator(
        final Sprite sprite, final Node node, final MapGraph mapGraph) {
        super(sprite, node, mapGraph, Config.INFILTRATOR_SPEED);
        this.setPosition(node.getX(), node.getY());
    }
    /** If the player is within this distance, stop destroying a system
     * and flee. */
    private static final float PLAYER_ALERT_DISTANCE = 250;
    /** The time the infiltrator is invisible for. */
    private static final float INVISIBLE_TIME = 10;
    /** The alpha value when the player is visible. */
    private static final float VISIBLE_ALPHA = .99f;

    /**
     * Step needs to be called in the update method, makes the NPC move
     * and check if it has reached its next node.
     * @param p The instance of the player.
     * @param delta The time in seconds since the previous frame.
     */
    public void step(final Player p, final float delta) {
        this.moveNPC(delta); // Moves the npc and sets their scale

        if (isDestroying) {
            KeySystem keySystem = KeySystemManager.getClosestKeySystem(
                getPreviousNode().getX(), getPreviousNode().getY());

            if (keySystem.isDestroyed()) {
                this.isDestroying = false;
                this.clearPathQueue();
                this.setGoal(
                    mapGraph.getRandomNode(), Config.INFILTRATOR_SPEED);
            }

            if (Vector2.dst(Player.x, Player.y, this.getX(), this.getY())
                < PLAYER_ALERT_DISTANCE) {
                keySystem.stopDestroy();
                this.useAbility(p);
                this.isDestroying = false;
            }
        }
        // If isDestroying, if the distance to the player is less than 250,
        // use ability and stop destroying, else keep adding time

        if (isInvisible) {
            timeInvisible += delta;
            if (timeInvisible > INVISIBLE_TIME) {
                this.isInvisible = false;
            }
        } else {
            timeInvisible = 0;
            this.setAlpha(VISIBLE_ALPHA);
        }
        // If isInvisible, keep adding time to timeInvisible, and if its
        // longer than 10 seconds set isInvisible to false. If not
        // timeInvisible, set  the alpha to 1 and time to 0

        this.incrementElapsedTime(delta);
        this.checkCollision(Config.INFILTRATOR_SPEED);
        // Add elapsed time and check collisions

        // this.collision.checkForCollision(
            // this, layer, this.velocity, collision);

        // System.out.println(elapsedTime + " - " + timeToWait);
        if ((this.getElapsedTime() >= timeToWait)
            && this.getPathQueue().isEmpty()) {
            this.resetElapsedTime();
            reachDestination();
        }
        // If there is no queue and elapsed time is greater than time to
        // wait, reach destination
        if (Player.getFreeze()) {
            setSpeedToNextNode(0);
        } else {
            setSpeedToNextNode(Config.INFILTRATOR_SPEED);
        }

        if (Player.getHighlight()) {
            isHighlighted = true;
        } else {
            isHighlighted = false;
        }
    }

    /** Used to convert from destruction times to wait times. */
    private static final int MILLISECONDS_IN_SECONDS = 1000;
    /** The chance the Infiltrator will destroy a key system. */
    private static final float DESTROY_KEY_SYSTEM_CHANCE = 0.2f;
    /** The maximum number of seconds to wait after finishing moving. */
    private static final float MAX_SECONDS_BETWEEN_MOVEMENTS = 5;

    /**
     * Called when the path queue is empty.
     */
    @Override
    public void reachDestination() {
        this.setVelocity(new Vector2(0, 0));
        timeToWait = Math.random() * MAX_SECONDS_BETWEEN_MOVEMENTS;

        if ((Math.random() < (1 / (double) NPCCreator.getInfiltrators().size))
            && !this.isDestroying
            && !this.isInvisible
            && KeySystemManager.safeKeySystemsCount() != 0) {
            // chance of infiltrator trying to destroy a key system increases
            // the fewer infiltrators there are
            this.destroyKeySystem();
            return;
        }
        // If not invisible or currently destroying a key system, random chance
        // to go destroying a key system

        if (getPathQueue().size == 0 && GraphCreator.getKeySystemNodes()
            .contains(this.getPreviousNode(), true)) {
            KeySystem keySystem = KeySystemManager.getClosestKeySystem(
                getPreviousNode().getX(), getPreviousNode().getY());
            if (keySystem == null) {
                this.isDestroying = false;
                setGoal(mapGraph.getRandomNode(), Config.INFILTRATOR_SPEED);
                return;
            }
            if (keySystem.isSafe()) {
                this.isDestroying = true;
                keySystem.startDestroy();
                timeToWait = Config.SYSTEM_DESTRUCTION_TIME
                    / MILLISECONDS_IN_SECONDS;
                return;
            }
        }
        // If no queue, and the last node in queue was a key systems node,
        // start destroying

        Node newGoal;
        do {
            newGoal = mapGraph.getNodes().random();
        } while (newGoal == getPreviousNode());
        setGoal(newGoal, Config.INFILTRATOR_SPEED);
        // Set a new goal node and start moving towards it

    }

    /**
     * Starts destroying a random keySystem, moves towards it, sets
     * isDestroying to true.
     */
    public void destroyKeySystem() {
        this.clearPathQueue();
        Node keySystemNode = GraphCreator.getKeySystemNodes().random();
        KeySystem keySystem = KeySystemManager.getClosestKeySystem(
            keySystemNode.getX(), keySystemNode.getY());

        if ((keySystem.isDestroyed() || keySystem.isBeingDestroyed())
            && KeySystemManager.safeKeySystemsCount() != 0) {
            destroyKeySystem();
        } else {
            //System.out.println(keySystemNode);
            this.setGoal(keySystemNode, Config.INFILTRATOR_SPEED);
        }
        // If Key system is being destroyed or is already destroyed, select a
        // new key system
    }


    /** The amount to damage Auber by if Auber is to be damaged. */
    public static final int AUBER_DAMAGE_VALUE = 15;
    /** The number of different abilities Auber can use. */
    private static final int AUBER_ABILITY_COUNT = 3;
    /**
     * Causes the infiltrator to use a random ability.
     * @param p The current instance of Auber.
     */
    public void useAbility(final Player p) {
        double chance = Math.random() * AUBER_ABILITY_COUNT;

        if (!this.isDestroying) {
            return;
        }
        if (chance < 1) {
            this.goInvisible();
        } else if (chance >= 1 && chance < 2) {
            this.damageAuber(p, AUBER_DAMAGE_VALUE);
        } else {
            this.stopAuberHealing();
        } // 1/3 chance of using each ability

        this.clearPathQueue();
        this.setGoal(mapGraph.getRandomNode(), Config.INFILTRATOR_SPEED);
        // After using an ability, go somewhere random
    }

    /** The alpha value used when Auber is invisible. */
    private static final float INVISIBLE_ALPHA = 0.05f;

    /**
     * Sets the sprite alpha to 0, records the time the invisibility started,
     * sets isInvisible to true.
     */
    public void goInvisible() {
        this.isInvisible = true;
        this.isDestroying = false;
        this.timeInvisible = 0;
        this.setAlpha(INVISIBLE_ALPHA);
    }

    /**
     * Damages Auber by an amount.
     * @param p      The player to damage.
     * @param amount Int amount of damage to deal.
     */
    public void damageAuber(final Player p, final int amount) {
        p.takeDamage(amount);
    }

    /**
     * Sets canHeal to false in player, records the time at which he stopped
     * being able to heal.
     */
    public void stopAuberHealing() {
        // System.out.println("Stopped healing");
        Player.canHeal = false;
        Player.healStopTime = 0;
    }

    /**
     * Fills out the array of sprites available for the infiltrators to take.
     */
    public static void createInfiltratorSprites() {
        Infiltrator.easySprites.add(new Sprite(new Texture("Doctor.png")));
        Infiltrator.easySprites.add(new Sprite(
            new Texture("InfiltratorEngineer.png")));
        Infiltrator.easySprites.add(new Sprite(
            new Texture("InfiltratorAlien.png")));
        Infiltrator.hardSprites.add(new Sprite(new Texture("AlienStand.png")));
        Infiltrator.hardSprites.add(new Sprite(new Texture("HumanStand.png")));
    }

    /**
     * Highlights each Infiltrator.
     * @param shapeRenderer The ShapeRenderer used to highlight the
     * Infiltrators.
     */
    public void setHighlight(final ShapeRenderer shapeRenderer) {
        // for some reason this was breaking NPC collision?
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(1, 0, 0, 1);
        shapeRenderer.circle(
            getX() - getWidth() / 2, getY() - getHeight() / 2, getHeight());
        shapeRenderer.end();

    }

    /**
     * Called when this object is deleted.
     */
    public void dispose() {
        easySprites.clear();
        // hardSprites.clear();
    }

    /** Whether infiltrators are currently highlighted.
     * @return True if they are, false otherwise.
     */
    public static boolean isHighlighted() {
        return isHighlighted;
    }

    /** Whether this infiltrator is currently destroying a system.
     * @return True if they are, false otherwise.
     */
    public boolean isDestroying() {
        return isDestroying;
    }

    /** Sets whether this Infiltrator is destroying a system.
     * @param newIsDestroying Whether this infiltrator should be destroying a
     * system or not.
     */
    public void setDestroying(final boolean newIsDestroying) {
        this.isDestroying = newIsDestroying;
    }

    /** Gets the list of easy sprites.
     * @return the list of easy sprites.
     */
    public static Array<Sprite> getEasySprites() {
        return easySprites;
    }

    /** Gets the list of hard sprites.
     * @return the list of hard sprites.
     */
    public static Array<Sprite> getHardSprites() {
        return hardSprites;
    }

    /**
     * Gets if infiltrator is destroying. Used for testing.
     * @return if this infiltrator is destroying
     */
    public boolean getIsDestroying(){
        return isDestroying;
    }

    /**
     * Sets if infiltrator is destroying. Used for testing ONLY.
     * @return if this infiltrator is destroying
     */
    public void setIsDestroying(){
        this.isDestroying=true;
    }

     /**
     * Encodes data of all infiltrators into a recognisable string.
     * @param sprites the set of infiltrators to encode.
     * @return the encoded data of the given array of sprites.
     */
    public static String encode(Array<Infiltrator> infiltrators) {
        String r = "";

        Array<Vector2> locations = new Array<>();
        Array<Boolean> isDestroying = new Array<>();
        Array<Boolean> isInvisible = new Array<>();
        Array<Float> timeInvisible = new Array<>();

        for (Infiltrator i : infiltrators) {
            locations.add(new Vector2(i.getX(), i.getY()));
            isDestroying.add(i.isDestroying);
            isInvisible.add(i.isInvisible);
            timeInvisible.add(i.timeInvisible);
        }

        r += locations.toString() + System.lineSeparator() + isDestroying.toString() + System.lineSeparator() + isInvisible.toString() + System.lineSeparator() + timeInvisible.toString();
        
        return r;
    }

    public static void loadFromEncoding(String coordinate, String isDestroying, String invisible, String timesInvisible) {

        String[] splitCoordinates = coordinate.split(",");
        // Remove useless stuff
        for (int i = 0; i < splitCoordinates.length; i++) {
            splitCoordinates[i] = splitCoordinates[i].replace("[", "");
            splitCoordinates[i] = splitCoordinates[i].replace("]", "");
            splitCoordinates[i] = splitCoordinates[i].replace("(", "");
            splitCoordinates[i] = splitCoordinates[i].replace(")", "");
        }

        String[] splitDestroyings = isDestroying.split(",");
        splitDestroyings[0] = splitDestroyings[0].replace("[", "");
        splitDestroyings[splitDestroyings.length - 1] = splitDestroyings[splitDestroyings.length - 1].replace("]", "");
        

    }

}
