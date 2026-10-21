package connectivity;
import graph.Graph;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
public class Kosaraju {
    public static class Result {
        public final List<List<Integer>> components = new ArrayList<>();
        public long operations;
    }
    private static void firstDfs(int vertex, Graph graph, boolean[] visited, List<Integer> finishOrder, Result result) {
        visited[vertex] = true;
        for (Graph.Edge edge : graph.neighbors(vertex)) {
            result.operations++;
            if (!visited[edge.to]) {
                firstDfs(edge.to, graph, visited, finishOrder, result);
            }
        }
        finishOrder.add(vertex);
    }
    private static void secondDfs(int vertex, List<List<Integer>> reversed, boolean[] visited, List<Integer> component, Result result) {
        visited[vertex] = true;
        component.add(vertex);
        for (int next : reversed.get(vertex)) {
            result.operations++;
            if (!visited[next]) {
                secondDfs(next, reversed, visited, component, result);
            }
        }
    }
    public static Result run(Graph graph) {
        Result result = new Result();
        int n = graph.vertexCount();
        boolean[] visited = new boolean[n];
        List<Integer> finishOrder = new ArrayList<>();
        for (int vertex = 0; vertex < n; vertex++) {
            if (!visited[vertex]) {
                firstDfs(vertex, graph, visited, finishOrder, result);
            }
        }
        List<List<Integer>> reversed = new ArrayList<>();
        for (int vertex = 0; vertex < n; vertex++) {
            reversed.add(new ArrayList<>());
        }
        for (Graph.Edge edge : graph.edges()) {
            reversed.get(edge.to).add(edge.from);
            result.operations++;
        }
        Collections.reverse(finishOrder);
        visited = new boolean[n];
        for (int vertex : finishOrder) {
            if (!visited[vertex]) {
                List<Integer> component = new ArrayList<>();
                secondDfs(vertex, reversed, visited, component, result);
                result.components.add(component);
            }
        }
        return result;
    }
}