package net.kaciras.blog.infrastructure.event.role;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class RoleRemovedEvent extends RoleEvent {
	public RoleRemovedEvent(int roleId) {
		super(roleId);
	}
}
