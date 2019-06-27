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
package de.hybris.platform.commerceservices.order.hook.impl;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.enums.QuoteAction;
import de.hybris.platform.commerceservices.order.strategies.QuoteUpdateStateStrategy;
import de.hybris.platform.commerceservices.order.strategies.QuoteUserIdentificationStrategy;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.commerceservices.service.data.CommerceOrderResult;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.user.UserService;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@Ignore
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CommercePlaceQuoteOrderMethodHookTest
{
	@InjectMocks
	private final CommercePlaceQuoteOrderMethodHook commercePlaceQuoteOrderMethodHook = new CommercePlaceQuoteOrderMethodHook();

	@Mock
	private QuoteUpdateStateStrategy quoteUpdateStateStrategy;

	@Mock
	private QuoteUserIdentificationStrategy quoteUserIdentificationStrategy;

	@Mock
	private CommerceOrderResult commerceOrderResult;

	@Mock
	private OrderModel orderModel;

	@Mock
	private UserService userService;

	@Test
	public void shouldSetQuoteAsOrderedForQuoteBasedOrder()
	{
		final QuoteModel quoteModel = new QuoteModel();
		final UserModel userModel = new UserModel();

		given(quoteUserIdentificationStrategy.getCurrentQuoteUser()).willReturn(userModel);
		given(commerceOrderResult.getOrder()).willReturn(orderModel);
		given(orderModel.getQuoteReference()).willReturn(quoteModel);

		commercePlaceQuoteOrderMethodHook.afterPlaceOrder(mock(CommerceCheckoutParameter.class), commerceOrderResult);

		verify(quoteUpdateStateStrategy).updateQuoteState(QuoteAction.ORDER, quoteModel, userModel);
	}

	@Test
	public void shouldNotTriggerUpdatesOnRegularOrder()
	{
		given(commerceOrderResult.getOrder()).willReturn(orderModel);

		commercePlaceQuoteOrderMethodHook.afterPlaceOrder(mock(CommerceCheckoutParameter.class), commerceOrderResult);

		verify(quoteUpdateStateStrategy, never()).updateQuoteState(any(), any(), any());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldCheckOrderArgumentAvailability()
	{
		commercePlaceQuoteOrderMethodHook.afterPlaceOrder(mock(CommerceCheckoutParameter.class), commerceOrderResult);
	}
}
