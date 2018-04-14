package net.kaciras.blog.infrastructure.event.role;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
public class RoleIncludeChangedEvent extends RoleEvent {

	private final List<Integer> oldList;
	private final List<Integer> newList;

	public RoleIncludeChangedEvent(int roleId, List<Integer> oldList, List<Integer> newList) {
		super(roleId);
		this.oldList = oldList;
		this.newList = newList;
	}
}
