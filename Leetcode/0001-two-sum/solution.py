# ============================================================
# 0001. Two Sum
# https://leetcode.com/problems/two-sum/
# Difficulty: Easy
# Tags: Array, Hash Table
# ============================================================


class Solution:
    def twoSum(self, nums: list[int], target: int) -> list[int]:
        """
        Optimal Solution (One-pass Hash Map)
        Approach: Hash Map da complement ni qidirish
        Time:  O(n) — massivni faqat 1 marta o'tamiz
        Space: O(n) — Hash Map ga eng ko'pi bilan n ta element
        """
        # Hash Map: qiymat → indeks
        # Har bir elementni ko'rganda, uning complementi (target - num)
        # oldin ko'rilganmi tekshiramiz
        seen = {}

        for i, num in enumerate(nums):
            # Complement ni hisoblash
            complement = target - num

            # Complement Hash Map da bormi?
            if complement in seen:
                # Topildi! complement ning indeksi seen[complement], hozirgi indeks i
                return [seen[complement], i]

            # Hozirgi elementni Hash Map ga qo'shish
            # Keyingi elementlar uchun complement sifatida ishlatiladi
            seen[num] = i

        # Constraint bo'yicha har doim javob mavjud
        # Bu yerga hech qachon kelmaydi
        return []


# ============================================================
# Test Cases
# ============================================================

if __name__ == "__main__":
    sol = Solution()

    # Test 1: Basic case — birinchi juftlikda topiladi
    print(sol.twoSum([2, 7, 11, 15], 9))  # Expected: [0, 1]

    # Test 2: O'rtada topiladi
    print(sol.twoSum([3, 2, 4], 6))  # Expected: [1, 2]

    # Test 3: Dublikat qiymatlar
    print(sol.twoSum([3, 3], 6))  # Expected: [0, 1]

    # Test 4: Salbiy sonlar
    print(sol.twoSum([-1, -2, -3, -4, -5], -8))  # Expected: [2, 4]

    # Test 5: Aralash sonlar (manfiy + musbat)
    print(sol.twoSum([-3, 4, 3, 90], 0))  # Expected: [0, 2]

    # Test 6: Nol qiymatlar
    print(sol.twoSum([0, 4, 3, 0], 0))  # Expected: [0, 3]

    # Test 7: Katta qiymatlar
    print(sol.twoSum([1000000000, -1000000000], 0))  # Expected: [0, 1]
