package net.kaciras.blog.infrastructure.codec;

import org.junit.jupiter.api.Test;

import java.net.Inet4Address;
import java.net.InetAddress;

import static org.assertj.core.api.Assertions.assertThat;

final class CodecUtilsTest {

	@Test
	void toIPv6Bytes() throws Exception {
		var addr = InetAddress.getByName("127.0.0.5");
		var bytes = CodecUtils.toIPv6Bytes(addr);

		assertThat(addr).isInstanceOf(Inet4Address.class);
		assertThat(bytes).containsExactly(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0xFF, 0xFF, 127, 0, 0, 5);
	}

	@Test
	void indexOfBytes() {
		var text = "CodecUtilsTest.indexOfBytes";
		var subText = "sTest.i";

		var i = CodecUtils.indexOfBytes(text.getBytes(), subText.getBytes(), 0);
		assertThat(i).isEqualTo(text.indexOf(subText));

		var k = CodecUtils.indexOfBytes(text.getBytes(), subText.getBytes(), 12);
		assertThat(k).isEqualTo(-1);
	}

	@Test
	void isHexDigit() {
		var text = "0123456789abcdefABCDEF";
		text.chars().forEach(c -> assertThat(CodecUtils.isHexDigit((char) c)).isTrue());

		var fullWidth  = "０１２３４５６７８９ａｂｃｄｅｆＡＢＣＤＥＦ";
		fullWidth.chars().forEach(c -> assertThat(CodecUtils.isHexDigit((char) c)).isFalse());

		var nonHex = "\r\n~!@@#$^&%*() /: @gG` 测下符号和边界值";
		nonHex.chars().forEach(c -> assertThat(CodecUtils.isHexDigit((char) c)).isFalse());
	}

	// CodecUtils 里的其他方法都是抄的，不测了
}
