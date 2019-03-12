package com.pcjz.lems.business.volley;

import java.util.Arrays;

/**
 * created by yezhengyu on 2018/5/7 15:12
 */

public class TestCaculate {

    public static void main(String[] args) {
        test1();
    }

    private static void test1() {
        //冒泡排序算法
        int[] arrays = {2, 4, 1, 3, 0, 9, 6, 8, 23, 11};
        int t = 0;
        for (int i = 0; i < arrays.length-1; i++) {
            for (int j = 0; j < arrays.length - 1 - i; j++) {
                if (arrays[j] > arrays[j + 1]) {
                    t = arrays[j];
                    arrays[j] = arrays[j + 1];
                    arrays[j + 1] = t;
                }
            }
        }
        System.out.println(Arrays.toString(arrays));
        //选择排序算法
        for (int i = 0; i < arrays.length; i++) {
            int k = i;
            for (int j = i + 1; j < arrays.length; j++) {
                if (arrays[j] < arrays[k]) {
                    k = j;
                }
            }
            int temp;
            if (k > i) {
                temp = arrays[i];
                arrays[i] = arrays[k];
                arrays[k] = temp;
            }
        }
        System.out.println(Arrays.toString(arrays));
        //二分法排序算法

    }
}
