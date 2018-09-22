package net.kaciras.blog.infrastructure.event;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(of = "eventId")
public abstract class Event implements Serializable {

	private String eventId;

	private LocalDateTime createdTime = LocalDateTime.now();
}
