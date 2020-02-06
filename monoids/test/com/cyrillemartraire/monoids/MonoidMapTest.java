package com.cyrillemartraire.monoids;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class MonoidMapTest {

	@Test
	public void equals() throws Exception {
		final MonoidMap config = config1();
		assertEquals(config, config);
	}

	@Test
	public void neutral() throws Exception {
		final MonoidMap config1 = config1();
		final MonoidMap config = config1.append(config1.neutral());
		assertEquals(config1, config);
	}

	@Test
	public void append() throws Exception {
		final MonoidMap config = config1().append(config2());
		assertEquals(config2().append(config1bis()), config);
	}

	private MonoidMap config1() {
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("COLOR", "RED");
		map.put("TIMEOUT", 25);
		map.put("ENABLE", false);
		map.put("MASTERCONF", "Config1");
		return new MonoidMap(map);
	}

	private MonoidMap config2() {
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("COLOR", "BLUE");
		map.put("TIMEOUT", 35);
		map.put("ENABLE", true);
		map.put("USER", "Cyrille");
		return new MonoidMap(map);
	}

	private MonoidMap config1bis() {
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("MASTERCONF", "Config1");
		return new MonoidMap(map);
	}

	/**
	 * A monoidal map with an operation defined so that the left-hand value
	 * always wins (overwrites)
	 */
	public static class MonoidMap {
		private final Map<String, Object> config;

		public MonoidMap() {
			this(new HashMap<String, Object>());
		}

		public MonoidMap(Map<String, Object> config) {
			this.config = config;
		}

		/**
		 * @return A new map with values merged with the other map, where the
		 *         other map values win. It's associative but not commutative.
		 */
		public MonoidMap append(MonoidMap other) {
			final Map<String, Object> map = new HashMap<String, Object>(config);
			map.putAll(other.config);
			return new MonoidMap(map);
		}

		public MonoidMap neutral() {
			return new MonoidMap();
		}

		@Override
		public int hashCode() {
			return config.hashCode();
		}

		@Override
		public boolean equals(Object o) {
			final MonoidMap other = (MonoidMap) o;
			return config.equals(other.config);
		}

		@Override
		public String toString() {
			return config.toString();
		}
	}
}
