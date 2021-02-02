package com.mygdx.auber.Scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.auber.Auber;
import com.mygdx.auber.Screens.PlayScreen;
import com.mygdx.auber.entities.Player;
import com.mygdx.auber.entities.KeySystemManager;

public final class Hud {
    /** Used to handle the viewport and distribute input events. */
    private Stage stage;
    /** Used to handle the camera used for drawing Labels. */
    private Viewport viewport;
    /** Used to hold the Labels in the correct position. */
    private Table hudTable;

    /** Font used to draw labels. */
    private BitmapFont font;

    /** The current number of infiltrators. */
    private static int arrestedInfiltratorCount;
    /** The current number of crewmates. */
    private static int incorrectArrestCount;

    /** The label displaying the number of remaining infiltrators. */
    private Label infiltratorCountLabel;
    /** The label displaying the number of incorrect arrests. */
    private Label incorrectArrestLabel;
    /** The label displaying the number of remaining key systems. */
    private Label keySystemsCountLabel;
    /** The label displaying the player's remaining health. */
    private Label playerHealthLabel;
    /** The PlayScreen the game is operating in. */
    private PlayScreen playScreen;

    /** The value to scale the font by. */
    private static final float FONT_SCALE = 0.5f;

    /** The amount to pad the labels left by. */
    private static final float TABLE_PAD_LEFT = 10;

    /**
     * Creates the HUD using the specified SpriteBatch.
     * @param batch The SpriteBatch used to draw the labels.
     * @param newPlayScreen The PlayScreen the game is operating in.
     * @param p The Player.
     */
    public Hud(final SpriteBatch batch, final PlayScreen newPlayScreen,
        final Player p) {
        arrestedInfiltratorCount = 0;
        incorrectArrestCount = 0;
        this.playScreen = newPlayScreen;
        viewport = new FitViewport(Auber.VIRTUAL_WIDTH, Auber.VIRTUAL_HEIGHT,
            new OrthographicCamera());
        stage = new Stage(viewport, batch);

        hudTable = new Table();
        hudTable.top();
        hudTable.setFillParent(true);

        font = new BitmapFont(Gdx.files.internal("montserrat.fnt"),
            Gdx.files.internal("montserrat.png"), false);
        font.getData().setScale(FONT_SCALE);

        infiltratorCountLabel = new Label(
            String.format("Imposter Arrests: %02d / %02d",
            arrestedInfiltratorCount, PlayScreen.getNumberOfInfiltrators()),
            new Label.LabelStyle(font, Color.GREEN));
        incorrectArrestLabel = new Label(
            String.format("Incorrect Arrests: %02d / %02d",
            incorrectArrestCount, newPlayScreen.getMaxIncorrectArrests()),
            new Label.LabelStyle(font, Color.YELLOW));
        playerHealthLabel = new Label(String.format("Health: %02d",
            (int) p.getHealth()),
            new Label.LabelStyle(font, Color.ORANGE));
        keySystemsCountLabel = new Label(
            String.format("Key systems destroyed: %02d / %02d", 1, 1),
            new Label.LabelStyle(font, Color.YELLOW));

        hudTable.add(infiltratorCountLabel).expandX().left()
            .padLeft(TABLE_PAD_LEFT);
        hudTable.row();
        hudTable.add(incorrectArrestLabel).expandX().left()
            .padLeft(TABLE_PAD_LEFT);
        hudTable.row();
        hudTable.add(keySystemsCountLabel).expandX().left()
            .padLeft(TABLE_PAD_LEFT);
        hudTable.row().bottom().expandY();
        hudTable.add(playerHealthLabel).expandX().left()
            .padLeft(TABLE_PAD_LEFT);
        stage.addActor(hudTable);
    }

    /**
     * Used to update the HUD every frame.
     * @param p The player.
     */
    public void update(final Player p) {
        infiltratorCountLabel.setText(
            String.format("Imposter Arrests: %02d / %02d",
            arrestedInfiltratorCount, PlayScreen.getNumberOfInfiltrators()));
        incorrectArrestLabel.setText(
            String.format("Incorrect Arrests: %02d / %02d",
            incorrectArrestCount, playScreen.getMaxIncorrectArrests()));
        keySystemsCountLabel.setText(
            String.format("Safe key systems: %02d / %02d",
            KeySystemManager.safeKeySystemsCount(),
            KeySystemManager.keySystemsCount()));
        playerHealthLabel.setText(String.format("Health: %02d",
            (int) p.getHealth()));
        if (!Player.canHeal) {
            playerHealthLabel.setStyle(new Label.LabelStyle(font,
                Color.FIREBRICK));
        } else {
            playerHealthLabel.setStyle(new Label.LabelStyle(font,
                Color.YELLOW));
        }
    }

    /** Draws this HUD. */
    public void draw() {
        this.stage.draw();
    }

    /** Returns the current number of incorrect arrests.
     * @return An int containing the current number of incorrect arrests.
     */
    public static int getIncorrectArrestCount() {
        return incorrectArrestCount;
    }

    /** Increments the number of arrested infiltrators. */
    public static void incrementArrestedInfiltrators() {
        arrestedInfiltratorCount += 1;
    }

    /** Increments the number of incorrect arrests. */
    public static void incrementIncorrectArrests() {
        incorrectArrestCount += 1;
    }
}
