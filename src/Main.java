import graph.Graph;
import connectivity.Kosaraju;
import connectivity.TopologicalSort;
public class Main {
    public static void main(String[] args) throws Exception {
        Tests.run();
        Graph roads = new Graph(3, false);
        roads.addEdge(0, 1, 4);
        roads.addEdge(0, 2, 2);
        roads.addEdge(1, 2, 1);
        MST.Result kruskal = MST.kruskal(roads);
        MST.Result prim = MST.prim(roads);
        System.out.println("Kruskal MST: weight=" + kruskal.weight + ", connected=" + kruskal.connected);
        System.out.println("Prim MST: weight=" + prim.weight + ", connected=" + prim.connected);
        for (Graph.Edge edge : kruskal.selectedEdges) {
            System.out.println("MST road: " + edge.from + " - " + edge.to + " (cost " + edge.weight + ")");
        }
        Dijkstra.Result routes = Dijkstra.run(roads, 0);
        for (int target = 0; target < roads.vertexCount(); target++) {
            System.out.println("Route 0 -> " + target + ": distance=" +
                    routes.distance[target] + ", path=" + routes.pathTo(target));
        }
        Graph services = new Graph(4, true);
        services.addEdge(0, 1, 1);
        services.addEdge(1, 2, 1);
        services.addEdge(2, 0, 1);
        services.addEdge(2, 3, 1);
        System.out.println("SCCs: " + Kosaraju.run(services).components);
        System.out.println("Topological order possible: " + TopologicalSort.run(services).possible);
        Graph dag = new Graph(3, true);
        dag.addEdge(0, 1, 1);
        dag.addEdge(1, 2, 1);
        System.out.println("Topological order for DAG: " + TopologicalSort.run(dag).order);
        Experiment.run();
    }
}