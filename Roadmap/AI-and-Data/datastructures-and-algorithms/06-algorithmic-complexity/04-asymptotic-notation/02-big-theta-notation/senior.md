# Big-Theta Notation -- Senior Level

## Table of Contents

1. [Theta in Production Systems](#theta-in-production-systems)
2. [Performance Guarantees and SLAs](#performance-guarantees-and-slas)
3. [Capacity Planning with Tight Bounds](#capacity-planning-with-tight-bounds)
4. [When Tight Bounds Matter](#when-tight-bounds-matter)
5. [Theta-Aware System Design](#theta-aware-system-design)
6. [Cost Modeling with Exact Growth](#cost-modeling-with-exact-growth)
7. [Code Examples: Production Patterns](#code-examples)
8. [Case Studies](#case-studies)
9. [Decision Framework](#decision-framework)
10. [Summary](#summary)

---

## Theta in Production Systems

In production, knowing that an algorithm is O(n^2) tells you the worst it can
get. But knowing it is Theta(n^2) tells you something much more powerful: **it
will always take that long.** This distinction matters for:

- **Resource allocation**: You cannot optimize away from Theta -- that IS the
  cost. If your core algorithm is Theta(n^2), no amount of tuning changes that.
- **Capacity planning**: Theta gives you exact growth predictions. When data
  doubles, Theta(n^2) means 4x the resources, guaranteed.
- **SLA commitments**: If the algorithm is Theta(n log n), you know both the
  floor and ceiling of latency at scale.

### The Difference in Practice

| Scenario                     | What O(n^2) tells you        | What Theta(n^2) tells you      |
|------------------------------|------------------------------|--------------------------------|
| Data doubles                 | At most 4x slower            | Exactly about 4x slower        |
| Provisioning servers         | Need up to 4x capacity       | Need exactly ~4x capacity      |
| SLA for p99 latency          | Worst case bound             | Tight prediction               |
| Budget for compute costs     | Upper bound on cost           | Accurate cost projection       |
| Deciding to rewrite          | Maybe worth it, maybe not    | Definitely this expensive      |

---

## Performance Guarantees and SLAs

### Writing SLAs with Theta Knowledge

When you know an operation is Theta(n), you can write precise SLAs:

```
SLA Definition:
  Operation: User search across catalog
  Algorithm: Full-text index lookup -- Theta(log n) per query
  Current catalog size: 1M items
  
  Latency guarantee:
    At 1M items: 2ms p99
    At 2M items: 2.2ms p99  (log doubles slowly)
    At 10M items: 2.7ms p99
    
  Scaling model:
    latency(n) = 0.1ms * log2(n)
    This is a TIGHT bound -- latency will not be significantly
    better OR worse than this formula.
```

Compare with an O(n) bound:
```
  If we only knew O(log n):
    At 2M items: at most 2.2ms p99 (could be much less)
    At 10M items: at most 2.7ms p99 (could be much less)
    -- We cannot commit to tighter guarantees
    -- We over-provision "just in case"
```

### SLA Contracts: Theta vs O

**With O(n^2) knowledge:**
- "Response time will not exceed 500ms for inputs up to 10,000 items"
- Over-provisioned for safety margin
- Cannot promise lower bound (might be faster, wasting pre-allocated resources)

**With Theta(n^2) knowledge:**
- "Response time will be between 200ms and 500ms for inputs of 10,000 items"
- Right-sized provisioning
- Predictable cost model

---

## Capacity Planning with Tight Bounds

### Growth Prediction Model

When an algorithm is Theta(g(n)), the resource usage scales exactly with g(n):

```
resources(n_new) = resources(n_current) * g(n_new) / g(n_current)
```

**Go:**

```go
package main

import (
    "fmt"
    "math"
)

type ComplexityClass struct {
    Name    string
    GrowthFn func(float64) float64
}

func predictResources(current float64, nCurrent, nNew float64, growth func(float64) float64) float64 {
    return current * growth(nNew) / growth(nCurrent)
}

func main() {
    classes := []ComplexityClass{
        {"Theta(n)", func(n float64) float64 { return n }},
        {"Theta(n log n)", func(n float64) float64 { return n * math.Log2(n) }},
        {"Theta(n^2)", func(n float64) float64 { return n * n }},
    }

    currentN := 100000.0
    currentCost := 100.0 // dollars per month
    futureN := 1000000.0  // 10x data growth

    fmt.Printf("Current: n=%.0f, cost=$%.0f/month\n\n", currentN, currentCost)
    fmt.Printf("Projected cost at n=%.0f:\n", futureN)
    fmt.Printf("%-20s %15s %15s\n", "Complexity", "Monthly Cost", "Growth Factor")
    fmt.Println("------------------------------------------------------")

    for _, cls := range classes {
        projected := predictResources(currentCost, currentN, futureN, cls.GrowthFn)
        factor := projected / currentCost
        fmt.Printf("%-20s $%14.2f %14.1fx\n", cls.Name, projected, factor)
    }
}
```

**Java:**

```java
import java.util.function.Function;

public class CapacityPlanner {

    static double predictResources(double current, double nCurrent,
                                    double nNew, Function<Double, Double> growth) {
        return current * growth.apply(nNew) / growth.apply(nCurrent);
    }

    public static void main(String[] args) {
        String[] names = {"Theta(n)", "Theta(n log n)", "Theta(n^2)"};
        @SuppressWarnings("unchecked")
        Function<Double, Double>[] growthFns = new Function[]{
            n -> n,
            n -> (double) n * Math.log((double) n) / Math.log(2),
            n -> (double) n * (double) n
        };

        double currentN = 100_000;
        double currentCost = 100.0;
        double futureN = 1_000_000;

        System.out.printf("Current: n=%.0f, cost=$%.0f/month%n%n", currentN, currentCost);
        System.out.printf("Projected cost at n=%.0f:%n", futureN);
        System.out.printf("%-20s %15s %15s%n", "Complexity", "Monthly Cost", "Growth Factor");
        System.out.println("------------------------------------------------------");

        for (int i = 0; i < names.length; i++) {
            double projected = predictResources(currentCost, currentN, futureN, growthFns[i]);
            double factor = projected / currentCost;
            System.out.printf("%-20s $%14.2f %14.1fx%n", names[i], projected, factor);
        }
    }
}
```

**Python:**

```python
import math


def predict_resources(current: float, n_current: float, n_new: float,
                      growth_fn) -> float:
    return current * growth_fn(n_new) / growth_fn(n_current)


if __name__ == "__main__":
    classes = [
        ("Theta(n)",       lambda n: n),
        ("Theta(n log n)", lambda n: n * math.log2(n)),
        ("Theta(n^2)",     lambda n: n * n),
    ]

    current_n = 100_000
    current_cost = 100.0  # dollars per month
    future_n = 1_000_000   # 10x data growth

    print(f"Current: n={current_n}, cost=${current_cost:.0f}/month\n")
    print(f"Projected cost at n={future_n}:")
    print(f"{'Complexity':<20} {'Monthly Cost':>15} {'Growth Factor':>15}")
    print("-" * 54)

    for name, growth_fn in classes:
        projected = predict_resources(current_cost, current_n, future_n, growth_fn)
        factor = projected / current_cost
        print(f"{name:<20} ${projected:>14.2f} {factor:>14.1f}x")
```

Expected output pattern:

```
Complexity           Monthly Cost   Growth Factor
------------------------------------------------------
Theta(n)             $      1000.00           10.0x
Theta(n log n)       $      1195.00           12.0x
Theta(n^2)           $    100000.00          100.0x  <-- danger!
```

---

## When Tight Bounds Matter

### Scenario 1: Database Query Optimizer

The query optimizer chooses between algorithms. If algorithm A is Theta(n log n)
and algorithm B is O(n log n) (but might be Theta(n) in some cases):

- For **worst-case guarantees**: both offer O(n log n)
- For **resource prediction**: A is predictable, B is not
- For **auto-scaling triggers**: A allows precise thresholds

### Scenario 2: Billing and Metering

Cloud services bill based on compute usage. If your pipeline is Theta(n):
- Customer uses 2x data = 2x cost. Predictable bills.
- You can offer tiered pricing based on data volume.
- No surprises for customers or for your margin calculations.

If your pipeline were only O(n), actual costs might be much less, and you would
either overcharge (upper bound pricing) or lose money (actual cost varies).

### Scenario 3: Real-Time Systems

In real-time systems (robotics, trading, embedded), you need BOTH:
- Upper bound: to not miss deadlines
- Lower bound: to not waste idle CPU cycles

Theta gives you both. If a sensor processing loop is Theta(n):
- You know it takes at least c1*n time (plan the schedule)
- You know it takes at most c2*n time (won't miss deadline)
- CPU utilization is predictable

---

## Theta-Aware System Design

### Architecture Decision Records

When documenting architecture decisions, include Theta analysis:

```
ADR-042: Choose merge sort over quicksort for payment processing

Context:
  Payment transactions must be sorted by timestamp before batch processing.
  We process 500K-2M transactions per batch.

Decision:
  Use merge sort (Theta(n log n) all cases) instead of quicksort
  (O(n^2) worst case, Theta(n log n) average).

Rationale:
  - Batch processing SLA: 30 seconds
  - Merge sort: Theta(n log n) guarantees predictable timing
  - At 2M records: ~42M operations (predictable)
  - Quicksort worst case at 2M: 4 trillion operations (SLA violation)
  - The extra space Theta(n) for merge sort is acceptable given
    our 16GB worker memory and ~100MB transaction data

Consequences:
  - Theta(n) additional memory usage
  - Consistently predictable batch times
  - Simpler capacity planning
```

### Load Balancer Configuration

```
Algorithm: Theta(n) per request where n = payload items

Measured:
  c1 = 0.8 microsecond per item (lower bound constant)
  c2 = 1.2 microseconds per item (upper bound constant)

Configuration:
  timeout = c2 * max_items * safety_factor
         = 1.2us * 10000 * 1.5
         = 18ms per request

  health_check_interval = expected_latency * 3
                        = (c1 + c2) / 2 * avg_items * 3
                        = 1.0us * 5000 * 3
                        = 15ms
```

---

## Cost Modeling with Exact Growth

### Cloud Compute Cost Projection

**Go:**

```go
package main

import (
    "fmt"
    "math"
)

type ServiceTier struct {
    Name         string
    Complexity   string
    GrowthFunc   func(float64) float64
    BaseCostPerOp float64
}

func projectCost(tier ServiceTier, currentUsers, targetUsers float64) (float64, float64) {
    currentOps := tier.GrowthFunc(currentUsers)
    targetOps := tier.GrowthFunc(targetUsers)
    currentCost := currentOps * tier.BaseCostPerOp
    targetCost := targetOps * tier.BaseCostPerOp
    return currentCost, targetCost
}

func main() {
    tiers := []ServiceTier{
        {
            Name:         "Search (inverted index)",
            Complexity:   "Theta(log n)",
            GrowthFunc:   func(n float64) float64 { return math.Log2(n) },
            BaseCostPerOp: 0.001,
        },
        {
            Name:         "Feed generation",
            Complexity:   "Theta(n log n)",
            GrowthFunc:   func(n float64) float64 { return n * math.Log2(n) },
            BaseCostPerOp: 0.00001,
        },
        {
            Name:         "Recommendation (pairwise)",
            Complexity:   "Theta(n^2)",
            GrowthFunc:   func(n float64) float64 { return n * n },
            BaseCostPerOp: 0.0000001,
        },
    }

    current := 100000.0
    targets := []float64{200000, 500000, 1000000}

    for _, tier := range tiers {
        fmt.Printf("\n%s [%s]\n", tier.Name, tier.Complexity)
        currentCost, _ := projectCost(tier, current, current)
        fmt.Printf("  Current (%.0f users): $%.2f/day\n", current, currentCost)

        for _, target := range targets {
            _, targetCost := projectCost(tier, current, target)
            ratio := targetCost / currentCost
            fmt.Printf("  At %.0f users: $%.2f/day (%.1fx)\n",
                target, targetCost, ratio)
        }
    }
}
```

**Java:**

```java
import java.util.function.Function;

public class CostProjection {

    public static void main(String[] args) {
        String[] names = {
            "Search (inverted index)",
            "Feed generation",
            "Recommendation (pairwise)"
        };
        String[] complexities = {"Theta(log n)", "Theta(n log n)", "Theta(n^2)"};
        @SuppressWarnings("unchecked")
        Function<Double, Double>[] growthFns = new Function[]{
            n -> Math.log(n) / Math.log(2),
            n -> n * Math.log(n) / Math.log(2),
            n -> n * n
        };
        double[] baseCosts = {0.001, 0.00001, 0.0000001};

        double current = 100_000;
        double[] targets = {200_000, 500_000, 1_000_000};

        for (int i = 0; i < names.length; i++) {
            System.out.printf("%n%s [%s]%n", names[i], complexities[i]);
            double currentCost = growthFns[i].apply(current) * baseCosts[i];
            System.out.printf("  Current (%.0f users): $%.2f/day%n", current, currentCost);

            for (double target : targets) {
                double targetCost = growthFns[i].apply(target) * baseCosts[i];
                double ratio = targetCost / currentCost;
                System.out.printf("  At %.0f users: $%.2f/day (%.1fx)%n",
                    target, targetCost, ratio);
            }
        }
    }
}
```

**Python:**

```python
import math

services = [
    {
        "name": "Search (inverted index)",
        "complexity": "Theta(log n)",
        "growth": lambda n: math.log2(n),
        "base_cost": 0.001,
    },
    {
        "name": "Feed generation",
        "complexity": "Theta(n log n)",
        "growth": lambda n: n * math.log2(n),
        "base_cost": 0.00001,
    },
    {
        "name": "Recommendation (pairwise)",
        "complexity": "Theta(n^2)",
        "growth": lambda n: n * n,
        "base_cost": 0.0000001,
    },
]

current = 100_000
targets = [200_000, 500_000, 1_000_000]

for svc in services:
    print(f"\n{svc['name']} [{svc['complexity']}]")
    current_cost = svc["growth"](current) * svc["base_cost"]
    print(f"  Current ({current} users): ${current_cost:.2f}/day")

    for target in targets:
        target_cost = svc["growth"](target) * svc["base_cost"]
        ratio = target_cost / current_cost
        print(f"  At {target} users: ${target_cost:.2f}/day ({ratio:.1f}x)")
```

---

## Case Studies

### Case Study 1: Payment Processor Migration

**Problem**: A payment processor used bubble sort (Theta(n^2)) for sorting
transactions before batch settlement. At 50K transactions, batch took 15 min.
Growth projection showed 200K transactions in 6 months.

**Analysis using Theta**:
- Current: Theta(n^2) at n=50K = 2.5 billion operations, 15 min
- Projected: Theta(n^2) at n=200K = 40 billion operations
- Growth factor: (200K/50K)^2 = 16x
- Projected time: 15 min * 16 = 240 min = 4 hours (SLA = 30 min)

**Decision**: Migrate to merge sort Theta(n log n).
- Projected: 200K * log2(200K) = ~3.5M operations
- Estimated time: < 1 second

The tight bound (Theta, not just O) gave confidence that merge sort would
actually perform at this level, not just "at most" this level.

### Case Study 2: Auto-Scaling with Theta

**Problem**: An API service needed auto-scaling rules. The core operation was
Theta(n) where n = request payload size.

**Theta-based scaling rules**:
```
measured_constant = 0.5ms per 1000 items (empirically determined)

scale_up_when:
  avg_latency > 0.6ms * (avg_payload_size / 1000)
  # 20% above Theta lower bound = something is wrong

scale_down_when:
  avg_latency < 0.3ms * (avg_payload_size / 1000)
  # Well below expected Theta = over-provisioned
```

Because the algorithm is Theta(n) (not just O(n)), the scaling rules could be
tight. If it were only O(n), the actual performance might vary widely and the
scaling rules would need much larger margins.

---

## Decision Framework

### When to Invest in Finding Theta

| Situation                          | Find Theta? | Why                                   |
|------------------------------------|-------------|---------------------------------------|
| Core business logic                | Yes         | Accurate cost/SLA predictions         |
| Rarely-called utility function     | No          | O is sufficient                       |
| Real-time / embedded system        | Yes         | Need both upper AND lower bounds      |
| Capacity planning for scale        | Yes         | Exact growth predictions              |
| Code review / interview            | Maybe       | O is usually sufficient               |
| Performance-critical hot path      | Yes         | Precise optimization targets          |
| One-off script                     | No          | Just make it work                     |

### Red Flags: When Theta Analysis Reveals Problems

1. **Theta(n^2) on a growing dataset** -- quadratic growth will eventually break
2. **Different Theta for different cases** on a critical path -- unpredictable
3. **Theta(n) space** in a memory-constrained environment -- constant growth
4. **Theta mismatch between components** -- bottleneck analysis needed

---

## Summary

1. **Theta in production** gives exact resource predictions, not just upper bounds.
2. **SLA contracts** benefit from Theta because you can commit to tight ranges.
3. **Capacity planning** with Theta: resources(n_new) = resources(n_old) * g(n_new)/g(n_old).
4. **Cost modeling** uses Theta to project compute costs as data grows.
5. **Auto-scaling** rules can be tighter when the algorithm has a known Theta.
6. **Architecture decisions** should document Theta when the operation is on a critical path.
7. **Invest in Theta analysis** for core business logic, real-time systems, and capacity planning.

---

*Next: Continue to the [Professional Level](professional.md) for formal proof techniques.*
