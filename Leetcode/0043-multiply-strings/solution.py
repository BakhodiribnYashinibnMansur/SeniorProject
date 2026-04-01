# ============================================================
# 0043. Multiply Strings
# https://leetcode.com/problems/multiply-strings/
# Difficulty: Medium
# Tags: Math, String, Simulation
# ============================================================


class Solution:
    def multiply(self, num1: str, num2: str) -> str:
        """
        Optimal Solution (Grade School Multiplication)
        Approach: Multiply digit by digit, accumulate at correct positions
        Time:  O(m*n) — multiply every digit pair
        Space: O(m+n) — result array of size m+n
        """
        # Edge case: anything times zero is zero
        if num1 == "0" or num2 == "0":
            return "0"

        m, n = len(num1), len(num2)
        # Product of m-digit and n-digit numbers has at most m+n digits
        result = [0] * (m + n)

        # Multiply each digit pair and accumulate at correct positions
        for i in range(m - 1, -1, -1):
            for j in range(n - 1, -1, -1):
                mul = int(num1[i]) * int(num2[j])
                p1, p2 = i + j, i + j + 1  # p1=tens position, p2=ones position

                # Add product to the ones position and propagate carry
                total = mul + result[p2]
                result[p2] = total % 10
                result[p1] += total // 10

        # Build result string, skip leading zeros
        result_str = ''.join(map(str, result)).lstrip('0')
        return result_str if result_str else "0"


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

    print("=== Grade School Multiplication (Optimal) ===")

    # Test 1: LeetCode Example 1
    test("Example 1", sol.multiply("2", "3"), "6")

    # Test 2: LeetCode Example 2
    test("Example 2", sol.multiply("123", "456"), "56088")

    # Test 3: Zero times number
    test("Zero * number", sol.multiply("0", "12345"), "0")

    # Test 4: Number times zero
    test("Number * zero", sol.multiply("12345", "0"), "0")

    # Test 5: Both zeros
    test("Zero * zero", sol.multiply("0", "0"), "0")

    # Test 6: Single digit multiplication
    test("Single digits", sol.multiply("9", "9"), "81")

    # Test 7: One times number (identity)
    test("Identity", sol.multiply("1", "999"), "999")

    # Test 8: Large carry propagation
    test("Large carry", sol.multiply("999", "999"), "998001")

    # Test 9: Different lengths
    test("Different lengths", sol.multiply("12", "3456"), "41472")

    # Test 10: Result with internal zeros
    test("Internal zeros", sol.multiply("100", "100"), "10000")

    # Test 11: Large numbers
    test("Large numbers", sol.multiply("123456789", "987654321"), "121932631112635269")

    # Test 12: Power of 10
    test("Power of 10", sol.multiply("10", "10"), "100")

    # Results
    print(f"\n\U0001f4ca Results: {passed} passed, {failed} failed, {passed + failed} total")
