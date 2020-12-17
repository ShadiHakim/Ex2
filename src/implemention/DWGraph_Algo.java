package implemention;

import api.directed_weighted_graph;
import api.dw_graph_algorithms;
import api.edge_data;
import api.node_data;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import implemention.jsonFormat.MyJsonDeserializer;
import implemention.jsonFormat.MyJsonSerializer;

import java.io.*;
import java.util.*;

/**
 * This class represents an algorithms unit for a directed (positive) weighted Graph
 * @author shadihakim
 */
public class DWGraph_Algo implements dw_graph_algorithms {
    private directed_weighted_graph graph;

    /**
     * This function initialize the graph that all the algorithms will be working on
     * @param g - The graph
     */
    @Override
    public void init(directed_weighted_graph g) {
        this.graph = g;
    }

    /**
     * A simple get function
     * @return the initialized graph
     */
    @Override
    public directed_weighted_graph getGraph() {
        return this.graph;
    }

    /**
     * This function compute a deep copy of the graph
     * @return the new graph
     */
    @Override
    public directed_weighted_graph copy() {
        // Creating a new empty graph
        directed_weighted_graph new_graph = new DWGraph_DS();
        // Creating an empty HashMap to be used as a dictionary between the "old" nodes and the "new" nodes
        HashMap<node_data, node_data> node_dictionary_copies = new HashMap<>();

        // Checking if there is any node in the graph
        if (graph.nodeSize() != 0) {
            // an iterator to go over all the nodes
            Iterator<node_data> iterator = graph.getV().iterator();
            // an iteration to go over all the nodes
            while (iterator.hasNext()) {
                // getting the nodes one by one from the current graph(old)
                node_data this_nodeData = iterator.next();
                // a pointer for the new copy of the node
                node_data new_nodeData;

                // Checking if the current node is already added to the dictionary
                if (!node_dictionary_copies.containsKey(this_nodeData)) {
                    // if not
                    // create a new node
                    new_nodeData = ((DWGraph_DS) graph).node_Copy(this_nodeData);

                    // add the new node to the dictionary
                    node_dictionary_copies.put(this_nodeData, new_nodeData);
                    // add the new node to the graph
                    new_graph.addNode(new_nodeData);
                } else {
                    //if yes
                    // get the node form the dictionary
                    new_nodeData = node_dictionary_copies.get(this_nodeData);
                }

                // going over all the neighbors of the node and connecting it
                for (edge_data edge :
                        graph.getE(this_nodeData.getKey())) {
                    // get neighbor at the other end of the edge
                    node_data neighbor = graph.getNode(edge.getDest());
                    // Checking if the current neighbor node is already added to the dictionary
                    if (!node_dictionary_copies.containsKey(neighbor)) {
                        // if not
                        // create a new node
                        new_nodeData = ((DWGraph_DS) graph).node_Copy(neighbor);

                        // add the new node to the dictionary
                        node_dictionary_copies.put(neighbor, new_nodeData);
                        // add the new node to the graph
                        new_graph.addNode(new_nodeData);
                    }
                    // either way
                    // connect the new node with the new neighbor
                    new_graph.connect(node_dictionary_copies.get(this_nodeData).getKey(), node_dictionary_copies.get(neighbor).getKey(), edge.getWeight());
                }

            }
        }
        // return the pointer of the new graph
        return new_graph;
    }

    //TODO maybe don't check the checked nodes
    /**
     * This function compute and check wither this graph is strongly connected
     * @return if the graph is strongly connected
     */
    @Override
    public boolean isConnected() {
        // check if there are nodes in the graph
        if (graph != null && graph.nodeSize() != 0) {

            for (node_data this_node_data : graph.getV()) {
                HashSet<Integer> visited = new HashSet<>();

                // start DFS from first vertex
                DFS(this_node_data.getKey(), visited);

                if (visited.size() != graph.nodeSize())
                    return false;
            }
        }
        return true;
    }

    // DFS Traversal
    private void DFS(int nodeData, HashSet<Integer> visited) {
        // mark current node as visited
        visited.add(nodeData);

        // do for every edge (v -> u)
        for (edge_data connected_node : graph.getE(nodeData)) {
            // u is not visited
            if (!visited.contains(connected_node.getDest()))
                DFS(connected_node.getDest(), visited);
        }
    }

    /**
     * This function uses the dijkstra algorithm to get the destination number between two nodes
     * @param src - start node
     * @param dest - end (target) node
     * @return - the destination between the two nodes
     */
    @Override
    public double shortestPathDist(int src, int dest) {
        double dst = -1;

        //dijkstra's shortest path algorithm
        // a map checking each node if has been visited and adding dest and connection node key
        Collection<node_data> visited = new HashSet<>();

        Map<node_data, DWGraph_Node_helper> dwhelper = new HashMap<>();
        for (node_data nodeInfo : graph.getV()) {
            dwhelper.put(nodeInfo, new DWGraph_Node_helper(Double.MAX_VALUE, null));
        }
        // a queue to go over each node in it and adding each one according to its wight (by comparator written in Lambda expression)
        Queue<node_data> q = new PriorityQueue<>(graph.nodeSize(),
                Comparator.comparingDouble(n -> dwhelper.get(n).get_dest()));

        if (graph.getNode(src) != null) {
            dwhelper.get(graph.getNode(src)).set_dest(0);
            // adding the first node to the queue
            q.add(graph.getNode(src));
            // go over the nodes in the queue while it is not empty
            while (!q.isEmpty()) {
                // get the first node in the queue
                node_data nodeInfo = q.poll();
                // mark this node as visited
                visited.add(nodeInfo);
                // go over all the node's neighbours
                for (edge_data edge :
                        graph.getE(nodeInfo.getKey())) {
                    // get neighbor at the other end of the edge
                    node_data neighbor = graph.getNode(edge.getDest());
                    // if the node is not visited
                    if (!visited.contains(neighbor)) {
                        // getting the wight of the edge that connects the current node and the neighbor
                        double beforew = dwhelper.get(nodeInfo).get_dest();
                        double currneighborw = dwhelper.get(neighbor).get_dest();
                        double new_w = beforew + edge.getWeight();
                        if (currneighborw > new_w) {
                            // set neighbor _dest to the best wight
                            dwhelper.get(neighbor).set_dest(new_w);;
                            // setting the _ckey field to the previous node_key
                            dwhelper.get(neighbor).set_ckey(nodeInfo.getKey());
                        }

                        // add the neighbor to the queue
                        q.offer(neighbor);
                    }
                }
            }
        }

        // the list of nodes that represent the path form src to dest
        List<node_data> path = new ArrayList<>();
        // building the path from dest to src
        node_data at = graph.getNode(dest);
        for (; at != null && dwhelper.get(at).get_ckey() != null && at.getKey() != src; at = graph.getNode(dwhelper.get(at).get_ckey()))//TODO CRITICAL BUG FIX NOW
            path.add(at);
        if (at != null)
            path.add(at);
        // reversing the path
        Collections.reverse(path);

        // checking if there is a path
        if (!path.isEmpty() && path.get(0).getKey() != src) {
            // if not
        } else {
            dst = dwhelper.get(path.get(path.size() - 1)).get_dest();
        }
        return dst;
    }

    /**
     * This function uses the dijkstra algorithm to get the the full path between two nodes
     * @param src - start node
     * @param dest - end (target) node
     * @return - the list in the order from src to dest
     */
    @Override
    public List<node_data> shortestPath(int src, int dest) {
        //dijkstra's shortest path algorithm
        // a map checking each node if has been visited and adding dest and connection node key
        Collection<node_data> visited = new HashSet<>();

        Map<node_data, DWGraph_Node_helper> dwhelper = new HashMap<>();
        for (node_data nodeInfo : graph.getV()) {
            dwhelper.put(nodeInfo, new DWGraph_Node_helper(Double.MAX_VALUE, null));
        }
        // a queue to go over each node in it and adding each one according to its wight (by comparator written in Lambda expression)
        Queue<node_data> q = new PriorityQueue<>(graph.nodeSize(),
                Comparator.comparingDouble(n -> dwhelper.get(n).get_dest()));

        if (graph.getNode(src) != null) {
            dwhelper.get(graph.getNode(src)).set_dest(0);
            // adding the first node to the queue
            q.add(graph.getNode(src));
            // go over the nodes in the queue while it is not empty
            while (!q.isEmpty()) {
                // get the first node in the queue
                node_data nodeInfo = q.poll();
                // mark this node as visited
                visited.add(nodeInfo);
                // go over all the node's neighbours
                for (edge_data edge :
                        graph.getE(nodeInfo.getKey())) {
                    // get neighbor at the other end of the edge
                    node_data neighbor = graph.getNode(edge.getDest());
                    // if the node is not visited
                    if (!visited.contains(neighbor)) {
                        // getting the wight of the edge that connects the current node and the neighbor
                        double beforew = dwhelper.get(nodeInfo).get_dest();
                        double currneighborw = dwhelper.get(neighbor).get_dest();
                        double new_w = beforew + edge.getWeight();
                        if (currneighborw > new_w) {
                            // set neighbor _dest to the best wight
                            dwhelper.get(neighbor).set_dest(new_w);
                            // setting the _ckey field to the previous node_key
                            dwhelper.get(neighbor).set_ckey(nodeInfo.getKey());
                        }

                        // add the neighbor to the queue
                        q.offer(neighbor);
                    }
                }
            }
        }

        // the list of nodes that represent the path form src to dest
        List<node_data> path = new ArrayList<>();
        // building the path from dest to src
        node_data at = graph.getNode(dest);
        for (; at != null && dwhelper.get(at).get_ckey() != null && at.getKey() != src; at = graph.getNode(dwhelper.get(at).get_ckey()))
            path.add(at);
        if (at != null)
            path.add(at);
        // reversing the path
        Collections.reverse(path);

        // checking if there is a path
        if (!path.isEmpty() && path.get(0).getKey() != src) {
            // if not
            // return a null list
            return null;
        }
        // finally returning the path
        return path;
    }

    /**
     * This function save the graph in a JSON format to the file
     * @param file - the file name (may include a relative path).
     * @return - if the graph is save successfully
     */
    @Override
    public boolean save(String file) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            SimpleModule serialization = new SimpleModule();
            serialization.addSerializer(directed_weighted_graph.class, new MyJsonSerializer());
            mapper.registerModule(serialization);
            mapper.writeValue(new File(file), graph);

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * This function load the graph from the given file in a JSON format
     * @param file - file name of JSON file
     * @return - if the graph initialized successfully
     */
    @Override
    public boolean load(String file) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            SimpleModule deserialization = new SimpleModule();
            deserialization.addDeserializer(directed_weighted_graph.class, new MyJsonDeserializer());
            mapper.registerModule(deserialization);

            graph = mapper.readValue(new File(file), directed_weighted_graph.class);

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
