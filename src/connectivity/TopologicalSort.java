package connectivity;
import graph.Graph;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
public class TopologicalSort {
    public static class Result {
        public final List<Integer> order = new ArrayList<>();
        public boolean possible;
        public long operations;
    }
    public static Result run(Graph graph) {
        int[] indegree = new int[graph.vertexCount()];
        Result result = new Result();
        for (Graph.Edge edge : graph.edges()) {
            indegree[edge.to]++;
            result.operations++;
        }
        Queue<Integer> queue = new ArrayDeque<>();
        for (int vertex = 0; vertex < graph.vertexCount(); vertex++) {
            if (indegree[vertex] == 0) {
                queue.add(vertex);
            }
        }
        while (!queue.isEmpty()) {
            int vertex = queue.poll();
            result.order.add(vertex);
            for (Graph.Edge edge : graph.neighbors(vertex)) {
                result.operations++;
                indegree[edge.to]--;
                if (indegree[edge.to] == 0) {
                    queue.add(edge.to);
                }
            }
        }
        result.possible = result.order.size() == graph.vertexCount();
        if (!result.possible) {
            result.order.clear();
        }
        return result;
    }
}