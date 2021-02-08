import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.concurrent.TimeUnit;

import com.mygdx.auber.Screens.PlayScreen;
import com.mygdx.auber.entities.CrewMembers;
import com.mygdx.auber.entities.Infiltrator;
import com.mygdx.auber.entities.KeySystemManager;
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
import com.mygdx.auber.Powerups.ShieldUp;
import com.mygdx.auber.Powerups.SpeedUp;
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
    Sprite sprite = new Sprite(new Texture("assets/Tutorial3.png")); // for janky reasons
    Player player;
    Array<TiledMapTileLayer> playerCollisionLayers = new Array<>();

    KeySystemManager keySystemManager = new KeySystemManager((TiledMapTileLayer) map.getLayers().get("Systems"));
    GraphCreator graphCreator = new GraphCreator( (TiledMapTileLayer) map.getLayers().get("Tile Layer 1"));

    Infiltrator frozen_infiltrator, infiltrator_damage_system, highlighted_infiltrator;
    CrewMembers not_frozen_crew;
    @Before
    public void before(){
        
    }

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
        MapGraph mapGraph = graphCreator.getMapGraph();
        playerCollisionLayers.add((TiledMapTileLayer) map.getLayers().get("Tile Layer 1"));
        playerCollisionLayers.add((TiledMapTileLayer) map.getLayers().get(2));
        player = new Player(sprite, playerCollisionLayers, true);

        frozen_infiltrator = new Infiltrator(sprite, mapGraph.getRandomNode(), mapGraph);
        not_frozen_crew = new CrewMembers(sprite, mapGraph.getRandomNode(), mapGraph);

        Vector2 not_frozen_velocity = new Vector2(50f,50f);

        frozen_infiltrator.setVelocity(not_frozen_velocity);
        Node node = mapGraph.getRandomNode();


        not_frozen_crew.setGoal(node,30);

        player.setPosition(Config.POWERUP_START_X, Config.POWERUP_START_Y);
        
        FreezeUp freezeUp = new FreezeUp(new Vector2(Config.POWERUP_START_X, Config.POWERUP_START_Y));

        freezeUp.update(player);
        frozen_infiltrator.step(player,0.01f);
        not_frozen_crew.step(0.01f);

        Vector2 frozen_velocity = new Vector2(0f,0f);

        assertEquals("Error: FreezeUp powerup doesn't freeze infiltrators (still have x velocity)",
            frozen_velocity.x, ((Vector2) Whitebox.getInternalState(frozen_infiltrator, "velocity")).x, 0.1f);
        assertEquals("Error: FreezeUp powerup doesn't freeze infiltrators (still have y velocity)",
            frozen_velocity.y, ((Vector2) Whitebox.getInternalState(frozen_infiltrator, "velocity")).y, 0.1f);
        assertNotEquals("Error: FreezeUp freezes crew members.",
            true,
            frozen_velocity.x == ((Vector2) Whitebox.getInternalState(not_frozen_crew, "velocity")).x &&
            frozen_velocity.y == ((Vector2) Whitebox.getInternalState(not_frozen_crew, "velocity")).y);
    }


    /**
     * Tests that the powerup HighlightUp highlights infiltrators
     */
    @Test
    public void HighlightUpTest(){
        MapGraph mapGraph = graphCreator.getMapGraph();
        playerCollisionLayers.add((TiledMapTileLayer) map.getLayers().get("Tile Layer 1"));
        playerCollisionLayers.add((TiledMapTileLayer) map.getLayers().get(2));
        player = new Player(sprite, playerCollisionLayers,true);
        player.setPosition(Config.POWERUP_START_X, Config.POWERUP_START_Y);
        highlighted_infiltrator = new Infiltrator(sprite, mapGraph.getRandomNode(), mapGraph);
        HighlightUp highlightUp = new HighlightUp(new Vector2(Config.POWERUP_START_X, Config.POWERUP_START_Y));
        highlightUp.update(player);
        highlighted_infiltrator.step(player, 0.1f);
        assertEquals("Error: HighlightUp powerup doesn't highlight infiltrators.",
            true, Infiltrator.isHighlighted());
    }

    /**
     * Tests that the powerup SheildUp stops the player from taking damage during
     * duration
     * 
     * @throws InterruptedException
     */
    @Test
    public void SheildUpTest(){
        MapGraph mapGraph = graphCreator.getMapGraph();
        playerCollisionLayers.add((TiledMapTileLayer) map.getLayers().get("Tile Layer 1"));
        playerCollisionLayers.add((TiledMapTileLayer) map.getLayers().get(2));
        player = new Player(sprite, playerCollisionLayers,true);
        player.setPosition(Config.POWERUP_START_X, Config.POWERUP_START_Y);
        highlighted_infiltrator = new Infiltrator(sprite, mapGraph.getRandomNode(), mapGraph);
        ShieldUp shieldUp = new ShieldUp(new Vector2(Config.POWERUP_START_X, Config.POWERUP_START_Y));
        shieldUp.update(player);
        player.takeDamage(33);
        player.update(0.01f);
        float healthDuringShield = player.getHealth();
        player.shieldUp(false);
        player.takeDamage(33);
        float healthAfterShield = player.getHealth();

        assertEquals("Error: ShieldUp powerup doesn't protect player from damage.",
            100, healthDuringShield, 0.01f);
        assertEquals("Error: ShieldUp powerup doesn't stop working.",
            67, healthAfterShield, 0.01f);
    }

    /**
     * Tests that the powerup SpeedUp increases the player speed.
     */
    @Test
    public void SpeedUpTest(){
        MapGraph mapGraph = graphCreator.getMapGraph();
        playerCollisionLayers.add((TiledMapTileLayer) map.getLayers().get("Tile Layer 1"));
        playerCollisionLayers.add((TiledMapTileLayer) map.getLayers().get(2));
        player = new Player(sprite, playerCollisionLayers,true);
        player.setPosition(Config.POWERUP_START_X, Config.POWERUP_START_Y);
        highlighted_infiltrator = new Infiltrator(sprite, mapGraph.getRandomNode(), mapGraph);
        SpeedUp speedUp = new SpeedUp(new Vector2(Config.POWERUP_START_X, Config.POWERUP_START_Y));
        speedUp.update(player);
        player.update(0.01f);

        assertEquals("Error: Speedup powerup doesn't speed up the player.",
            150, (float) Whitebox.getInternalState(player, "speed"), 0.01f);
    }
}
