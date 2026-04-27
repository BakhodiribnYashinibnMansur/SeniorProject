import java.util.*;

/**
 * 0060. Permutation Sequence
 * https://leetcode.com/problems/permutation-sequence/
 * Difficulty: Hard
 * Tags: Math, Recursion
 */
class Solution {

    /**
     * Optimal Solution (Factorial Number System).
     * Time:  O(n^2)
     * Space: O(n)
     */
    public String getPermutation(int n, int k) {
        int[] fact = new int[n + 1];
        fact[0] = 1;
        for (int i = 1; i <= n; i++) fact[i] = fact[i - 1] * i;
        List<Integer> digits = new ArrayList<>();
        for (int i = 1; i <= n; i++) digits.add(i);
        k--;
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) {
            int m = n - i;
            int q = k / fact[m - 1];
            sb.append(digits.remove(q));
            k %= fact[m - 1];
        }
        return sb.toString();
    }

    /**
     * Brute force via repeated next-permutation.
     * Time:  O(k * n)
     * Space: O(n)
     */
    public String getPermutationBrute(int n, int k) {
        char[] arr = new char[n];
        for (int i = 0; i < n; i++) arr[i] = (char) ('0' + i + 1);
        for (int step = 1; step < k; step++) nextPermInPlace(arr);
        return new String(arr);
    }

    private void nextPermInPlace(char[] a) {
        int n = a.length;
        int i = n - 2;
        while (i >= 0 && a[i] >= a[i + 1]) i--;
        if (i >= 0) {
            int j = n - 1;
            while (a[j] <= a[i]) j--;
            char t = a[i]; a[i] = a[j]; a[j] = t;
        }
        int l = i + 1, r = n - 1;
        while (l < r) {
            char t = a[l]; a[l] = a[r]; a[r] = t;
            l++; r--;
        }
    }

    // ============================================================
    // Test Cases
    // ============================================================

    static int passed = 0, failed = 0;
    static void test(String name, String got, String expected) {
        if (got.equals(expected)) {
            System.out.println("PASS: " + name);
            passed++;
        } else {
            System.out.println("FAIL: " + name);
            System.out.println("  Got:      " + got);
            System.out.println("  Expected: " + expected);
            failed++;
        }
    }

    public static void main(String[] args) {
        Solution sol = new Solution();
        Object[][] cases = {
            {"Example 1", 3, 3, "213"},
            {"Example 2", 4, 9, "2314"},
            {"Example 3", 3, 1, "123"},
            {"n=1", 1, 1, "1"},
            {"n=3 last", 3, 6, "321"},
            {"n=4 first", 4, 1, "1234"},
            {"n=4 last", 4, 24, "4321"},
            {"n=4 boundary", 4, 7, "2134"},
            {"n=9 first", 9, 1, "123456789"},
            {"n=9 last", 9, 362880, "987654321"},
            {"n=9 middle", 9, 200000, "596742183"},
        };

        System.out.println("=== Factorial Number System (Optimal) ===");
        for (Object[] c : cases) {
            test((String) c[0], sol.getPermutation((int) c[1], (int) c[2]), (String) c[3]);
        }

        System.out.println("\n=== Brute Force (small only) ===");
        for (Object[] c : cases) {
            if ((int) c[2] > 1000) continue;
            test("Brute " + c[0], sol.getPermutationBrute((int) c[1], (int) c[2]), (String) c[3]);
        }

        System.out.printf("%nResults: %d passed, %d failed, %d total%n",
            passed, failed, passed + failed);
    }
}
