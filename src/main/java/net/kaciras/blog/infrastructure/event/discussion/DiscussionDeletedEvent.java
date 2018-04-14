package net.kaciras.blog.infrastructure.event.discussion;


public class DiscussionDeletedEvent extends DiscussionEvent {
	public DiscussionDeletedEvent(int discussionId, int parent, int articleId, int userId) {
		super(discussionId, parent, articleId, userId);
	}
}
