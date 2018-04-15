package net.kaciras.blog.infrastructure.event.article;

import lombok.Getter;
import lombok.NoArgsConstructor;
import net.kaciras.blog.infrastructure.message.DomainEvent;

@NoArgsConstructor
@Getter
public abstract class ArticleEvent extends DomainEvent {

	private int articleId;

	ArticleEvent(int articleId) {
		this.articleId = articleId;
	}
}
