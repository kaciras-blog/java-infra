package net.kaciras.blog.infrastructure.event.category;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryMovedEvent extends CategoryEvent {

	private final int oldParent;
	private final int newParent;

	public CategoryMovedEvent(int id, int oldParent, int newParent) {
		super(id);
		this.oldParent = oldParent;
		this.newParent = newParent;
	}
}
