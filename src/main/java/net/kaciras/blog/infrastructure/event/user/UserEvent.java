package net.kaciras.blog.infrastructure.event.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public abstract class UserEvent {

	private final int userId;
}
