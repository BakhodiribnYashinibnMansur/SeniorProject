import java.util.*;

/**
 * 0049. Group Anagrams
 * https://leetcode.com/problems/group-anagrams/
 * Difficulty: Medium
 * Tags: Array, Hash Table, String, Sorting
 */
class Solution {

    /**
     * Optimal Solution (Character Count as Key)
     * Approach: Use character frequency string as Hash Map key
     * Time:  O(n * k) — n strings, each of length k
     * Space: O(n * k) — storing all strings in the Hash Map
     */
    public List<List<String>> groupAnagrams(String[] strs) {
        // Hash Map: character count string → list of original strings
        Map<String, List<String>> groups = new HashMap<>();

        for (String s : strs) {
            // Count frequency of each character (a-z)
            int[] count = new int[26];
            for (char c : s.toCharArray()) {
                count[c - 'a']++;
            }

            // Convert count to string key with separator
            // e.g., "1#0#0#0#1#0#...#1#0#0#0" for "eat"
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 26; i++) {
                sb.append(count[i]);
                sb.append('#');
            }
            String key = sb.toString();

            // Add to the group
            groups.computeIfAbsent(key, k -> new ArrayList<>()).add(s);
        }

        // Return all groups
        return new ArrayList<>(groups.values());
    }

    // ============================================================
    // Test Cases
    // ============================================================

    static int passed = 0, failed = 0;

    static void test(String name, List<List<String>> got, List<List<String>> expected) {
        // Sort inner lists and outer list for comparison
        List<String> gotSorted = normalizeGroups(got);
        List<String> expSorted = normalizeGroups(expected);

        if (gotSorted.equals(expSorted)) {
            System.out.printf("✅ PASS: %s%n", name);
            passed++;
        } else {
            System.out.printf("❌ FAIL: %s%n  Got:      %s%n  Expected: %s%n",
                name, got, expected);
            failed++;
        }
    }

    static List<String> normalizeGroups(List<List<String>> groups) {
        List<String> result = new ArrayList<>();
        for (List<String> g : groups) {
            List<String> sorted = new ArrayList<>(g);
            Collections.sort(sorted);
            result.add(String.join(",", sorted));
        }
        Collections.sort(result);
        return result;
    }

    public static void main(String[] args) {
        Solution sol = new Solution();

        // Test 1: Main example
        test("Main example",
            sol.groupAnagrams(new String[]{"eat", "tea", "tan", "ate", "nat", "bat"}),
            Arrays.asList(
                Arrays.asList("bat"),
                Arrays.asList("nat", "tan"),
                Arrays.asList("ate", "eat", "tea")));

        // Test 2: Empty string
        test("Empty string",
            sol.groupAnagrams(new String[]{""}),
            Arrays.asList(Arrays.asList("")));

        // Test 3: Single character
        test("Single character",
            sol.groupAnagrams(new String[]{"a"}),
            Arrays.asList(Arrays.asList("a")));

        // Test 4: No anagrams
        test("No anagrams",
            sol.groupAnagrams(new String[]{"abc", "def", "ghi"}),
            Arrays.asList(
                Arrays.asList("abc"),
                Arrays.asList("def"),
                Arrays.asList("ghi")));

        // Test 5: All anagrams
        test("All anagrams",
            sol.groupAnagrams(new String[]{"abc", "bca", "cab"}),
            Arrays.asList(Arrays.asList("abc", "bca", "cab")));

        // Test 6: Duplicate strings
        test("Duplicate strings",
            sol.groupAnagrams(new String[]{"a", "a"}),
            Arrays.asList(Arrays.asList("a", "a")));

        // Test 7: Mixed lengths
        test("Mixed lengths",
            sol.groupAnagrams(new String[]{"a", "ab", "ba", "abc", "bca"}),
            Arrays.asList(
                Arrays.asList("a"),
                Arrays.asList("ab", "ba"),
                Arrays.asList("abc", "bca")));

        // Test 8: Multiple empty strings
        test("Multiple empty strings",
            sol.groupAnagrams(new String[]{"", ""}),
            Arrays.asList(Arrays.asList("", "")));

        // Test 9: Long anagram group
        test("Long anagram group",
            sol.groupAnagrams(new String[]{"listen", "silent", "enlist", "inlets", "tinsel"}),
            Arrays.asList(Arrays.asList("listen", "silent", "enlist", "inlets", "tinsel")));

        // Results
        System.out.printf("%n📊 Results: %d passed, %d failed, %d total%n",
            passed, failed, passed + failed);
    }
}
