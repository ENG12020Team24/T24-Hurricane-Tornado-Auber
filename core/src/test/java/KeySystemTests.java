
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
    // core\assets\AuberMap.tmx
    TiledMap map = mapLoader.load("assets/AuberMap.tmx");
    //TiledMapTileLayer systemLayer = (TiledMapTileLayer) map.getLayers().get("Systems");
    //TiledMapTileLayer.Cell cell = systemLayer.getCell(25, 77);
    //KeySystem keySystem = new KeySystem(cell,"test",testVector);

    KeySystemManager keySystemManager = new KeySystemManager((TiledMapTileLayer) map.getLayers().get("Systems"));
    KeySystem keySystem_destroy;
    KeySystem keySystem_part_destroy;
    KeySystem keySystem_safe;

    @Test
    public void system_destroyed_test() throws InterruptedException{
        keySystem_destroy = KeySystemManager.keySystems.get(0);
        keySystem_destroy.startDestroy();
        TimeUnit.SECONDS.sleep(31);
        assertEquals(true, keySystem_destroy.isDestroyed());
    }

    @Test
    public void system_not_destroyed_test() throws InterruptedException{
        keySystem_part_destroy = KeySystemManager.keySystems.get(1);
        keySystem_part_destroy.startDestroy();
        TimeUnit.SECONDS.sleep(5);
        assertEquals(false, keySystem_part_destroy.isDestroyed());
    }
    
    @Test
    public void system_safe_test() throws InterruptedException{
        keySystem_safe = KeySystemManager.keySystems.get(2);
        TimeUnit.SECONDS.sleep(30);
        assertEquals(false, keySystem_safe.isDestroyed());
    }


    //KeySystem.destructionTime / 1000

    
}
