package com.mygdx.auber.entities;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Array;
import com.mygdx.auber.Pathfinding.MapGraph;
import com.mygdx.auber.Pathfinding.Node;

public class NPCCreator {
    public static Array<Infiltrator> infiltrators = new Array<>();
    public static Array<CrewMembers> crew = new Array<>(); // Arrays which hold each instance of Crewmembers and
                                                           // infiltrators

    private static int lastInfiltratorIndex = 0;
    private static int lastCrewIndex = 0; // Last index given to an infiltrator and crewmember

    /**
     * Creates infiltrators, adds them to the array, sets its index and increments
     * the index counter
     * 
     * @param sprite Sprite to give infiltrator
     * @param start  Start node for infiltrator
     * @param graph  MapGraph for the infiltrator to reference
     */
    public static void createInfiltrator(Sprite sprite, Node start, MapGraph graph) {
        Infiltrator infiltrator = new Infiltrator(sprite, start, graph);
        infiltrators.add(infiltrator);
        infiltrator.setIndex(lastInfiltratorIndex);
        lastInfiltratorIndex++;
    }

    /**
     * Creates crewmembers, adds them to the array, sets its index and increments
     * the index counter
     * 
     * @param sprite
     * @param start
     * @param graph
     */
    public static void createCrew(Sprite sprite, Node start, MapGraph graph) {
        CrewMembers crewMember = new CrewMembers(sprite, start, graph);
        crew.add(crewMember);
        crewMember.setIndex(lastCrewIndex);
        lastCrewIndex++;
    }

    /**
     * Removes infiltrator for given id
     * 
     * @param id id to remove
     */
    public static void removeInfiltrator(Player p, int id) {
        for (Infiltrator infiltrator : infiltrators) {
            if (infiltrator.index == id) {
                if (infiltrator.isDestroying) {
                    KeySystemManager.getClosestKeySystem(infiltrator.getX(), infiltrator.getY()).stopDestroy();
                    double chance = Math.random();
                    if (chance > .25f) {
                        p.takeDamage(10);
                    } // Random chance of player taking damage upon arresting infiltrator
                }
                infiltrator.isDestroying = false;
                infiltrator.step(p, 0.001f);
                Prisoners.addPrisoner(infiltrator);
            }
        }
        infiltrators.removeIndex(id);
        if (infiltrators.isEmpty()) {
            return;
        }
        for (int i = id; i < infiltrators.size; i++) {
            Infiltrator infiltrator = infiltrators.get(i);
            infiltrator.index -= 1;
        }
    }

    /**
     * Removes crewmember for given id
     * 
     * @param id id to remove
     */
    public static void removeCrewmember(int id) {
        CrewMembers newPrisoner = crew.get(id);
        Prisoners.addPrisoner(newPrisoner);

        crew.removeIndex(id);
        if (crew.isEmpty()) {
            return;
        }
        for (int i = id; i < crew.size; i++) {
            CrewMembers crewMember = crew.get(i);
            crewMember.index -= 1;
        }
    }

    public static void dispose() {
        lastInfiltratorIndex = 0;
        lastCrewIndex = 0;
        infiltrators.clear();
        crew.clear();
    }
}
