package org.codefx.java_after_eight.genealogy;

import org.codefx.java_after_eight.article.Article;
import org.codefx.java_after_eight.genealogist.TypedRelation;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

import static java.lang.Math.round;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.averagingDouble;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.teeing;
import static org.codefx.java_after_eight.Utils.collectEqualElement;

public class Relation {

	private final Article article1;
	private final Article article2;
	private final long score;

	Relation(Article article1, Article article2, long score) {
		this.article1 = requireNonNull(article1);
		this.article2 = requireNonNull(article2);
		this.score = score;

		if (score < 0 || 100 < score)
			throw new IllegalArgumentException("Score should be in interval [0; 100]: " + toString());
	}

	static Relation aggregate(Stream<TypedRelation> typedRelations, Weights weights) {
		return typedRelations.collect(
				teeing(
						mapping(
								rel -> new Article[]{ rel.article1(), rel.article2() },
								collectEqualElement(Arrays::equals)),
						averagingDouble(rel -> rel.score() * weights.weightOf(rel.type())),
						(articles, score) -> articles.map(arts -> new Relation(arts[0], arts[1], round(score)))
				))
				.orElseThrow(() -> new IllegalArgumentException("Can't create relation from zero typed relations."));
	}

	public Article article1() {
		return article1;
	}

	public Article article2() {
		return article2;
	}

	public long score() {
		return score;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Relation relation = (Relation) o;
		return score == relation.score &&
				article1.equals(relation.article1) &&
				article2.equals(relation.article2);
	}

	@Override
	public int hashCode() {
		return Objects.hash(article1, article2, score);
	}

	@Override
	public String toString() {
		return "Relation{" +
				"article1=" + article1.slug().value() +
				", article2=" + article2.slug().value() +
				", score=" + score +
				'}';
	}

}