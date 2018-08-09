package net.kaciras.blog.infrastructure.event.discussion;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.kaciras.blog.infrastructure.event.DomainEvent;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public abstract class DiscussionEvent extends DomainEvent {

	private int discussionId;
	private int parent;

	private int articleId;
	private int userId;
}
