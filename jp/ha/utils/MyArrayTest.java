package jp.ha.utils;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class MyArrayTest {

	@Nested
	class _6を与えたら6この要素を持つ配列を作る{
		@Test
		void _xとuをたすとxuになる() {
			// 実行
		    Integer[] array = {0, 1, 2, 3, 4, 5};
		    Integer[] result = MyArray.createArray(6);
//			System.out.println(result);
			// 検証 前が期待値
			assertEquals(array, result);

		}
	}
}
