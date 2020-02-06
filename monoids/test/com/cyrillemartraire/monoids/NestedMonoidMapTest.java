package com.cyrillemartraire.monoids;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class NestedMonoidMapTest {

	@Test
	public void equals() throws Exception {
		final NestedMonoidMap config = config1();
		assertEquals(config, config);
	}

	@Test
	public void neutral() throws Exception {
		final NestedMonoidMap config1 = config1();
		final NestedMonoidMap config = config1.append(config1.neutral());
		assertEquals(config1, config);
	}

	@Test
	public void append() throws Exception {
		final NestedMonoidMap config = config1().append(config2());
		assertEquals(expected(), config);
	}

	private NestedMonoidMap config1() {
		final Map<String, Monoid<?>> map = new HashMap<>();
		map.put("COLOR", new LastWinsString("RED"));
		map.put("TIMEOUT", new MinNumber(25));
		map.put("ENABLE", new AndBoolean(false));
		map.put("MASTERCONF", new ConcatenativeString("Config1"));
		return new NestedMonoidMap(map);
	}

	private NestedMonoidMap config2() {
		final Map<String, Monoid<?>> map = new HashMap<>();
		map.put("COLOR", new LastWinsString("BLUE"));
		map.put("TIMEOUT", new MinNumber(35));
		map.put("ENABLE", new AndBoolean(true));
		map.put("USER", new ConcatenativeString("Cyrille"));
		return new NestedMonoidMap(map);
	}

	private NestedMonoidMap expected() {
		final Map<String, Monoid<?>> map = new HashMap<>();
		map.put("COLOR", new LastWinsString("BLUE"));
		map.put("TIMEOUT", new MinNumber(25));
		map.put("ENABLE", new AndBoolean(false));
		map.put("MASTERCONF", new ConcatenativeString("Config1"));
		map.put("USER", new ConcatenativeString("Cyrille"));
		return new NestedMonoidMap(map);
	}

	public static class NestedMonoidMap implements Monoid<NestedMonoidMap> {
		private final Map<String, Monoid<?>> config;

		public NestedMonoidMap() {
			this(new HashMap<>());
		}

		public NestedMonoidMap(Map<String, Monoid<?>> config) {
			this.config = config;
		}

		/**
		 * @return A new map with values merged with the other map, where the
		 *         values are merged their own way.
		 */
		@SuppressWarnings({ "rawtypes", "unchecked" })
		@Override
		public NestedMonoidMap append(NestedMonoidMap other) {
			final Map<String, Monoid<?>> map = new HashMap<>(config);
			for (String key : other.config.keySet()) {
				final Monoid value = config.get(key);
				final Monoid otherValue = other.config.get(key);
				map.put(key, value == null ? otherValue : (Monoid) value.append(otherValue));
			}
			return new NestedMonoidMap(map);
		}

		@Override
		public NestedMonoidMap neutral() {
			return new NestedMonoidMap();
		}

		@Override
		public int hashCode() {
			return config.hashCode();
		}

		@Override
		public boolean equals(Object o) {
			final NestedMonoidMap other = (NestedMonoidMap) o;
			return config.equals(other.config);
		}

		@Override
		public String toString() {
			return config.toString();
		}
	}

	/**
	 * A structure with an associative append operation and a neutral element.
	 */
	public static interface Monoid<T> {
		T append(T other);

		T neutral();
	}

	public static class ConcatenativeString implements Monoid<ConcatenativeString> {

		private final String s;

		public ConcatenativeString(String s) {
			this.s = s;
		}

		@Override
		public ConcatenativeString append(ConcatenativeString other) {
			return new ConcatenativeString(s + other.s);
		}

		@Override
		public ConcatenativeString neutral() {
			return new ConcatenativeString("");
		}

		@Override
		public int hashCode() {
			return s.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			final ConcatenativeString other = (ConcatenativeString) obj;
			return s.equals(other.s);
		}

		@Override
		public String toString() {
			return "ConcatenativeString(" + s + ")";
		}
	}

	public static class LastWinsString implements Monoid<LastWinsString> {

		private final String s;

		public LastWinsString(String s) {
			this.s = s;
		}

		@Override
		public LastWinsString append(LastWinsString other) {
			return new LastWinsString(other.s);
		}

		@Override
		public LastWinsString neutral() {
			return new LastWinsString("");
		}

		@Override
		public int hashCode() {
			return s.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			final LastWinsString other = (LastWinsString) obj;
			return s.equals(other.s);
		}

		@Override
		public String toString() {
			return "LastWinsString(" + s + ")";
		}

	}

	public static class MinNumber implements Monoid<MinNumber> {

		private final int v;

		public MinNumber(int v) {
			this.v = v;
		}

		@Override
		public MinNumber append(MinNumber other) {
			return new MinNumber(Math.min(v, other.v));
		}

		@Override
		public MinNumber neutral() {
			return new MinNumber(Integer.MIN_VALUE);
		}

		@Override
		public int hashCode() {
			return v ^ 31;
		}

		@Override
		public boolean equals(Object obj) {
			final MinNumber other = (MinNumber) obj;
			return v == other.v;
		}

		@Override
		public String toString() {
			return "MinNumber(" + v + ")";
		}

	}

	public static class AndBoolean implements Monoid<AndBoolean> {

		private final boolean b;

		public AndBoolean(boolean v) {
			this.b = v;
		}

		@Override
		public AndBoolean append(AndBoolean other) {
			return new AndBoolean(b && other.b);
		}

		@Override
		public AndBoolean neutral() {
			return new AndBoolean(true);
		}

		@Override
		public int hashCode() {
			return b ? 1 : -1;
		}

		@Override
		public boolean equals(Object obj) {
			final AndBoolean other = (AndBoolean) obj;
			return b == other.b;
		}

		@Override
		public String toString() {
			return "AndBoolean(" + b + ")";
		}

	}
}
