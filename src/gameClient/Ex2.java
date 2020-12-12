package gameClient;

import Server.Game_Server_Ex2;
import api.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import gameClient.util.Point3D;
import implemention.DWGraph_Algo;
import implemention.jsonFormat.MyJsonDeserializer;
import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;
import org.graphstream.ui.spriteManager.Sprite;
import org.graphstream.ui.spriteManager.SpriteManager;
import org.json.JSONException;
import org.json.JSONObject;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.*;

public class Ex2 implements Runnable{

    private static int scenario_num = -1;
    private static int ID;

    private long totalTime;
    private game_service game;
    private dw_graph_algorithms graph_algorithms;
    private directed_weighted_graph graph;
    private Graph ui_graph;
    private SpriteManager sman_pokemons;
    private SpriteManager sman_agents;
    private List<CL_Agent> agentList = new ArrayList<>();
    private List<CL_Pokemon> pokemonList = new ArrayList<>();

    long dt=10;

    public static void main(String[] args) {
        System.setProperty("org.graphstream.ui", "swing");

        setUp(args);
    }

    @Override
    public void run() {
        init();

        game.startGame();
        totalTime = game.timeToEnd();

        while (game.isRunning()) {
            moveAgents();
            timer_update();
            try {
                Thread.sleep(dt);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        String res = game.toString();

        System.out.println(res);
        System.exit(0);
    }

    public void init() {
        game = Game_Server_Ex2.getServer(scenario_num); // you have [0,23] games
        ui_graph = new MultiGraph("scenario_num:" + scenario_num);

        game.login(ID);

        String g = game.getGraph();
        String fs = game.getPokemons();
        graph_algorithms = new DWGraph_Algo();
        graph_algorithms.init(load(g));
        graph = graph_algorithms.getGraph();

        drawGraph();

        sman_pokemons = new SpriteManager(ui_graph);
        drawPokemons(MyUtil.json2Pokemons(fs));

        sman_agents = new SpriteManager(ui_graph);

        ui_graph.display(false);
        String info = game.toString();
        JSONObject line;
        try {
            line = new JSONObject(info);
            JSONObject gameServer_json = line.getJSONObject("GameServer");
            int num_of_agents = gameServer_json.getInt("agents");
            System.out.println(info);
            System.out.println(game.getPokemons());

            init_Agents(num_of_agents);
        }
        catch (JSONException e) {e.printStackTrace();}
    }

    public void init_Agents(int num_of_agents){
        ArrayList<CL_Pokemon> newpokemonList = MyUtil.json2Pokemons(game.getPokemons());
        pokemonList = MyUtil.updatePokemons(newpokemonList, pokemonList, graph);

        pokemonList.sort(Comparator.comparingDouble(CL_Pokemon::getValue));

        for(int i = 0; i < num_of_agents; i++) {
            CL_Pokemon c = pokemonList.get(i);
            int nn = c.get_edge().getDest();
            if(c.getType()<0 ) {nn = c.get_edge().getSrc();}

            game.addAgent(nn);
        }
    }

    public static void setUp(String[] args){
        Ex2 ex2 = new Ex2();
        if (args != null && args.length == 2){
            try {
                ID = Integer.parseInt(args[0]);
                scenario_num = Integer.parseInt(args[1]);
            } catch (NumberFormatException exception) {
                System.out.println("Please enter a proper number " + exception.getMessage());
//                exception.printStackTrace();
                scenario_num = -1;
            }
            if (scenario_num != -1){
                Thread client = new Thread(ex2);
                client.start();
            }
        }
        else {
            SettingsUI settingsUI = new SettingsUI();
            settingsUI.setVisible(true);
            settingsUI.setClickListener(new SettingsUI.ClickListener() {
                @Override
                public void onClick(ActionEvent e) {
                    String s = e.getActionCommand();
                    if (s.equals("Start")) {
                        String[] args = settingsUI.getData();
                        if (args != null && args.length == 2){
                            try {
                                ID = Integer.parseInt(args[0]);
                                scenario_num = Integer.parseInt(args[1]);
                            } catch (NumberFormatException exception) {
                                System.out.println("Please enter a proper number " + exception.getMessage());
//                                exception.printStackTrace();
                                scenario_num = -1;
                                settingsUI.error("Please enter a number!");
                            }
                        }
                        if (scenario_num != -1){
                            settingsUI.setVisible(false);
                            Thread client = new Thread(ex2);
                            client.start();
                        }
                    }
                }
            });
        }
    }

    public directed_weighted_graph load(String json) {
        directed_weighted_graph graph = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            SimpleModule deserialization = new SimpleModule();
            deserialization.addDeserializer(directed_weighted_graph.class, new MyJsonDeserializer());
            mapper.registerModule(deserialization);

            graph = mapper.readValue(json, directed_weighted_graph.class);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return graph;
    }
    //----------------------------------------------------------------------------------------------------------------

    private void timer_update(){
        ui_graph.setAttribute("ui.title", "Scenario number: " + scenario_num + " - Time left: " + game.timeToEnd() / 1000);
    }

    /**
     * Moves each of the agents along the edge.
     * @param
     */
    private void moveAgents() {
        String lg = game.getAgents();
        MyUtil.getAgents(lg, graph, agentList);
        drawAgents(agentList);

        String fs =  game.getPokemons();
        List<CL_Pokemon> newpokemonList = MyUtil.json2Pokemons(fs);
        pokemonList = MyUtil.updatePokemons(newpokemonList,pokemonList,graph);
        drawPokemons(pokemonList);

        boolean flag = false;
        for(int i=0;i<agentList.size();i++) {
            CL_Agent ag = agentList.get(i);
            int id = ag.getID();
            int dest = ag.getNextNode();
            int src = ag.getSrcNode();
            double v = ag.getValue();
            double speed = ag.getSpeed();
            if(dest==-1 && !ag.isMoving()) {
                dest = nextNode(ag, pokemonList);
                ag.setNextNode(dest);
                long a = game.chooseNextEdge(id, dest);
                System.out.println("Agent: "+id+", val: "+v+"   turned to node: "+dest +" speed: "+speed + " rr: " + dt);
            }
            else {
                long t = (long) (ag.get_curr_edge().getWeight() / speed * 300);
                if (!flag || t < dt){
                    flag = true;
                    dt = t;
                }
            }
        }
        game.move();
    }

    /**
     * a very simple walk implementation!
     * @param agent
     * @return
     */
    private int nextNode(CL_Agent agent, List<CL_Pokemon> pokemonList) {
        int ans = -1;
        pokemonList.sort(Comparator.comparingDouble(CL_Pokemon::getValue));
        ans = MyUtil.best_next_move_for_agent(graph, agent, pokemonList);
        return ans;
    }

    //----------------------------------------------------------------------------------------------------------------

    private void drawGraph() {
        Iterator<node_data> iter = graph.getV().iterator();
        while(iter.hasNext()) {
            node_data n = iter.next();
            ui_graph.addNode(String.valueOf(n.getKey()));
            Node node = ui_graph.getNode(String.valueOf(n.getKey()));
            node.setAttribute("x" , n.getLocation().x());
            node.setAttribute("y" , n.getLocation().y());
            node.setAttribute("z" , n.getLocation().z());
            node.setAttribute("ui.style", " text-alignment: above;");
            node.setAttribute("ui.label", n.getKey() + "");

        }

        iter = graph.getV().iterator();
        while(iter.hasNext()) {
            Collection<edge_data> edges = graph.getE(iter.next().getKey());
            for (edge_data e :
                    edges) {
                if (ui_graph.getEdge(e.getDest() + "" + e.getSrc()) == null){
                    ui_graph.addEdge(e.getSrc() + "" + e.getDest(),String.valueOf(e.getSrc()), String.valueOf(e.getDest()), true);
                    ui_graph.getEdge(e.getSrc() + "" + e.getDest()).setAttribute("ui.style", "arrow-size: 15px, 5px;");
                }
                else {
                    ui_graph.removeEdge(e.getDest() + "" + e.getSrc());
                    ui_graph.addEdge(e.getSrc() + "" + e.getDest(),String.valueOf(e.getSrc()), String.valueOf(e.getDest()));
                }

            }
        }
    }

    private void drawPokemons(List<CL_Pokemon> pokemons) {
        int Sprite_num = sman_pokemons.getSpriteCount();
        for (int i = 0; i < Sprite_num; i++) {
            sman_pokemons.removeSprite("p" + i);
        }

        int i = 0;
        for (CL_Pokemon p : pokemons) {

            Sprite s = sman_pokemons.addSprite("p" + i);
            s.setPosition(p.getLocation().x(),p.getLocation().y(),p.getLocation().z());

            if (p.getType() < 0)
                s.setAttribute("ui.style", "size: 20px, 20px; fill-color: #ff7a01;");
            else
                s.setAttribute("ui.style", "size: 20px, 20px; fill-color: #00aa23;");

            i++;
        }
    }

    private void drawAgents(List<CL_Agent> agents) {
        int Sprite_num = sman_agents.getSpriteCount();
        for (int i = 0; i < Sprite_num; i++) {
            sman_agents.removeSprite("a" + i);
        }

        int i = 0;
        for (CL_Agent a : agents) {

            Sprite s = sman_agents.addSprite("a" + i);
            s.setPosition(a.getLocation().x(),a.getLocation().y(),a.getLocation().z());
            s.setAttribute("ui.style", "size: 20px, 20px; fill-color: #f23f2b; text-style:bold; text-alignment: above;");
            s.setAttribute("ui.label", a.getValue() + "");

            i++;
        }
    }
}
