package implemention.jsonFormat;

import api.directed_weighted_graph;
import api.edge_data;
import api.node_data;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import implemention.DWGraph_DS;

import java.io.IOException;
import java.util.Iterator;

/**
 * This class helps Deserialize from a JSON format to a graph
 * @author shadihakim
 */
public class MyJsonDeserializer extends StdDeserializer<directed_weighted_graph> {

    /**
     * A Simple empty constructor
     */
    public MyJsonDeserializer() {
        this(null);
    }

    /**
     * This constructor calls the super constructor
     * @param vc
     */
    protected MyJsonDeserializer(Class<?> vc) {
        super(vc);
    }

    /**
     * This is the main function that Deserialize to graph object
     * @param jsonParser
     * @param deserializationContext
     * @return
     * @throws IOException
     */
    @Override
    public directed_weighted_graph deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonNode node = jsonParser.readValueAsTree();
        directed_weighted_graph g = new DWGraph_DS();
        ObjectMapper mapper = new ObjectMapper();
        Iterator<JsonNode> jsonnodes = node.get("Nodes").elements();
        while (jsonnodes.hasNext()) {
            node_data nodeData = mapper.readValue(jsonnodes.next().traverse(), DWGraph_DS.DWGraph_Node.class);
            g.addNode(nodeData);
        }
        Iterator<JsonNode> jsonnodeconnections = node.get("Edges").elements();
        while (jsonnodeconnections.hasNext()) {
            edge_data edgeData = mapper.readValue(jsonnodeconnections.next().traverse(), DWGraph_DS.DWGraph_Edge.class);
            g.connect(edgeData.getSrc(), edgeData.getDest(), edgeData.getWeight());
        }
        return g;
    }
}
