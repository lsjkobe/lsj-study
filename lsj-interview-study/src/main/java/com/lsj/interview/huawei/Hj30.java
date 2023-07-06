package com.lsj.interview.huawei;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Hj30 {
    private static final Map<Character, Integer> CHAR_INT_MAP = new HashMap<Character, Integer>(64) {
        {
            put('0', 0);
            put('1', 1);
            put('2', 2);
            put('3', 3);
            put('4', 4);
            put('5', 5);
            put('6', 6);
            put('7', 7);
            put('8', 8);
            put('9', 9);
            put('a', 10);
            put('b', 11);
            put('c', 12);
            put('d', 13);
            put('e', 14);
            put('f', 15);
            put('A', 10);
            put('B', 11);
            put('C', 12);
            put('D', 13);
            put('E', 14);
            put('F', 15);
        }
    };

    private static final Map<Integer, Character> INT_CHAR_MAP = new HashMap<Integer, Character>(64) {
        {
            put(0, '0');
            put(1, '1');
            put(2, '2');
            put(3, '3');
            put(4, '4');
            put(5, '5');
            put(6, '6');
            put(7, '7');
            put(8, '8');
            put(9, '9');
            put(10, 'A');
            put(11, 'B');
            put(12, 'C');
            put(13, 'D');
            put(14, 'E');
            put(15, 'F');
        }
    };

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        while (in.hasNext()) {
            String str1 = in.nextLine();
            str1 = str1.replace(" ", "");
            String transStr = transStr(str1);
            char[] chars = new char[transStr.length()];
            for (int i = 0; i < transStr.length(); i++) {
                char c = transStr.charAt(i);
                char c1 = trans(c);
                chars[i] = c1;
            }
            System.out.println(String.valueOf(chars));
        }
    }

    private static String transStr(String str) {
        char[] charA = new char[((str.length() % 2) == 0) ? str.length() / 2 : str.length() / 2 + 1];
        char[] charB = new char[str.length() / 2];
        for (int i = 0; i < str.length(); i++) {
            if (i % 2 == 0) {
                charA[i / 2] = str.charAt(i);
            } else {
                charB[i / 2] = str.charAt(i);
            }
        }
        Arrays.sort(charA);
        Arrays.sort(charB);
        char[] charC = new char[str.length()];
        for (int i = 0; i < str.length(); i++) {
            if (i % 2 == 0) {
                charC[i] = charA[i / 2];
            } else {
                charC[i] = charB[i / 2];
            }
        }
        return String.valueOf(charC);
    }

    private static char trans(char c) {
        Integer cInt = charToInt(c);
        if (cInt == null) {
            return c;
        }
        // 将数字转换为二进制字符串
        String binaryString = Integer.toBinaryString(cInt);
        StringBuilder sb1 = new StringBuilder();
        // 创建一个StringBuilder对象用于存储翻转后的二进制字符串
        StringBuilder reversedString = new StringBuilder();
        int a;
        if ((a = (4 - binaryString.length())) > 0) {
            for (int i = 0; i < a; i++) {
                sb1.append(0);
            }
        }
        sb1.append(binaryString);
        // 从右向左遍历二进制字符串
        for (int i = sb1.toString().length() - 1; i >= 0; i--) {
            // 将每个字符添加到StringBuilder中
            reversedString.append(sb1.toString().charAt(i));
        }
        // 将翻转后的二进制字符串转换回整数
        Integer resInt = Integer.parseInt(reversedString.toString(), 2);
        return INT_CHAR_MAP.get(resInt);
    }

    private static Integer charToInt(char c) {
        return CHAR_INT_MAP.get(c);
    }
}
