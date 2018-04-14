package net.kaciras.blog.infrastructure.message;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@EqualsAndHashCode(of = "eventId")
abstract class Event implements Serializable {

	private final UUID eventId = UUID.randomUUID();

	private final LocalDateTime createdTime = LocalDateTime.now();
}
