package net.kaciras.blog.infrastructure.event.category;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class CategoryMovedEvent extends CategoryEvent {

	private int oldParent;
	private int newParent;

	public CategoryMovedEvent(int id, int oldParent, int newParent) {
		super(id);
		this.oldParent = oldParent;
		this.newParent = newParent;
	}
}
