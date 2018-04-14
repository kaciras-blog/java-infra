package net.kaciras.blog.infrastructure.event.article;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.kaciras.blog.infrastructure.message.DomainEvent;

@Getter
public abstract class ArticleEvent extends DomainEvent {

	private final int articleId;

	ArticleEvent(int articleId) {
		this.articleId = articleId;
	}
}
