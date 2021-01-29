package com.mygdx.auber.Pathfinding;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public class MapGraph implements IndexedGraph<Node> {

    /** The heuristic to use for pathfinding on this map. */
    private PathHeuristic pathHeuristic = new PathHeuristic();
    /** Array holding all nodes on map. */
    private static Array<Node> nodes = new Array<>();
    /** Array holding all paths on map. */
    private static Array<Path> paths = new Array<>();
    /** An ObjectMap that links nodes to the connections from them. */
    private static ObjectMap<Node, Array<Connection<Node>>> pathsMap = new
        ObjectMap<>();
    /** Increment counter to give each node a unique index. */
    private static int lastNodeIndex = 0;

    /**
     * Adds a node to the list of nodes in the graph, sets the node index and
     * increases the index by one.
     * @param node the Node to add.
     */
    public static void addNode(final Node node) {
        /* Sets node index to current lastNodeIndex, increments index by one,
         adds node to the list of nodes. */
        node.setIndex(lastNodeIndex);
        lastNodeIndex++;

        nodes.add(node);
    }

    /**
     * Returns a node based on the x,y position of the node.
     * @param x X coord of the node to find.
     * @param y Y coord of the node to find.
     * @return The node at x,y.
     */
    public static Node getNode(final float x, final float y) {
        /* Searches every node for x,y coordinate, returns node with matching
         coords. */
        for (Node node : nodes) {
            if (node.getX() == x && node.getY() == y && node != null) {
                return node;
            }
        }

        return null;
    }

    /**
     * Creates a path from one node to another.
     * @param fromNode Node path comes from.
     * @param toNode   Node path goes to.
     */
    public static void connectNodes(final Node fromNode, final Node toNode) {
        /* Adds a path from node to node, unless node is already in the
        pathsMap. */
        Path path = new Path(fromNode, toNode);
        if (!pathsMap.containsKey(fromNode)) {
            pathsMap.put(fromNode, new Array<Connection<Node>>());
        }
        pathsMap.get(fromNode).add(path);
        paths.add(path);
    }

    /**
     * Called when this object is deleted.
     */
    public static void dispose() {
        nodes.clear();
        paths.clear();
        pathsMap.clear();
        lastNodeIndex = 0;
    }

    /**
     * Calculates a path from one node to another, populates the nodePath
     * variable with the path it finds.
     * @param startNode Node to start the search from
     * @param goalNode  Node to finish search at
     * @return A path of nodes to follow to get from start to finish
     */
    public GraphPath<Node> findPath(final Node startNode, final Node goalNode) {
        GraphPath<Node> nodeGraphPath = new DefaultGraphPath<>();
        new IndexedAStarPathFinder<>(this).searchNodePath(startNode,
            goalNode, pathHeuristic, nodeGraphPath);
        GraphCreator.setNodePath(nodeGraphPath);
        return nodeGraphPath;
    }

    /**
     * Returns the index of a particular Node instance.
     * @param node Node to get index for
     * @return Int index for the node
     */
    @Override
    public int getIndex(final Node node) {
        return node.getIndex();
    }

    /**
     * Return the count of how many nodes are in our search space.
     * @return Number of nodes on map.
     */
    @Override
    public int getNodeCount() {
        return lastNodeIndex;
    }

    /**
     * Returns the list of paths that start at a particular node.
     * @param fromNode Node to get paths from.
     * @return Array of paths from node.
     */
    @Override
    public Array<Connection<Node>> getConnections(final Node fromNode) {
        if (pathsMap.containsKey(fromNode)) {
            return pathsMap.get(fromNode);
        }
        return new Array<>(0);
    }

    /**
     * Returns a random node from this MapGraph.
     * @return A random Node from this MapGraph.
     */
    public static Node getRandomNode() {
        return nodes.random();
    }

    /**
     * The Nodes in this MapGraph.
     * @return An Array of nodes in this graph.
     */
    public static Array<Node> getNodes() {
        return nodes;
    }

    /**
     * The connections in this MapGraph.
     * @return An Array of connections in this graph.
     */
    public static Array<Path> getPaths() {
        return paths;
    }
}
