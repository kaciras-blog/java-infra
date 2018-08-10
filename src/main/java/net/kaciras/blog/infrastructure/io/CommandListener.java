package net.kaciras.blog.infrastructure.io;

import lombok.extern.slf4j.Slf4j;
import net.kaciras.blog.infrastructure.io.SelectLoop;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

@Slf4j
public class CommandListener {

	private ServerSocketChannel serverChannel;
	private Runnable onShutdown;

	public CommandListener(int port) throws IOException {
		serverChannel = ServerSocketChannel.open();
		serverChannel.bind(new InetSocketAddress(port));
		serverChannel.configureBlocking(false);
	}

	public void start() throws IOException {
		SelectLoop.getLoop().register(serverChannel, SelectionKey.OP_ACCEPT, this::acceptSocket);
	}

	private void acceptSocket(SelectionKey key) {
		try {
			SocketChannel socketChannel = serverChannel.accept();
			socketChannel.configureBlocking(false);
			SelectLoop.getLoop().register(socketChannel, SelectionKey.OP_READ, this::handleCommand, 30000);
		} catch (IOException e) {
			logger.error("无法接受连接", e);
		}
	}

	private void handleCommand(SelectionKey key) {
		try {
			SocketChannel channel = (SocketChannel) key.channel();
			key.cancel();
			channel.configureBlocking(true);
			Scanner scanner = new Scanner(channel.socket().getInputStream());
			String line = scanner.nextLine();

			if(line.equals("SHUTDOWN")) {
				if(onShutdown != null) onShutdown.run();
			}
			channel.close();
		} catch (IOException e) {
			logger.error("读取命令时出错", e);
		}
	}

	public void onShutdown(Runnable callback) {
		this.onShutdown = callback;
	}
}
