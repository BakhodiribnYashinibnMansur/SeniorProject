import java.util.*;

/**
 * 0078. Subsets
 * https://leetcode.com/problems/subsets/
 * Difficulty: Medium
 * Tags: Array, Backtracking, Bit Manipulation
 */
class Solution {

    /**
     * Optimal Solution (Backtracking).
     * Time:  O(n * 2^n)
     * Space: O(n)
     */
    public List<List<Integer>> subsets(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        List<Integer> cur = new ArrayList<>();
        bt(0, nums, cur, result);
        return result;
    }
    private void bt(int start, int[] nums, List<Integer> cur, List<List<Integer>> result) {
        result.add(new ArrayList<>(cur));
        for (int i = start; i < nums.length; i++) {
            cur.add(nums[i]);
            bt(i + 1, nums, cur, result);
            cur.remove(cur.size() - 1);
        }
    }

    public List<List<Integer>> subsetsCascade(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        result.add(new ArrayList<>());
        for (int x : nums) {
            int size = result.size();
            for (int i = 0; i < size; i++) {
                List<Integer> cp = new ArrayList<>(result.get(i));
                cp.add(x);
                result.add(cp);
            }
        }
        return result;
    }

    public List<List<Integer>> subsetsBits(int[] nums) {
        int n = nums.length;
        List<List<Integer>> result = new ArrayList<>();
        for (int mask = 0; mask < (1 << n); mask++) {
            List<Integer> sub = new ArrayList<>();
            for (int i = 0; i < n; i++) if (((mask >> i) & 1) == 1) sub.add(nums[i]);
            result.add(sub);
        }
        return result;
    }

    static int passed = 0, failed = 0;

    static List<List<Integer>> canon(List<List<Integer>> out) {
        List<List<Integer>> cp = new ArrayList<>();
        for (List<Integer> x : out) {
            List<Integer> c = new ArrayList<>(x);
            Collections.sort(c);
            cp.add(c);
        }
        cp.sort((a, b) -> {
            for (int i = 0; i < Math.min(a.size(), b.size()); i++) {
                int cmp = Integer.compare(a.get(i), b.get(i));
                if (cmp != 0) return cmp;
            }
            return Integer.compare(a.size(), b.size());
        });
        return cp;
    }

    static void test(String name, List<List<Integer>> got, List<List<Integer>> expected) {
        if (canon(got).equals(canon(expected))) { System.out.println("PASS: " + name); passed++; }
        else { System.out.println("FAIL: " + name); failed++; }
    }

    public static void main(String[] args) {
        Solution sol = new Solution();
        Object[][] cases = {
            {"Example 1", new int[]{1, 2, 3}, Arrays.asList(
                Arrays.asList(), Arrays.asList(1), Arrays.asList(2), Arrays.asList(3),
                Arrays.asList(1, 2), Arrays.asList(1, 3), Arrays.asList(2, 3), Arrays.asList(1, 2, 3))},
            {"Example 2", new int[]{0}, Arrays.asList(Arrays.asList(), Arrays.asList(0))},
            {"Two", new int[]{4, 5}, Arrays.asList(
                Arrays.asList(), Arrays.asList(4), Arrays.asList(5), Arrays.asList(4, 5))},
            {"Negatives", new int[]{-1, 2}, Arrays.asList(
                Arrays.asList(), Arrays.asList(-1), Arrays.asList(2), Arrays.asList(-1, 2))}
        };
        System.out.println("=== Backtracking ===");
        for (Object[] c : cases) test((String) c[0], sol.subsets((int[]) c[1]), (List<List<Integer>>) c[2]);
        System.out.println("\n=== Cascade ===");
        for (Object[] c : cases) test("Casc " + c[0], sol.subsetsCascade((int[]) c[1]), (List<List<Integer>>) c[2]);
        System.out.println("\n=== Bits ===");
        for (Object[] c : cases) test("Bits " + c[0], sol.subsetsBits((int[]) c[1]), (List<List<Integer>>) c[2]);

        for (int sz : new int[]{5, 8, 10}) {
            int[] nums = new int[sz];
            for (int i = 0; i < sz; i++) nums[i] = i;
            int got = sol.subsets(nums).size();
            int want = 1 << sz;
            if (got == want) { System.out.println("PASS: count n=" + sz + " → " + got); passed++; }
            else { System.out.println("FAIL: count " + got + " vs " + want); failed++; }
        }

        System.out.printf("%nResults: %d passed, %d failed, %d total%n", passed, failed, passed + failed);
    }
}
