package net.kaciras.blog.infrastructure.event.role;

public class RoleRemovedEvent extends RoleEvent {
	public RoleRemovedEvent(int roleId) {
		super(roleId);
	}
}
