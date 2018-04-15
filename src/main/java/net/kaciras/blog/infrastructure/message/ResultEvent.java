package net.kaciras.blog.infrastructure.message;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public final class ResultEvent extends Event {

	private UUID sourceId;

	private List<Exception> errors;
}
