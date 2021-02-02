import org.junit.Test;
import static org.junit.Assert.*;
import java.beans.Transient;

import com.mygdx.auber.Screens.PlayScreen;
import com.mygdx.auber.Auber;

public class InfiltratorTests {
    //HEEEELP
    @Test
    public void NumberOfInfiltratorsTest(){
        assertEquals(PlayScreen.numberOfInfiltrators,8);
    }
    public void aaa(){
        assertEquals(PlayScreen.numberOfInfiltrators,8);
    }

}