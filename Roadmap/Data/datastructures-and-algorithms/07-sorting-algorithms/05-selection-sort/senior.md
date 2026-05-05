# Selection Sort — Senior Level

## Table of Contents
1. [Introduction](#introduction)
2. [Write-Expensive Storage Systems](#write-expensive-storage)
3. [Flash/SSD Wear Leveling](#flash-wear-leveling)
4. [Concurrency](#concurrency)
5. [Code Examples](#code-examples)
6. [Failure Modes](#failure-modes)
7. [Summary](#summary)

---

## Introduction

> Focus: "When does Selection Sort's few-writes property matter at scale?"

At senior level, Selection Sort is rarely the right choice for in-memory sorting (Insertion or Quick wins). But its **few-writes property** maps to several production scenarios:

1. **Flash/SSD wear leveling** — minimize writes to extend device life.
2. **Append-only log compaction** — minimize rewrites.
3. **Distributed databases** — minimize replicated writes.
4. **EEPROM in embedded systems** — limited write cycles.

The senior question: when is "fewest writes" worth O(n²) time?

---

## Write-Expensive Storage

### Cost Comparison

| Storage | Write Cost (relative) |
|---------|----------------------|
| RAM (DRAM) | 1× |
| L1 cache | 0.01× |
| SSD (NAND flash) | 100-1000× (vs read) |
| EEPROM | 1000-10000× (vs read) |
| Magnetic disk | 50-100× (random write) |
| Distributed DB write | 10⁶× (network round trip) |

For SSD/EEPROM/distributed: write cost dominates time complexity. n-1 swaps × (slow write) may beat n²/4 swaps × (slow write) → Selection Sort wins despite O(n²) compares.

---

## Flash Wear Leveling

NAND flash cells have limited write cycles (10⁵ - 10⁶). Wear leveling distributes writes evenly across cells. Sort algorithms that write less directly extend device life.

For an SSD-based key-value store:
- **Insertion Sort** with O(n²) writes: cells wear out faster.
- **Selection Sort** with O(n) writes: cells last longer.

In Linux's `flash_eraseall` and similar tools, the sorting of erase-block tables is sometimes done with Selection Sort precisely for this reason.

---

## Concurrency

Selection Sort modifies in-place. Concurrent reads see partial state. Standard mitigations:
- Snapshot before sort.
- Atomic publish via lock-free pointer swap.
- Per-thread sorted views.

Selection Sort is naturally **not parallelizable** — each pass depends on the previous. Heap Sort and Merge Sort are better for parallel sorts.

---

## Code Examples

### Write-Counting Sort Wrapper

Track writes to confirm Selection Sort's advantage on your storage layer:

```python
class WriteCountingArray:
    def __init__(self, data):
        self._data = list(data)
        self.writes = 0
    def __getitem__(self, i): return self._data[i]
    def __setitem__(self, i, v):
        self._data[i] = v
        self.writes += 1
    def __len__(self): return len(self._data)

import random
data = [random.randint(0, 1000) for _ in range(1000)]
wca = WriteCountingArray(data)
selection_sort(wca)
print(f"Selection writes: {wca.writes}")  # ~999

wca2 = WriteCountingArray(data)
insertion_sort(wca2)
print(f"Insertion writes: {wca2.writes}")  # ~250,000
```

### Selection Sort on EEPROM-backed Array

```python
class EEPROMArray:
    def __init__(self, eeprom_path, size):
        self.path = eeprom_path
        self.size = size
        # ... load from path
    def __getitem__(self, i):
        # read from EEPROM (fast)
        return self._read_byte(i)
    def __setitem__(self, i, v):
        # write to EEPROM (slow! count writes)
        self._write_byte(i, v)

# Selection Sort here makes sense — minimizes writes to EEPROM
```

---

## Failure Modes

| Mode | Symptom | Mitigation |
|------|---------|------------|
| Cells wear out faster than expected | Increased bit errors | Use sort algorithm with fewer writes; use SSD with TRIM |
| Concurrent modification | Sort produces wrong result | Snapshot pattern |
| O(n²) blow-up | Slow sort | Use Selection ONLY when writes >> compares cost |

---

## Summary

Selection Sort's few-writes property is its production niche: **flash memory wear leveling**, **EEPROM with limited cycles**, **distributed write minimization**. For these, n-1 writes vs. O(n²) writes is a 250×+ improvement that justifies the O(n²) compute cost. For all other cases, prefer Insertion Sort, Merge Sort, or Quick Sort.

Generalize to **Heap Sort** when you need O(n log n) — it preserves the "few writes" intuition (heap operations write less than Quick Sort's swaps) while gaining the speed of O(n log n).
