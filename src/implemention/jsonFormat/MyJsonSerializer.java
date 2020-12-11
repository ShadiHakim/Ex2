package implemention.jsonFormat;

import api.directed_weighted_graph;
import api.edge_data;
import api.node_data;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * This class helps Serialize a graph to a JSON format
 * @author shadihakim
 */
public class MyJsonSerializer extends StdSerializer<directed_weighted_graph> {

    /**
     * A Simple empty constructor
     */
    public MyJsonSerializer() {
        this(null);
    }

    /**
     * This constructor calls the super constructor
     * @param t
     */
    protected MyJsonSerializer(Class<directed_weighted_graph> t) {
        super(t);
    }

    /**
     * This is the main function that Serialize the graph
     * @param directed_weighted_graph
     * @param jsonGenerator
     * @param serializerProvider
     * @throws IOException
     */
    @Override
    public void serialize(directed_weighted_graph directed_weighted_graph, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeObjectField("Nodes", directed_weighted_graph.getV());
        Collection<edge_data> alledges = new ArrayList<>();
        for (node_data n :
                directed_weighted_graph.getV()) {
            alledges.addAll(directed_weighted_graph.getE(n.getKey()));
        }
        jsonGenerator.writeObjectField("Edges", alledges);
        jsonGenerator.writeEndObject();
    }
}