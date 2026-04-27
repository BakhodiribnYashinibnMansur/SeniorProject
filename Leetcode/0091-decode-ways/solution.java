class Solution {
    public int numDecodings(String s) {
        int n = s.length();
        if (n == 0 || s.charAt(0) == '0') return 0;
        int prev2 = 1, prev1 = 1;
        for (int i = 2; i <= n; i++) {
            int cur = 0;
            if (s.charAt(i - 1) != '0') cur += prev1;
            int two = (s.charAt(i - 2) - '0') * 10 + (s.charAt(i - 1) - '0');
            if (two >= 10 && two <= 26) cur += prev2;
            prev2 = prev1;
            prev1 = cur;
        }
        return prev1;
    }

    static int passed = 0, failed = 0;
    static void test(String name, int got, int exp) {
        if (got == exp) { System.out.println("PASS: " + name); passed++; }
        else { System.out.println("FAIL: " + name + " got=" + got); failed++; }
    }

    public static void main(String[] args) {
        Solution sol = new Solution();
        Object[][] cases = {
            {"12", 2}, {"226", 3}, {"06", 0}, {"0", 0}, {"10", 1}, {"20", 1},
            {"27", 1}, {"100", 0}, {"1", 1}, {"11106", 2}, {"111111", 13},
            {"2611055971756562", 4}, {"301", 0}
        };
        for (Object[] c : cases) test((String) c[0], sol.numDecodings((String) c[0]), (int) c[1]);
        System.out.printf("%nResults: %d passed, %d failed, %d total%n", passed, failed, passed + failed);
    }
}
