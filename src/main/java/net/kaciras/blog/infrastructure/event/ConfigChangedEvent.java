package net.kaciras.blog.infrastructure.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.kaciras.blog.infrastructure.message.DomainEvent;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ConfigChangedEvent extends DomainEvent {

	private String key;
	private String oldValue;
	private String newValue;
}
