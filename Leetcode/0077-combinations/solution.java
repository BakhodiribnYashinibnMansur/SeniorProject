import java.util.*;

/**
 * 0077. Combinations
 * https://leetcode.com/problems/combinations/
 * Difficulty: Medium
 * Tags: Backtracking
 */
class Solution {

    /**
     * Optimal Solution (Backtracking with Pruning).
     * Time:  O(C(n, k) * k)
     * Space: O(k)
     */
    public List<List<Integer>> combine(int n, int k) {
        List<List<Integer>> result = new ArrayList<>();
        List<Integer> cur = new ArrayList<>();
        bt(1, n, k, cur, result);
        return result;
    }

    private void bt(int start, int n, int k, List<Integer> cur, List<List<Integer>> result) {
        if (cur.size() == k) {
            result.add(new ArrayList<>(cur));
            return;
        }
        int need = k - cur.size();
        for (int v = start; v <= n - need + 1; v++) {
            cur.add(v);
            bt(v + 1, n, k, cur, result);
            cur.remove(cur.size() - 1);
        }
    }

    static int passed = 0, failed = 0;

    static List<List<Integer>> canon(List<List<Integer>> a) {
        List<List<Integer>> out = new ArrayList<>();
        for (List<Integer> x : a) out.add(new ArrayList<>(x));
        out.sort((x, y) -> {
            for (int i = 0; i < Math.min(x.size(), y.size()); i++) {
                int c = Integer.compare(x.get(i), y.get(i));
                if (c != 0) return c;
            }
            return Integer.compare(x.size(), y.size());
        });
        return out;
    }

    static void test(String name, List<List<Integer>> got, List<List<Integer>> expected) {
        if (canon(got).equals(canon(expected))) { System.out.println("PASS: " + name); passed++; }
        else { System.out.println("FAIL: " + name + " got=" + got); failed++; }
    }

    public static void main(String[] args) {
        Solution sol = new Solution();
        Object[][] cases = {
            {"Example 1", 4, 2, listOfLists(1,2, 1,3, 1,4, 2,3, 2,4, 3,4)},
            {"Example 2", 1, 1, listOfLists(1)},
            {"k = n", 3, 3, listOfLists(1,2,3)},
            {"k = 1", 3, 1, listOfLists(1, 2, 3)},
            {"n=4 k=3", 4, 3, listOfLists(1,2,3, 1,2,4, 1,3,4, 2,3,4)},
        };
        // Note: listOfLists for cases is custom — easier to just hand-build
        Object[][] manualCases = {
            {"Example 1", 4, 2, Arrays.asList(
                Arrays.asList(1,2), Arrays.asList(1,3), Arrays.asList(1,4),
                Arrays.asList(2,3), Arrays.asList(2,4), Arrays.asList(3,4))},
            {"Example 2", 1, 1, Arrays.asList(Arrays.asList(1))},
            {"k = n", 3, 3, Arrays.asList(Arrays.asList(1,2,3))},
            {"k = 1", 3, 1, Arrays.asList(Arrays.asList(1), Arrays.asList(2), Arrays.asList(3))},
            {"n=4 k=3", 4, 3, Arrays.asList(
                Arrays.asList(1,2,3), Arrays.asList(1,2,4),
                Arrays.asList(1,3,4), Arrays.asList(2,3,4))},
        };
        for (Object[] c : manualCases)
            test((String) c[0], sol.combine((int) c[1], (int) c[2]),
                 (List<List<Integer>>) c[3]);

        // Count check for larger inputs
        int[][] sizes = {{5, 3}, {10, 5}, {12, 6}};
        for (int[] p : sizes) {
            int got = sol.combine(p[0], p[1]).size();
            int want = expCount(p[0], p[1]);
            if (got == want) { System.out.println("PASS: count C(" + p[0] + "," + p[1] + ") = " + got); passed++; }
            else { System.out.println("FAIL: count " + got + " vs " + want); failed++; }
        }

        System.out.printf("%nResults: %d passed, %d failed, %d total%n", passed, failed, passed + failed);
    }

    static List<List<Integer>> listOfLists(int... vals) { return null; } // unused

    static int expCount(int n, int k) {
        if (k > n - k) k = n - k;
        long v = 1;
        for (int i = 0; i < k; i++) v = v * (n - i) / (i + 1);
        return (int) v;
    }
}
