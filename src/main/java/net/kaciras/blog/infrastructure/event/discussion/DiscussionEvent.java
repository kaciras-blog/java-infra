package net.kaciras.blog.infrastructure.event.discussion;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.kaciras.blog.infrastructure.message.DomainEvent;

@RequiredArgsConstructor
@Getter
public abstract class DiscussionEvent extends DomainEvent {

	private final int discussionId;
	private final int parent;

	private final int articleId;
	private final int userId;
}
