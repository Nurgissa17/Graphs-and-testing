import graph.Graph;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;
public class Dijkstra {
    public static final long INF = Long.MAX_VALUE;
    private static class State {
        int vertex;
        long distance;
        State(int vertex, long distance) {
            this.vertex = vertex;
            this.distance = distance;
        }
    }
    public static class Result {
        public long[] distance;
        public int[] previous;
        public long operations;
        public List<Integer> pathTo(int target) {
            List<Integer> path = new ArrayList<>();
            if (distance[target] == INF) {
                return path;
            }
            for (int at = target; at != -1; at = previous[at]) {
                path.add(at);
            }
            Collections.reverse(path);
            return path;
        }
    }
    public static Result run(Graph graph, int source) {
        Result result = new Result();
        result.distance = new long[graph.vertexCount()];
        result.previous = new int[graph.vertexCount()];
        Arrays.fill(result.distance, INF);
        Arrays.fill(result.previous, -1);
        result.distance[source] = 0;
        PriorityQueue<State> queue =
                new PriorityQueue<>((a, b) -> Long.compare(a.distance, b.distance));
        queue.add(new State(source, 0));
        result.operations++;
        while (!queue.isEmpty()) {
            State current = queue.poll();
            result.operations++;
            if (current.distance != result.distance[current.vertex]) {
                continue;
            }
            for (Graph.Edge edge : graph.neighbors(current.vertex)) {
                result.operations++;
                long distance = current.distance + edge.weight;
                if (distance < result.distance[edge.to]) {
                    result.distance[edge.to] = distance;
                    result.previous[edge.to] = current.vertex;
                    queue.add(new State(edge.to, distance));
                    result.operations++;
                }
            }
        }
        return result;
    }
}