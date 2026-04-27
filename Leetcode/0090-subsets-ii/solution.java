import java.util.*;

class Solution {
    public List<List<Integer>> subsetsWithDup(int[] nums) {
        Arrays.sort(nums);
        List<List<Integer>> result = new ArrayList<>();
        bt(0, nums, new ArrayList<>(), result);
        return result;
    }
    private void bt(int start, int[] nums, List<Integer> cur, List<List<Integer>> result) {
        result.add(new ArrayList<>(cur));
        for (int i = start; i < nums.length; i++) {
            if (i > start && nums[i] == nums[i - 1]) continue;
            cur.add(nums[i]);
            bt(i + 1, nums, cur, result);
            cur.remove(cur.size() - 1);
        }
    }

    static int passed = 0, failed = 0;
    static List<List<Integer>> canon(List<List<Integer>> out) {
        List<List<Integer>> cp = new ArrayList<>();
        for (List<Integer> s : out) {
            List<Integer> c = new ArrayList<>(s);
            Collections.sort(c);
            cp.add(c);
        }
        cp.sort((a, b) -> {
            for (int i = 0; i < Math.min(a.size(), b.size()); i++) {
                int cmp = Integer.compare(a.get(i), b.get(i));
                if (cmp != 0) return cmp;
            }
            return Integer.compare(a.size(), b.size());
        });
        return cp;
    }
    static void test(String name, List<List<Integer>> got, List<List<Integer>> expected) {
        if (canon(got).equals(canon(expected))) { System.out.println("PASS: " + name); passed++; }
        else { System.out.println("FAIL: " + name); failed++; }
    }

    public static void main(String[] args) {
        Solution sol = new Solution();
        Object[][] cases = {
            {"Example 1", new int[]{1, 2, 2}, Arrays.asList(
                Arrays.asList(), Arrays.asList(1), Arrays.asList(2), Arrays.asList(1, 2),
                Arrays.asList(2, 2), Arrays.asList(1, 2, 2))},
            {"Example 2", new int[]{0}, Arrays.asList(Arrays.asList(), Arrays.asList(0))},
            {"All same", new int[]{4, 4, 4}, Arrays.asList(
                Arrays.asList(), Arrays.asList(4), Arrays.asList(4, 4), Arrays.asList(4, 4, 4))}
        };
        for (Object[] c : cases) test((String) c[0], sol.subsetsWithDup(((int[]) c[1]).clone()), (List<List<Integer>>) c[2]);
        System.out.printf("%nResults: %d passed, %d failed, %d total%n", passed, failed, passed + failed);
    }
}
