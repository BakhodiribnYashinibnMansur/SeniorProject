/**
 * 0004. Median of Two Sorted Arrays
 * https://leetcode.com/problems/median-of-two-sorted-arrays/
 * Difficulty: Hard
 * Tags: Array, Binary Search, Divide and Conquer
 */
class Solution {

    /**
     * Optimal Solution (Binary Search on Smaller Array)
     * Approach: Partition both arrays so the left half contains (m+n+1)/2 elements.
     *           Binary search on the smaller array until:
     *           maxLeft1 <= minRight2 AND maxLeft2 <= minRight1.
     * Time:  O(log(min(m,n))) — binary search only on the shorter array
     * Space: O(1)             — constant extra space
     */
    public double findMedianSortedArrays(int[] nums1, int[] nums2) {
        // Always binary-search the smaller array
        if (nums1.length > nums2.length) {
            int[] tmp = nums1; nums1 = nums2; nums2 = tmp;
        }

        int m = nums1.length, n = nums2.length;
        // half = number of elements in the combined left partition
        int half = (m + n + 1) / 2;

        int lo = 0, hi = m;

        while (lo <= hi) {
            // i = elements taken from nums1 for the left partition
            int i = (lo + hi) / 2;
            // j = elements taken from nums2 for the left partition
            int j = half - i;

            // Boundary values (use extreme sentinels for out-of-bounds)
            int maxLeft1  = (i == 0) ? Integer.MIN_VALUE : nums1[i - 1];
            int minRight1 = (i == m) ? Integer.MAX_VALUE : nums1[i];
            int maxLeft2  = (j == 0) ? Integer.MIN_VALUE : nums2[j - 1];
            int minRight2 = (j == n) ? Integer.MAX_VALUE : nums2[j];

            if (maxLeft1 <= minRight2 && maxLeft2 <= minRight1) {
                // Perfect partition found
                if ((m + n) % 2 == 1) {
                    // Odd total: median is the max of the left half
                    return Math.max(maxLeft1, maxLeft2);
                }
                // Even total: average of max-left and min-right
                return (Math.max(maxLeft1, maxLeft2) + Math.min(minRight1, minRight2)) / 2.0;
            } else if (maxLeft1 > minRight2) {
                // Too many elements from nums1 — shift partition left
                hi = i - 1;
            } else {
                // Too few elements from nums1 — shift partition right
                lo = i + 1;
            }
        }

        return 0.0;
    }

    // ============================================================
    // Test Cases
    // ============================================================

    static int passed = 0, failed = 0;

    static void test(String name, double got, double expected) {
        if (Math.abs(got - expected) < 1e-5) {
            System.out.printf("✅ PASS: %s%n", name);
            passed++;
        } else {
            System.out.printf("❌ FAIL: %s%n  Got:      %.5f%n  Expected: %.5f%n",
                name, got, expected);
            failed++;
        }
    }

    public static void main(String[] args) {
        Solution sol = new Solution();

        // Test 1: LeetCode Example 1 — odd total length
        test("Example 1 (odd)", sol.findMedianSortedArrays(new int[]{1, 3}, new int[]{2}), 2.00000);

        // Test 2: LeetCode Example 2 — even total length
        test("Example 2 (even)", sol.findMedianSortedArrays(new int[]{1, 2}, new int[]{3, 4}), 2.50000);

        // Test 3: One empty array
        test("One empty array", sol.findMedianSortedArrays(new int[]{}, new int[]{1}), 1.00000);

        // Test 4: Both single elements
        test("Both single elements", sol.findMedianSortedArrays(new int[]{2}, new int[]{1}), 1.50000);

        // Test 5: nums1 entirely smaller than nums2
        test("nums1 all smaller", sol.findMedianSortedArrays(new int[]{1, 2}, new int[]{3, 4, 5}), 3.00000);

        // Test 6: nums2 entirely smaller than nums1
        test("nums2 all smaller", sol.findMedianSortedArrays(new int[]{3, 4, 5}, new int[]{1, 2}), 3.00000);

        // Test 7: Negative numbers
        test("Negative numbers", sol.findMedianSortedArrays(new int[]{-5, -3, -1}, new int[]{-4, -2}), -3.00000);

        // Test 8: Mixed negative and positive
        test("Mixed neg/pos", sol.findMedianSortedArrays(new int[]{-2, 0}, new int[]{1, 3}), 0.50000);

        // Test 9: Equal arrays
        test("Equal arrays", sol.findMedianSortedArrays(new int[]{1, 3}, new int[]{1, 3}), 2.00000);

        // Test 10: Single element arrays, even total
        test("Single elements even", sol.findMedianSortedArrays(new int[]{3}, new int[]{4}), 3.50000);

        System.out.printf("%n📊 Results: %d passed, %d failed, %d total%n",
            passed, failed, passed + failed);
    }
}
