package net.kaciras.blog.infrastructure.event.role;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Getter
public class RoleIncludeChangedEvent extends RoleEvent {

	private List<Integer> oldList;
	private List<Integer> newList;

	public RoleIncludeChangedEvent(int roleId, List<Integer> oldList, List<Integer> newList) {
		super(roleId);
		this.oldList = oldList;
		this.newList = newList;
	}
}
