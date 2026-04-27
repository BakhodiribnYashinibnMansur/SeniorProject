import java.util.*;

class Solution {
    private Map<String, Boolean> memo = new HashMap<>();

    public boolean isScramble(String s1, String s2) {
        return solve(s1, s2);
    }

    private boolean solve(String a, String b) {
        if (a.equals(b)) return true;
        String key = a + "#" + b;
        if (memo.containsKey(key)) return memo.get(key);
        int[] c = new int[26];
        for (int i = 0; i < a.length(); i++) {
            c[a.charAt(i) - 'a']++;
            c[b.charAt(i) - 'a']--;
        }
        for (int v : c) if (v != 0) { memo.put(key, false); return false; }
        int n = a.length();
        for (int i = 1; i < n; i++) {
            if ((solve(a.substring(0, i), b.substring(0, i)) && solve(a.substring(i), b.substring(i))) ||
                (solve(a.substring(0, i), b.substring(n - i)) && solve(a.substring(i), b.substring(0, n - i)))) {
                memo.put(key, true); return true;
            }
        }
        memo.put(key, false); return false;
    }

    static int passed = 0, failed = 0;
    static void test(String name, boolean got, boolean expected) {
        if (got == expected) { System.out.println("PASS: " + name); passed++; }
        else { System.out.println("FAIL: " + name + " got=" + got); failed++; }
    }

    public static void main(String[] args) {
        Object[][] cases = {
            {"Example 1", "great", "rgeat", true},
            {"Example 2", "abcde", "caebd", false},
            {"Single", "a", "a", true},
            {"ab ba", "ab", "ba", true},
            {"ab ab", "ab", "ab", true},
            {"ab cd", "ab", "cd", false},
            {"abc abc", "abc", "abc", true},
            {"abc bca", "abc", "bca", true},
            {"abc cab", "abc", "cab", true},
            {"abcd dcba", "abcd", "dcba", true},
            {"abcd cdab", "abcd", "cdab", true}
        };
        for (Object[] c : cases) test((String) c[0], new Solution().isScramble((String) c[1], (String) c[2]), (boolean) c[3]);
        System.out.printf("%nResults: %d passed, %d failed, %d total%n", passed, failed, passed + failed);
    }
}
