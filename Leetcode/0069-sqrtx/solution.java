/**
 * 0069. Sqrt(x)
 * https://leetcode.com/problems/sqrtx/
 * Difficulty: Easy
 * Tags: Math, Binary Search
 */
class Solution {

    /**
     * Optimal Solution (Binary Search).
     * Time:  O(log x)
     * Space: O(1)
     */
    public int mySqrt(int x) {
        if (x < 2) return x;
        int lo = 1, hi = x / 2, ans = 0;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            if ((long) mid * mid <= x) {
                ans = mid;
                lo = mid + 1;
            } else {
                hi = mid - 1;
            }
        }
        return ans;
    }

    /**
     * Newton's Method.
     * Time:  O(log log x)
     * Space: O(1)
     */
    public int mySqrtNewton(int x) {
        if (x < 2) return x;
        long r = x;
        while (r * r > x) {
            r = (r + x / r) / 2;
        }
        return (int) r;
    }

    /**
     * Linear search (slow).
     */
    public int mySqrtLinear(int x) {
        if (x < 2) return x;
        long r = 1;
        while ((r + 1) * (r + 1) <= x) r++;
        return (int) r;
    }

    public int mySqrtBits(int x) {
        if (x < 2) return x;
        long result = 0;
        long bit = 1L << 16;
        while (bit > 0) {
            long cand = result | bit;
            if (cand * cand <= x) result = cand;
            bit >>= 1;
        }
        return (int) result;
    }

    static int passed = 0, failed = 0;
    static void test(String name, int got, int expected) {
        if (got == expected) { System.out.println("PASS: " + name); passed++; }
        else { System.out.println("FAIL: " + name + " got=" + got); failed++; }
    }

    public static void main(String[] args) {
        Solution sol = new Solution();
        int[][] cases = {
            {0, 0}, {1, 1}, {2, 1}, {3, 1}, {4, 2}, {8, 2},
            {15, 3}, {16, 4}, {17, 4}, {25, 5}, {26, 5},
            {99, 9}, {100, 10}, {101, 10},
            {2147395599, 46339}, {2147483647, 46340}
        };
        System.out.println("=== Binary Search ===");
        for (int[] c : cases) test("x=" + c[0], sol.mySqrt(c[0]), c[1]);
        System.out.println("\n=== Newton's Method ===");
        for (int[] c : cases) test("Newton x=" + c[0], sol.mySqrtNewton(c[0]), c[1]);
        System.out.println("\n=== Bit Manipulation ===");
        for (int[] c : cases) test("Bits x=" + c[0], sol.mySqrtBits(c[0]), c[1]);
        System.out.println("\n=== Linear (small only) ===");
        for (int[] c : cases) {
            if (c[0] > 10000) continue;
            test("Linear x=" + c[0], sol.mySqrtLinear(c[0]), c[1]);
        }
        System.out.printf("%nResults: %d passed, %d failed, %d total%n", passed, failed, passed + failed);
    }
}
