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
package de.hybris.platform.sap.c4c.quote.inbound.impl;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
import de.hybris.platform.order.QuoteService;
import de.hybris.platform.servicelayer.model.ModelService;

@UnitTest
public class DefaultInboundQuoteHelperTest
{
	@InjectMocks
	private DefaultInboundQuoteHelper inboundQuoteHelper = new DefaultInboundQuoteHelper();
	
	@Mock
	private QuoteService quoteService;
	@Mock
	private ModelService modelService;
	
	@Before
   public void setUp() {
       MockitoAnnotations.initMocks(this);
   }
	
	@Test
	public void testCreateQuoteSnapshot()
	{
		QuoteModel quote = mock(QuoteModel.class);
		QuoteModel newQuoteSnapshot = mock(QuoteModel.class);
		when(quoteService.getCurrentQuoteForCode(Mockito.anyString())).thenReturn(quote);
		when(quoteService.createQuoteSnapshot(quote, QuoteState.SELLER_REQUEST)).thenReturn(newQuoteSnapshot);
		
		doNothing().when(modelService).save(Mockito.anyObject());
		
		QuoteModel resultQuote = inboundQuoteHelper.createQuoteSnapshot("12345", "SELLER_REQUEST");
		
		Assert.assertNotNull(resultQuote);
		Assert.assertEquals(newQuoteSnapshot, resultQuote);
		
	}
	
	@Test
	public void testCreateQuoteSnapshotWithoutQuote()
	{
		QuoteModel quote = null;
		when(quoteService.getCurrentQuoteForCode(Mockito.anyString())).thenReturn(quote);
		
		QuoteModel resultQuote = inboundQuoteHelper.createQuoteSnapshot("12345", "SELLER_REQUEST");
		
		Assert.assertNull(resultQuote);		
	}

}
