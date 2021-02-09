import org.junit.Test;
import static org.junit.Assert.*;
//import java.beans.Transient;

import com.mygdx.auber.Screens.PlayScreen;
import com.mygdx.auber.entities.Infiltrator;
import com.mygdx.auber.entities.KeySystemManager;
import com.mygdx.auber.entities.NPC;
import com.mygdx.auber.entities.NPCCreator;
import com.mygdx.auber.entities.Player;
//import com.mygdx.auber.Auber;
import com.mygdx.auber.Pathfinding.GraphCreator;
import com.mygdx.auber.Pathfinding.MapGraph;
import com.mygdx.auber.Pathfinding.Node;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Colors;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.Array;


import org.junit.runner.RunWith;

@RunWith(GdxTestRunner.class)
public class InfiltratorTests {
    TmxMapLoader mapLoader = new TmxMapLoader();
    TiledMap map = mapLoader.load("assets/AuberMap.tmx");
    KeySystemManager keySystemManager = new KeySystemManager((TiledMapTileLayer) map.getLayers().get("Systems"));
    GraphCreator graphCreator = new GraphCreator( (TiledMapTileLayer) map.getLayers().get("Tile Layer 1"));    
    MapGraph mapGraph = graphCreator.getMapGraph();
    Infiltrator infiltrator_invisible, infiltrator_damage, infiltrator_stop_healing, infiltrator_damage_system;
    Sprite sprite=new Sprite(new Texture("assets/Tutorial3.png")); //for janky reasons
    Player player;
    Array<TiledMapTileLayer> playerCollisionLayers = new Array<>();

    /***
     * Tests that there are exactly 8 infiltrators
     */
    @Test
    public void NumberOfInfiltratorsTest(){
        //System.out.println(doctor.getHeight());
        assertEquals("Error: Not 8 infiltrators", PlayScreen.getNumberOfInfiltrators(),8);
    }

    /**
     * Tests that the infiltrator can damage the player when they have that ability
     */
    @Test
    public void InfiltratorDamagePlayer(){
        playerCollisionLayers.add((TiledMapTileLayer) map.getLayers().get("Tile Layer 1"));
        playerCollisionLayers.add((TiledMapTileLayer) map.getLayers().get(2));
        player = new Player(sprite, playerCollisionLayers,true);
        infiltrator_damage=new Infiltrator(sprite, mapGraph.getRandomNode(), mapGraph, false);
        infiltrator_damage.damageAuber(player,Infiltrator.AUBER_DAMAGE_VALUE);
        assertEquals("Error: Player not damaged by infiltrator",
            100-Infiltrator.AUBER_DAMAGE_VALUE,player.getHealth(),0.000001);
    }

    /**
     * Tests that the infiltrator can stop the player from healing
     */
    @Test
    public void InfiltratorStopPlayerHealing(){
        playerCollisionLayers.add((TiledMapTileLayer) map.getLayers().get("Tile Layer 1"));
        playerCollisionLayers.add((TiledMapTileLayer) map.getLayers().get(2));
        player = new Player(sprite, playerCollisionLayers,false);
        infiltrator_stop_healing=new Infiltrator(sprite, mapGraph.getRandomNode(), mapGraph, false);
        infiltrator_stop_healing.stopAuberHealing(player);
        player.takeDamage(30);
        player.heal(20);
        assertEquals("Error: Player can heal whilst healing is blocked",
            70, player.getHealth(),0.000001);
    }

    /**
     * Tests that the stopping of healing isn't permenant (takes 15 seconds)
     */
    @Test
    public void InfiltratorStopPlayerHealingNonPermentant(){
        playerCollisionLayers.add((TiledMapTileLayer) map.getLayers().get("Tile Layer 1"));
        playerCollisionLayers.add((TiledMapTileLayer) map.getLayers().get(2));
        player = new Player(sprite, playerCollisionLayers,false);
        infiltrator_stop_healing=new Infiltrator(sprite, mapGraph.getRandomNode(), mapGraph, false);
        infiltrator_stop_healing.stopAuberHealing(player);
        player.takeDamage(30);
        player.update(16);
        player.heal(20);
        assertEquals("Error: Player can heal whilst healing is blocked",
            true, 90 <= player.getHealth());
    }

    /**
     * Tests that the stopping of healing isn't permenant (takes 15 seconds) for demo mode
     */
    @Test
    public void InfiltratorStopPlayerHealingNonPermentantDemo(){
        playerCollisionLayers.add((TiledMapTileLayer) map.getLayers().get("Tile Layer 1"));
        playerCollisionLayers.add((TiledMapTileLayer) map.getLayers().get(2));
        player = new Player(sprite, playerCollisionLayers,true);
        infiltrator_stop_healing=new Infiltrator(sprite, mapGraph.getRandomNode(), mapGraph, false);
        infiltrator_stop_healing.stopAuberHealing(player);
        player.takeDamage(30);
        player.update(16);
        player.heal(20);
        assertEquals("Error: Player can't heal when not blocked",
            90, player.getHealth(), 3);
    }

    /**
     * Tests that the infiltrator can go invisible and no longer be seen
     */
    @Test
    public void InfiltratorTurnInvisible(){
        infiltrator_invisible=new Infiltrator(sprite, mapGraph.getRandomNode(), mapGraph, false);
        infiltrator_invisible.goInvisible();
        assertEquals("Error: Infiltrator still visible whislt invisible", 
            "ffffff0c",infiltrator_invisible.getColor().toString());
            //ffffff0c means that the sprite doesn't have a colour tint but
            //is far far fainter, almost invisible.
    }

    /**
     * Tests that infiltrators stop destroing when going invisible 
     */
    @Test
    public void NotDestroyingWhilstInvisible(){
        infiltrator_invisible=new Infiltrator(sprite, mapGraph.getRandomNode(), mapGraph, false);
        infiltrator_invisible.setIsDestroying();
        infiltrator_invisible.goInvisible();
        assertEquals("Error: Infiltrator still destroying whislt invisible", 
            false,infiltrator_invisible.getIsDestroying());
    }

    // /**
    //  * Tests that the infiltrators can damage systems
    //  */
    // @Test
    // public void DestroySystemsTest() {
    //     MapGraph mapGraph = graphCreator.getMapGraph();

    //     playerCollisionLayers.add((TiledMapTileLayer) map.getLayers().get("Tile Layer 1"));
    //     playerCollisionLayers.add((TiledMapTileLayer) map.getLayers().get(2));
    //     player = new Player(sprite, playerCollisionLayers, true);

    //     infiltrator_damage_system = new Infiltrator(sprite, mapGraph.getRandomNode(), mapGraph);

    //     infiltrator_damage_system.destroyKeySystem();
    //     infiltrator_damage_system.setIsDestroying();

    //     for (int i=0; i==10000000; i++){
    //         infiltrator_damage_system.step(player,100000);
    //     }

    //     // System.out.println(KeySystemManager.beingDestroyedKeySystemsCount());
    //     // System.out.println(KeySystemManager.destroyedKeySystemsCount());
    //     // assertEquals("Error: Infiltrators cannot detroy systems",
    //     //     true, KeySystemManager.beingDestroyedKeySystemsCount() > 0 &&  KeySystemManager.destroyedKeySystemsCount() > 0);
    // }

}
