package net.kaciras.blog.infrastructure.event.discussion;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class DiscussionRestoreEvent extends DiscussionEvent {
	public DiscussionRestoreEvent(int discussionId, int parent, int articleId, int userId) {
		super(discussionId, parent, articleId, userId);
	}
}
