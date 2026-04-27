import java.math.BigInteger;

/**
 * 0067. Add Binary
 * https://leetcode.com/problems/add-binary/
 * Difficulty: Easy
 * Tags: Math, String, Bit Manipulation, Simulation
 */
class Solution {

    /**
     * Optimal Solution (Walk + Carry).
     * Time:  O(max(n, m))
     * Space: O(max(n, m))
     */
    public String addBinary(String a, String b) {
        StringBuilder sb = new StringBuilder();
        int i = a.length() - 1, j = b.length() - 1, carry = 0;
        while (i >= 0 || j >= 0 || carry > 0) {
            int s = carry;
            if (i >= 0) s += a.charAt(i--) - '0';
            if (j >= 0) s += b.charAt(j--) - '0';
            sb.append(s % 2);
            carry = s / 2;
        }
        return sb.reverse().toString();
    }

    /**
     * BigInteger XOR/AND.
     * Time:  O((max len)^2)
     * Space: O(max len)
     */
    public String addBinaryBits(String a, String b) {
        BigInteger x = new BigInteger(a, 2);
        BigInteger y = new BigInteger(b, 2);
        while (!y.equals(BigInteger.ZERO)) {
            BigInteger sum = x.xor(y);
            BigInteger carry = x.and(y).shiftLeft(1);
            x = sum; y = carry;
        }
        return x.toString(2);
    }

    static int passed = 0, failed = 0;
    static void test(String name, String got, String expected) {
        if (got.equals(expected)) { System.out.println("PASS: " + name); passed++; }
        else { System.out.println("FAIL: " + name + " got=" + got); failed++; }
    }

    public static void main(String[] args) {
        Solution sol = new Solution();
        String[][] cases = {
            {"Example 1", "11", "1", "100"},
            {"Example 2", "1010", "1011", "10101"},
            {"Both zero", "0", "0", "0"},
            {"One zero", "0", "1", "1"},
            {"One zero left", "1", "0", "1"},
            {"Carry chain", "1", "1", "10"},
            {"Cascading carry", "111", "1", "1000"},
            {"Different lengths", "1", "111", "1000"},
            {"All ones", "1111", "1111", "11110"},
            {"Result with leading carry", "11111111", "1", "100000000"},
            {"Equal length no carry", "1010", "0101", "1111"}
        };
        System.out.println("=== Walk + Carry ===");
        for (String[] c : cases) test(c[0], sol.addBinary(c[1], c[2]), c[3]);
        System.out.println("\n=== BigInteger XOR ===");
        for (String[] c : cases) test("Bits " + c[0], sol.addBinaryBits(c[1], c[2]), c[3]);
        System.out.printf("%nResults: %d passed, %d failed, %d total%n", passed, failed, passed + failed);
    }
}
