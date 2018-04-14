package net.kaciras.blog.infrastructure.message;

import io.reactivex.Single;

import java.util.function.Consumer;

public interface MessageClient {

	/**
	 * 发送一个事件，并返回一个用于通知事件完成状态的对象。
	 *
	 * @param event 事件
	 * @param <T> 事件类型
	 * @return 通知事件完成状态的对象
	 */
	<T extends DomainEvent> Single<ResultEvent> send(T event);

	/**
	 * 订阅事件，将在在收到事件时调用consumer的方法。
	 * 事件具有继承机制，订阅父类的事件能够接收到子类型事件的通知。
	 *
	 * @param type 事件的Class
	 * @param consumer 监听器，在收到事件时将被调用
	 * @param <T> 事件类型
	 */
	<T extends DomainEvent> void subscribe(Class<T> type, Consumer<T> consumer);
}
