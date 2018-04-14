package net.kaciras.blog.infrastructure.bootstarp;

import net.kaciras.blog.infrastructure.io.SelectLoop;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

class SelectLoopTest {

	@Test
	void test() throws Exception {
		SelectLoop loop = SelectLoop.getLoop();
		ServerSocketChannel ssc = ServerSocketChannel.open();
		ssc.configureBlocking(false);
		ssc.bind(new InetSocketAddress(0));

		AtomicBoolean ab = new AtomicBoolean();
		loop.register(ssc, SelectionKey.OP_ACCEPT, k -> {
			if(k == null) ab.set(true);
		}, 500);

		Thread.sleep(1000);
		Assertions.assertTrue(ab.get());
		ssc.close();
	}

	@Test
	void test2() throws Exception {
		SelectLoop loop = SelectLoop.getLoop();
		ServerSocketChannel ssc = ServerSocketChannel.open();
		ssc.configureBlocking(false);
		ssc.bind(new InetSocketAddress(61429));

		AtomicBoolean ab = new AtomicBoolean();
		AtomicReference<Object> exception = new AtomicReference<>();

		loop.register(ssc, SelectionKey.OP_ACCEPT, k -> {
			try {
				SocketChannel channel = ssc.accept();
				channel.configureBlocking(false);
				loop.register(channel, SelectionKey.OP_READ, k1 -> ab.set(true));
			} catch (IOException e) {
				exception.set(e);
			}
		});

		SocketChannel sender = SocketChannel.open();
		sender.configureBlocking(false);
		sender.connect(new InetSocketAddress("localhost", 61429));
		sender.finishConnect();
		loop.register(sender, SelectionKey.OP_WRITE, k -> {
			try {
				sender.write(ByteBuffer.wrap(new byte[8]));
				k.cancel();
			} catch (IOException e) {
				exception.set(e);
			}
		});

		Thread.sleep(1000);
		Assertions.assertNull(exception.get());
		Assertions.assertTrue(ab.get());
		ssc.close();
	}

	@Test
	void testAddr() throws UnknownHostException {
		byte[] data = new byte[16];
		data[10] = data[11] = (byte) 0xFF;
		Random random = new Random();
		byte[] a = new byte[4];
		random.nextBytes(a);
		System.arraycopy(a,0,data,12,4);

		InetAddress address = InetAddress.getByAddress(data);
		System.out.println(address);
	}
}
