package com.mygdx.auber.Pathfinding;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;

public final class GraphCreator extends ApplicationAdapter {
    // final is used to prevent subclassing
    /** Used to draw debug information. */
    private ShapeRenderer shapeRenderer;
    /** Used to draw debug information. */
    private SpriteBatch batch;
    /** Used to write debug text. */
    private BitmapFont font;
    /** The MapGraph created by this class. */
    private MapGraph mapGraph;
    /** The tiled map layer containing the data for this map graph. */
    private TiledMapTileLayer tileLayer;
    /** This indicates the nodes occupied by key systems. */
    private static Array<Node> keySystemsNodes = new Array<>();
    /** A path between two nodes in this map graph. */
    private static GraphPath<Node> nodePath;

    /**
     * The constructor for this MapGraph.
     * @param chosenTileLayer the layer on the map to check for the data.
     */
    public GraphCreator(final TiledMapTileLayer chosenTileLayer) {
        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();
        font = new BitmapFont();

        mapGraph = new MapGraph();
        this.tileLayer = chosenTileLayer;

        generateNodeMap();
        generateConnections();
    }

    /**
     * Renders the nodes and paths on the screen using their render methods,
     * used for debugging purposes.
     */
    @Override
    public void render() {
        for (Path path : MapGraph.getPaths()) {
            path.render(shapeRenderer);
        }

        for (Node node : MapGraph.getNodes()) {
            node.render(shapeRenderer, batch, font, false);
        }

        for (Node node : nodePath) {
            node.render(shapeRenderer, batch, font, true);
        }
    }

    /**
     * Generates a node on every tile in the map, if it matches the id.
     * This is called when the GraphCreator object is instantiated.
     */
    public void generateNodeMap() {
        for (int i = 0; i < tileLayer.getWidth(); i++) {
            for (int j = 0; j < tileLayer.getHeight(); j++) {
                // Scans every tile
                int x = (i * tileLayer.getTileWidth())
                + tileLayer.getTileWidth() / 2;
                int y = (j * tileLayer.getTileHeight())
                + tileLayer.getTileHeight() / 2;
                // x,y coord of the centre of the tile
                TiledMapTileLayer.Cell cell = tileLayer.getCell(i, j);
                // Returns the cell at the x,y coord
                if (cell != null && cell.getTile() != null
                && (cell.getTile().getProperties().containsKey("node"))) {
                    // If ID matches floor/corridor tiles, and is not null
                    Node node = new Node(x, y);
                    MapGraph.addNode(node);
                    // Create new node and add it to the map graph
                    if (cell.getTile().getProperties()
                    .containsKey("keysystemnode")) {
                        keySystemsNodes.add(node);
                    }
                }
            }
        }
    }

    /**
     * Generates a path from each node to its neighbour nodes (up, down, left,
     * right). Called when GraphCreator is instantiated, after the nodes are
     * created.
     */
    public void generateConnections() {
        for (int i = 0; i < MapGraph.getNodes().size; i++) {
            Node node = MapGraph.getNodes().get(i);
            Array<Node> neighbourNodes = getNeighbourNodes(node);
            // For every node in the map, get its neighbours
            for (Node neighbourNode : neighbourNodes) {
                MapGraph.connectNodes(node, neighbourNode);
                // For each neighbour, connect it to the original node
            }
        }
    }

    /**
     * Returns every valid neighbour node to the node passed as input.
     * @param node Node to get neighbours for
     * @return Array of Nodes
     */
    public Array<Node> getNeighbourNodes(final Node node) {
        // Creates an array of each valid (not null) neighbour node and returns
        // it.
        Array<Node> nodes = new Array<>();
        float x = node.getX();
        float y = node.getY();

        if (MapGraph.getNode(x + tileLayer.getTileWidth(), y) != null) {
            nodes.add(MapGraph.getNode(x + tileLayer.getTileWidth(), y));
        }
        if (MapGraph.getNode(x - tileLayer.getTileWidth(), y) != null) {
            nodes.add(MapGraph.getNode(x - tileLayer.getTileWidth(), y));
        }
        if (MapGraph.getNode(x, y + tileLayer.getTileHeight()) != null) {
            nodes.add(MapGraph.getNode(x, y + tileLayer.getTileHeight()));
        }
        if (MapGraph.getNode(x, y - tileLayer.getTileHeight()) != null) {
            nodes.add(MapGraph.getNode(x, y - tileLayer.getTileHeight()));
        }

        return nodes;
    }

    /**
     * Gets this GraphCreator's mapGraph.
     * @return This GraphCreator's mapGraph.
     */
    public MapGraph getMapGraph() {
        return mapGraph;
    }

    /**
     * Sets the nodePath for this GraphCreator.
     * @param inputNodePath
     */
    public static void setNodePath(final GraphPath<Node> inputNodePath) {
        GraphCreator.nodePath = inputNodePath;
    }

    /**
     * Returns the key system nodes for this GraphCreator.
     * @return An array of nodes that represent key systems.
     */
    public static Array<Node> getKeySystemNodes() {
        return keySystemsNodes;
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        batch.dispose();
        font.dispose();
        keySystemsNodes.clear();
        nodePath.clear();
        MapGraph.dispose();
    }
}
