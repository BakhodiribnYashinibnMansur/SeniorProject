/**
 * 0038. Count and Say
 * https://leetcode.com/problems/count-and-say/
 * Difficulty: Medium
 * Tags: String
 */
class Solution {

    /**
     * Optimal Solution (Iterative)
     * Approach: Build each term by performing RLE on the previous term
     * Time:  O(n * L) — n iterations, each processing string of length L
     * Space: O(L) — store the current and next strings
     */
    public String countAndSay(int n) {
        String result = "1";

        for (int step = 2; step <= n; step++) {
            StringBuilder next = new StringBuilder();
            int i = 0;

            while (i < result.length()) {
                char digit = result.charAt(i);
                int count = 1;

                // Count consecutive identical digits
                while (i + count < result.length() && result.charAt(i + count) == digit) {
                    count++;
                }

                // Append count and digit
                next.append(count);
                next.append(digit);
                i += count;
            }

            result = next.toString();
        }

        return result;
    }

    /**
     * Recursive approach
     * Approach: Base case n=1 returns "1", otherwise RLE of countAndSay(n-1)
     * Time:  O(n * L) — n recursive calls
     * Space: O(n * L) — recursion stack + strings
     */
    public String countAndSayRecursive(int n) {
        // Base case
        if (n == 1) {
            return "1";
        }

        // Recursively get the previous term
        String prev = countAndSayRecursive(n - 1);

        // Perform RLE on the previous term
        StringBuilder result = new StringBuilder();
        int i = 0;

        while (i < prev.length()) {
            char digit = prev.charAt(i);
            int count = 1;

            // Count consecutive identical digits
            while (i + count < prev.length() && prev.charAt(i + count) == digit) {
                count++;
            }

            // Append count and digit
            result.append(count);
            result.append(digit);
            i += count;
        }

        return result.toString();
    }

    // ============================================================
    // Test Cases
    // ============================================================

    static int passed = 0, failed = 0;

    static void test(String name, String got, String expected) {
        if (got.equals(expected)) {
            System.out.printf("\u2705 PASS: %s%n", name);
            passed++;
        } else {
            System.out.printf("\u274c FAIL: %s%n  Got:      %s%n  Expected: %s%n",
                name, got, expected);
            failed++;
        }
    }

    public static void main(String[] args) {
        Solution sol = new Solution();

        System.out.println("=== Iterative (Optimal) ===");

        // Test 1: Base case
        test("n=1", sol.countAndSay(1), "1");

        // Test 2: One 1
        test("n=2", sol.countAndSay(2), "11");

        // Test 3: Two 1s
        test("n=3", sol.countAndSay(3), "21");

        // Test 4: LeetCode Example
        test("n=4", sol.countAndSay(4), "1211");

        // Test 5: Multiple runs
        test("n=5", sol.countAndSay(5), "111221");

        // Test 6: Longer sequence
        test("n=6", sol.countAndSay(6), "312211");

        // Test 7: Even longer
        test("n=7", sol.countAndSay(7), "13112221");

        // Test 8: n=8
        test("n=8", sol.countAndSay(8), "1113213211");

        // Test 9: n=10
        test("n=10", sol.countAndSay(10), "13211311123113112211");

        System.out.println("\n=== Recursive ===");

        // Test 10: Recursive — Base case
        test("Recursive n=1", sol.countAndSayRecursive(1), "1");

        // Test 11: Recursive — n=4
        test("Recursive n=4", sol.countAndSayRecursive(4), "1211");

        // Test 12: Recursive — n=6
        test("Recursive n=6", sol.countAndSayRecursive(6), "312211");

        // Results
        System.out.printf("%n\uD83D\uDCCA Results: %d passed, %d failed, %d total%n",
            passed, failed, passed + failed);
    }
}
