package com.cyrillemartraire.monoids;

import static com.cyrillemartraire.monoids.EnvironmentalImpactTest.CertifiedAmount.certified;
import static com.cyrillemartraire.monoids.EnvironmentalImpactTest.CertifiedAmount.uncertified;
import static com.cyrillemartraire.monoids.EnvironmentalImpactTest.EnvironmentalImpact.singleSupplier;
import static java.lang.Double.doubleToLongBits;
import static java.lang.Math.abs;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class EnvironmentalImpactTest {

	@Test
	public void pizzaImpact() {
		EnvironmentalImpact cooking = singleSupplier(certified(1, "kWh", 0.3), certified(1, "T", 0.25));
		EnvironmentalImpact dough = singleSupplier(uncertified(5, "kWh", 5.), uncertified(0.5, "T", 1.));
		EnvironmentalImpact tomatoSauce = singleSupplier(uncertified(3, "kWh", 1.), certified(0.2, "T", 0.1));

		EnvironmentalImpact expectedPizza = new EnvironmentalImpact(3,
				new CertifiedAmount(new Amount(6.9, "kWh", 5.6), 1., 2.3),
				new CertifiedAmount(new Amount(1.56, "T", 1.28), 1.3, 2.3));

		EnvironmentalImpact pizza = cooking.add(dough).add(tomatoSauce.times(0.3));
		assertEquals(expectedPizza, pizza);

		System.out.println(cooking);
		System.out.println(dough);
		System.out.println(tomatoSauce);
		System.out.println(pizza);
	}

	/**
	 * The energy and carbon impacts across a supply chain
	 */
	public static class EnvironmentalImpact {

		private final int supplierCount;
		private final CertifiedAmount energyConsumption;
		private final CertifiedAmount carbonEmission;

		public static final EnvironmentalImpact neutral() {
			return new EnvironmentalImpact(0, CertifiedAmount.neutral("kWh"), CertifiedAmount.neutral("T"));
		}

		public static final EnvironmentalImpact singleSupplier(CertifiedAmount energyConsumption,
				CertifiedAmount carbonEmission) {
			return new EnvironmentalImpact(1, energyConsumption, carbonEmission);
		}

		public EnvironmentalImpact(int supplierCount, CertifiedAmount energyConsumption,
				CertifiedAmount carbonEmission) {
			this.supplierCount = supplierCount;
			this.energyConsumption = energyConsumption;
			this.carbonEmission = carbonEmission;
		}

		public EnvironmentalImpact add(EnvironmentalImpact other) {
			return new EnvironmentalImpact(supplierCount + other.supplierCount,
					energyConsumption.add(other.energyConsumption), carbonEmission.add(other.carbonEmission));
		}

		public EnvironmentalImpact add(double coefficient, EnvironmentalImpact other) {
			return add(other.times(coefficient));
		}

		public EnvironmentalImpact times(double coefficient) {
			return new EnvironmentalImpact(supplierCount, energyConsumption.times(coefficient),
					carbonEmission.times(coefficient));
		}

		@Override
		public int hashCode() {
			return 31 ^ carbonEmission.hashCode() + energyConsumption.hashCode() ^ supplierCount;
		}

		@Override
		public boolean equals(Object obj) {
			EnvironmentalImpact other = (EnvironmentalImpact) obj;
			return supplierCount == other.supplierCount && energyConsumption.equals(other.energyConsumption)
					&& carbonEmission.equals(other.carbonEmission);
		}

		@Override
		public String toString() {
			return "EnvironmentalImpact(" + supplierCount + " supplier" + (supplierCount == 1 ? "" : "s") + ", energy: "
					+ energyConsumption + ", carbon: " + carbonEmission + ")";
		}

	}

	/**
	 * An amount that keeps track of its percentage of certification
	 */
	public static class CertifiedAmount {
		private final Amount amount;
		private final double score;// the total certification score
		private final double weight; // the total weight of the certified thing

		public static final CertifiedAmount certified(Amount amount) {
			return new CertifiedAmount(amount, 1., 1.);
		}

		public static CertifiedAmount neutral(String unit) {
			return new CertifiedAmount(Amount.neutral(unit), 0., 0.);
		}

		public static final CertifiedAmount certified(double value, String unit, double errorMargin) {
			return new CertifiedAmount(new Amount(value, unit, errorMargin), 1., 1.);
		}

		public static final CertifiedAmount uncertified(double value, String unit, double errorMargin) {
			return new CertifiedAmount(new Amount(value, unit, errorMargin), 0., 1.);
		}

		public CertifiedAmount(Amount amount, double score, double weight) {
			this.amount = amount;
			this.score = score;
			this.weight = weight;
		}

		public CertifiedAmount add(CertifiedAmount other) {
			return new CertifiedAmount(amount.add(other.amount), score + other.score, weight + other.weight);
		}

		public CertifiedAmount times(double coefficient) {
			return new CertifiedAmount(amount.times(coefficient), coefficient * score, coefficient);
		}

		@Override
		public int hashCode() {
			return (int) (amount.hashCode() ^ doubleToLongBits(score) ^ doubleToLongBits(weight));
		}

		@Override
		public boolean equals(Object obj) {
			CertifiedAmount other = (CertifiedAmount) obj;
			return amount.equals(other.amount) && abs(score - other.score) <= 0.01
					&& abs(weight - other.weight) <= 0.01;
		}

		@Override
		public String toString() {
			return amount + " (" + ((int) (score * 100. / weight)) + "% certified)";
		}
	}

	/**
	 * An amount of a physical quantity, with its unit, margin of error and
	 * percentage of certification
	 */
	public static class Amount {
		private final double value;
		private final String unit;
		private final double errorMargin;

		public static final Amount neutral(String unit) {
			return new Amount(0., unit, 0.);
		}

		public Amount(double value, String unit, double errorMargin) {
			this.value = value;
			this.unit = unit;
			this.errorMargin = errorMargin;
		}

		public Amount add(double coefficient, Amount other) {
			return this.add(other.times(coefficient));
		}

		public Amount add(Amount other) {
			if (!unit.equals(other.unit))
				throw new IllegalArgumentException(
						"Cannot add amounts of different units: " + unit + " <> " + other.unit);
			return new Amount(value + other.value, unit, errorMargin + other.errorMargin);
		}

		public Amount times(double coefficient) {
			return new Amount(coefficient * value, unit, coefficient * errorMargin);
		}

		@Override
		public int hashCode() {
			return (int) (31 ^ doubleToLongBits(errorMargin) + unit.hashCode() ^ doubleToLongBits(value));
		}

		@Override
		public boolean equals(Object obj) {
			Amount other = (Amount) obj;
			return abs(value - other.value) <= 0.01 && unit.equals(other.unit)
					&& abs(errorMargin - other.errorMargin) <= 0.1;
		}

		@Override
		public String toString() {
			return value + (errorMargin == 0. ? "" : "+/-" + errorMargin) + " " + unit;
		}
	}

}
