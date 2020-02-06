package com.cyrillemartraire.monoids;

import static java.util.Arrays.stream;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class AverageTest {

	@Test
	public void singleValue() throws Exception {
		assertEquals(2, Average.of(2).average(), 0.0001);
	}
	
	@Test
	public void basicAverage() throws Exception {
		assertEquals(2, Average.of(1, 2, 3).average(), 0.0001);
	}

	@Test
	public void neutralElement() throws Exception {
		assertEquals(Average.of(1, 2, 3), Average.of(1, 2, 3).add(Average.NEUTRAL));
	}

	@Test
	public void combiningAverages() throws Exception {
		final Average combinedAverages = Average.of(1, 2, 3).add(Average.of(4, 6));
		assertEquals(5, combinedAverages.count());
		assertEquals(16. / 5., combinedAverages.average(), 0.0001);
	}

	/** An average that composes well */
	public static class Average {

		private final int count;
		private final int sum;

		public static final Average NEUTRAL = new Average(0, 0);

		public static final Average of(int... values) {
			return new Average(values.length, stream(values).sum());
		}

		private Average(int count, int sum) {
			this.count = count;
			this.sum = sum;
		}

		public double average() {
			return (double) sum / count;
		}

		public int count() {
			return count;
		}

		public Average add(Average other) {
			return new Average(count + other.count, sum + other.sum);
		}

		@Override
		public int hashCode() {
			return 31 + count ^ sum;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (!(obj instanceof Average)) {
				return false;
			}
			Average other = (Average) obj;
			return count == other.count && sum == other.sum;
		}

		@Override
		public String toString() {
			return " " + sum + " / " + sum + " = " + average();
		}

	}
}
