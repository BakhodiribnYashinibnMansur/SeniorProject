import java.util.*;

class Solution {
    public List<Integer> grayCode(int n) {
        int size = 1 << n;
        List<Integer> result = new ArrayList<>(size);
        for (int i = 0; i < size; i++) result.add(i ^ (i >> 1));
        return result;
    }

    static int passed = 0, failed = 0;
    static boolean validate(List<Integer> seq, int n) {
        if (seq.size() != (1 << n)) return false;
        if (seq.get(0) != 0) return false;
        Set<Integer> seen = new HashSet<>();
        for (int v : seq) {
            if (v < 0 || v >= (1 << n) || !seen.add(v)) return false;
        }
        for (int i = 0; i < seq.size(); i++) {
            int j = (i + 1) % seq.size();
            if (Integer.bitCount(seq.get(i) ^ seq.get(j)) != 1) return false;
        }
        return true;
    }

    public static void main(String[] args) {
        Solution sol = new Solution();
        for (int n : new int[]{1, 2, 3, 4, 5, 8}) {
            List<Integer> seq = sol.grayCode(n);
            if (validate(seq, n)) { System.out.println("PASS: n=" + n + " len=" + seq.size()); passed++; }
            else { System.out.println("FAIL: n=" + n); failed++; }
        }
        System.out.printf("%nResults: %d passed, %d failed, %d total%n", passed, failed, passed + failed);
    }
}
