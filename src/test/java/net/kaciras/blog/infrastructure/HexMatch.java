package net.kaciras.blog.infrastructure;

import net.kaciras.blog.infrastructure.codec.CodecUtils;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.results.RunResult;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
public class HexMatch {

	private static final Pattern REGEX = Pattern.compile("^[0-9a-fA-F]{64}$");

	@Param({"f1894c00ba-default-background", "0de735be2d228599d4a48fe37f7cdc45b6134296a9bd59959590f7cefffeaf96"})
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
			default:
				return false;
		}
	}

	@Benchmark
	public void ifRange(Blackhole blackhole) {
		for (int i = 0; i < 100; i++) {
			for (var ch : text.toCharArray()) {
				blackhole.consume(CodecUtils.isHexDigit(ch));
			}
		}
	}

	@Benchmark
	public void bySwitch(Blackhole blackhole) {
		for (int i = 0; i < 100; i++) {
			for (var ch : text.toCharArray()) {
				blackhole.consume(doBySwitch(ch));
			}
		}
	}

	@Benchmark
	public void regexp(Blackhole blackhole) {
		for (int i = 0; i < 100; i++) {
			blackhole.consume(REGEX.matcher(text).find());
		}
	}

	@Benchmark
	public void clr(Blackhole blackhole) {
		for (int i = 0; i < 100; i++) {
			for (var ch : text.toCharArray()) {
				blackhole.consume(Character.digit(ch, 16) != -1);
			}
		}
	}

	public static void main(String[] args) throws RunnerException {
		var opt = new OptionsBuilder()
				.include(HexMatch.class.getSimpleName())
				.forks(1)
				.warmupIterations(5)
				.measurementIterations(5)
				.build();
		Collection<RunResult> results = new Runner(opt).run();
	}
}
