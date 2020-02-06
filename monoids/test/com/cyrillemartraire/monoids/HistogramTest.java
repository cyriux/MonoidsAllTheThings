package com.cyrillemartraire.monoids;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

public class HistogramTest {

	private static final double EPSILON = 0.00001;
	private static final Histogram EMPTY = Histogram.empty(10);

	@Test
	public void singlePointDistribution() throws Exception {
		final Histogram point = EMPTY.withPoints(29.99);
		assertEquals(EMPTY.withPoints(21.), point);
	}

	@Test
	public void summingHistogramsSameBucket() throws Exception {
		final Histogram h1 = EMPTY.withPoints(49.99);
		final Histogram h2 = EMPTY.withPoints(41.01);
		assertEquals(EMPTY.withPoints(42., 45.), h1.add(h2));
		assertEquals(100., h1.frequencyInBin(4), 0.00001);
	}

	@Test
	public void summingHistogramsMultipleBuckets() throws Exception {
		final Histogram h1 = EMPTY.withPoints(49.99);
		final Histogram h2 = EMPTY.withPoints(100);
		final Histogram h3 = EMPTY.withPoints(62);
		final Histogram h = h1.add(h2).add(h3).add(h3);
		assertEquals(EMPTY.withPoints(41, 65, 65, 95), h);
		assertEquals(25., h.frequencyInBin(4), EPSILON);
		assertEquals(25., h.frequencyInBin(9), EPSILON);
		assertEquals(50., h.frequencyInBin(6), EPSILON);
	}


	/** https://en.wikipedia.org/wiki/Histogram */
	public static class Histogram {

		// split from 0 to 100 into N equal buckets
		private final int[] buckets;

		public final static Histogram empty(final int binsCount) {
			return new Histogram(new int[binsCount]);
		}

		private Histogram(int[] buckets) {
			this.buckets = buckets;
		}

		public final Histogram withPoints(double... values) {
			final int[] bins = new int[buckets.length];
			for (int i = 0; i < values.length; i++) {
				final double value = values[i];
				final int index = bucketFor(value);
				bins[index] += 1;
			}
			return new Histogram(bins);
		}

		private final static void isInRange(double value) {
			if (value < 0 || value > 100) {
				throw new IllegalArgumentException("Value must be between 0 and 100");
			}
		}

		protected Histogram buckets(int[] buckets) {
			if (buckets.length > size()) {
				throw new IllegalArgumentException("There must have exactly " + size() + "bucket");
			}
			return new Histogram(buckets);
		}

		private final int bucketFor(double value) {
			isInRange(value);
			return value == 100 ? 9 : (int) (value / 10);
		}

		public Histogram add(Histogram other) {
			if (buckets.length != other.buckets.length) {
				throw new IllegalArgumentException("Can only add Histograms of same size");
			}
			final int[] bins = new int[buckets.length];
			for (int i = 0; i < bins.length; i++) {
				bins[i] = buckets[i] + other.buckets[i];
			}
			return new Histogram(bins);
		}

		public int size() {
			return buckets.length;
		}

		public int count() {
			int count = 0;
			for (int i = 0; i < buckets.length; i++) {
				count += buckets[i];
			}
			return count;
		}

		public double frequencyInBin(int bucketIndex) {
			if (bucketIndex < 0 || bucketIndex >= size()) {
				throw new IllegalArgumentException("Bucket index must be between 0 and " + (size() - 1));
			}
			return 100. * buckets[bucketIndex] / count();
		}

		@Override
		public int hashCode() {
			return Arrays.hashCode(buckets) ^ count();
		}

		@Override
		public boolean equals(Object obj) {
			final Histogram other = (Histogram) obj;
			return Arrays.equals(buckets, other.buckets);
		}

		@Override
		public String toString() {
			return "Histogram " + Arrays.toString(buckets);
		}

	}
}
