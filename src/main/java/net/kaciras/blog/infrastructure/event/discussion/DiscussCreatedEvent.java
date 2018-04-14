package net.kaciras.blog.infrastructure.event.discussion;

import lombok.Getter;
import lombok.Setter;
import net.kaciras.blog.infrastructure.message.DomainEvent;

@Getter
public final class DiscussCreatedEvent extends DiscussionEvent {

	public DiscussCreatedEvent(int discussionId, int parent, int articleId, int userId) {
		super(discussionId, parent, articleId, userId);
	}
}
