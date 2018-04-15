package net.kaciras.blog.infrastructure.event.category;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class CategoryRemovedEvent extends CategoryEvent {

	private int parent;

	public CategoryRemovedEvent(int id, int parent) {
		super(id);
		this.parent = parent;
	}
}
