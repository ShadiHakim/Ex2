package implemention;

import api.directed_weighted_graph;
import api.edge_data;
import api.geo_location;
import api.node_data;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import gameClient.util.Point3D;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * This class represent a graph object (directional weighted graph)
 * @author shadihakim
 */
public class DWGraph_DS implements directed_weighted_graph {
    private HashMap<Integer, node_data> nodes;
    private HashMap<Integer, HashMap<Integer, edge_data>> node_connections;
    private int edge_size;
    private int mc;

    /**
     * A simple constructor that initialize its values
     */
    public DWGraph_DS() {
        this.nodes = new HashMap<>();
        this.node_connections = new HashMap<>();
        this.edge_size = 0;
        this.mc = 0;
    }

    /**
     * This function convert a node_key to a pointer of object that have this key in the graph
     * If not exist will return null
     * @param key - the node_id
     * @return a node_data pointer
     */
    @Override
    public node_data getNode(int key) {
        return nodes.get(key);
    }

    /**
     * This function convert the node_src_key and node_dest_key to a pointer of object representing the edge
     * @param src - The src node key
     * @param dest - The dest node key
     * @return an edge_data pointer
     */
    @Override
    public edge_data getEdge(int src, int dest) {
        if (src == dest)
            return null;
        return node_connections.get(src) != null ? node_connections.get(src).get(dest) : null;
    }

    /**
     * This function adds a node to the graph (if doesn't exist)
     * @param n - The node that will be added
     */
    @Override
    public void addNode(node_data n) {
        nodes.putIfAbsent(n.getKey(), n);
        mc++;
    }

    /**
     * This function connects two nodes in the graph with a wight (if they are connected it will do nothing)
     * @param src - the source of the edge.
     * @param dest - the destination of the edge.
     * @param w - positive weight representing the cost (aka time, price, etc) between src-->dest.
     */
    @Override
    public void connect(int src, int dest, double w) {
        if (getNode(src) != null && getNode(dest) != null && src != dest && w >= 0) {
            edge_data edgeData = getEdge(src, dest);
            if (edgeData == null) {
                HashMap<Integer, edge_data> edges = node_connections.get(src);
                if (edges == null){
                    edges = new HashMap<>();
                    node_connections.put(src,edges);
                }
                edges.put(dest, new DWGraph_Edge(src, dest, w));
                edge_size++;
                mc++;
            }
        }
    }

    /**
     * @return This function will return all the nodes in the graph
     */
    @Override
    public Collection<node_data> getV() {
        return nodes.values();
    }

    /**
     * This function will return all the edges that get out of the given node key (The node should be in the graph)
     * @param node_id - The node key
     * @return - a Collection of edge_data
     */
    @Override
    public Collection<edge_data> getE(int node_id) {
        return node_connections.get(node_id) != null ? node_connections.get(node_id).values() : new HashSet<>();
    }

    /**
     * This function will remove the node and all the edges that get in or out of it from the graph
     * @param key - The node key that will be removed
     * @return - The removed node
     */
    @Override
    public node_data removeNode(int key) {
        node_data remove_node = nodes.remove(key);
        if (remove_node != null) {
            for(Map.Entry<Integer, HashMap<Integer,edge_data>> entry
                    : node_connections.entrySet()) {
                Integer src = entry.getKey();
                if (entry.getValue().containsKey(key))
                    removeEdge(src,key);
            }
            mc += node_connections.get(key) != null ? node_connections.get(key).size() : 0;
            edge_size -= node_connections.get(key) != null ? node_connections.get(key).size() : 0;
            node_connections.remove(key);
            nodes.remove(key);
            mc++;
        }
        return remove_node;
    }

    /**
     * This function will remove the edge that connects the src with dest nodes (if exists)
     * @param src - The src node key
     * @param dest - The dest node key
     * @return - The edge that connected the two nodes
     */
    @Override
    public edge_data removeEdge(int src, int dest) {
        edge_data edgeData = getEdge(src, dest);
        if (edgeData != null){
            node_connections.get(src).remove(dest);
            edge_size--;
            mc++;
        }
        return edgeData;
    }

    /**
     * A simple function that returns the number of nodes in the graph
     * @return - The size of graph
     */
    @Override
    public int nodeSize() {
        return nodes.size();
    }

    /**
     * A simple function that returns the number of edges in the graph
     * @return - The number of edges
     */
    @Override
    public int edgeSize() {
        return edge_size;
    }

    /**
     * A simple function that returns the number of changes made on the graph
     * @return - The number of changes
     */
    @Override
    public int getMC() {
        return mc;
    }

    /**
     * This function is unique to this class it helps create a deep copy of a node
     * @param node - The node that will be copied
     * @return A pointer of the new copy of the node
     */
    public node_data node_Copy(node_data node) {
        return new DWGraph_Node(node);
    }

    /**
     * This is an inner class that represents a node in the graph
     */
    public static class DWGraph_Node implements node_data{
        @JsonProperty("id")
        private int key;
        @JsonProperty("pos")
        @JsonDeserialize(as = Point3D.class)
        private geo_location location;
        @JsonProperty("w")
        private double weight;
        private String info;
        private int tag;

        /**
         * an empty constructor that is used by the JSON library
         */
        public DWGraph_Node() {
        }

        /**
         * A simple constructor that create a node with the given key
         * @param key - The of the new node
         */
        public DWGraph_Node(int key) {
            this.key = key;
        }

        /**
         * This is a constructor that create a copy of the given node
         * @param node_data_copy - The node that will be deep copied
         */
        public DWGraph_Node(node_data node_data_copy){
            this.key = node_data_copy.getKey();
            this.location = node_data_copy.getLocation();
            this.weight = node_data_copy.getWeight();
            this.info = node_data_copy.getInfo();
            this.tag = node_data_copy.getTag();
        }

        /**
         * A simple get function
         * @return - The key of the node
         */
        @Override
        public int getKey() {
            return key;
        }

        /**
         * A simple get function
         * @return - The location of the node
         */
        @Override
        public geo_location getLocation() {
            return location;
        }

        /**
         * A simple set function
         * @param p - new location (position) of this node.
         */
        @Override
        public void setLocation(geo_location p) {
            location = p;
        }

        /**
         * A simple get function
         * @return the wight of the node
         */
        @Override
        public double getWeight() {
            return weight;
        }

        /**
         * A simple set function
         * @param w - the new weight
         */
        @Override
        public void setWeight(double w) {
            weight = w;
        }

        /**
         * A simple get function
         * @return the info of the node
         */
        @Override
        public String getInfo() {
            return info;
        }

        /**
         * A simple set function
         * @param s - the new info
         */
        @Override
        public void setInfo(String s) {
            info = s;
        }

        /**
         * A simple get function
         * @return the tag of the node
         */
        @Override
        public int getTag() {
            return tag;
        }

        /**
         * A simple set function
         * @param t - the new value of the tag
         */
        @Override
        public void setTag(int t) {
            tag = t;
        }
    }

    /**
     * This is an inner class representing an edge that connects two nodes with a wight and a direction
     */
    public static class DWGraph_Edge implements edge_data{
        private int src;
        private int dest;
        @JsonProperty("w")
        private double weight;
        private String info;
        private int tag;

        /**
         * an empty constructor that is used by the JSON library
         */
        public DWGraph_Edge() {
        }

        /**
         * A simple constructor that create an edge between two nodes and gives the edge a wight
         * @param src - the src node key
         * @param dest - the dest node key
         * @param weight - the wight of the edge
         */
        public DWGraph_Edge(int src, int dest, double weight) {
            this.src = src;
            this.dest = dest;
            this.weight = weight;
        }

        /**
         * A simple get function
         * @return the src node key
         */
        @Override
        public int getSrc() {
            return src;
        }

        /**
         * A simple get function
         * @return the dest node key
         */
        @Override
        public int getDest() {
            return dest;
        }

        /**
         * A simple get function
         * @return the wight of the edge
         */
        @Override
        public double getWeight() {
            return weight;
        }

        /**
         * A simple get function
         * @return the info of the edge
         */
        @Override
        public String getInfo() {
            return info;
        }

        /**
         * A simple set function
         * @param s - the new info
         */
        @Override
        public void setInfo(String s) {
            info = s;
        }

        /**
         * A simple get function
         * @return the of edge
         */
        @Override
        public int getTag() {
            return tag;
        }

        /**
         * A simple set function
         * @param t - the new value of the tag
         */
        @Override
        public void setTag(int t) {
            tag = t;
        }
    }
}
