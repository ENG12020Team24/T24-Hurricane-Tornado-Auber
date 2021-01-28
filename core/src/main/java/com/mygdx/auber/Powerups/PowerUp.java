package com.mygdx.auber.Powerups;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.auber.entities.Player;

public abstract class PowerUp extends Sprite {
    public Vector2 position = new Vector2(0,0);
    public static int type;
    public boolean taken;
    public boolean used;
    public static final int DIAMETRE = 32;
    public float r,g,b;

    public PowerUp (int type, Vector2 position){
        this.type = type;
        this.position = position;
        this.taken = false;
        this.used = false;
    }

    public abstract void update(Player player);

    public boolean playerCollision(float playerx, float playery, float playerwidth, float playerheight){
        if (playerx >= position.x+DIAMETRE || playerx+playerwidth <= position.x){
            return false;
        }
        if (playery >= position.y+DIAMETRE || playery+playerheight <= position.y){
            return false;
        }
        return true;
    }

    public void render(ShapeRenderer shapeRenderer){

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(r,g,b,1);
        shapeRenderer.circle(position.x+DIAMETRE/2, position.y+DIAMETRE/2, DIAMETRE/2);
        shapeRenderer.end();

    }
}
