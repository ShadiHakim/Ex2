# Ex2
A project representing a directional weighted graph that support a large number of nodes (over 100,000).

## Getting started

### Installation
To install the project open the Terminal and run the following command

    git clone https://github.com/ShadiHakim/Ex2.git

### Usage
To run the project using **CLI(Command Line)** open the terminal and change the directory to Ex2.
run the following command

    java -jar Ex2.jar [ID] [Scenario_Number]

To run the project using **GUI(Graphical User Interface)** just open the directory of Ex2 and run Ex2.jar.

## Data structure

#### DWGraph_DS

```java
HashMap<Integer, node_data> nodes  //all the nodes in the graph <key, node>
```

```java
HashMap<Integer, HashMap<Integer, edge_data>> node_connections  //all the connections of the node <key, map<neighbour_key, edge>>
```

```java
int edge_size  //the number of edges in the graph
```

```java
int mc  //the number of changes occurred in the graph
```

----

#### DWGraph_DS:DWGraph_Node

```java
int key  //The key represents a unique id for the node 
```

```java
geo_location location  //The location of the node 
```

```java
double weight  //The weight for the node 
```

```java
String info  //This is an Info field
```

```java
int tag  //This is a tag field
```

----

#### DWGraph_DS:DWGraph_Edge

```java
int src  //This field represent the node key that is at the beginning of the edge  
```

```java
int dest  //This field represent the node key that is at the ending of the edge
```

```java
double weight  //This field represent the wieght of the edge
```

```java
String info  //This is an Info field
```

```java
int tag  //This is a tag field
```

----

#### DWGraph_Node_helper

```java
double _dest  //This represent the shortest destination to the node
```

```java
Integer _ckey  //This represent the node key that is before it
```

----

#### DWGraph_Algo

```java
directed_weighted_graph graph  //Pointer of the graph, that all the functions will be done on
```

## Functions

#### WGraph_DS

| Function name | Complexity | Description |
| ------------- | ------------- | ------------------------------ |
| `getNode(int key)` | O(1) | return node by key |
| `getEdge(int src, int dest)` | O(1) | return the object of the edge that connects the two given key_nodes |
| `addNode(node_data n)` | O(1) | adds a nodes with the given key to the graph |
| `connect(int src, int dest, double w)` | O(1) | connect the to nodes (if they exist) with the given weight |
| `getV()` | O(1) | a simple get func that returns all the nodes in the graph |
| `getE(int node_id)` | O(1) | a get func that returns all the edges that get out of the given node |
| `removeNode(int key)` | O(n) | return node by key after cutting all the connections with its neighbours |
| `removeEdge(int src, int dest)` | O(1) | cut the connection between the two given nodes  |
| `nodeSize()` | O(1) | a simple get func that returns number of the node in the graph |
| `edgeSize()` | O(1) | a simple get func that returns the number of edges in the graph |
| `getMC()` | O(1) | a simple get func that returns the number of changes occurred in the graph |
| **`node_Copy(node_data node)`** | O(1) | return a deep copy of the given node |


#### WGraph_Algo

| Function name | Complexity | Description |
| ------------- | ------------- | ------------------------------ |
| `init(directed_weighted_graph g)` | O(1) | initialize the graph (a simple set for the graph) |
| `getGraph()` | O(1) | a simple get func that returns the graph |
| `copy()` | O(n) | returns a deep copy of the init graph |
| `isConnected()` | O(n^2) | returns if the graph is strongly connected (can reach any node from any node) |
| `shortestPathDist(int src, int dest)` | O(n) | return the the shortest path distinction from the src_nodes to dest_node |
| `shortestPath(int src, int dest)` | O(n) | return the the shortest path from the src_nodes to dest_node (node by node) |
| `save(String file)` | | saves the graph as a JSON to the given destination file and returns true if success  |
| `load(String file)` | | retrieve the graph from a JSON file returns true if success |

----
