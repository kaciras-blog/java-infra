package net.kaciras.blog.infrastructure.event.article;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ArticleUpdatedEvent extends ArticleEvent {

	private int draftId;
	private Integer category;

	public ArticleUpdatedEvent(int articleId, int draftId, Integer category) {
		super(articleId);
		this.draftId = draftId;
		this.category = category;
	}
}
