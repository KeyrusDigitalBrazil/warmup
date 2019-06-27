/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.accountsummaryaddon.document;

import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.accountsummaryaddon.model.B2BDocumentModel;

import java.util.stream.IntStream;

import org.joda.time.LocalDateTime;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class B2BDocumentDueDateRangePredicateTest
{

	@Mock
	private NumberOfDayRange dateRange;

	@InjectMocks
	private B2BDocumentDueDateRangePredicate b2BDocumentDueDateRangePredicate;

	@Mock
	private B2BDocumentModel document;

	@Test
	public void testNonB2DocumentModel()
	{
		final Object doc = new Object();
		Assert.assertFalse(b2BDocumentDueDateRangePredicate.evaluate(doc));
	}

	@Test
	public void testWhenMaxBoundaryIsNull()
	{
		// Assuming MinBoundary = today - 5 days
		when(dateRange.getMinBoundary()).thenReturn(Integer.valueOf(5));
		when(dateRange.getMaxBoundary()).thenReturn(null);

		when(document.getDueDate()).thenReturn(null);
		Assert.assertFalse(b2BDocumentDueDateRangePredicate.evaluate(document));

		when(document.getDueDate()).thenReturn(LocalDateTime.now().plusWeeks(1).toDate());
		Assert.assertFalse(b2BDocumentDueDateRangePredicate.evaluate(document));

		when(document.getDueDate()).thenReturn(LocalDateTime.now().minusDays(6).toDate());
		Assert.assertTrue(b2BDocumentDueDateRangePredicate.evaluate(document));

		when(document.getDueDate()).thenReturn(LocalDateTime.now().minusDays(5).toDate());
		Assert.assertTrue(b2BDocumentDueDateRangePredicate.evaluate(document));
	}

	@Test
	public void testWhenMaxBoundaryIsNotNull()
	{
		// Assuming MinBoundary = today - 5 days
		when(dateRange.getMinBoundary()).thenReturn(Integer.valueOf(5));
		when(dateRange.getMaxBoundary()).thenReturn(Integer.valueOf(6));

		when(document.getDueDate()).thenReturn(null);
		Assert.assertFalse(b2BDocumentDueDateRangePredicate.evaluate(document));

		when(document.getDueDate()).thenReturn(LocalDateTime.now().plusDays(1).toDate());
		Assert.assertFalse(b2BDocumentDueDateRangePredicate.evaluate(document));

		when(document.getDueDate()).thenReturn(LocalDateTime.now().minusDays(4).toDate());
		Assert.assertFalse(b2BDocumentDueDateRangePredicate.evaluate(document));

		when(document.getDueDate()).thenReturn(LocalDateTime.now().minusDays(6).toDate());
		Assert.assertTrue(b2BDocumentDueDateRangePredicate.evaluate(document));

		when(document.getDueDate()).thenReturn(LocalDateTime.now().minusDays(5).toDate());
		Assert.assertTrue(b2BDocumentDueDateRangePredicate.evaluate(document));
	}

	@Test
	public void testEqualBoundaries()
	{
		when(dateRange.getMinBoundary()).thenReturn(Integer.valueOf(5));
		when(dateRange.getMaxBoundary()).thenReturn(Integer.valueOf(5));

		when(document.getDueDate()).thenReturn(null);
		Assert.assertFalse(b2BDocumentDueDateRangePredicate.evaluate(document));

		when(document.getDueDate()).thenReturn(LocalDateTime.now().plusDays(1).toDate());
		Assert.assertFalse(b2BDocumentDueDateRangePredicate.evaluate(document));

		when(document.getDueDate()).thenReturn(LocalDateTime.now().minusDays(6).toDate());
		Assert.assertFalse(b2BDocumentDueDateRangePredicate.evaluate(document));

		when(document.getDueDate()).thenReturn(LocalDateTime.now().minusDays(4).toDate());
		Assert.assertFalse(b2BDocumentDueDateRangePredicate.evaluate(document));

		when(document.getDueDate()).thenReturn(LocalDateTime.now().minusDays(5).toDate());
		Assert.assertTrue(b2BDocumentDueDateRangePredicate.evaluate(document));
	}

	@Test
	public void testUnusualBoundaries()
	{
		// What if maxBoundary == minBoundary ...
		when(dateRange.getMinBoundary()).thenReturn(Integer.valueOf(5));
		when(dateRange.getMaxBoundary()).thenReturn(Integer.valueOf(4));

		IntStream.rangeClosed(-10, 10).forEach(i -> {
			when(document.getDueDate()).thenReturn(LocalDateTime.now().minusDays(i).toDate());
			Assert.assertFalse(b2BDocumentDueDateRangePredicate.evaluate(document));
		});

		// Or even negative values ...
		when(dateRange.getMinBoundary()).thenReturn(Integer.valueOf(-5));
		when(dateRange.getMaxBoundary()).thenReturn(Integer.valueOf(5));

		IntStream.rangeClosed(-5, 5).forEach(i -> {
			when(document.getDueDate()).thenReturn(LocalDateTime.now().minusDays(i).toDate());
			Assert.assertTrue(b2BDocumentDueDateRangePredicate.evaluate(document));
		});

		IntStream.range(-10, -5).forEach(i -> {
			when(document.getDueDate()).thenReturn(LocalDateTime.now().minusDays(i).toDate());
			Assert.assertFalse(b2BDocumentDueDateRangePredicate.evaluate(document));
		});

		IntStream.rangeClosed(6, 10).forEach(i -> {
			when(document.getDueDate()).thenReturn(LocalDateTime.now().minusDays(i).toDate());
			Assert.assertFalse(b2BDocumentDueDateRangePredicate.evaluate(document));
		});
	}


}
