package net.kaciras.blog.infrastructure.event.role;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.kaciras.blog.infrastructure.message.DomainEvent;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public abstract class RoleEvent extends DomainEvent {

	private int roleId;
}
