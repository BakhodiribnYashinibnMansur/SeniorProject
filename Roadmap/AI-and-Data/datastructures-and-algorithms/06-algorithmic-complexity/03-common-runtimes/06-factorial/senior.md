# Factorial Time O(n!) -- Senior Level

## Table of Contents

1. [Introduction](#introduction)
2. [TSP in Logistics and Routing](#tsp-in-logistics-and-routing)
3. [Scheduling Problems in Production](#scheduling-problems-in-production)
4. [Combinatorial Optimization in Manufacturing](#combinatorial-optimization-in-manufacturing)
5. [Approximation Algorithms](#approximation-algorithms)
6. [Heuristic Alternatives](#heuristic-alternatives)
7. [Metaheuristic Approaches](#metaheuristic-approaches)
8. [System Design Considerations](#system-design-considerations)
9. [Case Studies](#case-studies)
10. [Key Takeaways](#key-takeaways)

---

## Introduction

At the senior level, we shift focus from understanding O(n!) algorithms to deploying
practical solutions for real-world problems that have O(n!) brute-force solutions. In
production systems, we never run brute-force factorial algorithms on real-sized inputs.
Instead, we use approximation algorithms, heuristics, and metaheuristics that trade
optimality guarantees for feasibility.

This document covers how factorial-time problems appear in industry and the engineering
strategies used to handle them.

---

## TSP in Logistics and Routing

### The Real-World Problem

Every delivery company faces a variant of TSP daily:

- **Amazon/UPS/FedEx**: Route hundreds of delivery drivers, each visiting 50-200 stops.
- **Uber/Lyft**: Optimize ride-sharing pickups and drop-offs.
- **Food delivery**: DoorDash, UberEats routing between restaurants and customers.

Brute-force TSP on 200 stops would require 200! ~ 10^375 operations. This is beyond any
conceivable computing capability. Even with the Held-Karp DP algorithm at O(2^n * n^2),
200 stops would require 2^200 * 200^2 ~ 10^64 operations -- still impossible.

### Vehicle Routing Problem (VRP)

The real logistics problem is actually harder than TSP. The **Vehicle Routing Problem**
involves:

- Multiple vehicles with capacity constraints.
- Time windows (customer must be served between 9am-11am).
- Different vehicle types and costs.
- Real-time traffic conditions.
- Driver break requirements (legally mandated in many countries).

VRP is at least as hard as TSP, meaning its brute-force complexity is also factorial or
worse.

### Industry Solutions

Modern logistics companies use a layered approach:

```
Layer 1: Clustering
    - Divide the service area into geographic clusters.
    - Assign vehicles to clusters.
    - Reduces each sub-problem to manageable size.

Layer 2: Route Construction Heuristics
    - Nearest neighbor, savings algorithm, or insertion heuristics.
    - Generate initial feasible routes quickly (milliseconds).

Layer 3: Route Improvement
    - Apply local search (2-opt, 3-opt, or-opt).
    - Run for a time budget (seconds to minutes).

Layer 4: Metaheuristics
    - Simulated annealing, genetic algorithms, or ant colony optimization.
    - Run longer for higher-quality solutions.

Layer 5: Real-Time Adjustment
    - Dynamic re-routing as conditions change.
    - Must respond in seconds.
```

### Go: Nearest Neighbor TSP Heuristic

```go
package main

import (
    "fmt"
    "math"
)

func nearestNeighborTSP(dist [][]float64) (float64, []int) {
    n := len(dist)
    visited := make([]bool, n)
    route := make([]int, 0, n)
    totalDist := 0.0

    current := 0
    visited[current] = true
    route = append(route, current)

    for len(route) < n {
        bestNext := -1
        bestDist := math.Inf(1)
        for j := 0; j < n; j++ {
            if !visited[j] && dist[current][j] < bestDist {
                bestDist = dist[current][j]
                bestNext = j
            }
        }
        visited[bestNext] = true
        route = append(route, bestNext)
        totalDist += bestDist
        current = bestNext
    }
    totalDist += dist[current][0]
    return totalDist, route
}

func main() {
    dist := [][]float64{
        {0, 29, 20, 21, 16, 31, 100, 12, 4, 31},
        {29, 0, 15, 29, 28, 40, 72, 21, 29, 41},
        {20, 15, 0, 15, 14, 25, 81, 9, 23, 27},
        {21, 29, 15, 0, 4, 12, 92, 12, 25, 13},
        {16, 28, 14, 4, 0, 16, 94, 9, 20, 16},
        {31, 40, 25, 12, 16, 0, 95, 24, 36, 3},
        {100, 72, 81, 92, 94, 95, 0, 90, 101, 99},
        {12, 21, 9, 12, 9, 24, 90, 0, 15, 25},
        {4, 29, 23, 25, 20, 36, 101, 15, 0, 35},
        {31, 41, 27, 13, 16, 3, 99, 25, 35, 0},
    }
    cost, route := nearestNeighborTSP(dist)
    fmt.Printf("Nearest neighbor cost: %.1f\n", cost)
    fmt.Println("Route:", route)
}
```

### Java: Nearest Neighbor TSP Heuristic

```java
import java.util.ArrayList;
import java.util.List;

public class NearestNeighborTSP {

    public static double solve(double[][] dist, List<Integer> route) {
        int n = dist.length;
        boolean[] visited = new boolean[n];
        double totalDist = 0;

        int current = 0;
        visited[current] = true;
        route.add(current);

        while (route.size() < n) {
            int bestNext = -1;
            double bestDist = Double.MAX_VALUE;
            for (int j = 0; j < n; j++) {
                if (!visited[j] && dist[current][j] < bestDist) {
                    bestDist = dist[current][j];
                    bestNext = j;
                }
            }
            visited[bestNext] = true;
            route.add(bestNext);
            totalDist += bestDist;
            current = bestNext;
        }
        totalDist += dist[current][0];
        return totalDist;
    }

    public static void main(String[] args) {
        double[][] dist = {
            {0, 29, 20, 21, 16},
            {29, 0, 15, 29, 28},
            {20, 15, 0, 15, 14},
            {21, 29, 15, 0, 4},
            {16, 28, 14, 4, 0}
        };
        List<Integer> route = new ArrayList<>();
        double cost = solve(dist, route);
        System.out.printf("Cost: %.1f%n", cost);
        System.out.println("Route: " + route);
    }
}
```

### Python: Nearest Neighbor TSP Heuristic

```python
import math


def nearest_neighbor_tsp(dist):
    n = len(dist)
    visited = [False] * n
    route = [0]
    visited[0] = True
    total = 0.0

    for _ in range(n - 1):
        current = route[-1]
        best_next = min(
            (j for j in range(n) if not visited[j]),
            key=lambda j: dist[current][j]
        )
        total += dist[current][best_next]
        visited[best_next] = True
        route.append(best_next)

    total += dist[route[-1]][0]
    return total, route
```

**Time complexity**: O(n^2) -- polynomial, vastly better than O(n!).
**Quality**: typically within 20-25% of optimal for random instances.

---

## Scheduling Problems in Production

### Job Shop Scheduling

In manufacturing, **job shop scheduling** assigns n jobs to m machines, where each job
has a specific sequence of operations. The goal is to minimize makespan (total time).

- With n jobs and m machines, the number of possible schedules can reach (n!)^m.
- For 10 jobs on 3 machines: (10!)^3 ~ 2.3 x 10^19

### Flow Shop Scheduling

In a flow shop, all jobs pass through machines in the same order. The number of
possible schedules is n! (the order of jobs). For 15 jobs: 15! ~ 1.3 x 10^12.

### NEH Heuristic for Flow Shop

The **Nawaz-Enscore-Ham (NEH)** heuristic is one of the best-known heuristics for flow
shop scheduling:

```python
def neh_heuristic(processing_times):
    """
    NEH heuristic for flow shop scheduling.
    processing_times[i][j] = time for job i on machine j.
    Returns a permutation of jobs minimizing makespan.
    """
    n = len(processing_times)
    m = len(processing_times[0])

    # Step 1: Sort jobs by total processing time (descending)
    total_times = [(sum(processing_times[i]), i) for i in range(n)]
    total_times.sort(reverse=True)
    sorted_jobs = [job for _, job in total_times]

    # Step 2: Build schedule by inserting each job at best position
    schedule = [sorted_jobs[0]]
    for k in range(1, n):
        job = sorted_jobs[k]
        best_makespan = float("inf")
        best_pos = 0
        for pos in range(len(schedule) + 1):
            candidate = schedule[:pos] + [job] + schedule[pos:]
            ms = compute_makespan(candidate, processing_times, m)
            if ms < best_makespan:
                best_makespan = ms
                best_pos = pos
        schedule = schedule[:best_pos] + [job] + schedule[best_pos:]

    return schedule, compute_makespan(schedule, processing_times, m)


def compute_makespan(schedule, pt, m):
    n = len(schedule)
    completion = [[0] * m for _ in range(n)]
    for i in range(n):
        job = schedule[i]
        for j in range(m):
            prev_job = completion[i - 1][j] if i > 0 else 0
            prev_machine = completion[i][j - 1] if j > 0 else 0
            completion[i][j] = max(prev_job, prev_machine) + pt[job][j]
    return completion[n - 1][m - 1]
```

**Time complexity**: O(n^2 * m) -- polynomial vs O(n!) brute force.

---

## Combinatorial Optimization in Manufacturing

### Assembly Line Balancing

Assigning n tasks to workstations on an assembly line to minimize the number of
stations while respecting precedence constraints and cycle time limits.

Brute-force would try all n! orderings, but precedence constraints dramatically reduce
the feasible set. Practical approaches:

1. **Ranked positional weight heuristic**: assign tasks greedily by priority.
2. **COMSOAL**: computer method of sequencing operations for assembly lines.
3. **Branch and bound**: with effective pruning, can solve instances up to ~50 tasks.

### Cutting Stock Problem

Given a set of orders for pieces of different sizes and a fixed stock material width,
minimize waste when cutting. The number of cutting patterns grows factorially.

**Column generation** (an LP-based approach) solves this efficiently by only considering
promising patterns, avoiding the factorial enumeration entirely.

---

## Approximation Algorithms

Unlike heuristics (which offer no guarantees), **approximation algorithms** provide
provable bounds on solution quality.

### Christofides' Algorithm for Metric TSP

For TSP instances satisfying the triangle inequality:

1. Find a minimum spanning tree (MST).
2. Find a minimum-weight perfect matching on odd-degree vertices.
3. Combine MST and matching to form an Eulerian graph.
4. Find an Euler tour and shortcut repeated vertices.

**Guarantee**: solution cost <= 1.5 * optimal cost.
**Time complexity**: O(n^3) -- polynomial.

### 2-Approximation for Metric TSP

An even simpler approach:

1. Find MST.
2. Perform DFS, listing vertices in order of first visit.

**Guarantee**: solution cost <= 2 * optimal cost.
**Time complexity**: O(n^2).

```python
def mst_tsp_2approx(dist):
    """2-approximation for metric TSP using MST."""
    n = len(dist)
    # Prim's MST
    in_mst = [False] * n
    key = [float("inf")] * n
    parent = [-1] * n
    key[0] = 0
    adj = [[] for _ in range(n)]

    for _ in range(n):
        u = min((i for i in range(n) if not in_mst[i]), key=lambda i: key[i])
        in_mst[u] = True
        for v in range(n):
            if not in_mst[v] and dist[u][v] < key[v]:
                key[v] = dist[u][v]
                parent[v] = u

    # Build adjacency list for MST
    for v in range(1, n):
        adj[parent[v]].append(v)
        adj[v].append(parent[v])

    # DFS to get tour
    visited = [False] * n
    tour = []

    def dfs(u):
        visited[u] = True
        tour.append(u)
        for v in adj[u]:
            if not visited[v]:
                dfs(v)

    dfs(0)

    total = sum(dist[tour[i]][tour[i + 1]] for i in range(n - 1))
    total += dist[tour[-1]][tour[0]]
    return total, tour
```

---

## Heuristic Alternatives

### 2-opt Local Search

The **2-opt** heuristic improves a tour by repeatedly removing two edges and
reconnecting the tour in a different way. Each improvement iteration scans O(n^2) pairs.

```go
package main

import "fmt"

func twoOpt(dist [][]float64, route []int) (float64, []int) {
    n := len(route)
    improved := true
    for improved {
        improved = false
        for i := 1; i < n-1; i++ {
            for j := i + 1; j < n; j++ {
                oldDist := dist[route[i-1]][route[i]] + dist[route[j]][route[(j+1)%n]]
                newDist := dist[route[i-1]][route[j]] + dist[route[i]][route[(j+1)%n]]
                if newDist < oldDist {
                    // Reverse segment from i to j
                    for l, r := i, j; l < r; l, r = l+1, r-1 {
                        route[l], route[r] = route[r], route[l]
                    }
                    improved = true
                }
            }
        }
    }
    total := 0.0
    for i := 0; i < n; i++ {
        total += dist[route[i]][route[(i+1)%n]]
    }
    return total, route
}

func main() {
    dist := [][]float64{
        {0, 10, 15, 20},
        {10, 0, 35, 25},
        {15, 35, 0, 30},
        {20, 25, 30, 0},
    }
    route := []int{0, 1, 2, 3}
    cost, improved := twoOpt(dist, route)
    fmt.Printf("2-opt cost: %.1f, route: %v\n", cost, improved)
}
```

### Java: 2-opt

```java
public class TwoOpt {
    public static double improve(double[][] dist, int[] route) {
        int n = route.length;
        boolean improved = true;
        while (improved) {
            improved = false;
            for (int i = 1; i < n - 1; i++) {
                for (int j = i + 1; j < n; j++) {
                    double oldD = dist[route[i-1]][route[i]] +
                                  dist[route[j]][route[(j+1) % n]];
                    double newD = dist[route[i-1]][route[j]] +
                                  dist[route[i]][route[(j+1) % n]];
                    if (newD < oldD) {
                        for (int l = i, r = j; l < r; l++, r--) {
                            int tmp = route[l];
                            route[l] = route[r];
                            route[r] = tmp;
                        }
                        improved = true;
                    }
                }
            }
        }
        double total = 0;
        for (int i = 0; i < n; i++) total += dist[route[i]][route[(i+1) % n]];
        return total;
    }
}
```

### Python: 2-opt

```python
def two_opt(dist, route):
    n = len(route)
    improved = True
    while improved:
        improved = False
        for i in range(1, n - 1):
            for j in range(i + 1, n):
                old = dist[route[i-1]][route[i]] + dist[route[j]][route[(j+1) % n]]
                new = dist[route[i-1]][route[j]] + dist[route[i]][route[(j+1) % n]]
                if new < old:
                    route[i:j+1] = reversed(route[i:j+1])
                    improved = True
    total = sum(dist[route[i]][route[(i+1) % n]] for i in range(n))
    return total, route
```

---

## Metaheuristic Approaches

### Simulated Annealing for TSP

```python
import random
import math


def simulated_annealing_tsp(dist, initial_route, temp=10000, cooling=0.9995,
                             min_temp=1e-8, max_iter=1000000):
    n = len(initial_route)
    current = initial_route[:]
    current_cost = tour_cost(dist, current)
    best = current[:]
    best_cost = current_cost

    t = temp
    for iteration in range(max_iter):
        if t < min_temp:
            break
        # Random 2-opt swap
        i, j = sorted(random.sample(range(n), 2))
        new_route = current[:i] + current[i:j+1][::-1] + current[j+1:]
        new_cost = tour_cost(dist, new_route)
        delta = new_cost - current_cost
        if delta < 0 or random.random() < math.exp(-delta / t):
            current = new_route
            current_cost = new_cost
            if current_cost < best_cost:
                best = current[:]
                best_cost = current_cost
        t *= cooling

    return best_cost, best


def tour_cost(dist, route):
    return sum(dist[route[i]][route[(i+1) % len(route)]] for i in range(len(route)))
```

### Genetic Algorithm for Scheduling

```python
import random


def genetic_scheduling(tasks, pop_size=100, generations=500, mutation_rate=0.1):
    n = len(tasks)

    def fitness(schedule):
        time = 0
        cost = 0
        for idx in schedule:
            time += tasks[idx][0]
            cost += time * tasks[idx][1]
        return -cost  # negative because we minimize

    # Initialize population
    population = [random.sample(range(n), n) for _ in range(pop_size)]

    for gen in range(generations):
        population.sort(key=fitness, reverse=True)
        survivors = population[:pop_size // 2]

        children = []
        while len(children) < pop_size - len(survivors):
            p1, p2 = random.sample(survivors, 2)
            # Order crossover
            start, end = sorted(random.sample(range(n), 2))
            child = [-1] * n
            child[start:end] = p1[start:end]
            fill = [g for g in p2 if g not in child]
            j = 0
            for i in range(n):
                if child[i] == -1:
                    child[i] = fill[j]
                    j += 1
            # Mutation: swap two random positions
            if random.random() < mutation_rate:
                a, b = random.sample(range(n), 2)
                child[a], child[b] = child[b], child[a]
            children.append(child)

        population = survivors + children

    best = max(population, key=fitness)
    return best, -fitness(best)
```

---

## System Design Considerations

### Architecture for Routing Services

```
                    +------------------+
                    |  API Gateway     |
                    +--------+---------+
                             |
                    +--------v---------+
                    | Route Optimizer   |
                    | Service           |
                    +--------+---------+
                             |
              +--------------+--------------+
              |              |              |
     +--------v---+  +------v------+  +----v--------+
     | Clustering  |  | Heuristic   |  | Improvement |
     | Service     |  | Constructor |  | Service     |
     +------------+  +-------------+  +-------------+
```

### Key Design Decisions

1. **Time budgets**: Allocate computation time based on business value. A 1% improvement
   on a million-dollar routing problem is worth $10,000.

2. **Anytime algorithms**: Use algorithms that produce a valid (suboptimal) solution
   quickly and improve it if more time is available.

3. **Warm starting**: Cache previous solutions and use them as starting points when
   the problem changes slightly (e.g., one new delivery added).

4. **Parallelization**: Run multiple heuristics or random restarts in parallel and take
   the best result.

---

## Case Studies

### Case Study 1: Last-Mile Delivery Optimization

**Problem**: 500 delivery drivers, each with 100-150 stops, recalculated every morning.

**Approach**:
- Cluster stops into driver territories using k-means on geographic coordinates.
- Apply nearest-neighbor heuristic for initial route per driver.
- Improve each route with 2-opt for 2 seconds per route.
- Total computation: ~20 minutes on a 32-core server.

**Result**: 12% reduction in total distance vs previous manual planning.

### Case Study 2: Semiconductor Fab Scheduling

**Problem**: Schedule 200 wafer lots across 50 machines with complex precedence
constraints and setup times.

**Approach**:
- Decompose into stages using rolling horizon.
- Apply dispatching rules (shortest processing time, earliest due date).
- Fine-tune with simulated annealing for each stage.

**Result**: 15% improvement in throughput vs rule-based scheduling.

---

## Key Takeaways

1. **Never run O(n!) in production** on real-sized inputs. Always use heuristics or
   approximation algorithms.

2. **Layered approach**: Start with a fast heuristic, then improve iteratively.

3. **Approximation algorithms** provide provable quality bounds (e.g., Christofides
   gives 1.5x optimal for metric TSP).

4. **2-opt and simulated annealing** are versatile improvement strategies applicable to
   many combinatorial problems.

5. **System design** should support anytime computation, parallelism, and warm starting
   for routing and scheduling services.

6. **The gap between brute force and heuristics** is enormous: O(n!) vs O(n^2) or
   O(n^3), with solution quality typically within 5-20% of optimal.

7. **Domain-specific structure** (geographic clustering, precedence constraints) can be
   exploited to dramatically reduce problem size before applying general techniques.
