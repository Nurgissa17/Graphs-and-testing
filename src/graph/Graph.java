package graph;
import java.util.ArrayList;
import java.util.List;
public class Graph {
    public static class Edge {
        public final int from;
        public final int to;
        public final int weight;
        public Edge(int from, int to, int weight) {
            this.from = from;
            this.to = to;
            this.weight = weight;
        }
    }
    private final int vertices;
    private final boolean directed;
    private final List<Edge> edges = new ArrayList<>();
    private final List<List<Edge>> neighbors = new ArrayList<>();
    public Graph(int vertices, boolean directed) {
        this.vertices = vertices;
        this.directed = directed;
        for (int i = 0; i < vertices; i++) {
            neighbors.add(new ArrayList<>());
        }
    }
    public void addEdge(int from, int to, int weight) {
        Edge edge = new Edge(from, to, weight);
        edges.add(edge);
        neighbors.get(from).add(edge);
        if (!directed) {
            neighbors.get(to).add(new Edge(to, from, weight));
        }
    }
    public int vertexCount() {
        return vertices;
    }
    public List<Edge> edges() {
        return edges;
    }
    public List<Edge> neighbors(int vertex) {
        return neighbors.get(vertex);
    }
}