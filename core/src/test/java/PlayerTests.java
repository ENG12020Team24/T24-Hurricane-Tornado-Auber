import org.junit.Test;
import static org.junit.Assert.*;

import com.mygdx.auber.Screens.PlayScreen;
import com.mygdx.auber.entities.CrewMembers;
import com.mygdx.auber.entities.Infiltrator;
import com.mygdx.auber.entities.NPC;
import com.mygdx.auber.entities.NPCCreator;
import com.mygdx.auber.entities.Player;
import com.mygdx.auber.Config;
import com.mygdx.auber.Pathfinding.GraphCreator;
import com.mygdx.auber.Pathfinding.MapGraph;
import com.mygdx.auber.Pathfinding.Node;
import com.mygdx.auber.Powerups.ArrestUp;
import com.mygdx.auber.Powerups.FreezeUp;
import com.mygdx.auber.Powerups.HighlightUp;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Colors;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.math.Vector2;

import org.junit.runner.RunWith;
import org.mockito.internal.util.reflection.Whitebox;

@RunWith(GdxTestRunner.class)
public class PlayerTests {
    TmxMapLoader mapLoader = new TmxMapLoader();
    TiledMap map = mapLoader.load("assets/AuberMap.tmx");
    MapGraph mapGraph = new MapGraph();
    Node node = new Node(2416, 2768);
    Node node2 = new Node(2446, 3312);
    Node node3 = new Node(500, 5000);
    Sprite sprite = new Sprite(new Texture("assets/Tutorial3.png")); // for janky reasons
    Player player;
    Array<TiledMapTileLayer> playerCollisionLayers = new Array<>();

    Infiltrator frozen_infiltrator;
    CrewMembers not_frozen_crew;

    /**
     * Tests that players can take damage
     */
    @Test
    public void TakeDamageTest() {
        playerCollisionLayers.add((TiledMapTileLayer) map.getLayers().get("Tile Layer 1"));
        playerCollisionLayers.add((TiledMapTileLayer) map.getLayers().get(2));
        player = new Player(sprite, playerCollisionLayers, true);
        player.takeDamage(30);
        assertEquals("Error: Player can't take damage'", 70, player.getHealth(), 0.000001);
    }

    /**
     * Tests that player can heal
     */
    @Test
    public void HealTest() {
        playerCollisionLayers.add((TiledMapTileLayer) map.getLayers().get("Tile Layer 1"));
        playerCollisionLayers.add((TiledMapTileLayer) map.getLayers().get(2));
        player = new Player(sprite, playerCollisionLayers, true);
        player.takeDamage(30);
        player.heal(20);
        assertEquals("Error: Player can't heal'", 90, player.getHealth(), 0.000001);
    }

    /**
     * Tests that the powerup ArrestUp increases the arrestable range
     */
    @Test
    public void ArrestUpTest() {
        playerCollisionLayers.add((TiledMapTileLayer) map.getLayers().get("Tile Layer 1"));
        playerCollisionLayers.add((TiledMapTileLayer) map.getLayers().get(2));
        player = new Player(sprite, playerCollisionLayers, true);
        player.setPosition(Config.POWERUP_START_X, Config.POWERUP_START_Y);
        ArrestUp arrestUp = new ArrestUp(new Vector2(Config.POWERUP_START_X, Config.POWERUP_START_Y));
        arrestUp.update(player);
        player.update(0.01f);
        assertEquals("Error: ArrestUp powerup doesn't increase arrest range", Config.EXTEND_ARREST_RANGE,
                ((Float) Whitebox.getInternalState(player, "arrestRadius")), 0.1f);
    }

    /**
     * Tests that the powerup FreezeUp freezes infiltrators but not innocent crew members
     */
    @Test
    public void FreezeUpTest() {
        playerCollisionLayers.add((TiledMapTileLayer) map.getLayers().get("Tile Layer 1"));
        playerCollisionLayers.add((TiledMapTileLayer) map.getLayers().get(2));
        player = new Player(sprite, playerCollisionLayers, true);
        mapGraph.addNode(node);
        mapGraph.addNode(node2);
        mapGraph.addNode(node3);
        mapGraph.connectNodes(node, node2);
        mapGraph.connectNodes(node, node3);

        frozen_infiltrator = new Infiltrator(sprite, node, mapGraph);
        not_frozen_crew = new CrewMembers(sprite, node, mapGraph);

        Vector2 not_frozen_velocity = new Vector2(50f,50f);

        frozen_infiltrator.setVelocity(not_frozen_velocity);
        //not_frozen_crew.setVelocity(not_frozen_velocity);
        not_frozen_crew.setGoal(node2,2);

        //System.out.println(not_frozen_crew.getVelocity());
        player.setPosition(Config.POWERUP_START_X, Config.POWERUP_START_Y);
        
        FreezeUp freezeUp = new FreezeUp(new Vector2(Config.POWERUP_START_X, Config.POWERUP_START_Y));

        //System.out.println(not_frozen_crew.getVelocity());
        freezeUp.update(player);
        frozen_infiltrator.step(player,0.01f);
        not_frozen_crew.step(0.01f);
        //System.out.println(not_frozen_crew.getVelocity());
        Vector2 frozen_velocity = new Vector2(0f,0f);

        assertEquals("Error: FreezeUp powerup doesn't freeze infiltrators (still have x velocity)",
            frozen_velocity.x, ((Vector2) Whitebox.getInternalState(frozen_infiltrator, "velocity")).x, 0.1f);
        assertEquals("Error: FreezeUp powerup doesn't freeze infiltrators (still have y velocity)",
            frozen_velocity.y, ((Vector2) Whitebox.getInternalState(frozen_infiltrator, "velocity")).y, 0.1f);
        assertEquals("Error: FreezeUp freezes crew members (don't have x velocity)",
            true, frozen_velocity.x != ((Vector2) Whitebox.getInternalState(not_frozen_crew, "velocity")).x);
        assertEquals("Error: FreezeUp freezes crew members (don't have y velocity)",
            true, frozen_velocity.y != ((Vector2) Whitebox.getInternalState(not_frozen_crew, "velocity")).y);
    }

        /**
     * Tests that the powerup HighlightUp freezes infiltrators
     */
    @Test
    public void HighlightUpTest(){
        playerCollisionLayers.add((TiledMapTileLayer) map.getLayers().get("Tile Layer 1"));
        playerCollisionLayers.add((TiledMapTileLayer) map.getLayers().get(2));
        player = new Player(sprite, playerCollisionLayers,true);
        player.setPosition(Config.POWERUP_START_X, Config.POWERUP_START_Y);
        HighlightUp highlightUp = new HighlightUp(new Vector2(Config.POWERUP_START_X, Config.POWERUP_START_Y));
        highlightUp.update(player);
        //highlighted_infiltrator=
        // assertEquals("Error: FreezeUp powerup doesn't freeze infiltrators (still have x velocity)",
        //     " ", ((Vector2) Whitebox.getInternalState(player, "velocity")).x, 0.1f);
    }

}
