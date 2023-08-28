package com.longten.chat;

public class BinarySearch {
    public static int binarySearch(int[] array, int target) {
        int left = 0;
        int right = array.length - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;

            if (array[mid] == target) {
                return mid; // 找到目标值，返回索引
            } else if (array[mid] < target) {
                left = mid + 1; // 目标值在右半部分
            } else {
                right = mid - 1; // 目标值在左半部分
            }
        }

        return -1; // 没有找到目标值
    }

    public static void main(String[] args) {
        int[] sortedArray = { 1, 3, 5, 7, 9, 11, 13, 15, 17, 19 };
        int target = 10;

        int result = binarySearch(sortedArray, target);

        if (result != -1) {
            System.out.println("目标值 " + target + " 在索引 " + result + " 处找到。");
        } else {
            System.out.println("未找到目标值 " + target + "。");
        }
    }
}
