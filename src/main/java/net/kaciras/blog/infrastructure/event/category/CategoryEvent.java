package net.kaciras.blog.infrastructure.event.category;

import lombok.Getter;
import lombok.Setter;
import net.kaciras.blog.infrastructure.message.DomainEvent;

@Getter
public abstract class CategoryEvent extends DomainEvent {

	private final int id;

	CategoryEvent(int id) {
		this.id = id;
	}
}
