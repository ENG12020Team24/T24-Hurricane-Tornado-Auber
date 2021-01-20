package auber.com.mygdx.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.Map;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import auber.com.mygdx.Pathfinding.GraphCreator;
import auber.com.mygdx.Pathfinding.MapGraph;
import auber.com.mygdx.Pathfinding.*;
import auber.com.mygdx.Screens.PlayScreen;

public class Infiltrator extends NPC{
    public boolean isDestroying = false;
    public double timeToWait = Math.random() * 5;

    private float timeInvisible;
    private boolean isInvisible = false;

    public static Array<Sprite> easySprites = new Array<>();
    public static Array<Sprite> hardSprites = new Array<>();

    public Infiltrator(Sprite sprite, Node node, MapGraph mapGraph) {
        super(sprite, node, mapGraph);
        this.setPosition(node.x, node.y);
    }

    /**
     * Step needs to be called in the update method, makes the NPC move and check if it has reached its next node
     */
    public void step(float delta)
    {
        this.moveNPC(); //Moves the npc and sets their scale

        if(isDestroying)
        {
            KeySystem keySystem = KeySystemManager.getClosestKeySystem(previousNode.x, previousNode.y);

            if(keySystem.isDestroyed())
            {
                this.isDestroying = false;
                this.pathQueue.clear();
                this.setGoal(MapGraph.getRandomNode());
            }

            if(Vector2.dst(Player.x, Player.y, this.getX(), this.getY()) < 250)
            {
                keySystem.stopDestroy();
                this.useAbility();
                this.isDestroying = false;
            }
        } //If isDestroying, if the distance to the player is less than 250, use ability and stop destroying, else keep adding time

        if(isInvisible)
        {
            timeInvisible += delta;
            if(timeInvisible  > 10)
            {
                this.isInvisible = false;
            }
        }
        else
        {
            timeInvisible = 0;
            this.setAlpha(.99f);
        } //If isInvisible, keep adding time to timeInvisible, and if its longer than 10 seconds set isInvisible to false. If not timeInvisible, set the alpha to 1 and time to 0

        this.elapsedTime += delta;
        this.checkCollision(); //Add elapsed time and check collisions

        //this.collision.checkForCollision(this, layer, this.velocity, collision);

        //System.out.println(elapsedTime + " - " + timeToWait);
        if((this.elapsedTime >= timeToWait) && this.pathQueue.isEmpty()) {
            this.elapsedTime = 0;
            reachDestination();
        } //If there is no queue and elapsed time is greater than time to wait, reach destination
    }

    /**
     * Called when the path queue is empty
     */
    @Override
    public void reachDestination()
    {
        this.velocity.x = 0;
        this.velocity.y = 0;
        timeToWait = Math.random() * 5;
        double chance = 1 / (double) NPCCreator.infiltrators.size;

        if((Math.random() - 0.2f < chance)  && !this.isDestroying && !this.isInvisible && KeySystemManager.safeKeySystemsCount() != 0) // 1/10 chance of infiltrator deciding to destroy a keysystem
        {
            this.destroyKeySystem();
            return;
        } //If not invisible or currently destroying a key system, random chance to go destroying a key system

        if(pathQueue.size == 0 && GraphCreator.keySystemsNodes.contains(this.previousNode, true))
        {

            KeySystem keySystem = KeySystemManager.getClosestKeySystem(previousNode.x, previousNode.y);
            if(keySystem == null)
            {
                this.isDestroying = false;
                setGoal(MapGraph.getRandomNode());
                return;
            }
            if(keySystem.isSafe())
            {
                this.isDestroying = true;
                keySystem.startDestroy();
                timeToWait = KeySystem.destructionTime / 1000;
                return;
            }
        } //If no queue, and the last node in queue was a key systems node, start destroying

        Node newGoal;
        do {
            newGoal = MapGraph.nodes.random();
        }while(newGoal == previousNode);
        {
            setGoal(newGoal);
        } //Set a new goal node and start moving towards it

    }

    /**
     * Starts destroying a random keySystem, moves towards it, sets isDestroying to true
     */
    public void destroyKeySystem()
    {
        this.pathQueue.clear();
        Node keySystemNode = GraphCreator.keySystemsNodes.random();
        KeySystem keySystem = KeySystemManager.getClosestKeySystem(keySystemNode.x, keySystemNode.y);

        if((keySystem.isDestroyed() || keySystem.isBeingDestroyed()) && KeySystemManager.safeKeySystemsCount() != 0)
        {
            destroyKeySystem();
        }
        else
        {
            this.setGoal(keySystemNode);
        } //If Key system is being destroyed or is already destroyed, select a new key system
    }

    /**
     * Causes the infiltrator to use a random ability
     */
    public void useAbility()
    {
        double chance = Math.random() * 3;

        if(!this.isDestroying)
        {
            return;
        }
        if(chance < 1)
        {
            this.goInvisible();
        }
        else if(chance >= 1 && chance < 2)
        {
            this.damageAuber(15);
        }
        else
        {
            this.stopAuberHealing();
        } // 1/3 chance of using each ability

        this.pathQueue.clear();
        this.setGoal(MapGraph.getRandomNode()); //After using an ability, go somewhere random
    }

    /**
     * Sets the sprite alpha to 0, records the time the invisibility started, sets isInvisible to true
     */
    private void goInvisible()
    {
        this.isInvisible = true;
        this.isDestroying = false;
        this.timeInvisible = 0;
        this.setAlpha(0.05f);
    }

    /**
     * Damages Auber by an amount
     * @param amount Int amount of damage to deal
     */
    private void damageAuber(int amount)
    {
        Player.takeDamage(amount);
    }

    /**
     * Sets canHeal to false in player, records the time at which he stopped being able to heal
     */
    private void stopAuberHealing()
    {
        //System.out.println("Stopped healing");
        Player.canHeal = false;
        Player.healStopTime = 0;
    }

    /**
     * Fills out the array of sprites available for the infiltrators to take
     */
    public static void createInfiltratorSprites()
    {
        Infiltrator.easySprites.add(new Sprite(new Texture("Doctor.png")));
        Infiltrator.easySprites.add(new Sprite(new Texture("InfiltratorEngineer.png")));
        Infiltrator.easySprites.add(new Sprite(new Texture("InfiltratorAlien.png")));

        Infiltrator.hardSprites.add(new Sprite(new Texture("AlienStand.png")));
        Infiltrator.hardSprites.add(new Sprite(new Texture("HumanStand.png")));
    }

    public void setIndex(int index)
    {
        this.index = index;
    }

    public void dispose()
    {
        easySprites.clear();
        //hardSprites.clear();
    }
}

