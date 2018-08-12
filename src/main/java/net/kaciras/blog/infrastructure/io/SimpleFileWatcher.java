package net.kaciras.blog.infrastructure.io;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Slf4j
public class SimpleFileWatcher implements AutoCloseable {

	private final WatchService watcher;
	private final Thread thread;

	private volatile boolean running = true;

	private final Map<WatchKey, Consumer<WatchEvent>> listeners = new ConcurrentHashMap<>();

	public SimpleFileWatcher() {
		try {
			watcher = FileSystems.getDefault().newWatchService();
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}

		thread = new Thread(this::watchLoop, "FileWatcher");
		thread.setDaemon(true);
		thread.setUncaughtExceptionHandler((t, e) -> logger.error("未检查的错误", e));
		thread.start();
	}

	private void watchLoop() {
		try {
			while (running) {
				var key = watcher.take();
				var listener = listeners.get(key);
				key.pollEvents().forEach(listener);
			}
		} catch (InterruptedException ex) {
			Thread.interrupted();
		} catch (ClosedWatchServiceException ignore) {
			logger.trace("文件监视服务在等待事件时被关闭");
		}
		logger.debug("监视循环线程结束");
	}

	public void register(Path path, Consumer<WatchEvent> callback,
						 WatchEvent.Kind<?>... events) throws IOException {
		listeners.put(path.register(watcher, events), callback);
	}

	@Override
	public void close() throws Exception {
		running = false;
		thread.interrupt();
		watcher.close();
	}
}
