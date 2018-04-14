package net.kaciras.blog.infrastructure.event.article;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

@Getter
public class ArticleCreatedEvent extends ArticleEvent {

	private final int draftId;
	private final List<Integer> categories;

	public ArticleCreatedEvent(int article, int draftId, List<Integer> categories) {
		super(article);
		this.draftId = draftId;
		this.categories = categories;
	}
}
