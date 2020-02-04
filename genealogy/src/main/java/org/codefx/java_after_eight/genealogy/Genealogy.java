package org.codefx.java_after_eight.genealogy;

import org.codefx.java_after_eight.article.Article;
import org.codefx.java_after_eight.genealogist.Genealogist;
import org.codefx.java_after_eight.genealogist.TypedRelation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

public class Genealogy {

	private final Collection<Article> articles;
	private final Collection<Genealogist> genealogists;
	private final Weights weights;

	public Genealogy(Collection<Article> articles, Collection<Genealogist> genealogists, Weights weights) {
		this.articles = requireNonNull(articles);
		this.genealogists = requireNonNull(genealogists);
		this.weights = requireNonNull(weights);
	}

	public Stream<Relation> inferRelations() {
		return aggregateTypedRelations(inferTypedRelations());
	}

	private Stream<Relation> aggregateTypedRelations(Stream<TypedRelation> typedRelations) {
		Map<Article, Map<Article, Collection<TypedRelation>>> sortedTypedRelations = new HashMap<>();
		typedRelations.forEach(relation -> sortedTypedRelations
				.computeIfAbsent(relation.article1(), __ -> new HashMap<>())
				.computeIfAbsent(relation.article2(), __ -> new ArrayList<>())
				.add(relation));
		return sortedTypedRelations
				.values().stream()
				.flatMap(articleWithRelations -> articleWithRelations.values().stream())
				.map(relations -> Relation.aggregate(relations.stream(), weights));
	}

	private Stream<TypedRelation> inferTypedRelations() {
		record Articles(Article article1,Article article2) {}
		record ArticleResearch(Genealogist genealogist,Articles articles) {}
		return articles.stream()
				.flatMap(article1 -> articles.stream()
						.map(article2 -> new Articles(article1, article2)))
				// no need to compare articles with themselves
				.filter(articles -> articles.article1 != articles.article2)
				.flatMap(articles -> genealogists.stream()
						.map(genealogist -> new ArticleResearch(genealogist, articles)))
				.map(research -> research.genealogist()
						.infer(research.articles().article1(), research.articles().article2()));
	}

}
