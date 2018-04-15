package net.kaciras.blog.infrastructure.event.discussion;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public final class DiscussCreatedEvent extends DiscussionEvent {

	public DiscussCreatedEvent(int discussionId, int parent, int articleId, int userId) {
		super(discussionId, parent, articleId, userId);
	}
}
