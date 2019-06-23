package net.kaciras.blog.infrastructure.codec;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.BitSet;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * 对比几种检查字符串是否是HEX的方式的性能。
 *
 * f1894c00ba-default-非英文字符:
 * Benchmark                Mode    Cnt   Score   Error   Units
 * HexMatchPerf.bitSet      thrpt    5   9.068 ± 0.147  ops/us
 * HexMatchPerf.bySwitch    thrpt    5  10.783 ± 0.908  ops/us
 * HexMatchPerf.clr         thrpt    5   7.194 ± 0.563  ops/us
 * HexMatchPerf.ifRange     thrpt    5   9.684 ± 1.026  ops/us
 * HexMatchPerf.regexp      thrpt    5   5.826 ± 0.024  ops/us
 *
 * 0de735be2d228599d4a48fe37f7cdc45b6134296a9bd59959590f7cefffeaf96:
 * Benchmark                Mode    Cnt   Score   Error   Units
 * HexMatchPerf.bitSet      thrpt    5   2.977 ± 0.135  ops/us
 * HexMatchPerf.bySwitch    thrpt    5   2.275 ± 0.018  ops/us
 * HexMatchPerf.clr         thrpt    5   3.610 ± 0.317  ops/us
 * HexMatchPerf.ifRange     thrpt    5   4.166 ± 0.267  ops/us
 * HexMatchPerf.regexp      thrpt    5   1.917 ± 0.007  ops/us
 */
@State(Scope.Thread)
@Fork(1)
@Measurement(iterations = 5, time = 5)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class HexMatchPerf {

	private static final Pattern REGEX = Pattern.compile("^[0-9a-fA-F]+$");
	private static final BitSet BIT_SET = new BitSet();

	static {
		for (char c = '0'; c <= '9'; c++) BIT_SET.set(c);
		for (char c = 'a'; c <= 'z'; c++) BIT_SET.set(c);
		for (char c = 'A'; c <= 'Z'; c++) BIT_SET.set(c);
	}

	@Param({"f1894c00ba-default-非英文字符", "0de735be2d228599d4a48fe37f7cdc45b6134296a9bd59959590f7cefffeaf96"})
	private String text;

	private boolean doBySwitch(char character) {
		switch (character) {
			case '0':
			case '1':
			case '2':
			case '3':
			case '4':
			case '5':
			case '6':
			case '7':
			case '8':
			case '9':
			case 'a':
			case 'b':
			case 'c':
			case 'd':
			case 'e':
			case 'f':
			case 'A':
			case 'B':
			case 'C':
			case 'D':
			case 'E':
			case 'F':
				return true;
		}
		return false;
	}

	@Benchmark
	public void ifRange(Blackhole blackhole) {
		for (var c: text.toCharArray()) {
			blackhole.consume(CodecUtils.isHexDigit(c));
		}
	}

	@Benchmark
	public void bySwitch(Blackhole blackhole) {
		for (var c : text.toCharArray()) {
			blackhole.consume(doBySwitch(c));
		}
	}

	@Benchmark
	public void regexp(Blackhole blackhole) {
		blackhole.consume(REGEX.matcher(text).find());
	}

	@Benchmark
	public void clr(Blackhole blackhole) {
		for (var c : text.toCharArray()) {
			blackhole.consume(Character.digit(c, 16) != -1);
		}
	}

	@Benchmark
	public void bitSet(Blackhole blackhole) {
		for (var c : text.toCharArray()) {
			blackhole.consume(BIT_SET.get(c));
		}
	}

	public static void main(String[] args) throws RunnerException {
		var opt = new OptionsBuilder()
				.include(HexMatchPerf.class.getSimpleName())
				.build();
		var results = new Runner(opt).run();
	}
}
