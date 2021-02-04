import org.junit.Test;
import static org.junit.Assert.*;
//import java.beans.Transient;

import com.mygdx.auber.Screens.PlayScreen;
import com.mygdx.auber.entities.Infiltrator;
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

//import java.util.concurrent.TimeUnit;

import org.junit.runner.RunWith;

@RunWith(GdxTestRunner.class)
public class InfiltratorTests {
    TmxMapLoader mapLoader = new TmxMapLoader();
    TiledMap map = mapLoader.load("assets/AuberMap.tmx");
    //GraphCreator graphCreator; // = new GraphCreator( (TiledMapTileLayer) map.getLayers().get("Tile Layer 1"));
    MapGraph mapGraph = new MapGraph();
    Node node = new Node(2416, 2768);
    Node node2 = new Node(2416, 3312);
    Infiltrator infiltrator_invisible, infiltrator_damage, infiltrator_stop_healing;
    Sprite doctor=new Sprite(new Texture("assets/Tutorial3.png"));
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
        player = new Player(doctor, playerCollisionLayers,true);
        mapGraph.addNode(node);
        mapGraph.addNode(node2);
        mapGraph.connectNodes(node, node2);
        infiltrator_damage=new Infiltrator(doctor, node, mapGraph);
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
        player = new Player(doctor, playerCollisionLayers,false);
        mapGraph.addNode(node);
        mapGraph.addNode(node2);
        mapGraph.connectNodes(node, node2);
        infiltrator_stop_healing=new Infiltrator(doctor, node, mapGraph);
        infiltrator_stop_healing.stopAuberHealing();
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
        player = new Player(doctor, playerCollisionLayers,true);
        mapGraph.addNode(node);
        mapGraph.addNode(node2);
        mapGraph.connectNodes(node, node2);
        infiltrator_stop_healing=new Infiltrator(doctor, node, mapGraph);
        infiltrator_stop_healing.stopAuberHealing();
        player.takeDamage(30);
        player.update(16);
        player.heal(0);
        assertEquals("Error: Player can heal whilst healing is blocked",
            100, player.getHealth(),0.000001);
    }

    /**
     * Tests that the infiltrator can go invisible and no longer be seen
     */
    @Test
    public void InfiltratorTurnInvisible(){
        mapGraph.addNode(node);
        mapGraph.addNode(node2);
        mapGraph.connectNodes(node, node2);
        infiltrator_invisible=new Infiltrator(doctor, node, mapGraph);
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
        mapGraph.addNode(node);
        mapGraph.addNode(node2);
        mapGraph.connectNodes(node, node2);
        infiltrator_invisible=new Infiltrator(doctor, node, mapGraph);
        infiltrator_invisible.setIsDestroying();
        infiltrator_invisible.goInvisible();
        assertEquals("Error: Infiltrator still destroying whislt invisible", 
            false,infiltrator_invisible.getIsDestroying());
    }
}