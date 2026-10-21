import graph.Graph;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
public class MST {
    public static class Result {
        public int weight;
        public final List<Graph.Edge> selectedEdges = new ArrayList<>();
        public boolean connected;
        public long operations;
    }
    public static Result kruskal(Graph graph) {
        List<Graph.Edge> edges = new ArrayList<>(graph.edges());
        Result result = new Result();
        edges.sort((first, second) -> {
            result.operations++;
            return Integer.compare(first.weight, second.weight);
        });
        DSU dsu = new DSU(graph.vertexCount());
        for (Graph.Edge edge : edges) {
            if (result.selectedEdges.size() == graph.vertexCount() - 1) {
                break;
            }
            result.operations++;
            if (dsu.union(edge.from, edge.to)) {
                result.weight += edge.weight;
                result.selectedEdges.add(edge);
            }
        }
        result.operations += dsu.findCalls + dsu.unionCalls;
        result.connected = result.selectedEdges.size() == graph.vertexCount() - 1;
        return result;
    }
    public static Result prim(Graph graph) {
        Result result = new Result();
        boolean[] visited = new boolean[graph.vertexCount()];
        PriorityQueue<Graph.Edge> queue =
                new PriorityQueue<>(Comparator.comparingInt(edge -> edge.weight));
        visited[0] = true;
        queue.addAll(graph.neighbors(0));
        result.operations += graph.neighbors(0).size() * 2L;
        while (!queue.isEmpty() && result.selectedEdges.size() < graph.vertexCount() - 1) {
            Graph.Edge edge = queue.poll();
            result.operations++;
            if (visited[edge.to]) {
                continue;
            }
            visited[edge.to] = true;
            result.weight += edge.weight;
            result.selectedEdges.add(edge);
            for (Graph.Edge next : graph.neighbors(edge.to)) {
                result.operations++;
                if (!visited[next.to]) {
                    queue.add(next);
                    result.operations++;
                }
            }
        }
        result.connected = result.selectedEdges.size() == graph.vertexCount() - 1;
        return result;
    }
}