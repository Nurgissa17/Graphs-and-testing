public class DSU {
    private final int[] parent;
    private final int[] size;
    public long findCalls;
    public long unionCalls;
    public DSU(int vertices) {
        parent = new int[vertices];
        size = new int[vertices];
        for (int i = 0; i < vertices; i++) {
            parent[i] = i;
            size[i] = 1;
        }
    }
    public int find(int vertex) {
        findCalls++;
        if (parent[vertex] != vertex) {
            parent[vertex] = find(parent[vertex]);
        }
        return parent[vertex];
    }
    public boolean union(int first, int second) {
        unionCalls++;
        int rootA = find(first);
        int rootB = find(second);
        if (rootA == rootB) {
            return false;
        }
        if (size[rootA] < size[rootB]) {
            int temp = rootA;
            rootA = rootB;
            rootB = temp;
        }
        parent[rootB] = rootA;
        size[rootA] += size[rootB];
        return true;
    }
}