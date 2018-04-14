package net.kaciras.blog.infrastructure.message;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class ResultEvent extends Event {

	private final UUID sourceId;

	private final List<Exception> errors;
}
