package net.kaciras.blog.infrastructure.event.article;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Getter
public class ArticleCreatedEvent extends ArticleEvent {

	private int draftId;
	private List<Integer> categories;

	public ArticleCreatedEvent(int article, int draftId, List<Integer> categories) {
		super(article);
		this.draftId = draftId;
		this.categories = categories;
	}
}
