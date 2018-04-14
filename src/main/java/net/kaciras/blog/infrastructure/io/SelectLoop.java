package net.kaciras.blog.infrastructure.io;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.channels.*;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.function.Consumer;

@Slf4j
public final class SelectLoop implements AutoCloseable {

	private static volatile SelectLoop instance;

	public static SelectLoop getLoop() throws IOException {
		if(instance == null) {
			synchronized (SelectLoop.class) {
				if(instance == null) instance = new SelectLoop();
			}
		}
		return instance;
	}

	private final Selector selector = Selector.open();
	private final Queue<Warpper> timeoutQueue = new PriorityBlockingQueue<>();
	private final ConcurrentLinkedQueue<Warpper> queue = new ConcurrentLinkedQueue<>();

	@Getter
	private volatile boolean running;

	private SelectLoop() throws IOException {
		running = true;
		Thread thread = new Thread(this::selectLoop, "SelectLoop");
		thread.setDaemon(true);
		thread.start();
	}

	private void selectLoop() {
		try (selector) {
			while (running) {
				selector.select(checkTimeout());
				service(selector.selectedKeys());
				addToSelector();
			}
		} catch (ClosedSelectorException ignore) {
		} catch (Exception ex) {
			log.error("主循环中发生了未捕获的错误", ex);
		}
		running = false;
		log.info("主循环线程结束");
	}

	private long checkTimeout() {
		long time = System.currentTimeMillis();
		Warpper peek;
		while ((peek = timeoutQueue.poll()) != null) {
			if (peek.period > time) {
				return peek.period - time;
			}
			SelectionKey k = peek.selectionKey;
			if (k != null && k.isValid()) {
				k.cancel();
				peek.handler.accept(null);
			}
		}
		return 0;
	}

	private void addToSelector() throws ClosedChannelException {
		Warpper w;
		while ((w = queue.poll()) != null) {
			w.selectionKey = w.channel.register(selector, w.opeartions, w);
			if (w.period > 0) {
				timeoutQueue.add(w);
			}
		}
	}

	private void service(Set<SelectionKey> keys) {
		Iterator<SelectionKey> itr = keys.iterator();
		while (itr.hasNext()) {
			SelectionKey key = itr.next();
			itr.remove();
			if (!key.isValid()) {
				continue;
			}
			Warpper w = (Warpper) key.attachment();
			w.handler.accept(key);
		}
	}

	public int keySize() {
		return selector.keys().size();
	}

	public void register(SelectableChannel channel, int opeartions, Consumer<SelectionKey> callback) {
		register(channel, opeartions, callback, 0);
	}

	public void register(SelectableChannel channel,
						 int opeartions,
						 Consumer<SelectionKey> callback,
						 long timeout) {
		long period = timeout > 0 ? System.currentTimeMillis() + timeout : 0;
		queue.add(new Warpper(channel, callback, opeartions, period));
		selector.wakeup();
	}

	public void remove(SelectableChannel channel) {
		SelectionKey key = channel.keyFor(selector);
		if (key != null) key.cancel();
	}

	@Override
	public void close() {
		running = false;
		selector.wakeup();
	}

	@RequiredArgsConstructor
	private static final class Warpper implements Comparable<Warpper> {

		private final SelectableChannel channel;
		private final Consumer<SelectionKey> handler;
		private final int opeartions;
		private final long period;

		private SelectionKey selectionKey;

		@Override
		public int compareTo(Warpper o) {
			return Long.compare(period, o.period);
		}
	}
}
