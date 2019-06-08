package net.kaciras.blog.infrastructure.codec;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.Inet4Address;
import java.net.InetAddress;

final class CodecUtilsTest {

	@Test
	void toIPv6Bytes() throws Exception {
		var addr = InetAddress.getByName("127.0.0.5");
		var bytes = CodecUtils.toIPv6Bytes(addr);

		Assertions.assertThat(addr).isInstanceOf(Inet4Address.class);
		Assertions.assertThat(bytes).containsExactly(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0xFF, 0xFF, 127, 0, 0, 5);
	}

	// 其他方法直接抄别人的，不测了
}
