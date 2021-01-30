package com.mygdx.auber;

public final class Config {
    // Making this final makes it impossible to subclass.
    /**
     * The constructor for this class.  Making it private makes it
     * impossible to instantiate.
     */
    private Config() {

    }

    /** The diameter of powerups in pixels. */
    public static final int POWERUP_DIAMETER = 32;

    /** The speed of the player in pixels/second without powerups. */
    public static final float NORMAL_PLAYER_SPEED = 78f;

    /** The speed of the player in pixels/second when using a speed powerup. */
    public static final float FAST_PLAYER_SPEED = 120f;

    /** The infiltrators' speed in pixels/second. */
    public static final float INFILTRATOR_SPEED = 60f;

    /** The crew members' speed in pixels/second. */
    public static final float CREW_MEMBER_SPEED = 60f;

    /** The red component of the speedup powerup's default colour. */
    public static final float DEFAULT_SPEEDUP_RED = 0f;
    /** The green component of the speedup powerup's default colour. */
    public static final float DEFAULT_SPEEDUP_GREEN = 0.5f;
    /** The blue component of the speedup powerup's default colour. */
    public static final float DEFAULT_SPEEDUP_BLUE = 0.4f;

    /** The red component of the speedup powerup's colour after colliding with
     * the player. */
    public static final float COLLISION_SPEEDUP_RED = 0.2f;
    /** The green component of the speedup powerup's colour after colliding 
     * with the player. */
    public static final float COLLISION_SPEEDUP_GREEN = 0.2f;
    /** The blue component of the speedup powerup's colour after colliding with
     * the player. */
    public static final float COLLISION_SPEEDUP_BLUE = 1f;

    /** The time that the speedup powerup affects the player for in seconds. */
    public static final float SPEEDUP_TIME = 10f;
}
