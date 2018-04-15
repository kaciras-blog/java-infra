package net.kaciras.blog.infrastructure.event.article;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Getter
public class ArticleUpdatedEvent extends ArticleEvent {

	private int draftId;
	private List<Integer> categories;

	public ArticleUpdatedEvent(int articleId, int draftId, List<Integer> categories) {
		super(articleId);
		this.draftId = draftId;
		this.categories = categories;
	}
}
