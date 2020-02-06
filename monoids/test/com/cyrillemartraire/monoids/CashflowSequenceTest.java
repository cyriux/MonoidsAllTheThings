package com.cyrillemartraire.monoids;

import static java.lang.Double.doubleToLongBits;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Currency;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

public class CashflowSequenceTest {

	@Test
	public void test() {
		final LocalDate expiry = LocalDate.parse("2018-07-19");
		final LocalDate month1 = LocalDate.parse("2018-06-19");
		final LocalDate month2 = LocalDate.parse("2018-05-19");
		final Currency ccy = Currency.getInstance("EUR");
		CashflowSequence reimbursement = new CashflowSequence(new Cashflow(10000, ccy, expiry));
		CashflowSequence interests = new CashflowSequence(new Cashflow(120, ccy, month1),
				new Cashflow(120, ccy, month2), new Cashflow(120, ccy, expiry));

		CashflowSequence expected = new CashflowSequence(new Cashflow(120, ccy, month1), new Cashflow(120, ccy, month2),
				new Cashflow(120, ccy, expiry), new Cashflow(10000, ccy, expiry));
		assertEquals(expected, reimbursement.add(interests));
	}

	/** A sequence of ordered cashflows with an addition operation */
	public static class CashflowSequence implements Iterable<Cashflow> {
		private final List<Cashflow> cashflows;

		public final static CashflowSequence EMPTY = new CashflowSequence();

		public final CashflowSequence add(CashflowSequence... sequences) {
			return add(Arrays.asList(sequences));
		}

		public final CashflowSequence add(Iterable<CashflowSequence> cashFlows) {
			final List<Cashflow> all = new ArrayList<>(this.cashflows);
			for (CashflowSequence seq : cashFlows) {
				all.addAll(seq.cashflows);
			}
			return new CashflowSequence(all);
		}

		public CashflowSequence(Cashflow... cashflows) {
			this(asList(cashflows));
		}

		public CashflowSequence(List<Cashflow> cashflows) {
			this.cashflows = cashflows;
			Collections.sort(this.cashflows);
		}

		/* You may also consider extending with more behavior, e.g.
		 *   // random access
		 * boolean isEmpty()
		 * int size()
		 * Cashflow get(int index)
		 * stream()
		 * 
		 *  // space vector
		 * CashflowSequence multiply(double factor)
		 * CashflowSequence opposite()
		 * 
		 *  // dates convenience
		 * List<Date> allDates() 
		 * Date firstDate()
		 * Date lastDate()
		 * Range<Date> getDateRange()
		 * CashflowSequence truncateWithin(Range<Date> range)
		 */

		@Override
		public Iterator<Cashflow> iterator() {
			return cashflows.iterator();
		}

		@Override
		public int hashCode() {
			return cashflows.hashCode();
		}

		@Override
		public boolean equals(Object o) {
			CashflowSequence other = (CashflowSequence) o;
			return cashflows.equals(other.cashflows);
		}

		@Override
		public String toString() {
			return cashflows.toString();
		}

	}

	public static class Cashflow implements Comparable<Cashflow> {
		private final double amount;
		private final Currency currency;
		private final LocalDate date;

		public Cashflow(double amount, Currency currency, LocalDate date) {
			this.amount = amount;
			this.currency = currency;
			this.date = date;
		}

		public Cashflow add(Cashflow other) {
			if (!date.equals(other.date)) {
				throw new IllegalArgumentException("Can only add at same date: " + date + "<>" + other.date);
			}
			if (!currency.equals(other.currency)) {
				throw new IllegalArgumentException("Can only add same currencies " + currency + "<>" + other.currency);
			}
			return new Cashflow(amount + other.amount, currency, date);
		}

		@Override
		public int hashCode() {
			return (int) (31 ^ doubleToLongBits(amount) + currency.hashCode() ^ date.hashCode());
		}

		@Override
		public boolean equals(Object o) {
			Cashflow other = (Cashflow) o;
			return doubleToLongBits(amount) == doubleToLongBits(other.amount) && currency.equals(other.currency)
					&& date.equals(other.date);
		}

		@Override
		public int compareTo(Cashflow o) {
			if (date.equals(o.date)) {
				return (int) (amount - o.amount);
			}
			return date.compareTo(o.date);
		}

		@Override
		public String toString() {
			return amount + " " + currency + " on " + date;
		}

	}

}
