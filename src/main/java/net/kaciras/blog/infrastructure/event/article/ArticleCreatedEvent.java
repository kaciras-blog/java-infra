package net.kaciras.blog.infrastructure.event.article;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ArticleCreatedEvent extends ArticleEvent {

	private int draftId;
	private int category;

	public ArticleCreatedEvent(int article, int draftId, int category) {
		super(article);
		this.draftId = draftId;
		this.category = category;
	}
}
