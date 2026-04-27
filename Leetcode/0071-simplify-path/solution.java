import java.util.*;

/**
 * 0071. Simplify Path
 * https://leetcode.com/problems/simplify-path/
 * Difficulty: Medium
 * Tags: String, Stack
 */
class Solution {

    /**
     * Optimal Solution (Split + Stack).
     * Time:  O(n)
     * Space: O(n)
     */
    public String simplifyPath(String path) {
        String[] parts = path.split("/");
        Deque<String> stack = new ArrayDeque<>();
        for (String p : parts) {
            if (p.isEmpty() || p.equals(".")) continue;
            if (p.equals("..")) {
                if (!stack.isEmpty()) stack.pop();
                continue;
            }
            stack.push(p);
        }
        StringBuilder sb = new StringBuilder();
        Iterator<String> it = stack.descendingIterator();
        while (it.hasNext()) sb.append('/').append(it.next());
        return sb.length() == 0 ? "/" : sb.toString();
    }

    public String simplifyPathManual(String path) {
        Deque<String> stack = new ArrayDeque<>();
        int i = 0, n = path.length();
        while (i < n) {
            while (i < n && path.charAt(i) == '/') i++;
            int j = i;
            while (j < n && path.charAt(j) != '/') j++;
            String seg = path.substring(i, j);
            i = j;
            if (seg.isEmpty() || seg.equals(".")) continue;
            if (seg.equals("..")) {
                if (!stack.isEmpty()) stack.pop();
                continue;
            }
            stack.push(seg);
        }
        StringBuilder sb = new StringBuilder();
        Iterator<String> it = stack.descendingIterator();
        while (it.hasNext()) sb.append('/').append(it.next());
        return sb.length() == 0 ? "/" : sb.toString();
    }

    static int passed = 0, failed = 0;
    static void test(String name, String got, String expected) {
        if (got.equals(expected)) { System.out.println("PASS: " + name); passed++; }
        else { System.out.println("FAIL: " + name + " got=" + got); failed++; }
    }

    public static void main(String[] args) {
        Solution sol = new Solution();
        String[][] cases = {
            {"Trailing slash", "/home/", "/home"},
            {"Up from root", "/../", "/"},
            {"Double slash", "/home//foo/", "/home/foo"},
            {"Mixed", "/a/./b/../../c/", "/c"},
            {"Just root", "/", "/"},
            {"Many ups", "/a/b/c/../../../", "/"},
            {"Hidden file", "/.hidden/file", "/.hidden/file"},
            {"Three dots", "/.../", "/..."},
            {"Multiple slashes", "//", "/"},
            {"Long names", "/abc/def/", "/abc/def"},
            {"Mixed with up at start", "/../abc/", "/abc"},
            {"Trailing trailing", "/abc//", "/abc"}
        };
        System.out.println("=== Split + Stack ===");
        for (String[] c : cases) test(c[0], sol.simplifyPath(c[1]), c[2]);
        System.out.println("\n=== Manual Parser ===");
        for (String[] c : cases) test("Manual " + c[0], sol.simplifyPathManual(c[1]), c[2]);
        System.out.printf("%nResults: %d passed, %d failed, %d total%n", passed, failed, passed + failed);
    }
}
