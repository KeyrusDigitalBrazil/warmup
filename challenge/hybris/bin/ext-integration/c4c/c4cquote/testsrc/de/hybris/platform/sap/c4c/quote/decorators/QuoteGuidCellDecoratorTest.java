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
 * Unit Test for @QuoteGuidCellDecorator
 */
public class QuoteGuidCellDecoratorTest {
	private static final String QUOTE_ID_INPUT = "620001701";
	private static final String GUID = "guid";
	private static final int POSITION_INT = 1;
	private static final Integer POSITION = Integer.valueOf(POSITION_INT);

	@InjectMocks
	private final QuoteGuidCellDecorator decorator = new QuoteGuidCellDecorator();

	@Mock
	private InboundQuoteHelper inboundQuoteHelper;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void decorateTest() {
		final Map<Integer, String> srcLine = new HashMap<Integer, String>();
		srcLine.put(POSITION, QUOTE_ID_INPUT);
		when(inboundQuoteHelper.getGuid(QUOTE_ID_INPUT)).thenReturn(GUID);
		String result = decorator.decorate(POSITION_INT, srcLine);
		Assert.assertNotNull(result);
		Assert.assertEquals(GUID, result);
	}
	@Test
	public void testForNull() {
		final Map<Integer, String> srcLine = new HashMap<Integer, String>();
		srcLine.put(POSITION, "");
		String result = decorator.decorate(POSITION_INT, srcLine);
		Assert.assertNull(result);
	}
}
