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

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.order.QuoteService;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;

/**
 * JUnit for DefaultInboundQuoteVersionControlHelper
 */
@UnitTest
public class DefaultInboundQuoteVersionControlHelperTest {
	@Mock
	private QuoteService quoteService;
	@Mock
	private QuoteModel quote;

	private static String CODE = "6010022";

	private static int VERSION = 1;

	@InjectMocks
	private final DefaultInboundQuoteVersionControlHelper helper = new DefaultInboundQuoteVersionControlHelper();

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void getQuoteForCode() {
		when(quoteService.getCurrentQuoteForCode(CODE)).thenReturn(quote);
		QuoteModel result = helper.getQuoteforCode(CODE);
		Assert.assertEquals(quote, result);
	}

	@Test
	public void shouldGetQuoteForCode() {
		doThrow(ModelNotFoundException.class).when(quoteService).getCurrentQuoteForCode(CODE);
		QuoteModel result = helper.getQuoteforCode(CODE);
		Assert.assertNull(result);
	}

	@Test
	public void getUpdatedVersion() {
		when(quote.getVersion()).thenReturn(VERSION);
		int result = helper.getUpdatedVersionNumber(quote);
		Assert.assertEquals(VERSION+1, result);
	}

}
