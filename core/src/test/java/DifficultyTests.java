
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;

import static org.junit.Assert.*;
import java.beans.Transient;

import com.mygdx.auber.Screens.ChooseDifficultyScreen;
import com.mygdx.auber.Screens.PlayScreen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.auber.Auber;


@RunWith(GdxTestRunner.class)
public class DifficultyTests {
    @Before
    public void before(){
        Gdx.gl = Mockito.mock(GL20.class);
        //SpriteBatch = Mockito.mock
    }

    @Test
    public void IncorrectArrestsTest() {
        //Gdx.gl = Mockito.mock(GL20.class);
        //gl = ""
        //Gdx.gl = Mockito.spy(Gdx.gl);
        Auber a = new Auber();
        SpriteBatch spriteBatch = Mockito.mock(SpriteBatch.class);
        //a.create();
        Whitebox.setInternalState(a, "batch", spriteBatch);
        //setScreen()
        //ChooseDifficultyScreen s = new ChooseDifficultyScreen(a);
        PlayScreen playTest_easy = new PlayScreen(a, false, 0);
        PlayScreen playTest_medium = new PlayScreen(a, false, 0);
        PlayScreen playTest_hard = new PlayScreen(a, false, 0);
        assertEquals("Error: Easy difficulty does not allow exactly 9 mistaken arrests", 9, ((int) Whitebox.getInternalState(playTest_easy, "maxIncorrectArrests")));
        assertEquals("Error: Medium difficulty  does not allow exactly 6 mistaken arrests", 6, ((int) Whitebox.getInternalState(playTest_medium, "maxIncorrectArrests")));
        assertEquals("Error: Hard Difficulty  does not allow exactly 3 mistaken arrests", 3, ((int) Whitebox.getInternalState(playTest_hard, "maxIncorrectArrests")));

    }
}
