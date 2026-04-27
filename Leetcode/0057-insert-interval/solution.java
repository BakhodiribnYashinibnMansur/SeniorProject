import java.util.*;

/**
 * 0057. Insert Interval
 * https://leetcode.com/problems/insert-interval/
 * Difficulty: Medium
 * Tags: Array
 */
class Solution {

    /**
     * Optimal Solution (Three-Phase Linear Scan).
     * Time:  O(n)
     * Space: O(n)
     */
    public int[][] insert(int[][] intervals, int[] newInterval) {
        int n = intervals.length;
        List<int[]> result = new ArrayList<>(n + 1);
        int[] cur = new int[]{newInterval[0], newInterval[1]};
        int i = 0;
        while (i < n && intervals[i][1] < cur[0]) {
            result.add(intervals[i].clone());
            i++;
        }
        while (i < n && intervals[i][0] <= cur[1]) {
            cur[0] = Math.min(cur[0], intervals[i][0]);
            cur[1] = Math.max(cur[1], intervals[i][1]);
            i++;
        }
        result.add(cur);
        while (i < n) {
            result.add(intervals[i].clone());
            i++;
        }
        return result.toArray(new int[0][]);
    }

    /**
     * Binary search boundaries.
     * Time:  O(n) (output copy), O(log n) for boundaries
     * Space: O(n)
     */
    public int[][] insertBinary(int[][] intervals, int[] newInterval) {
        int n = intervals.length;
        if (n == 0) return new int[][]{newInterval.clone()};
        int lo = lowerBoundEnd(intervals, newInterval[0]);
        int hi = upperBoundStart(intervals, newInterval[1]);
        int mergedStart = newInterval[0], mergedEnd = newInterval[1];
        if (lo < hi) {
            mergedStart = Math.min(mergedStart, intervals[lo][0]);
            mergedEnd = Math.max(mergedEnd, intervals[hi - 1][1]);
        }
        List<int[]> result = new ArrayList<>();
        for (int k = 0; k < lo; k++) result.add(intervals[k].clone());
        result.add(new int[]{mergedStart, mergedEnd});
        for (int k = hi; k < n; k++) result.add(intervals[k].clone());
        return result.toArray(new int[0][]);
    }

    private int lowerBoundEnd(int[][] iv, int target) {
        int l = 0, r = iv.length;
        while (l < r) {
            int m = (l + r) >>> 1;
            if (iv[m][1] < target) l = m + 1;
            else r = m;
        }
        return l;
    }

    private int upperBoundStart(int[][] iv, int target) {
        int l = 0, r = iv.length;
        while (l < r) {
            int m = (l + r) >>> 1;
            if (iv[m][0] <= target) l = m + 1;
            else r = m;
        }
        return l;
    }

    /**
     * Append + sort + merge.
     * Time:  O(n log n)
     * Space: O(n)
     */
    public int[][] insertReMerge(int[][] intervals, int[] newInterval) {
        int[][] items = new int[intervals.length + 1][2];
        for (int i = 0; i < intervals.length; i++) items[i] = intervals[i].clone();
        items[intervals.length] = newInterval.clone();
        Arrays.sort(items, (a, b) -> Integer.compare(a[0], b[0]));
        List<int[]> result = new ArrayList<>();
        for (int[] iv : items) {
            if (result.isEmpty() || result.get(result.size() - 1)[1] < iv[0]) {
                result.add(new int[]{iv[0], iv[1]});
            } else {
                int[] last = result.get(result.size() - 1);
                last[1] = Math.max(last[1], iv[1]);
            }
        }
        return result.toArray(new int[0][]);
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
            {"Example 1", new int[][]{{1, 3}, {6, 9}}, new int[]{2, 5}, new int[][]{{1, 5}, {6, 9}}},
            {"Example 2", new int[][]{{1, 2}, {3, 5}, {6, 7}, {8, 10}, {12, 16}}, new int[]{4, 8},
                new int[][]{{1, 2}, {3, 10}, {12, 16}}},
            {"Empty input", new int[][]{}, new int[]{5, 7}, new int[][]{{5, 7}}},
            {"Insert at start no overlap", new int[][]{{3, 5}}, new int[]{1, 2},
                new int[][]{{1, 2}, {3, 5}}},
            {"Insert at end no overlap", new int[][]{{1, 2}}, new int[]{4, 5},
                new int[][]{{1, 2}, {4, 5}}},
            {"Insert in middle no overlap", new int[][]{{1, 2}, {6, 7}}, new int[]{3, 4},
                new int[][]{{1, 2}, {3, 4}, {6, 7}}},
            {"Engulfs everything", new int[][]{{1, 2}, {5, 6}}, new int[]{0, 10},
                new int[][]{{0, 10}}},
            {"Touching merge left", new int[][]{{1, 4}}, new int[]{4, 6}, new int[][]{{1, 6}}},
            {"Touching merge right", new int[][]{{4, 6}}, new int[]{1, 4}, new int[][]{{1, 6}}},
            {"Zero-length new", new int[][]{{1, 5}}, new int[]{3, 3}, new int[][]{{1, 5}}},
            {"Insert before all touching", new int[][]{{4, 6}, {7, 9}}, new int[]{0, 4},
                new int[][]{{0, 6}, {7, 9}}},
            {"Single existing absorbed", new int[][]{{2, 3}}, new int[]{1, 5},
                new int[][]{{1, 5}}},
        };

        System.out.println("=== Three-Phase Linear ===");
        for (Object[] c : cases) {
            test((String) c[0], sol.insert(deepCopy((int[][]) c[1]), ((int[]) c[2]).clone()),
                 (int[][]) c[3]);
        }

        System.out.println("\n=== Binary Search Boundaries ===");
        for (Object[] c : cases) {
            test("Binary " + c[0], sol.insertBinary(deepCopy((int[][]) c[1]), ((int[]) c[2]).clone()),
                 (int[][]) c[3]);
        }

        System.out.println("\n=== Re-Merge ===");
        for (Object[] c : cases) {
            test("ReMerge " + c[0], sol.insertReMerge(deepCopy((int[][]) c[1]), ((int[]) c[2]).clone()),
                 (int[][]) c[3]);
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
