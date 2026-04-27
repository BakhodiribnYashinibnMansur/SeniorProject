import java.util.*;

/**
 * 0056. Merge Intervals
 * https://leetcode.com/problems/merge-intervals/
 * Difficulty: Medium
 * Tags: Array, Sorting
 */
class Solution {

    /**
     * Optimal Solution (Sort + Sweep).
     * Time:  O(n log n)
     * Space: O(n)
     */
    public int[][] merge(int[][] intervals) {
        if (intervals.length == 0) return new int[0][];
        int[][] copy = new int[intervals.length][2];
        for (int i = 0; i < intervals.length; i++) {
            copy[i][0] = intervals[i][0];
            copy[i][1] = intervals[i][1];
        }
        Arrays.sort(copy, (a, b) -> Integer.compare(a[0], b[0]));
        List<int[]> result = new ArrayList<>();
        for (int[] iv : copy) {
            if (result.isEmpty() || result.get(result.size() - 1)[1] < iv[0]) {
                result.add(new int[]{iv[0], iv[1]});
            } else {
                int[] last = result.get(result.size() - 1);
                last[1] = Math.max(last[1], iv[1]);
            }
        }
        return result.toArray(new int[0][]);
    }

    /**
     * Sweep Line.
     * Time:  O(n log n)
     * Space: O(n)
     */
    public int[][] mergeSweep(int[][] intervals) {
        int n = intervals.length;
        int[][] events = new int[2 * n][3];
        for (int i = 0; i < n; i++) {
            events[2 * i]     = new int[]{intervals[i][0], 0, +1};
            events[2 * i + 1] = new int[]{intervals[i][1], 1, -1};
        }
        Arrays.sort(events, (a, b) ->
            a[0] != b[0] ? Integer.compare(a[0], b[0]) : Integer.compare(a[1], b[1]));
        List<int[]> result = new ArrayList<>();
        int cur = 0, start = 0;
        for (int[] e : events) {
            if (cur == 0 && e[2] == +1) start = e[0];
            cur += e[2];
            if (cur == 0) result.add(new int[]{start, e[0]});
        }
        return result.toArray(new int[0][]);
    }

    /**
     * Pairwise merge fixed-point.
     * Time:  O(n^3)
     * Space: O(n)
     */
    public int[][] mergeBrute(int[][] intervals) {
        List<int[]> items = new ArrayList<>();
        for (int[] iv : intervals) items.add(new int[]{iv[0], iv[1]});
        boolean changed = true;
        while (changed) {
            changed = false;
            for (int i = 0; i < items.size(); i++) {
                for (int j = i + 1; j < items.size(); ) {
                    int a = items.get(i)[0], b = items.get(i)[1];
                    int c = items.get(j)[0], d = items.get(j)[1];
                    if (a <= d && c <= b) {
                        items.get(i)[0] = Math.min(a, c);
                        items.get(i)[1] = Math.max(b, d);
                        items.remove(j);
                        changed = true;
                    } else j++;
                }
            }
        }
        items.sort((x, y) -> Integer.compare(x[0], y[0]));
        return items.toArray(new int[0][]);
    }

    // ============================================================
    // Test Cases
    // ============================================================

    static int passed = 0, failed = 0;
    static void test(String name, int[][] got, int[][] expected) {
        if (Arrays.deepEquals(got, expected)) {
            System.out.println("PASS: " + name);
            passed++;
        } else {
            System.out.println("FAIL: " + name);
            System.out.println("  Got:      " + Arrays.deepToString(got));
            System.out.println("  Expected: " + Arrays.deepToString(expected));
            failed++;
        }
    }

    public static void main(String[] args) {
        Solution sol = new Solution();
        Object[][] cases = {
            {"Example 1", new int[][]{{1, 3}, {2, 6}, {8, 10}, {15, 18}},
                new int[][]{{1, 6}, {8, 10}, {15, 18}}},
            {"Touching endpoints", new int[][]{{1, 4}, {4, 5}}, new int[][]{{1, 5}}},
            {"Single interval", new int[][]{{1, 4}}, new int[][]{{1, 4}}},
            {"Disjoint", new int[][]{{1, 2}, {3, 4}}, new int[][]{{1, 2}, {3, 4}}},
            {"Contained", new int[][]{{1, 10}, {2, 3}}, new int[][]{{1, 10}}},
            {"Identical", new int[][]{{1, 1}, {1, 1}}, new int[][]{{1, 1}}},
            {"Reverse sorted", new int[][]{{4, 5}, {1, 3}}, new int[][]{{1, 3}, {4, 5}}},
            {"Two chains", new int[][]{{1, 3}, {2, 4}, {6, 8}, {7, 9}},
                new int[][]{{1, 4}, {6, 9}}},
            {"Zero-length disjoint", new int[][]{{1, 1}, {2, 2}},
                new int[][]{{1, 1}, {2, 2}}},
            {"Large containment", new int[][]{{1, 100}, {2, 3}, {4, 50}},
                new int[][]{{1, 100}}},
            {"Mixed order", new int[][]{{2, 6}, {1, 3}, {15, 18}, {8, 10}},
                new int[][]{{1, 6}, {8, 10}, {15, 18}}},
        };

        System.out.println("=== Sort + Merge ===");
        for (Object[] c : cases) {
            // Defensive copy because merge sorts in place inside
            int[][] in = deepCopy((int[][]) c[1]);
            test((String) c[0], sol.merge(in), (int[][]) c[2]);
        }

        System.out.println("\n=== Sweep Line ===");
        for (Object[] c : cases) {
            int[][] in = deepCopy((int[][]) c[1]);
            test("Sweep " + c[0], sol.mergeSweep(in), (int[][]) c[2]);
        }

        System.out.println("\n=== Brute Force ===");
        for (Object[] c : cases) {
            int[][] in = deepCopy((int[][]) c[1]);
            test("Brute " + c[0], sol.mergeBrute(in), (int[][]) c[2]);
        }

        System.out.printf("%nResults: %d passed, %d failed, %d total%n",
            passed, failed, passed + failed);
    }

    static int[][] deepCopy(int[][] a) {
        int[][] b = new int[a.length][];
        for (int i = 0; i < a.length; i++) b[i] = a[i].clone();
        return b;
    }
}
