# ============================================================
# 0038. Count and Say
# https://leetcode.com/problems/count-and-say/
# Difficulty: Medium
# Tags: String
# ============================================================


class Solution:
    def countAndSay(self, n: int) -> str:
        """
        Optimal Solution (Iterative)
        Approach: Build each term by performing RLE on the previous term
        Time:  O(n * L) — n iterations, each processing string of length L
        Space: O(L) — store the current and next strings
        """
        result = "1"

        for step in range(2, n + 1):
            next_result = []
            i = 0

            while i < len(result):
                digit = result[i]
                count = 1

                # Count consecutive identical digits
                while i + count < len(result) and result[i + count] == digit:
                    count += 1

                # Append count and digit
                next_result.append(str(count))
                next_result.append(digit)
                i += count

            result = "".join(next_result)

        return result

    def countAndSayRecursive(self, n: int) -> str:
        """
        Recursive approach
        Approach: Base case n=1 returns "1", otherwise RLE of countAndSay(n-1)
        Time:  O(n * L) — n recursive calls
        Space: O(n * L) — recursion stack + strings
        """
        # Base case
        if n == 1:
            return "1"

        # Recursively get the previous term
        prev = self.countAndSayRecursive(n - 1)

        # Perform RLE on the previous term
        result = []
        i = 0

        while i < len(prev):
            digit = prev[i]
            count = 1

            # Count consecutive identical digits
            while i + count < len(prev) and prev[i + count] == digit:
                count += 1

            # Append count and digit
            result.append(str(count))
            result.append(digit)
            i += count

        return "".join(result)


# ============================================================
# Test Cases
# ============================================================

if __name__ == "__main__":
    sol = Solution()
    passed = failed = 0

    def test(name: str, got, expected):
        global passed, failed
        if got == expected:
            print(f"\u2705 PASS: {name}")
            passed += 1
        else:
            print(f"\u274c FAIL: {name}")
            print(f"  Got:      {got}")
            print(f"  Expected: {expected}")
            failed += 1

    print("=== Iterative (Optimal) ===")

    # Test 1: Base case
    test("n=1", sol.countAndSay(1), "1")

    # Test 2: One 1
    test("n=2", sol.countAndSay(2), "11")

    # Test 3: Two 1s
    test("n=3", sol.countAndSay(3), "21")

    # Test 4: LeetCode Example
    test("n=4", sol.countAndSay(4), "1211")

    # Test 5: Multiple runs
    test("n=5", sol.countAndSay(5), "111221")

    # Test 6: Longer sequence
    test("n=6", sol.countAndSay(6), "312211")

    # Test 7: Even longer
    test("n=7", sol.countAndSay(7), "13112221")

    # Test 8: n=8
    test("n=8", sol.countAndSay(8), "1113213211")

    # Test 9: n=10
    test("n=10", sol.countAndSay(10), "13211311123113112211")

    print("\n=== Recursive ===")

    # Test 10: Recursive — Base case
    test("Recursive n=1", sol.countAndSayRecursive(1), "1")

    # Test 11: Recursive — n=4
    test("Recursive n=4", sol.countAndSayRecursive(4), "1211")

    # Test 12: Recursive — n=6
    test("Recursive n=6", sol.countAndSayRecursive(6), "312211")

    # Results
    print(f"\n\U0001f4ca Results: {passed} passed, {failed} failed, {passed + failed} total")
