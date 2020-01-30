package org.codefx.demo.java_after_eight.genealogy;

import java.util.HashMap;
import java.util.Map;

public class Weights {

	private final Map<RelationType, Double> weights;
	private final double defaultWeight;

	private Weights(Map<RelationType, Double> weights, double defaultWeight) {
		// REFACTOR 10: Map::copyOf
		this.weights = new HashMap<>(weights);
		if (this.weights.entrySet().stream().anyMatch(entry -> entry.getKey() == null || entry.getValue() == null))
			throw new NullPointerException("Neither relation type nor weight can be null.");
		this.defaultWeight = defaultWeight;
	}

	public static Weights from(Map<RelationType, Double> weights, double defaultWeight) {
		return new Weights(weights, defaultWeight);
	}

	public static Weights allEqual() {
		return new Weights(new HashMap<>(), 1);
	}

	public double weightOf(RelationType genealogistType) {
		return weights.getOrDefault(genealogistType, defaultWeight);
	}

}
