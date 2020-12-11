package gameClient;

import api.*;
import gameClient.util.Point3D;
import implemention.DWGraph_Algo;
import implemention.DWGraph_Node_helper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

import static gameClient.util.Point3D.EPS;

/**
 * This class represent a utility for Ex2 (a tools class)
 * @author shadihakim
 */
public class MyUtil {

    /**
     * This function convert a JSON string to a list of Pokemon
     * @param fs - The JSON string
     * @return - a list of pokemon
     */
    public static ArrayList<CL_Pokemon> json2Pokemons(String fs) {
        ArrayList<CL_Pokemon> ans = new  ArrayList<CL_Pokemon>();
        try {
            JSONObject ttt = new JSONObject(fs);
            JSONArray ags = ttt.getJSONArray("Pokemons");
            for(int i=0;i<ags.length();i++) {
                JSONObject pp = ags.getJSONObject(i);
                JSONObject pk = pp.getJSONObject("Pokemon");
                int t = pk.getInt("type");
                double v = pk.getDouble("value");
                //double s = 0;//pk.getDouble("speed");
                String p = pk.getString("pos");
                CL_Pokemon f = new CL_Pokemon(new Point3D(p), t, v, 0, null);
                ans.add(f);
            }
        }
        catch (JSONException e) {e.printStackTrace();}
        return ans;
    }

    /**
     * This function create and update the agents form a JSON string
     * @param aa - The JSON string
     * @param gg - The graph
     * @param agentList - The list that will be modified
     */
    public static void getAgents(String aa, directed_weighted_graph gg, List<CL_Agent> agentList) {
        try {
            JSONObject ttt = new JSONObject(aa);
            JSONArray ags = ttt.getJSONArray("Agents");
            if (agentList.isEmpty()){
                for(int i=0;i<ags.length();i++) {
                    CL_Agent c = new CL_Agent(gg,0);
                    c.update(ags.get(i).toString());
                    agentList.add(c);
                }
            }
            else {
                int i = 0;
                for (CL_Agent agent : agentList) {
                    agent.update(ags.get(i++).toString());
                }
            }
            //= getJSONArray("Agents");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * This function updates all the list of Pokemons form old list to new list
     * @param newpokemons - The list of the new pokmons
     * @param oldpokemons - The list of the old pokemons
     * @param g - The graph
     * @return - The new update list of pokemons
     */
    public static List<CL_Pokemon> updatePokemons(List<CL_Pokemon> newpokemons, List<CL_Pokemon> oldpokemons, directed_weighted_graph g){
        for (int a = 0; a < newpokemons.size(); a++) {
            updateEdge(newpokemons.get(a),g);
            for (CL_Pokemon oldpokemon : oldpokemons) {
                if (oldpokemon.getLocation().equals(newpokemons.get(a).getLocation())){
                    newpokemons.get(a).setTarget_agent_id(oldpokemon.getTarget_agent_id());
                    oldpokemons.remove(oldpokemon);
                    break;
                }
            }
        }
        return newpokemons;
    }

    /**
     * This compute and update the edge of the pokemon
     * @param fr - The pokemon that will be updated
     * @param g - The graph
     */
    public static void updateEdge(CL_Pokemon fr, directed_weighted_graph g) {
        //	oop_edge_data ans = null;
        Iterator<node_data> itr = g.getV().iterator();
        while(itr.hasNext()) {
            node_data v = itr.next();
            Iterator<edge_data> iter = g.getE(v.getKey()).iterator();
            while(iter.hasNext()) {
                edge_data e = iter.next();
                boolean f = isOnEdge(fr.getLocation(), e,fr.getType(), g);
                if(f) {fr.set_edge(e);}
            }
        }
    }

    /**
     * This function check if the location p is on the edge the start from src and end with dest
     * @param p - The point to check if on the graph
     * @param src - The starting location of the edge
     * @param dest - The ending location of the edge
     * @return if the point on the edge
     */
    private static boolean isOnEdge(geo_location p, geo_location src, geo_location dest ) {

        boolean ans = false;
        double dist = src.distance(dest);
        double d1 = src.distance(p) + p.distance(dest);
        if(dist>d1-EPS) {ans = true;}
        return ans;
    }

    /**
     * This function helps to get the location of the edge on the graph and to check if the point is on it
     * @param p - The point to check if on the graph
     * @param s - The src node of the edge
     * @param d - The dest node of the edge
     * @param g - The graph
     * @return if the point on the edge
     */
    private static boolean isOnEdge(geo_location p, int s, int d, directed_weighted_graph g) {
        geo_location src = g.getNode(s).getLocation();
        geo_location dest = g.getNode(d).getLocation();
        return isOnEdge(p,src,dest);
    }

    /**
     * This function check whether the pokemon (point) is on the edge depending on pokemon type
     * @param p - The pokemon to check if on the graph
     * @param e - The edge
     * @param type - The type of the pokemon
     * @param g - The graph
     * @return if the pokemon on the edge
     */
    private static boolean isOnEdge(geo_location p, edge_data e, int type, directed_weighted_graph g) {
        int src = g.getNode(e.getSrc()).getKey();
        int dest = g.getNode(e.getDest()).getKey();
        if(type<0 && dest>src) {return false;}
        if(type>0 && src>dest) {return false;}
        return isOnEdge(p,src, dest, g);
    }

    /**
     * This function check whether the pokemon (point) is on the opposite edge depending on pokemon type
     * @param p - The pokemon to check if on the graph
     * @param e - The edge
     * @param type - The type of the pokemon
     * @param g - The graph
     * @return if the pokemon on the edge
     */
    private static boolean isOnOppositeEdge(geo_location p, edge_data e, int type, directed_weighted_graph g) {
        int src = g.getNode(e.getSrc()).getKey();
        int dest = g.getNode(e.getDest()).getKey();
        if(type<0 && dest>src) {return isOnEdge(p,src, dest, g);}
        if(type>0 && src>dest) {return isOnEdge(p,src, dest, g);}
        return false;
    }

    //-----------------------------------------------------------------

    /**
     * This function runs the dijkstra algorithm form a node on the graph to each reachable node
     * and builds a Map form each node to the dest and the before node (that it needs to reach it)
     * @param graph - The graph
     * @param src - The src node key
     * @return The map representing how and at what destination each node can be reached form src node
     */
    public static Map<node_data, DWGraph_Node_helper> shortestPaths(directed_weighted_graph graph, int src) {
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
        return dwhelper;
    }

    /**
     * This function get the map from shortestPaths function and return all the paths from src to each node in the graph
     * @param graph - This graph
     * @param dwhelper - The map
     * @param src - The src node key
     * @return A map representing each node key and its path from src to it
     */
    public static Map<Integer,List<node_data>> getPaths(directed_weighted_graph graph, Map<node_data, DWGraph_Node_helper> dwhelper, int src){

        Map<Integer, List<node_data>> res = new HashMap<>();

        for (node_data node : graph.getV()) {

            if (node.getKey() != src) {

                // the list of nodes that represent the path form src to dest
                List<node_data> path = new ArrayList<>();
                // building the path from dest to src
                node_data at = graph.getNode(node.getKey());
                for (; dwhelper.get(at).get_ckey() != null && at.getKey() != src; at = graph.getNode(dwhelper.get(at).get_ckey()))
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

                res.put(node.getKey(),path);
            }
        }

        // finally returning the path
        return res;
    }

    public static Map<Double,List<CL_Pokemon>> path_by_pokemon_value(directed_weighted_graph graph, List<node_data> path, List<CL_Pokemon> pokemonList){
        Map<Double,List<CL_Pokemon>> res = new HashMap<>();
        Double value = 0.0;
        List<CL_Pokemon> pokemons_on_path = new ArrayList<>();
        for (int i = 0; i < path.size() - 1; i++) {
            for (CL_Pokemon p : pokemonList) {
                if (isOnEdge(p.getLocation(), graph.getEdge(path.get(i).getKey(),path.get(i + 1).getKey()), p.getType(),graph))
                {
                    pokemons_on_path.add(p);
                    value += p.getValue();
                }
            }
        }
        res.put(value,pokemons_on_path);
        return res;
    }

    public static Map<Double,List<CL_Pokemon>> path_by_pokemon_value_planB(directed_weighted_graph graph, List<node_data> path, List<CL_Pokemon> pokemonList){
        Map<Double,List<CL_Pokemon>> res = new HashMap<>();
        Double value = 0.0;
        List<CL_Pokemon> pokemons_on_path = new ArrayList<>();
        for (int i = 0; i < path.size() - 1; i++) {
            for (CL_Pokemon p : pokemonList) {
                if (isOnEdge(p.getLocation(), graph.getEdge(path.get(i).getKey(),path.get(i + 1).getKey()), p.getType(),graph)
                        || isOnOppositeEdge(p.getLocation(), graph.getEdge(path.get(i).getKey(),path.get(i + 1).getKey()), p.getType(),graph))
                {
                    pokemons_on_path.add(p);
                    value += p.getValue();
                }
            }
        }
        res.put(value,pokemons_on_path);
        return res;
    }

    public static int best_next_move_for_agent(directed_weighted_graph graph, CL_Agent agent, List<CL_Pokemon> pokemons) {
        int dest = -1;
        List<CL_Pokemon> dest_Pokemons = new ArrayList<>();

        int agentSrc = agent.getSrcNode();

        // if one move for pokemon
//        int max = -1;
//        for (CL_Pokemon p : pokemons) {
//            if (p.getTarget_agent_id() == null || p.getTarget_agent_id() == agentSrc) {
//                for (edge_data e : graph.getE(agent.getSrcNode())) {
//                    if (e == p.get_edge() && p.getValue() > max) {
//                        dest = e.getDest();
//                        dest_Pokemons.add(p);
//                    }
//                }
//            }
//        }

        dw_graph_algorithms graph_algorithms = new DWGraph_Algo();
        graph_algorithms.init(graph);

        for (CL_Pokemon p : pokemons) {
            if (p.getTarget_agent_id() != null && p.getTarget_agent_id() == agent.getID()) {
                if (p.getType() == -1) {
                    List<node_data> node_dataList = graph_algorithms.shortestPath(agentSrc, p.get_edge().getDest());
                    if (node_dataList.size() > 1) {
                        dest_Pokemons.add(p);
                        dest = node_dataList.get(1).getKey();
                    }
                    else {
                        dest_Pokemons.add(p);
                        dest = p.get_edge().getSrc();
                    }
                }
                else {
                    List<node_data> node_dataList = graph_algorithms.shortestPath(agentSrc, p.get_edge().getSrc());
                    if (node_dataList.size() > 1) {
                        dest_Pokemons.add(p);
                        dest = node_dataList.get(1).getKey();
                    }
                    else {
                        dest_Pokemons.add(p);
                        dest = p.get_edge().getDest();
                    }
                }
                break;
            }
        }

        double best_wight_pokemon_value = -1;
        if (dest == -1) {
            Map<node_data, DWGraph_Node_helper> dwhelper = shortestPaths(graph, agentSrc);
            Map<Integer, List<node_data>> allpaths = getPaths(graph, dwhelper, agentSrc);
            for (Map.Entry<Integer, List<node_data>> entry : allpaths.entrySet()) {
                int path_dest = entry.getKey();
                List<node_data> path = entry.getValue();

                double path_weight = dwhelper.get(graph.getNode(path_dest)).get_dest();

                Map<Double, List<CL_Pokemon>> pokemons_value_on_path = path_by_pokemon_value(graph, path, pokemons);

                Double value = pokemons_value_on_path.entrySet().iterator().next().getKey();
                List<CL_Pokemon> pokemonsOnPath = pokemons_value_on_path.entrySet().iterator().next().getValue();

                if (value > 0) {
                    CL_Pokemon pokemon_d = pokemonsOnPath.get(pokemonsOnPath.size() - 1);

                    if (pokemon_d.getTarget_agent_id() == null || pokemon_d.getTarget_agent_id() == agent.getID()) {
                        if (value / path_weight > best_wight_pokemon_value) {
                            best_wight_pokemon_value = value / path_weight;
                            dest_Pokemons.addAll(pokemonsOnPath);
                            dest = path.get(1).getKey();
                        }
                    }
                }

            }

            double best_wight_pokemon_value_planB = -1;
            if (dest == -1) {
                for (Map.Entry<Integer, List<node_data>> entry : allpaths.entrySet()) {
                    int path_dest = entry.getKey();
                    List<node_data> path = entry.getValue();

                    double path_weight = dwhelper.get(graph.getNode(path_dest)).get_dest();

                    Map<Double, List<CL_Pokemon>> pokemons_value_on_path = path_by_pokemon_value_planB(graph, path, pokemons);

                    Double value = pokemons_value_on_path.entrySet().iterator().next().getKey();
                    List<CL_Pokemon> pokemonsOnPath = pokemons_value_on_path.entrySet().iterator().next().getValue();

                    if (value > 0) {
                        CL_Pokemon pokemon_d = pokemonsOnPath.get(pokemonsOnPath.size() - 1);

                        if (pokemon_d.getTarget_agent_id() == null || pokemon_d.getTarget_agent_id() == agent.getID()) {
                            if (value / path_weight > best_wight_pokemon_value_planB) {
                                best_wight_pokemon_value_planB = value / path_weight;
                                dest_Pokemons.addAll(pokemonsOnPath);
                                dest = path.get(1).getKey();
                            }
                        }
                    }

                }
            }

            if (dest == -1) {
                for (CL_Pokemon p : pokemons) {
                    if (p.getTarget_agent_id() == null || p.getTarget_agent_id() == agent.getID()) {
                        List<node_data> node_dataList = graph_algorithms.shortestPath(agentSrc, p.get_edge().getSrc());
                        if (node_dataList == null || node_dataList.size() <= 1) {
                            node_dataList = graph_algorithms.shortestPath(agentSrc, p.get_edge().getDest());
                        }
                        dest_Pokemons.add(p);
                        dest = node_dataList.get(1).getKey();
                        break;
                    }
                }
            }

            if (dest != -1)
                for (CL_Pokemon dest_Pokemon : dest_Pokemons) {
                    dest_Pokemon.setTarget_agent_id(agent.getID());
                }

            double path_w = Double.MAX_VALUE;
            if (dest == -1) {
                for (CL_Pokemon p : pokemons) {
                    double p_w = graph_algorithms.shortestPathDist(agentSrc, p.get_edge().getSrc());
                    if (p_w < path_w) {
                        List<node_data> node_dataList = graph_algorithms.shortestPath(agentSrc, p.get_edge().getSrc());
                        if (node_dataList == null || node_dataList.size() <= 1) {
                            node_dataList = graph_algorithms.shortestPath(agentSrc, p.get_edge().getDest());
                        }
                        dest = node_dataList.get(1).getKey();
                        path_w = p_w;
                        System.out.println("just go!");
                    }
                }
            }
        }

        return dest;
    }
    //TODO If oneway edge
}
