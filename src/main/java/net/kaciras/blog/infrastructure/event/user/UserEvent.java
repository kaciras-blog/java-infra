package net.kaciras.blog.infrastructure.event.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public abstract class UserEvent {

	private int userId;
}
