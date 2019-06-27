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

import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.hybris.platform.sap.c4c.quote.inbound.InboundQuoteHelper;

/**
 * Unit Test for @QuoteEntryProductCellDecorator
 */
public class QuoteEntryProductCellDecoratorTest{
	private static final String INPUT = "620001701|PROD01";
	private static final String QUOTE_ID = "620001701";
	private static final String PRODUCT_ID = "PROD01";
	private static final String PRODUCT_PK = "7387387398313";
	private static final int POSITION_INT = 1;
	private static final Integer POSITION = Integer.valueOf(POSITION_INT);

	@InjectMocks
	private final QuoteEntryProductCellDecorator decorator = new QuoteEntryProductCellDecorator();

	@Mock
	private InboundQuoteHelper inboundQuoteHelper;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void decorateTest() {
		final Map<Integer, String> srcLine = new HashMap<Integer, String>();
		srcLine.put(POSITION, INPUT);
		when(inboundQuoteHelper.createQuoteEntryProduct(QUOTE_ID, PRODUCT_ID)).thenReturn(PRODUCT_PK);
		String result = decorator.decorate(POSITION_INT, srcLine);
		Assert.assertNotNull(result);
		Assert.assertEquals(PRODUCT_PK, result);
	}
	@Test
	public void testForNull() {
		final Map<Integer, String> srcLine = new HashMap<Integer, String>();
		srcLine.put(POSITION, "' '|' '");
		String result = decorator.decorate(POSITION_INT, srcLine);
		Assert.assertNull(result);
	}
}
