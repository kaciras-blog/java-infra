package net.kaciras.blog.infrastructure.text;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.List;

final class DangerWordDetectorTest {

	private final DangerWordDetector dwd = new DangerWordDetector();

	@Test
	void test() {
		dwd.addWords("法轮功", "法轮", "法轮大法");
		dwd.addStopChars("~!@#$%&*()".toCharArray());

		String text = "测试法轮法法()轮测试法轮大*法*";
		Assertions.assertIterableEquals(List.of("法轮", "法()轮", "法轮大*法"), dwd.getDangerWords(text));
		Assertions.assertEquals("测试**法**测试***", dwd.replace(text, "**"));
	}

	@Test
	void test1() {
		dwd.addWords("ABCDEF", "CDE"); //first includes the second.
		Assertions.assertIterableEquals(List.of("CDE"), dwd.getDangerWords("ABCDE"));
	}

	@Test
	void test2() {
		dwd.addWords("A");
		Assertions.assertIterableEquals(List.of("A", "A", "A"), dwd.getDangerWords("AABBA"));
	}

	@Disabled("Performance test")
	@Test
	void testPerformance() {
		dwd.addWords("法轮功", "法轮", "法轮大法");
		dwd.addStopChars("~!@#$%&*()".toCharArray());

		//warm up
		for (int i = 0; i < 100000; i++) {
			String text = "The Java HotSpot compiler kicks in when it sees a ‘hot spot’ in your code.";
			dwd.replace(text, "**");
		}

		ThreadMXBean mxBean = ManagementFactory.getThreadMXBean();
		long st = mxBean.getCurrentThreadCpuTime();

		for (int i = 0; i < 1e5; i++) {
			String text = "测试法轮法法()轮_(这是一个性能测试)_法轮大*法_123456";
			dwd.replace(text, "**");
		}

		long et = mxBean.getCurrentThreadCpuTime();
		System.out.println((et - st) / 1e6d + " ms");
	}
}
