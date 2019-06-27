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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.sap.c4c.quote.inbound.InboundQuoteCommentConversionHelper;
import de.hybris.platform.sap.c4c.quote.inbound.InboundQuoteHelper;
import de.hybris.platform.servicelayer.user.UserService;

/**
 * JUnit for @QuoteCommentCellDecorator
 */
@UnitTest
public class QuoteCommentCellDecoratorTest {

	private static final String USER = "testemail@test.com";
	private static final String COMMENT_INPUT = "620001701|More discount was expected|"+USER;
	private static final String VERSION_INPUT = "620001701";
	private static final int COMMENT_POSITION_INT = 1;
	private static final String COMMENT_CODE = "0000ED4";
	private static final Integer COMMENT_POSITION = Integer.valueOf(COMMENT_POSITION_INT);

	@InjectMocks
	private final QuoteCommentCellDecorator decorator = new QuoteCommentCellDecorator();

	@Mock
	private InboundQuoteCommentConversionHelper inboundQuoteCommentConversionHelper;

	@Mock
	private InboundQuoteHelper inboundQuoteHelper;
	
	@Mock
	private UserService userService;
	
	@Mock
	private UserModel userModel;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void decorateTest() {
		final Map<Integer, String> srcLine = new HashMap<Integer, String>();
		srcLine.put(COMMENT_POSITION, COMMENT_INPUT);
		when(userService.getUserForUID(USER)).thenReturn(userModel);
		when(inboundQuoteCommentConversionHelper.createHeaderComment(VERSION_INPUT, "More discount was expected",USER)).thenReturn(COMMENT_CODE);
		String resultComment = decorator.decorate(COMMENT_POSITION_INT, srcLine);
		Assert.assertNotNull(resultComment);
		Assert.assertEquals(COMMENT_CODE, resultComment);
	}

}
