package net.kaciras.blog.infrastructure.event.discussion;


import lombok.NoArgsConstructor;

@NoArgsConstructor
public class DiscussionDeletedEvent extends DiscussionEvent {
	public DiscussionDeletedEvent(int discussionId, int parent, int articleId, int userId) {
		super(discussionId, parent, articleId, userId);
	}
}
