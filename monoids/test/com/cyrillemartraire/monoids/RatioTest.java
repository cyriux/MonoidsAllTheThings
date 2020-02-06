package com.cyrillemartraire.monoids;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class RatioTest {

	@Test
	public void testNeutral() {
		assertEquals(new Ratio(2, 1), new Ratio(2, 1).multiply(Ratio.NEUTRAL));
	}

	@Test
	public void compositionOfRatios() throws Exception {
		assertEquals(new Ratio(200, 100), new Ratio(100, 50).multiply(new Ratio(2, 2)));
		assertEquals(new Ratio(6, 2), new Ratio(2, 1).multiply(new Ratio(3, 2)));
	}

	@Test
	public void trace() throws Exception {
		assertEquals("(1/3)*(5/2)", new Ratio(1, 3).multiply(new Ratio(5, 2)).trace());
	}

	/** A ratio that compose under multiplication */
	public static class Ratio {

		private final int numerator;
		private final int denumerator;
		private final String trace;

		public static final Ratio NEUTRAL = new Ratio(1, 1, "");

		public Ratio(int numerator, int denumerator) {
			this(numerator, denumerator, "");
		}

		public Ratio(int numerator, int denumerator, String trace) {
			this.numerator = numerator;
			this.denumerator = denumerator;
			this.trace = trace;
		}

		public double ratio() {
			return numerator / denumerator;
		}

		public Ratio multiply(Ratio other) {
			return new Ratio(numerator * other.numerator, denumerator * other.denumerator,
					"(" + asString() + ")*(" + other.asString() + ")");
		}

		@Override
		public int hashCode() {
			return 31 + denumerator ^ numerator;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (!(obj instanceof Ratio)) {
				return false;
			}
			Ratio other = (Ratio) obj;
			return denumerator == other.denumerator && numerator == other.numerator;
		}

		public String asString() {
			return numerator + "/" + denumerator;
		}

		public String trace() {
			return trace;
		}

		@Override
		public String toString() {
			return asString() + " = " + ratio();
		}
	}
}
