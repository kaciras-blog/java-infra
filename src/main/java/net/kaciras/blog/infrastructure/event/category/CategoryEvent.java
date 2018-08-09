package net.kaciras.blog.infrastructure.event.category;

import lombok.Getter;
import lombok.NoArgsConstructor;
import net.kaciras.blog.infrastructure.event.DomainEvent;

@NoArgsConstructor
@Getter
public abstract class CategoryEvent extends DomainEvent {

	private int id;

	CategoryEvent(int id) {
		this.id = id;
	}
}
