import java.util.regex.Pattern;

/**
 * 0065. Valid Number
 * https://leetcode.com/problems/valid-number/
 * Difficulty: Hard
 * Tags: String
 */
class Solution {

    /**
     * Optimal Solution (Single Pass with Flags).
     * Time:  O(n)
     * Space: O(1)
     */
    public boolean isNumber(String s) {
        boolean sawDigit = false, sawDot = false, sawE = false, digitAfterE = true;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (Character.isDigit(c)) {
                sawDigit = true;
                if (sawE) digitAfterE = true;
            } else if (c == '+' || c == '-') {
                if (i != 0 && s.charAt(i - 1) != 'e' && s.charAt(i - 1) != 'E') return false;
            } else if (c == '.') {
                if (sawDot || sawE) return false;
                sawDot = true;
            } else if (c == 'e' || c == 'E') {
                if (sawE || !sawDigit) return false;
                sawE = true;
                digitAfterE = false;
            } else {
                return false;
            }
        }
        return sawDigit && digitAfterE;
    }

    private static final Pattern P =
        Pattern.compile("^[+-]?(\\d+\\.\\d*|\\.\\d+|\\d+)([eE][+-]?\\d+)?$");

    public boolean isNumberRegex(String s) { return P.matcher(s).matches(); }

    public boolean isNumberDFA(String s) {
        int[][] trans = {
            {2, 1, 4, -1, -1},
            {2, -1, 4, -1, -1},
            {2, -1, 3, 6, -1},
            {5, -1, -1, 6, -1},
            {5, -1, -1, -1, -1},
            {5, -1, -1, 6, -1},
            {8, 7, -1, -1, -1},
            {8, -1, -1, -1, -1},
            {8, -1, -1, -1, -1}
        };
        java.util.Set<Integer> accept = new java.util.HashSet<>(java.util.Arrays.asList(2, 3, 5, 8));
        int state = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            int k;
            if (Character.isDigit(c)) k = 0;
            else if (c == '+' || c == '-') k = 1;
            else if (c == '.') k = 2;
            else if (c == 'e' || c == 'E') k = 3;
            else return false;
            int next = trans[state][k];
            if (next == -1) return false;
            state = next;
        }
        return accept.contains(state);
    }

    static int passed = 0, failed = 0;
    static void test(String name, boolean got, boolean expected) {
        if (got == expected) { System.out.println("PASS: " + name); passed++; }
        else { System.out.println("FAIL: " + name + " got=" + got); failed++; }
    }

    public static void main(String[] args) {
        Solution sol = new Solution();
        Object[][] cases = {
            {"0", true}, {"e", false}, {".", false}, {"2", true}, {"0089", true},
            {"-0.1", true}, {"+3.14", true}, {"4.", true}, {"-.9", true},
            {"2e10", true}, {"-90E3", true}, {"3e+7", true}, {"+6e-1", true},
            {"53.5e93", true}, {"-123.456e789", true}, {"abc", false}, {"1a", false},
            {"1e", false}, {"e3", false}, {"99e2.5", false}, {"--6", false},
            {"-+3", false}, {"95a54e53", false}, {"6+1", false}, {"+", false},
            {"-", false}, {"+.", false}, {".e1", false}, {"6e6.5", false},
            {".1", true}, {"1.", true}, {"1.5", true}, {"+1", true}, {"-1", true},
            {".e", false}, {"+e", false}, {"6.e2", true}
        };
        System.out.println("=== Single Pass ===");
        for (Object[] c : cases) test("isNumber(" + c[0] + ")", sol.isNumber((String) c[0]), (boolean) c[1]);
        System.out.println("\n=== Regex ===");
        for (Object[] c : cases) test("regex(" + c[0] + ")", sol.isNumberRegex((String) c[0]), (boolean) c[1]);
        System.out.println("\n=== DFA ===");
        for (Object[] c : cases) test("dfa(" + c[0] + ")", sol.isNumberDFA((String) c[0]), (boolean) c[1]);
        System.out.printf("%nResults: %d passed, %d failed, %d total%n", passed, failed, passed + failed);
    }
}
