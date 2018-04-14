package net.kaciras.blog.infrastructure.event.category;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CategoryRemovedEvent extends CategoryEvent {

	private final int parent;

	public CategoryRemovedEvent(int id, int parent) {
		super(id);
		this.parent = parent;
	}
}
