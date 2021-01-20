package auber.com.mygdx.Scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import auber.com.mygdx.Auber;
import auber.com.mygdx.Screens.PlayScreen;
import auber.com.mygdx.entities.*;

public class Hud {
    public Stage stage;//2D scene graph, handles viewport and distributes input events.
    private Viewport viewport;
    private Table hudTable;

    BitmapFont font;

    public static Integer ImposterCount;
    public static Integer CrewmateCount;

    Label imposterCountLabel;
    Label crewmateCountLabel;
    Label keySystemsCountLabel;
    Label playerHealthLabel;

    public Hud(SpriteBatch spritebatch){
        ImposterCount = 0;
        CrewmateCount = 0;

        viewport = new FitViewport(Auber.VirtualWidth, Auber.VirtualHeight, new OrthographicCamera());
        stage = new Stage(viewport, spritebatch);

        hudTable = new Table();
        hudTable.top();
        hudTable.setFillParent(true);

        font = new BitmapFont(Gdx.files.internal("montserrat.fnt"), Gdx.files.internal("montserrat.png"), false);
        font.getData().setScale(.5f);

        imposterCountLabel = new Label(String.format("Imposter Arrests: %02d / %02d", ImposterCount, PlayScreen.numberOfInfiltrators), new Label.LabelStyle(font, Color.GREEN));
        crewmateCountLabel = new Label(String.format("Incorrect Arrests: %02d / %02d", CrewmateCount, PlayScreen.maxIncorrectArrests), new Label.LabelStyle(font, Color.YELLOW));
        playerHealthLabel = new Label(String.format("Health: %02d", (int) Player.health), new Label.LabelStyle(font, Color.ORANGE));
        keySystemsCountLabel = new Label(String.format("Key systems destroyed: %02d / %02d", 1, 1), new Label.LabelStyle(font, Color.YELLOW));

        hudTable.add(imposterCountLabel).expandX().left().padLeft(10);
        hudTable.row();
        hudTable.add(crewmateCountLabel).expandX().left().padLeft(10);
        hudTable.row();
        hudTable.add(keySystemsCountLabel).expandX().left().padLeft(10);
        hudTable.row().bottom().expandY();
        hudTable.add(playerHealthLabel).expandX().left().padLeft(10);


        stage.addActor(hudTable);
    }

    public void update() {
        imposterCountLabel.setText(String.format("Imposter Arrests: %02d / %02d", ImposterCount, PlayScreen.numberOfInfiltrators));
        crewmateCountLabel.setText(String.format("Incorrect Arrests: %02d / %02d", CrewmateCount, PlayScreen.maxIncorrectArrests));
        keySystemsCountLabel.setText(String.format("Safe key systems: %02d / %02d", KeySystemManager.safeKeySystemsCount(), KeySystemManager.keySystemsCount()));
        playerHealthLabel.setText(String.format("Health: %02d",(int) Player.health));
        if(!Player.canHeal)
        {
            playerHealthLabel.setStyle(new Label.LabelStyle(font, Color.FIREBRICK));
        }
        else
        {
            playerHealthLabel.setStyle(new Label.LabelStyle(font, Color.YELLOW));
        }
    }

}
