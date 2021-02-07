
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.internal.util.reflection.Whitebox;

import static org.junit.Assert.*;
import java.beans.Transient;

import com.mygdx.auber.Screens.ChooseDifficultyScreen;
import com.mygdx.auber.Screens.PlayScreen;
import com.mygdx.auber.Auber;


@RunWith(GdxTestRunner.class)
public class DifficultyTests {
    @Test
    public void IncorrectArrestsTest() {
        Auber a = new Auber();
        //ChooseDifficultyScreen s = new ChooseDifficultyScreen(a);
        PlayScreen playTest = new PlayScreen(a, false, 0);
        int incorrectArrestTest = 3;
        assertEquals(incorrectArrestTest, ((int) Whitebox.getInternalState(playTest, "maxIncorrectArrests")));
    }
}
