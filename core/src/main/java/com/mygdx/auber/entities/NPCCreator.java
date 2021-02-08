package com.mygdx.auber.entities;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.auber.Pathfinding.MapGraph;
import com.mygdx.auber.Pathfinding.Node;

public class NPCCreator {
    /** An array holding the infiltrators. */
    private static Array<Infiltrator> infiltrators = new Array<>();
    /** An array holding the crew members. */
    private static Array<CrewMembers> crew = new Array<>();
    // Arrays which hold each instance of Crewmembers and infiltrators

    /** The last index given to an infiltrator. */
    private static int lastInfiltratorIndex = 0;
    /** The last index given to a crewmember. */
    private static int lastCrewIndex = 0;

   
    /**
     * Encodes all of the NPCs in the game into a recognisable string.
     * @return encoded data of all the NPCs in the game.
     */
    public static String encode() {
        String r = "";
        // Encode infiltrator related data.
        r += Infiltrator.encode(NPCCreator.infiltrators) + System.lineSeparator();
        r += String.valueOf(lastInfiltratorIndex) + System.lineSeparator();
        r += CrewMembers.encode(NPCCreator.crew) + System.lineSeparator();
        r += String.valueOf(lastCrewIndex);
        return r;
    }

    public static void loadInfiltratorsFromEncoding(String coordinate, String isDestroying, String invisible, String timesInvisible, String isHardSprites, String lastInfiltratorIndex, MapGraph mapGraph) {


        // Parse the input strings

        String[] splitCoordinates = coordinate.split(",");
        for (int i = 0; i < splitCoordinates.length; i++) {
            splitCoordinates[i] = splitCoordinates[i].replace("[", "");
            splitCoordinates[i] = splitCoordinates[i].replace("]", "");
            splitCoordinates[i] = splitCoordinates[i].replace("(", "");
            splitCoordinates[i] = splitCoordinates[i].replace(")", "");
        }

        String[] splitDestroyings = isDestroying.split(",");
        splitDestroyings[0] = splitDestroyings[0].replace("[", "");
        splitDestroyings[splitDestroyings.length - 1] = splitDestroyings[splitDestroyings.length - 1].replace("]", "");

        String[] splitInvisibles = invisible.split(",");
        splitInvisibles[0] = splitInvisibles[0].replace("[", "");
        splitInvisibles[splitInvisibles.length - 1] = splitInvisibles[splitInvisibles.length - 1].replace("]", "");
        
        String[] splitTimeInvisible = timesInvisible.split(",");
        splitTimeInvisible[0] = splitTimeInvisible[0].replace("[", "");
        splitTimeInvisible[splitTimeInvisible.length - 1] = splitTimeInvisible[splitTimeInvisible.length - 1].replace("]", "");

        // last infiltrator index does not need to be parsed.

        String[] splitIsHardSprites = isHardSprites.split(",");
        splitIsHardSprites[0] = splitIsHardSprites[0].replace("[", "");
        splitIsHardSprites[splitIsHardSprites.length - 1] = splitIsHardSprites[splitIsHardSprites.length - 1].replace("]", "");

        // end of parsing.

        for (int i = 0; i < splitDestroyings.length; i++) {
            Sprite sprite;
            if (Boolean.valueOf(splitIsHardSprites[i]) == true) {
                sprite = Infiltrator.getHardSprites().random();
            } else {
                sprite = Infiltrator.getEasySprites().random();
            }
            NPCCreator.createInfiltrator(sprite, Float.valueOf(splitCoordinates[2*i]), Float.valueOf(splitCoordinates[(2*i)+1]), mapGraph);
            NPCCreator.getInfiltrators().get(i).setDestroying(Boolean.valueOf(splitDestroyings[i]));
            NPCCreator.getInfiltrators().get(i).setIsInvisible(Boolean.valueOf(splitInvisibles[i]));
            NPCCreator.getInfiltrators().get(i).setTimeInvisible(Float.valueOf(splitTimeInvisible[i]));
        }

        NPCCreator.lastInfiltratorIndex = Integer.valueOf(lastInfiltratorIndex);
        
    }

    public static void LoadCrewFromEncoding(String coordinate, String lastCrewIndex, MapGraph mapGraph) {
        // Parse coordinates.
        String[] splitCoordinates = coordinate.split(",");
        for (int i = 0; i < splitCoordinates.length; i++) {
            splitCoordinates[i] = splitCoordinates[i].replace("[", "");
            splitCoordinates[i] = splitCoordinates[i].replace("]", "");
            splitCoordinates[i] = splitCoordinates[i].replace("(", "");
            splitCoordinates[i] = splitCoordinates[i].replace(")", "");
        }

        for (int i = 0; i < splitCoordinates.length; i+= 2) {
            NPCCreator.createCrew(CrewMembers.selectSprite(), MapGraph.getNode(Float.valueOf(splitCoordinates[i]), Float.valueOf(splitCoordinates[i+1])), mapGraph);
        }

        NPCCreator.lastCrewIndex = Integer.valueOf(lastCrewIndex);
    }


    /**
     * Creates infiltrators, adds them to the array, sets its index and
     * increments the index counter.
     * @param sprite Sprite to give infiltrator
     * @param start  Start node for infiltrator
     * @param graph  MapGraph for the infiltrator to reference
     */
    public static void createInfiltrator(
        final Sprite sprite, final Node start, final MapGraph graph) {
        boolean isHard = Infiltrator.getHardSprites().contains(sprite, false);
        Infiltrator infiltrator = new Infiltrator(sprite, start, graph, isHard);
        infiltrators.add(infiltrator);
        infiltrator.setIndex(lastInfiltratorIndex);
        lastInfiltratorIndex++;
    }

    /**
     * Creates infiltrators, adds them to the array, sets its index and
     * increments the index counter.
     * @param sprite Sprite to give infiltrator
     * @param x  Start x coordinate for the infiltrator.
     * @param y Start y coordinate for the infiltrator.
     * @param graph  MapGraph for the infiltrator to reference
     */
    public static void createInfiltrator(
        final Sprite sprite, final Float x, final Float y, final MapGraph graph) {
        boolean isHard = Infiltrator.getHardSprites().contains(sprite, false);
        Infiltrator infiltrator = new Infiltrator(sprite, graph.getNode(x, y), graph, isHard);
        infiltrators.add(infiltrator);
        infiltrator.setIndex(lastInfiltratorIndex);
        lastInfiltratorIndex++;
    }

    /**
     * Creates crewmembers, adds them to the array, sets its index and
     * increments the index counter.
     * @param sprite The sprite used for this crewmate.
     * @param start The node this crewmate will start on.
     * @param graph The MapGraph this crewmate will use to navigate.
     */
    public static void createCrew(final Sprite sprite, final Node start,
    final MapGraph graph) {
        CrewMembers crewMember = new CrewMembers(sprite, start, graph);
        crew.add(crewMember);
        crewMember.setIndex(lastCrewIndex);
        lastCrewIndex++;
    }

    /** A small step value to use when transferring an infiltrator to
     * prison. */
    private static final float SMALL_STEP = 0.01f;
    /** The damage caused to the player when they arrest an infiltrator. */
    private static final int PLAYER_ARREST_DAMAGE = 10;
    /** The chance that the player will take damage when arresting an
     * infiltrator. */
    private static final float PLAYER_ARREST_DAMAGE_CHANCE = 0.25f;

    /**
     * Removes infiltrator for given id.
     * @param p The current Player.
     * @param id id to remove.
     */
    public static void removeInfiltrator(final Player p, final int id) {
        for (Infiltrator infiltrator : infiltrators) {
            if (infiltrator.getIndex() == id) {
                if (infiltrator.isDestroying()) {
                    KeySystemManager.getClosestKeySystem(
                        infiltrator.getX(), infiltrator.getY()).stopDestroy();
                    double chance = Math.random();
                    if (chance < PLAYER_ARREST_DAMAGE_CHANCE) {
                        p.takeDamage(PLAYER_ARREST_DAMAGE);
                    }
                    // Random chance of player taking damage upon arresting
                    // infiltrator
                }
                infiltrator.setDestroying(false);
                infiltrator.step(p, SMALL_STEP);
                Prisoners.addPrisoner(infiltrator);
            }
        }
        infiltrators.removeIndex(id);
        if (infiltrators.isEmpty()) {
            return;
        }
        for (int i = id; i < infiltrators.size; i++) {
            Infiltrator infiltrator = infiltrators.get(i);
            infiltrator.setIndex(infiltrator.getIndex() - 1);
        }
    }

    /**
     * Removes crewmember for given id.
     * @param id id to remove.
     */
    public static void removeCrewmember(final int id) {
        CrewMembers newPrisoner = crew.get(id);
        Prisoners.addPrisoner(newPrisoner);

        crew.removeIndex(id);
        if (crew.isEmpty()) {
            return;
        }
        for (int i = id; i < crew.size; i++) {
            CrewMembers crewMember = crew.get(i);
            crewMember.setIndex(crewMember.getIndex() - 1);
        }
    }

    /** Called when this object is deleted. */
    public static void dispose() {
        lastInfiltratorIndex = 0;
        lastCrewIndex = 0;
        infiltrators.clear();
        crew.clear();
    }

    /** Returns if this NPCCreator has crew.
     * @return true if crew is not empty, false otherwise.
     */
    public static boolean hasCrew() {
        return !crew.isEmpty();
    }

    /** Returns if this NPCCreator has infiltrators.
     * @return true if infiltrators is not empty, false otherwise.
     */
    public static boolean hasInfiltrators() {
        return !infiltrators.isEmpty();
    }

    /** Returns the list of Infiltrators.
     * @return the list of Infiltrators.
     */
    public static Array<Infiltrator> getInfiltrators() {
        return infiltrators;
    }

    /** Returns the list of Crew Members.
     * @return the list of Crew Members.
     */
    public static Array<CrewMembers> getCrew() {
        return crew;
    }
}
