package net.kaciras.blog.infrastructure.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.kaciras.blog.infrastructure.message.DomainEvent;

@RequiredArgsConstructor
@Getter
public class ConfigChangedEvent extends DomainEvent {

	private final String key;
	private final String oldValue;
	private final String newValue;
}
