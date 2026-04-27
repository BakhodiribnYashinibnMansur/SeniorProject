class Solution {
    public boolean search(int[] nums, int target) {
        int lo = 0, hi = nums.length - 1;
        while (lo <= hi) {
            int mid = (lo + hi) >>> 1;
            if (nums[mid] == target) return true;
            if (nums[lo] == nums[mid] && nums[mid] == nums[hi]) {
                lo++; hi--;
            } else if (nums[lo] <= nums[mid]) {
                if (nums[lo] <= target && target < nums[mid]) hi = mid - 1;
                else lo = mid + 1;
            } else {
                if (nums[mid] < target && target <= nums[hi]) lo = mid + 1;
                else hi = mid - 1;
            }
        }
        return false;
    }

    static int passed = 0, failed = 0;
    static void test(String name, boolean got, boolean expected) {
        if (got == expected) { System.out.println("PASS: " + name); passed++; }
        else { System.out.println("FAIL: " + name + " got=" + got); failed++; }
    }

    public static void main(String[] args) {
        Solution sol = new Solution();
        Object[][] cases = {
            {"Example 1", new int[]{2, 5, 6, 0, 0, 1, 2}, 0, true},
            {"Example 2", new int[]{2, 5, 6, 0, 0, 1, 2}, 3, false},
            {"Single match", new int[]{5}, 5, true},
            {"Single miss", new int[]{5}, 6, false},
            {"All duplicates match", new int[]{1, 1, 1, 1}, 1, true},
            {"All duplicates miss", new int[]{1, 1, 1, 1}, 2, false},
            {"Not rotated", new int[]{1, 2, 3, 4}, 3, true},
            {"Pivot at start", new int[]{4, 5, 6, 1, 2, 3}, 1, true},
            {"Edges first", new int[]{4, 5, 6, 7, 0, 1, 2}, 4, true},
            {"Edges last", new int[]{4, 5, 6, 7, 0, 1, 2}, 2, true},
            {"Tricky duplicates", new int[]{1, 0, 1, 1, 1}, 0, true},
            {"Tricky duplicates miss", new int[]{1, 0, 1, 1, 1}, 2, false},
        };
        for (Object[] c : cases) test((String) c[0], sol.search((int[]) c[1], (int) c[2]), (boolean) c[3]);
        System.out.printf("%nResults: %d passed, %d failed, %d total%n", passed, failed, passed + failed);
    }
}
