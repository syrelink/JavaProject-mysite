import java.util.Scanner;

/**
 * 解决 "四重数" 问题的 Java 程序
 */
public class Main {

    /**
     * 检查一个正整数是否为 "四重数"
     * 四重数：当且仅当其至少有四个数位是相同的。
     *
     * @param num 要检查的数字
     * @return 如果是四重数返回 true，否则返回 false
     */
    public static boolean isQuadruple(int num) {
        // 最小的四重数是 1111，小于此数的（1到1110）都不可能是
        // (注意：10000 是四重数，但 1000 不是)
        if (num < 1000 && num != 0) {
            // 0, 1, 2, 3 位数都不可能
            return false;
        }

        // 使用一个数组来统计 0-9 各位数字的出现次数
        int[] digitCounts = new int[10];

        // 将数字转为字符串以便遍历其数位
        String s = Integer.toString(num);

        for (char c : s.toCharArray()) {
            // c - '0' 将字符 '0'-'9' 转换为整数 0-9
            digitCounts[c - '0']++;
        }

        // 检查是否有任何一个计数值大于等于 4
        for (int count : digitCounts) {
            if (count >= 4) {
                return true;
            }
        }

        // 没有找到满足条件的数位
        return false;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        sc.close();

        // 1. 向上搜索 (n, n+1, n+2, ...)
        int upperQuad = n;
        // Integer.MAX_VALUE 是一个安全上限，
        // 题目范围 10^9 远小于它，且四重数密度不低，很快会找到
        while (upperQuad <= Integer.MAX_VALUE) {
            if (isQuadruple(upperQuad)) {
                break; // 找到了
            }
            upperQuad++;
        }

        // 2. 向下搜索 (n, n-1, n-2, ...)
        int lowerQuad = n;
        // 最小的正整数是 1
        while (lowerQuad >= 1) {
            if (isQuadruple(lowerQuad)) {
                break; // 找到了
            }
            lowerQuad--;
        }

        // 3. 对比结果

        // 如果 lowerQuad < 1，意味着从 n 向下到 1 都没有找到四重数
        // (例如 n=1110 时, lowerQuad 会变成 0)
        // 此时唯一的答案是 upperQuad
        if (lowerQuad < 1) {
            System.out.println(upperQuad);
        } else {
            // 两个方向都找到了（或者 n 本身就是）
            long distUpper = (long)upperQuad - n; // 使用 long 避免 n=1, upper=2222 时的溢出（虽然此题不会）
            long distLower = (long)n - lowerQuad;

            // 规则：如果存在多个距离 n 最近的，输出最小的那个。
            // (distLower <= distUpper) 这个条件完美处理了平局：
            // 如果距离相等 (distLower == distUpper)，它会返回 true，输出较小的 lowerQuad。
            if (distLower <= distUpper) {
                System.out.println(lowerQuad);
            } else {
                System.out.println(upperQuad);
            }
        }
    }
}