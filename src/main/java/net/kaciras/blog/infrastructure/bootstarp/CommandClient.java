package net.kaciras.blog.infrastructure.bootstarp;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class CommandClient implements AutoCloseable{

	private final Socket socket;

	public CommandClient(int port) throws IOException {
		socket = new Socket("localhost", port);
	}

	public void sendCommand(String command) throws IOException {
		socket.getOutputStream().write(command.getBytes(StandardCharsets.UTF_8));
		socket.getOutputStream().write('\r');
		socket.getOutputStream().write('\n');
		socket.getOutputStream().flush();
	}

	public void waitForDisconnect(int timeout) {
		try {
			socket.setSoTimeout(timeout);
			socket.getInputStream().read();
		} catch (IOException ignore) {
			/* 等待服务端关闭连接 */
		}
	}

	@Override
	public void close() throws IOException {
		socket.close();
	}
}
