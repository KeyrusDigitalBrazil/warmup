/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.sap.c4c.quote.outbound.impl;

import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.sap.c4c.quote.constants.QuoteCsvColumns;

@UnitTest
public class DefaultQuoteApprovalContributorTest
{
	@InjectMocks
	private DefaultQuoteApprovalContributor approvalContributor = new DefaultQuoteApprovalContributor(); 
	
	@Before
   public void setUp() {
       MockitoAnnotations.initMocks(this);
   }

	@Test
	public void testCreateRows()
	{
		QuoteModel quote = Mockito.mock(QuoteModel.class);
		when(quote.getOrderId()).thenReturn("12345");
		when(quote.getC4cQuoteId()).thenReturn("54321");
		
		final List<Map<String, Object>> rows = approvalContributor.createRows(quote);
		Assert.assertNotNull(rows);
		Assert.assertEquals(1, rows.size());
		Assert.assertEquals("54321",rows.get(0).get(QuoteCsvColumns.C4C_QUOTE_ID));
		Assert.assertEquals("12345",rows.get(0).get(QuoteCsvColumns.ORDER_ID));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testCreateRowsWithException()
	{
		QuoteModel quote = Mockito.mock(QuoteModel.class);
		approvalContributor.createRows(quote);
		
	}
}
