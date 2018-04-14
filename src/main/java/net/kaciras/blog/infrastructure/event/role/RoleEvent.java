package net.kaciras.blog.infrastructure.event.role;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.kaciras.blog.infrastructure.message.DomainEvent;

@RequiredArgsConstructor
@Getter
public abstract class RoleEvent extends DomainEvent {

	private final int roleId;
}
