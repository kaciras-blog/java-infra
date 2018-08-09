package net.kaciras.blog.infrastructure.event;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public final class ResultEvent extends Event {

	private UUID sourceId;

	private List<Exception> errors;
}
