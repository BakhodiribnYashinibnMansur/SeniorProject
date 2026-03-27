# ============================================================
# 0004. Median of Two Sorted Arrays
# https://leetcode.com/problems/median-of-two-sorted-arrays/
# Difficulty: Hard
# Tags: Array, Binary Search, Divide and Conquer
# ============================================================


class Solution:
    def findMedianSortedArrays(self, nums1: list[int], nums2: list[int]) -> float:
        """
        Optimal Solution (Binary Search on Smaller Array)
        Approach: Partition both arrays so the left half has (m+n+1)//2 elements.
                  Binary search on the smaller array to find the partition where
                  maxLeft1 <= minRight2 AND maxLeft2 <= minRight1.
        Time:  O(log(min(m,n))) — binary search only on the shorter array
        Space: O(1)             — constant extra space
        """
        # Always binary-search the smaller array for efficiency
        if len(nums1) > len(nums2):
            nums1, nums2 = nums2, nums1

        m, n = len(nums1), len(nums2)
        # half = total elements in the combined left partition
        half = (m + n + 1) // 2

        lo, hi = 0, m

        while lo <= hi:
            # i = elements taken from nums1 for the left partition
            i = (lo + hi) // 2
            # j = elements taken from nums2 for the left partition
            j = half - i

            # Boundary values (sentinels for out-of-bounds positions)
            max_left1  = nums1[i - 1] if i > 0 else float('-inf')
            min_right1 = nums1[i]     if i < m else float('inf')
            max_left2  = nums2[j - 1] if j > 0 else float('-inf')
            min_right2 = nums2[j]     if j < n else float('inf')

            if max_left1 <= min_right2 and max_left2 <= min_right1:
                # Perfect partition found
                if (m + n) % 2 == 1:
                    # Odd total length: median is max of the left half
                    return float(max(max_left1, max_left2))
                # Even total length: average of max-left and min-right
                return (max(max_left1, max_left2) + min(min_right1, min_right2)) / 2.0

            elif max_left1 > min_right2:
                # Too many elements from nums1 — move partition left
                hi = i - 1
            else:
                # Too few elements from nums1 — move partition right
                lo = i + 1

        return 0.0


# ============================================================
# Test Cases
# ============================================================

if __name__ == "__main__":
    sol = Solution()
    passed = failed = 0

    def test(name: str, got, expected):
        global passed, failed
        if abs(got - expected) < 1e-5:
            print(f"✅ PASS: {name}")
            passed += 1
        else:
            print(f"❌ FAIL: {name}")
            print(f"  Got:      {got:.5f}")
            print(f"  Expected: {expected:.5f}")
            failed += 1

    # Test 1: LeetCode Example 1 — odd total length
    test("Example 1 (odd)", sol.findMedianSortedArrays([1, 3], [2]), 2.00000)

    # Test 2: LeetCode Example 2 — even total length
    test("Example 2 (even)", sol.findMedianSortedArrays([1, 2], [3, 4]), 2.50000)

    # Test 3: One empty array
    test("One empty array", sol.findMedianSortedArrays([], [1]), 1.00000)

    # Test 4: Both single elements
    test("Both single elements", sol.findMedianSortedArrays([2], [1]), 1.50000)

    # Test 5: nums1 entirely smaller than nums2
    test("nums1 all smaller", sol.findMedianSortedArrays([1, 2], [3, 4, 5]), 3.00000)

    # Test 6: nums2 entirely smaller than nums1
    test("nums2 all smaller", sol.findMedianSortedArrays([3, 4, 5], [1, 2]), 3.00000)

    # Test 7: Negative numbers
    test("Negative numbers", sol.findMedianSortedArrays([-5, -3, -1], [-4, -2]), -3.00000)

    # Test 8: Mixed negative and positive
    test("Mixed neg/pos", sol.findMedianSortedArrays([-2, 0], [1, 3]), 0.50000)

    # Test 9: Equal arrays
    test("Equal arrays", sol.findMedianSortedArrays([1, 3], [1, 3]), 2.00000)

    # Test 10: Single element arrays, even total
    test("Single elements even", sol.findMedianSortedArrays([3], [4]), 3.50000)

    print(f"\n📊 Results: {passed} passed, {failed} failed, {passed + failed} total")
