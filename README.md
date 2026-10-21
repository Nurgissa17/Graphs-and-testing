# Assignment 3 — Smart City Infrastructure and Route Planning

## Overview

This Java program treats city districts as vertices and roads as weighted edges.
It uses undirected graphs for road construction and directed graphs for one-way roads and dependencies.
Graphs are stored as adjacency lists. Vertices are numbered from `0` to `V - 1`.

## Implementation

- **DSU:** `find` uses path compression; `union` joins the smaller group to the larger one.
- **Kruskal:** sorts roads by cost and uses DSU to avoid cycles.
- **Prim:** uses a priority queue to select the cheapest road connecting a new district.
- **Kosaraju:** uses DFS, reverses the edges, then uses DFS again to find SCCs, as in Lecture 7.
- **Topological sorting (Kahn):** takes vertices with no incoming edges first. If vertices remain, the graph has a cycle.
- **Dijkstra:** uses a priority queue to find distances and paths from vertex `0`. Unreachable vertices have distance `INF` and an empty path.

Both MST algorithms return the chosen roads, total cost and whether all districts are connected.
Dijkstra needs non-negative weights: a negative edge could make an already chosen shortest distance smaller later.

**Run:** Open the project in IntelliJ, mark `src` as **Sources Root**, and run `Main.java`.
It runs the tests, prints examples and runs the experiments. Only standard Java is used.

## Complexity Analysis

`V` = vertices, `E` = edges, `α(V)` = a very slowly growing function.
Graphs use adjacency lists; priority queues use binary heaps.

| Algorithm | Time | Extra space |
| --- | --- | --- |
| DSU `find` / `union` | `O(α(V))` each on average over many calls | `O(V)` |
| Kruskal | `O(E log E + E α(V))` | `O(V + E)` |
| Prim | `O(E log V)` | `O(V + E)` |
| Kosaraju (SCC) | `O(V + E)` | `O(V + E)` |
| Kahn (topological order) | `O(V + E)` | `O(V)` |
| Dijkstra | `O((V + E) log V)` | `O(V + E)` |

Kruskal sorts edges. Prim and Dijkstra use priority queues.
Kosaraju and Kahn examine vertices and edges. DSU path compression and union by size keep its trees shallow.
More vertices need more storage; more edges mean more work.

## Experimental Setup

| Size | Vertices | Edges |
| --- | ---: | ---: |
| Small | 10 | 30 |
| Medium | 100 | 500 |
| Large | 1000 | 5000 |

Graphs use fixed random seeds so they can be generated again.
Undirected graphs start with a spanning tree, so they are connected.
DAGs use edges from lower to higher vertex numbers, so they have no cycles.
Cyclic directed graphs also include `0 → 1 → 2 → 0`.
Extra edges are random and unique; weights range from 1 to 20.
Seeds are `42 + V` (undirected), `142 + V` (DAG), and `242 + V` (cyclic).

Each algorithm runs **5 times** per graph size. `System.nanoTime()` measures only the algorithm, not graph creation.
The program counts edge checks and relevant queue, DSU or sorting operations.

## Results

Values are **average milliseconds / average operations** over 5 runs.
The same numbers are saved in `results/tables/benchmark.csv` and shown in the plots.

| Algorithm | 10 vertices, 30 edges | 100 vertices, 500 edges | 1000 vertices, 5000 edges |
| --- | ---: | ---: | ---: |
| Kruskal | 0.048 / 185 | 1.001 / 4858 | 3.211 / 58023 |
| Prim | 0.119 / 105 | 0.934 / 1713 | 4.752 / 17833 |
| Kosaraju | 0.049 / 90 | 0.192 / 1500 | 1.883 / 15000 |
| Topological (DAG) | 0.025 / 60 | 0.438 / 1000 | 0.855 / 10000 |
| Topological (cycle) | 0.006 / 30 | 0.025 / 500 | 0.269 / 5000 |
| Dijkstra | 0.043 / 54 | 0.232 / 740 | 1.618 / 7412 |

**Execution time vs. vertices:**

![Time versus vertices](results/plots/time_vs_vertices.png)

**Execution time vs. edges:**

![Time versus edges](results/plots/time_vs_edges.png)

## Comparison and Discussion

- **Kruskal vs. Prim:** both give the same MST weight. Kruskal sorts edges; Prim uses a priority queue. Their operation counts measure different work, so they are not directly comparable.
- **DSU:** path compression and union by size make repeated `find` and `union` operations fast.
- **SCC:** Kosaraju examines edges in two DFS passes and while reversing them; the work grows with graph size.
- **Topological ordering:** Kahn processes all DAG vertices but stops early on these cyclic graphs because none has zero incoming edges.
- **Dijkstra:** larger graphs need more edge checks and queue operations.

The results generally agree with the complexity analysis. Exact times may vary between runs.
Since `V` and `E` grow together, the plots do not measure their effects separately.

## Testing

Seven tests cover small, disconnected and single-vertex graphs; cycles; equal-weight edges and multiple MSTs; SCCs; and unreachable vertices.
They check that both MST weights match, DAG orders are valid, cycles are detected, SCCs include each vertex once, and Dijkstra returns correct paths and distances.

**Result: 7/7 passed.** Experiments also compare MST weights and check cycle detection.

## Conclusion

Use Kruskal or Prim to connect districts at minimum cost, Kosaraju to find mutually reachable districts, Kahn to order tasks without cycles, and Dijkstra to find shortest routes with non-negative weights.
