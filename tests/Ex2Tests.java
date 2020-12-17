import api.directed_weighted_graph;
import api.dw_graph_algorithms;
import api.geo_location;
import api.node_data;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import gameClient.util.Point3D;
import implemention.DWGraph_Algo;
import implemention.DWGraph_DS;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class Ex2Tests {
    @Test
    public void graphTest_0() {
        directed_weighted_graph g = new DWGraph_DS();
        g.addNode(new DWGraph_DS.DWGraph_Node(0));
        assertEquals(1,g.nodeSize());
        node_data node_info = g.getNode(0);
        assertNotNull(node_info);
        node_info = g.getNode(1);
        assertNull(node_info);
        g.removeNode(0);
        assertEquals(0,g.nodeSize());
    }

    @Test
    public void graphTest_1() {
        directed_weighted_graph g = new DWGraph_DS();
        g.addNode(new DWGraph_DS.DWGraph_Node(0));
        g.addNode(new DWGraph_DS.DWGraph_Node(1));
        g.connect(0,1,10);
        assertEquals(1, g.edgeSize());
        g.connect(0,1,2);
        assertNotNull(g.getEdge(0,1));
        g.removeEdge(0,1);
        assertEquals(0, g.edgeSize());
        g.connect(0,1,25);
        g.addNode(new DWGraph_DS.DWGraph_Node(2));
        g.connect(0,2,10);
        g.connect(1,2,15);
        assertEquals(3,g.edgeSize());
        g.removeNode(0);
        assertEquals(1,g.edgeSize());
        assertEquals(15, g.getEdge(1,2).getWeight());
    }

    @Test
    public void graphTest_2(){
        directed_weighted_graph g = new DWGraph_DS();
        g.addNode(new DWGraph_DS.DWGraph_Node(0));
        g.addNode(new DWGraph_DS.DWGraph_Node(1));
        g.addNode(new DWGraph_DS.DWGraph_Node(2));
        g.addNode(new DWGraph_DS.DWGraph_Node(3));
        g.addNode(new DWGraph_DS.DWGraph_Node(4));
        g.connect(0,1,1);
        g.connect(1,2,2);
        g.connect(2,3,3);
        g.connect(3,4,4);
        g.connect(4,0,5);
        dw_graph_algorithms weightedGraphAlgorithms = new DWGraph_Algo();
        weightedGraphAlgorithms.init(g);
        directed_weighted_graph ng = weightedGraphAlgorithms.copy();
        assertEquals(g.nodeSize(),ng.nodeSize());
        assertEquals(g.edgeSize(),ng.edgeSize());
        assertEquals(g.getMC(),ng.getMC());
    }

    @Test
    public void graphTest_3(){
        directed_weighted_graph g = new DWGraph_DS();
        g.addNode(new DWGraph_DS.DWGraph_Node(0));
        g.addNode(new DWGraph_DS.DWGraph_Node(1));
        g.addNode(new DWGraph_DS.DWGraph_Node(2));
        g.addNode(new DWGraph_DS.DWGraph_Node(3));
        g.addNode(new DWGraph_DS.DWGraph_Node(4));
        g.connect(0,1,1);
        g.connect(1,2,2);
        g.connect(2,3,3);
        g.connect(3,4,4);
        g.connect(4,0,5);
        dw_graph_algorithms weightedGraphAlgorithms = new DWGraph_Algo();
        weightedGraphAlgorithms.init(g);
        assertTrue(weightedGraphAlgorithms.isConnected());
    }

    @Test
    public void graphTest_4(){
        directed_weighted_graph g = new DWGraph_DS();
        g.addNode(new DWGraph_DS.DWGraph_Node(0));
        g.addNode(new DWGraph_DS.DWGraph_Node(1));
        g.addNode(new DWGraph_DS.DWGraph_Node(2));
        g.addNode(new DWGraph_DS.DWGraph_Node(3));
        g.addNode(new DWGraph_DS.DWGraph_Node(4));
        g.connect(0,1,6);
        g.connect(4,0,1);
        g.connect(1,2,5);
        g.connect(1,3,2);
        g.connect(2,3,5);
        g.connect(3,4,1);
        g.connect(4,1,2);

        dw_graph_algorithms weightedGraphAlgorithms = new DWGraph_Algo();
        weightedGraphAlgorithms.init(g);
        weightedGraphAlgorithms.shortestPath(0,5);
        assertEquals(3,weightedGraphAlgorithms.shortestPath(0,2).size());
        double sh = weightedGraphAlgorithms.shortestPathDist(1,0);
        assertEquals(4.0,sh);
    }

    @Test
    public void graphTest_x(){
        directed_weighted_graph g = new DWGraph_DS();
        g.addNode(new node_data_maker(0,new Point3D("35.2016888087167,32.10601755126051,0.0"),1.5));
        g.addNode(new node_data_maker(1));
        g.addNode(new node_data_maker(2));
        g.addNode(new node_data_maker(3));
        g.addNode(new node_data_maker(4));
        g.connect(0,1,1);
        g.connect(1,2,2);
        g.connect(2,3,3);
        g.connect(3,4,4);
        g.connect(4,0,5);
        dw_graph_algorithms weightedGraphAlgorithms = new DWGraph_Algo();
        weightedGraphAlgorithms.init(g);
        weightedGraphAlgorithms.save("object.json");
        dw_graph_algorithms weightedGraphAlgorithms1 = new DWGraph_Algo();
        weightedGraphAlgorithms1.load("object.json");
        assertEquals(weightedGraphAlgorithms.getGraph().nodeSize(),weightedGraphAlgorithms1.getGraph().nodeSize());
        assertEquals(weightedGraphAlgorithms.getGraph().edgeSize(),weightedGraphAlgorithms1.getGraph().edgeSize());
    }

    public class node_data_maker implements node_data{
        @JsonProperty("id")
        private int key;
        @JsonProperty("pos")
        @JsonDeserialize(as = Point3D.class)
        private geo_location location;
        @JsonProperty("w")
        private double weight;
        private String info;
        private int tag;

        public node_data_maker() {
        }

        public node_data_maker(int key) {
            this.key = key;
        }

        public node_data_maker(int key, geo_location location, double weight) {
            this.key = key;
            this.location = location;
            this.weight = weight;
        }

        public node_data_maker(node_data node_data_copy){
            this.key = node_data_copy.getKey();
            this.location = node_data_copy.getLocation();
            this.weight = node_data_copy.getWeight();
            this.info = node_data_copy.getInfo();
            this.tag = node_data_copy.getTag();
        }

        @Override
        public int getKey() {
            return key;
        }

        @Override
        public geo_location getLocation() {
            return location;
        }

        @Override
        public void setLocation(geo_location p) {
            location = p;
        }

        @Override
        public double getWeight() {
            return weight;
        }

        @Override
        public void setWeight(double w) {
            weight = w;
        }

        @Override
        public String getInfo() {
            return info;
        }

        @Override
        public void setInfo(String s) {
            info = s;
        }

        @Override
        public int getTag() {
            return tag;
        }

        @Override
        public void setTag(int t) {
            tag = t;
        }
    }
}
