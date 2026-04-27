import java.util.*;

class Solution {
    public List<String> restoreIpAddresses(String s) {
        List<String> result = new ArrayList<>();
        bt(s, 0, 0, new ArrayList<>(), result);
        return result;
    }
    private void bt(String s, int start, int segs, List<String> parts, List<String> result) {
        if (segs == 4) {
            if (start == s.length()) result.add(String.join(".", parts));
            return;
        }
        int remaining = s.length() - start, need = 4 - segs;
        if (remaining < need || remaining > need * 3) return;
        for (int len = 1; len <= 3 && start + len <= s.length(); len++) {
            String seg = s.substring(start, start + len);
            if (seg.length() > 1 && seg.charAt(0) == '0') continue;
            int n = Integer.parseInt(seg);
            if (n > 255) continue;
            parts.add(seg);
            bt(s, start + len, segs + 1, parts, result);
            parts.remove(parts.size() - 1);
        }
    }

    static int passed = 0, failed = 0;
    static void test(String name, List<String> got, List<String> exp) {
        List<String> g = new ArrayList<>(got); Collections.sort(g);
        List<String> e = new ArrayList<>(exp); Collections.sort(e);
        if (g.equals(e)) { System.out.println("PASS: " + name); passed++; }
        else { System.out.println("FAIL: " + name); failed++; }
    }

    public static void main(String[] args) {
        Solution sol = new Solution();
        Object[][] cases = {
            {"Example 1", "25525511135", Arrays.asList("255.255.11.135", "255.255.111.35")},
            {"Example 2", "0000", Arrays.asList("0.0.0.0")},
            {"Example 3", "101023", Arrays.asList("1.0.10.23","1.0.102.3","10.1.0.23","10.10.2.3","101.0.2.3")},
            {"Too short", "111", Arrays.asList()},
            {"Too long", "1111111111111", Arrays.asList()},
            {"Min", "1111", Arrays.asList("1.1.1.1")}
        };
        for (Object[] c : cases) test((String) c[0], sol.restoreIpAddresses((String) c[1]), (List<String>) c[2]);
        System.out.printf("%nResults: %d passed, %d failed, %d total%n", passed, failed, passed + failed);
    }
}
