import graph.Graph;
import connectivity.Kosaraju;
import connectivity.TopologicalSort;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Random;
public class Experiment {
    private static final int REPEATS = 5;
    private static final String[] NAMES = {
            "Kruskal", "Prim", "Kosaraju", "Topological (DAG)",
            "Topological (cycle)", "Dijkstra"
    };
    private static final String[] GRAPH_TYPES = {
            "undirected", "undirected", "directed cyclic", "DAG",
            "directed cyclic", "DAG"
    };
    private static Graph undirectedGraph(int vertices, int edges, long seed) {
        Graph graph = new Graph(vertices, false);
        Random random = new Random(seed);
        boolean[][] used = new boolean[vertices][vertices];
        int count = 0;
        for (int v = 1; v < vertices; v++) {
            int u = random.nextInt(v);
            graph.addEdge(u, v, 1 + random.nextInt(20));
            used[u][v] = true;
            used[v][u] = true;
            count++;
        }
        while (count < edges) {
            int u = random.nextInt(vertices);
            int v = random.nextInt(vertices);
            if (u == v || used[u][v]) {
                continue;
            }
            graph.addEdge(u, v, 1 + random.nextInt(20));
            used[u][v] = true;
            used[v][u] = true;
            count++;
        }
        return graph;
    }
    private static Graph directedGraph(int vertices, int edges, boolean cyclic, long seed) {
        Graph graph = new Graph(vertices, true);
        Random random = new Random(seed);
        boolean[][] used = new boolean[vertices][vertices];
        int count = 0;
        for (int v = 0; v < vertices - 1; v++) {
            graph.addEdge(v, v + 1, 1 + random.nextInt(20));
            used[v][v + 1] = true;
            count++;
        }
        if (cyclic) {
            graph.addEdge(2, 0, 1 + random.nextInt(20));
            used[2][0] = true;
            count++;
        }
        while (count < edges) {
            int u = random.nextInt(vertices);
            int v = random.nextInt(vertices);
            if (u >= v || used[u][v]) {
                continue;
            }
            graph.addEdge(u, v, 1 + random.nextInt(20));
            used[u][v] = true;
            count++;
        }
        return graph;
    }
    public static void run() throws IOException {
        int[] vertices = {10, 100, 1000};
        int[] edges = {30, 500, 5000};
        StringBuilder csv = new StringBuilder("algorithm,graph_type,V,E,repeats,average_ms,average_operations\n");
        for (int i = 0; i < vertices.length; i++) {
            int v = vertices[i];
            int e = edges[i];
            Graph roads = undirectedGraph(v, e, 42 + v);
            Graph dag = directedGraph(v, e, false, 142 + v);
            Graph cyclic = directedGraph(v, e, true, 242 + v);
            long[] totalNanos = new long[NAMES.length];
            long[] totalOperations = new long[NAMES.length];
            for (int repetition = 0; repetition < REPEATS; repetition++) {
                long start = System.nanoTime();
                MST.Result kruskal = MST.kruskal(roads);
                totalNanos[0] += System.nanoTime() - start;
                totalOperations[0] += kruskal.operations;
                start = System.nanoTime();
                MST.Result prim = MST.prim(roads);
                totalNanos[1] += System.nanoTime() - start;
                totalOperations[1] += prim.operations;
                if (!kruskal.connected || !prim.connected || kruskal.weight != prim.weight) {
                    throw new IllegalStateException("MST results do not match");
                }
                start = System.nanoTime();
                Kosaraju.Result scc = Kosaraju.run(cyclic);
                totalNanos[2] += System.nanoTime() - start;
                totalOperations[2] += scc.operations;
                start = System.nanoTime();
                TopologicalSort.Result topoDag = TopologicalSort.run(dag);
                totalNanos[3] += System.nanoTime() - start;
                totalOperations[3] += topoDag.operations;
                start = System.nanoTime();
                TopologicalSort.Result topoCycle = TopologicalSort.run(cyclic);
                totalNanos[4] += System.nanoTime() - start;
                totalOperations[4] += topoCycle.operations;
                if (!topoDag.possible || topoCycle.possible) {
                    throw new IllegalStateException("Topological ordering failed");
                }
                start = System.nanoTime();
                Dijkstra.Result dijkstra = Dijkstra.run(dag, 0);
                totalNanos[5] += System.nanoTime() - start;
                totalOperations[5] += dijkstra.operations;
            }
            for (int algorithm = 0; algorithm < NAMES.length; algorithm++) {
                double averageMs = totalNanos[algorithm] / (double) REPEATS / 1_000_000.0;
                double averageOperations = totalOperations[algorithm] / (double) REPEATS;
                csv.append(String.format(Locale.US, "%s,%s,%d,%d,%d,%.6f,%.0f%n",
                        NAMES[algorithm], GRAPH_TYPES[algorithm], v, e,
                        REPEATS, averageMs, averageOperations));
            }
        }
        Path path = Path.of("results/tables/benchmark.csv");
        Files.createDirectories(path.getParent());
        Files.writeString(path, csv.toString());
    }
}