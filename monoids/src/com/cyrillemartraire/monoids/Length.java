package com.cyrillemartraire.monoids;

import static java.lang.Double.doubleToLongBits;

public class Length {
	private final double length;

	public final static Length ZERO = new Length(0.);

	public Length(double length) {
		if (length < 0) {
			throw new IllegalArgumentException("Length must be positive");
		}
		this.length = length;
	}

	public Length add(Length other) {
		return new Length(length + other.length);
	}

	@Override
	public int hashCode() {
		return (int) (31 ^ doubleToLongBits(length));
	}

	@Override
	public boolean equals(Object o) {
		Length other = (Length) o;
		return doubleToLongBits(length) == doubleToLongBits(other.length);
	}

	@Override
	public String toString() {
		return length + "kg";
	}

}
