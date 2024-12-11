package jp.ha.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MyArray {
    public static Integer[] createArray(int n) {
        Integer[] array = new Integer[n];
        for (int i = 0; i < n; i++) {
            array[i] = i;
        }
        return array;
    }

    public static Integer[] shuffle(Integer[] array) {
     // 配列をリストに変換
        List<Integer> list = new ArrayList<>(Arrays.asList(array));

        // リストをシャッフル
        Collections.shuffle(list);

        // リストを配列に戻す
        list.toArray(array);
        return array;
    }
}
