package net.kaciras.blog.infrastructure.event.article;

import lombok.Getter;

import java.util.List;

@Getter
public class ArticleUpdatedEvent extends ArticleEvent {

	private final int draftId;
	private final List<Integer> categories;

	public ArticleUpdatedEvent(int articleId, int draftId, List<Integer> categories) {
		super(articleId);
		this.draftId = draftId;
		this.categories = categories;
	}
}
