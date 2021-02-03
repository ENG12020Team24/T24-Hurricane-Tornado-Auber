
import com.mygdx.auber.entities.KeySystem;
import com.mygdx.auber.entities.KeySystemManager;

import org.junit.Test;

import static org.junit.Assert.*;
import java.util.concurrent.TimeUnit;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.TiledMap;


import org.junit.runner.RunWith;

@RunWith(GdxTestRunner.class)
public class KeySystemTests {
    Vector2 testVector = new Vector2(300,300);
    TmxMapLoader mapLoader = new TmxMapLoader();
    TiledMap map = mapLoader.load("assets/AuberMap.tmx");

    KeySystemManager keySystemManager = new KeySystemManager((TiledMapTileLayer) map.getLayers().get("Systems"));
    KeySystem keySystem_destroy;
    KeySystem keySystem_part_destroy;
    KeySystem keySystem_safe;

    /* Tests whether a system is destroyed if it is being destroyed for 30 seconds.
    It should be.
    */ 
    @Test
    public void system_destroyed_test() throws InterruptedException{
        keySystem_destroy = KeySystemManager.keySystems.get(0);
        keySystem_destroy.startDestroy();
        TimeUnit.SECONDS.sleep(31);
        assertEquals("Error: keySystem not destroyed when it should be." , true, keySystem_destroy.isDestroyed());
    }

    /* Tests whether a system is destroyed if it is being destroyed for 5 seconds.
    It should not be.
    */ 
    @Test
    public void system_not_destroyed_test() throws InterruptedException{
        keySystem_part_destroy = KeySystemManager.keySystems.get(1);
        keySystem_part_destroy.startDestroy();
        TimeUnit.SECONDS.sleep(5);
        assertEquals("Error: keySystem destroyed when it should not be.", false, keySystem_part_destroy.isDestroyed());
    }

    /* Tests whether a system is destroyed if it is not destroyed for 30 seconds.
    It should not be.
    */ 
    @Test
    public void system_safe_test() throws InterruptedException{
        keySystem_safe = KeySystemManager.keySystems.get(1);
        TimeUnit.SECONDS.sleep(30);
        assertEquals("Error: keySystem destroyed when it should not be.", false, keySystem_safe.isDestroyed());
    }

    //KeySystem.destructionTime / 1000

    
}
