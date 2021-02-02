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
    public static final float NORMAL_PLAYER_SPEED = 80f;

    /** The speed of the player in pixels/second when using a speed powerup. */
    public static final float FAST_PLAYER_SPEED = 150f;

    /** The radius of the player arrest range circle. */
    public static final int NORMAL_ARREST_RANGE = 200;

    /** The radius of the player arrest range circle when with arrest
     * power up. */
    public static final int EXTEND_ARREST_RANGE = 300;

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

    /** The red component of the arrestup powerup's default colour. */
    public static final float DEFAULT_ARRESTUP_RED = 0.1f;
    /** The green component of the arrestup powerup's default colour. */
    public static final float DEFAULT_ARRESTUP_GREEN = 0.8f;
    /** The blue component of the arrestup powerup's default colour. */
    public static final float DEFAULT_ARRESTUP_BLUE = 0.4f;
    /** The time that the arrestup powerup affects the player for in
     * seconds. */
    public static final float ARRESTUP_TIME = 10f;

    /** The amount of crew to add per difficulty level increase. */
    public static final int CREW_COUNT_DIFFICULTY_MULTIPLIER = 40;
    /**
     * The amount of incorrect arrests to remove per difficulty level increase.
     */
    public static final int INCORRECT_ARREST_DIFFICULTY_MULTIPLIER = 3;
    /** The x position the player starts at. */
    public static final int PLAYER_START_X = 1700;
    /** The y position the player starts at. */
    public static final int PLAYER_START_Y = 3000;
    /** The x position the powerups spawn at. */
    public static final int POWERUP_START_X = 1700;
    /** The y position the powerups spawn at. */
    public static final int POWERUP_START_Y = 2800;

    /** The red component of the arrestup powerup's default colour. */
    public static final float DEFAULT_FREEZEUP_RED = 0.5f;
    /** The green component of the arrestup powerup's default colour. */
    public static final float DEFAULT_FREEZEUP_GREEN = 0.5f;
    /** The blue component of the arrestup powerup's default colour. */
    public static final float DEFAULT_FREEZEUP_BLUE = 0.9f;
    /** The time that the arrestup powerup affects the player for in
     * seconds. */
    public static final float FREEZEUP_TIME = 10f;

    /** The red component of the arrestup powerup's default colour. */
    public static final float DEFAULT_SHIELDUP_RED = 0.4f;
    /** The green component of the arrestup powerup's default colour. */
    public static final float DEFAULT_SHIELDUP_GREEN = 0.4f;
    /** The blue component of the arrestup powerup's default colour. */
    public static final float DEFAULT_SHIELDUP_BLUE = 0.4f;
    /** The time that the arrestup powerup affects the player for in
     * seconds. */
    public static final float SHIELDUP_TIME = 10f;

    /** The red component of the arrestup powerup's default colour. */
    public static final float DEFAULT_HIGHLIGHTUP_RED = 0.9f;
    /** The green component of the arrestup powerup's default colour. */
    public static final float DEFAULT_HIGHLIGHTUP_GREEN = 0.1f;
    /** The blue component of the arrestup powerup's default colour. */
    public static final float DEFAULT_HIGHLIGHTUP_BLUE = 0.1f;
    /** The time that the arrestup powerup affects the player for in
     * seconds. */
    public static final float HIGHLIGHTUP_TIME = 10f;

    /** The amount of systems that can be destroyed before the player loses. */
    public static final int MAX_LOST_SYSTEMS = 15;
}
