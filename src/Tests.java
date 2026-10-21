import graph.Graph;
import connectivity.Kosaraju;
import connectivity.TopologicalSort;
import java.util.List;
public class Tests {
    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
    private static void checkTopologicalOrder(Graph graph, List<Integer> order) {
        check(order.size() == graph.vertexCount(), "Wrong number of vertices in order");
        int[] position = new int[graph.vertexCount()];
        boolean[] found = new boolean[graph.vertexCount()];
        for (int i = 0; i < order.size(); i++) {
            int vertex = order.get(i);
            check(!found[vertex], "Duplicate vertex in order");
            found[vertex] = true;
            position[vertex] = i;
        }
        for (Graph.Edge edge : graph.edges()) {
            check(position[edge.from] < position[edge.to], "Incorrect topological order");
        }
    }
    private static void testSmallGraphAndDsu() {
        DSU dsu = new DSU(3);
        check(dsu.union(0, 1), "DSU union failed");
        check(dsu.union(1, 2), "DSU union failed");
        check(dsu.find(0) == dsu.find(2), "DSU find failed");
        check(!dsu.union(0, 2), "DSU accepted an existing connection");
        Graph graph = new Graph(3, false);
        graph.addEdge(0, 1, 2);
        graph.addEdge(1, 2, 3);
        graph.addEdge(0, 2, 10);
        check(MST.kruskal(graph).weight == 5, "Wrong Kruskal weight");
        check(MST.prim(graph).weight == 5, "Wrong Prim weight");
    }
    private static void testDisconnectedGraph() {
        Graph graph = new Graph(4, false);
        graph.addEdge(0, 1, 2);
        graph.addEdge(2, 3, 3);
        check(!MST.kruskal(graph).connected, "Kruskal missed disconnection");
        check(!MST.prim(graph).connected, "Prim missed disconnection");
    }
    private static void testDagAndCycle() {
        Graph dag = new Graph(4, true);
        dag.addEdge(0, 1, 1);
        dag.addEdge(0, 2, 1);
        dag.addEdge(1, 3, 1);
        dag.addEdge(2, 3, 1);
        TopologicalSort.Result result = TopologicalSort.run(dag);
        check(result.possible, "Valid DAG rejected");
        checkTopologicalOrder(dag, result.order);
        dag.addEdge(3, 0, 1);
        check(!TopologicalSort.run(dag).possible, "Cycle not detected");
    }
    private static void testMultipleMstsAndDuplicateWeights() {
        Graph graph = new Graph(4, false);
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 3, 1);
        graph.addEdge(3, 0, 1);
        graph.addEdge(0, 2, 5);
        MST.Result kruskal = MST.kruskal(graph);
        MST.Result prim = MST.prim(graph);
        check(kruskal.connected && prim.connected, "Graph should be connected");
        check(kruskal.weight == 3 && prim.weight == 3, "MST weights must match");
    }
    private static void testScc() {
        Graph graph = new Graph(5, true);
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 0, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 3, 1);
        graph.addEdge(3, 2, 1);
        Kosaraju.Result result = Kosaraju.run(graph);
        check(result.components.size() == 3, "Wrong number of SCCs");
        boolean[] seen = new boolean[5];
        for (List<Integer> component : result.components) {
            for (int vertex : component) {
                check(!seen[vertex], "Vertex appears in two SCCs");
                seen[vertex] = true;
            }
        }
        for (boolean included : seen) {
            check(included, "Vertex belongs to no SCC");
        }
        for (List<Integer> component : result.components) {
            if (component.contains(0)) {
                check(component.size() == 2 && component.contains(1), "Wrong SCC of vertex 0");
            }
            if (component.contains(2)) {
                check(component.size() == 2 && component.contains(3), "Wrong SCC of vertex 2");
            }
        }
    }
    private static void testDijkstraAndUnreachable() {
        Graph graph = new Graph(4, true);
        graph.addEdge(0, 1, 10);
        graph.addEdge(0, 2, 2);
        graph.addEdge(2, 1, 3);
        Dijkstra.Result result = Dijkstra.run(graph, 0);
        check(result.distance[0] == 0, "Wrong source distance");
        check(result.distance[1] == 5, "Wrong shortest distance");
        check(result.pathTo(1).equals(List.of(0, 2, 1)), "Wrong shortest path");
        check(result.distance[3] == Dijkstra.INF, "Unreachable vertex not detected");
        check(result.pathTo(3).isEmpty(), "Unreachable path should be empty");
    }
    private static void testSingleVertex() {
        Graph undirected = new Graph(1, false);
        MST.Result kruskal = MST.kruskal(undirected);
        MST.Result prim = MST.prim(undirected);
        check(kruskal.connected && prim.connected, "Single vertex MST failed");
        check(kruskal.weight == 0 && prim.weight == 0, "Wrong single vertex MST weight");
        Graph directed = new Graph(1, true);
        check(TopologicalSort.run(directed).order.equals(List.of(0)), "Single vertex order failed");
        check(Kosaraju.run(directed).components.size() == 1, "Single vertex SCC failed");
        check(Dijkstra.run(directed, 0).distance[0] == 0, "Single vertex path failed");
    }
    public static void run() {
        testSmallGraphAndDsu();
        testDisconnectedGraph();
        testDagAndCycle();
        testMultipleMstsAndDuplicateWeights();
        testScc();
        testDijkstraAndUnreachable();
        testSingleVertex();
        System.out.println("Tests: 7/7 passed");
    }
}