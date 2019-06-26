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
package de.hybris.platform.sap.c4c.quote.decorators;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.enums.QuoteState;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.sap.c4c.quote.inbound.InboundQuoteHelper;
import de.hybris.platform.sap.c4c.quote.inbound.InboundQuoteVersionControlHelper;

@UnitTest
public class QuoteConfirmationVersionCellDecoratorTest
{
	@InjectMocks
	private QuoteConfirmationVersionCellDecorator quoteConfirmationVersionCellDecorator = new QuoteConfirmationVersionCellDecorator();
	
	@Mock
	private InboundQuoteVersionControlHelper inboundQuoteVersionControlHelper;
	@Mock
	private InboundQuoteHelper inboundQuoteHelper;
	
	@Before
   public void setUp() {
       MockitoAnnotations.initMocks(this);
   }
	
	@Test
	public void testDecorateWithBuyerSubmittedState()
	{
		int position = 1;
		String quoteId = new String("12345");
		Map<Integer, String> impexLine = new HashMap<>();
		impexLine.put(new Integer(position), quoteId);
		QuoteModel quote = mock(QuoteModel.class);
		QuoteModel resultQuote = mock(QuoteModel.class);
		when(inboundQuoteVersionControlHelper.getQuoteforCode(Mockito.anyString())).thenReturn(quote);
		when(quote.getState()).thenReturn(QuoteState.BUYER_SUBMITTED);
		when(inboundQuoteHelper.createQuoteSnapshot(Mockito.anyString(), Mockito.anyString())).thenReturn(resultQuote);
		when(resultQuote.getState()).thenReturn(QuoteState.SELLER_REQUEST);
		when(resultQuote.getVersion()).thenReturn(new Integer(2));
		
		String resultVersion = quoteConfirmationVersionCellDecorator.decorate(position, impexLine);
		
		Assert.assertNotNull(resultVersion);
		Assert.assertEquals(resultQuote.getVersion().toString(), resultVersion);
		
	}
	
	@Test
	public void testDecorateWithCancelledState()
	{
		int position = 1;
		String quoteId = new String("12345");
		Map<Integer, String> impexLine = new HashMap<>();
		impexLine.put(new Integer(position), quoteId);
		QuoteModel quote = mock(QuoteModel.class);
		when(inboundQuoteVersionControlHelper.getQuoteforCode(Mockito.anyString())).thenReturn(quote);
		when(quote.getState()).thenReturn(QuoteState.CANCELLED);		
		when(quote.getVersion()).thenReturn(new Integer(2));
		String resultVersion = quoteConfirmationVersionCellDecorator.decorate(position, impexLine);
		
		Assert.assertNotNull(resultVersion);
		Assert.assertEquals(quote.getVersion().toString(), resultVersion);
		
	}
	
}
