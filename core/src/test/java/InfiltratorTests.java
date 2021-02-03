import org.junit.Test;
import static org.junit.Assert.*;
//import java.beans.Transient;

import com.mygdx.auber.Screens.PlayScreen;
import com.mygdx.auber.entities.Infiltrator;
import com.mygdx.auber.entities.NPC;
import com.mygdx.auber.entities.NPCCreator;
//import com.mygdx.auber.Auber;
import com.mygdx.auber.Pathfinding.GraphCreator;
import com.mygdx.auber.Pathfinding.MapGraph;
import com.mygdx.auber.Pathfinding.Node;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;

import org.junit.runner.RunWith;

@RunWith(GdxTestRunner.class)
public class InfiltratorTests {
    TmxMapLoader mapLoader = new TmxMapLoader();
    TiledMap map = mapLoader.load("assets/AuberMap.tmx");
    //GraphCreator graphCreator; // = new GraphCreator( (TiledMapTileLayer) map.getLayers().get("Tile Layer 1"));
    MapGraph mapGraph = new MapGraph();
    Node node = new Node(2416, 2768);
    Node node2 = new Node(2416, 3312);

    Infiltrator infiltrator_invisible;
    Sprite doctor=new Sprite(new Texture("assets/Doctor.png"));


    public void main(String[] args){
        //Infiltrator.createInfiltratorSprites();

        // for (int i = 0; i < 8; i++) {
        //     if (i == 7) {
        //         NPCCreator.createInfiltrator(Infiltrator.hardSprites.random(), 
        //                 MapGraph.getRandomNode(),
        //                 graphCreator.getMapGraph());
        //         break;
        //     }
        //     NPCCreator.createInfiltrator(
        //         Infiltrator.easySpri/tes.random(), MapGraph.getRandomNode(),
        //         graphCreator.getMapGraph());
        // }

        // infiltrator_invisible = NPCCreator.infiltrators.get(0);
    }

    @Test
    public void NumberOfInfiltratorsTest(){
        assertEquals("Error: Not 8 infiltrators", PlayScreen.getNumberOfInfiltrators(),8);
    }

    @Test
    public void InvisibilityTest(){
        mapGraph.addNode(node);
        mapGraph.addNode(node2);
        mapGraph.connectNodes(node, node2);
        infiltrator_invisible=new Infiltrator(doctor, node, mapGraph);
        //infiltrator_invisible.destroyKeySystem();;
        //infiltrator_invisible.isDestroying
        //infiltrator_invisible.goInvisible();
        //assertEquals("Error: aaa", false,infiltrator_invisible.isDestroying);
    }
}