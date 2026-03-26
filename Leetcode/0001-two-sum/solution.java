import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

/**
 * 0001. Two Sum
 * https://leetcode.com/problems/two-sum/
 * Difficulty: Easy
 * Tags: Array, Hash Table
 */
class Solution {

    /**
     * Optimal Solution (One-pass Hash Map)
     * Approach: Hash Map da complement ni qidirish
     * Time:  O(n) — massivni faqat 1 marta o'tamiz
     * Space: O(n) — Hash Map ga eng ko'pi bilan n ta element
     */
    public int[] twoSum(int[] nums, int target) {
        // Hash Map: qiymat → indeks
        // Har bir elementni ko'rganda, uning complementi (target - num)
        // oldin ko'rilganmi tekshiramiz
        HashMap<Integer, Integer> seen = new HashMap<>();

        for (int i = 0; i < nums.length; i++) {
            // Complement ni hisoblash
            int complement = target - nums[i];

            // Complement Hash Map da bormi?
            if (seen.containsKey(complement)) {
                // Topildi! complement ning indeksi seen[complement], hozirgi indeks i
                return new int[]{seen.get(complement), i};
            }

            // Hozirgi elementni Hash Map ga qo'shish
            // Keyingi elementlar uchun complement sifatida ishlatiladi
            seen.put(nums[i], i);
        }

        // Constraint bo'yicha har doim javob mavjud
        // Bu yerga hech qachon kelmaydi
        return new int[]{};
    }

    // ============================================================
    // Test Cases
    // ============================================================

    static int passed = 0, failed = 0;

    static void test(String name, int[] got, int[] expected) {
        if (Arrays.equals(got, expected)) {
            System.out.printf("✅ PASS: %s%n", name);
            passed++;
        } else {
            System.out.printf("❌ FAIL: %s%n  Got:      %s%n  Expected: %s%n",
                name, Arrays.toString(got), Arrays.toString(expected));
            failed++;
        }
    }

    public static void main(String[] args) {
        Solution sol = new Solution();

        // Test 1: Basic case — birinchi juftlikda topiladi
        test("Basic case", sol.twoSum(new int[]{2, 7, 11, 15}, 9), new int[]{0, 1});

        // Test 2: O'rtada topiladi
        test("O'rtada topiladi", sol.twoSum(new int[]{3, 2, 4}, 6), new int[]{1, 2});

        // Test 3: Dublikat qiymatlar
        test("Dublikat qiymatlar", sol.twoSum(new int[]{3, 3}, 6), new int[]{0, 1});

        // Test 4: Salbiy sonlar
        test("Salbiy sonlar", sol.twoSum(new int[]{-1, -2, -3, -4, -5}, -8), new int[]{2, 4});

        // Test 5: Aralash sonlar (manfiy + musbat)
        test("Aralash sonlar", sol.twoSum(new int[]{-3, 4, 3, 90}, 0), new int[]{0, 2});

        // Test 6: Nol qiymatlar
        test("Nol qiymatlar", sol.twoSum(new int[]{0, 4, 3, 0}, 0), new int[]{0, 3});

        // Test 7: Katta qiymatlar
        test("Katta qiymatlar", sol.twoSum(new int[]{1000000000, -1000000000}, 0), new int[]{0, 1});

        // Natija
        System.out.printf("%n📊 Natija: %d passed, %d failed, %d total%n",
            passed, failed, passed + failed);
    }
}
