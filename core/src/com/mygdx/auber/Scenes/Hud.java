package com.mygdx.auber.Scenes;

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
import com.mygdx.auber.Auber;
import com.mygdx.auber.entities.CrewMembers;
import com.mygdx.auber.entities.KeySystem;
import com.mygdx.auber.entities.KeySystemManager;
import com.mygdx.auber.entities.Player;


public class Hud {
    public Stage stage;//2D scene graph, handles viewport and distributes input events.
    private Viewport viewport;
    private Table hudTable;

    public static Integer ImposterCount;
    public static Integer CrewmateCount;

    Array<KeySystem> keySystems = new Array<>();
    Array<Label> keySystemAlertLabels = new Array<>();

    Label imposterCountLabel;
    Label crewmateCountLabel;
    Label playerHealthLabel;

    public Hud(SpriteBatch spritebatch){
        ImposterCount = 0;
        CrewmateCount = 0;

        viewport = new FitViewport(Auber.VirtualWidth, Auber.VirtualHeight, new OrthographicCamera());
        stage = new Stage(viewport, spritebatch);

        hudTable = new Table();
        hudTable.top();
        hudTable.setFillParent(true);

        imposterCountLabel = new Label(String.format("Imposter Arrests: %02d", ImposterCount), new Label.LabelStyle(new BitmapFont(), Color.RED));
        crewmateCountLabel = new Label(String.format("Crewmate Arrests: %02d", CrewmateCount), new Label.LabelStyle(new BitmapFont(), Color.RED));
        playerHealthLabel = new Label(String.format("Health: %02d", Player.health), new Label.LabelStyle(new BitmapFont(), Color.RED));

        hudTable.add(imposterCountLabel).expandX().left().padLeft(10);
        hudTable.add(crewmateCountLabel).expandX().right().padRight(10);

        hudTable.row().bottom().expandY();
        hudTable.add(playerHealthLabel).expandX().left().padLeft(10);

        stage.addActor(hudTable);
    }

    public void update() {
        imposterCountLabel.setText(String.format("Imposter Arrests: %02d", ImposterCount));
        crewmateCountLabel.setText(String.format("Crewmate Arrests: %02d", CrewmateCount));
        playerHealthLabel.setText(String.format("Health: %02d", Player.health));

        for (KeySystem keySystem :
                KeySystemManager.getBeingDestroyedKeySystems()) {
            if (!keySystems.contains(keySystem, false)) {
                keySystems.add(keySystem);
                keySystemAlertLabels.add(new Label(String.format("Alert! Key System being destroyed: %s", keySystem.name), new Label.LabelStyle(new BitmapFont(), Color.RED)));
            }
        }
    }

}
