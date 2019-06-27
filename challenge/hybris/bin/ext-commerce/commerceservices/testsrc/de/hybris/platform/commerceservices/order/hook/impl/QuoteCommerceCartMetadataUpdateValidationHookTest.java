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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.enums.QuoteAction;
import de.hybris.platform.commerceservices.enums.QuoteUserType;
import de.hybris.platform.commerceservices.order.exceptions.IllegalQuoteStateException;
import de.hybris.platform.commerceservices.order.strategies.QuoteActionValidationStrategy;
import de.hybris.platform.commerceservices.order.strategies.QuoteUserIdentificationStrategy;
import de.hybris.platform.commerceservices.order.strategies.QuoteUserTypeIdentificationStrategy;
import de.hybris.platform.commerceservices.service.data.CommerceCartMetadataParameter;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.time.TimeService;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class QuoteCommerceCartMetadataUpdateValidationHookTest
{
	@InjectMocks
	public QuoteCommerceCartMetadataUpdateValidationHook quoteCommerceCartMetadataUpdateValidationHook = new QuoteCommerceCartMetadataUpdateValidationHook();

	@Mock
	private QuoteActionValidationStrategy quoteActionValidationStrategy;

	@Mock
	private QuoteUserIdentificationStrategy quoteUserIdentificationStrategy;

	@Mock
	private QuoteUserTypeIdentificationStrategy quoteUserTypeIdentificationStrategy;

	@Mock
	private TimeService timeService;

	@Mock
	private CommerceCartMetadataParameter metadataParameter;

	@Mock
	private CartModel cartModel;

	@Mock
	private QuoteModel cartQuoteModel;

	@Mock
	private UserModel quoteUser;

	@Before
	public void setup()
	{
		given(cartModel.getCode()).willReturn("00000001");
		given(cartQuoteModel.getCode()).willReturn("00000001");

		given(cartModel.getQuoteReference()).willReturn(cartQuoteModel);
		given(quoteUserIdentificationStrategy.getCurrentQuoteUser()).willReturn(quoteUser);

		given(metadataParameter.getCart()).willReturn(cartModel);
	}

	@Test
	public void shouldNotUpdateWhenQuotReferenceNull()
	{
		given(cartModel.getQuoteReference()).willReturn(null);

		quoteCommerceCartMetadataUpdateValidationHook.beforeMetadataUpdate(metadataParameter);

		verify(quoteUserIdentificationStrategy, never()).getCurrentQuoteUser();
		verify(quoteActionValidationStrategy, never()).validate(any(), any(), any());
	}

	@Test(expected = IllegalQuoteStateException.class)
	public void shouldNotUpdateWhenActionValidationFails()
	{
		doThrow(new IllegalQuoteStateException(QuoteAction.SAVE, null, null, null)).when(quoteActionValidationStrategy)
				.validate(QuoteAction.SAVE, cartQuoteModel, quoteUser);

		quoteCommerceCartMetadataUpdateValidationHook.beforeMetadataUpdate(metadataParameter);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotUpdateWhenQuoteUserTypeCanNotBeDetermined()
	{
		given(quoteUserTypeIdentificationStrategy.getCurrentQuoteUserType(quoteUser)).willReturn(Optional.empty());

		quoteCommerceCartMetadataUpdateValidationHook.beforeMetadataUpdate(metadataParameter);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldValidateExpirationTimeForBuyer()
	{
		given(quoteUserTypeIdentificationStrategy.getCurrentQuoteUserType(quoteUser)).willReturn(Optional.of(QuoteUserType.BUYER));

		given(metadataParameter.getExpirationTime()).willReturn(Optional.of(DateUtils.addDays(new Date(), 1000)));

		quoteCommerceCartMetadataUpdateValidationHook.beforeMetadataUpdate(metadataParameter);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldValidateRemoveExpirationTimeForBuyer()
	{
		given(quoteUserTypeIdentificationStrategy.getCurrentQuoteUserType(quoteUser)).willReturn(Optional.of(QuoteUserType.BUYER));

		given(metadataParameter.getExpirationTime()).willReturn(Optional.empty());
		given(Boolean.valueOf(metadataParameter.isRemoveExpirationTime())).willReturn(Boolean.TRUE);

		quoteCommerceCartMetadataUpdateValidationHook.beforeMetadataUpdate(metadataParameter);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldValidateExpirationTimeForSeller()
	{
		given(quoteUserTypeIdentificationStrategy.getCurrentQuoteUserType(quoteUser)).willReturn(Optional.of(QuoteUserType.SELLER));
		given(timeService.getCurrentDateWithTimeNormalized()).willReturn(DateUtils.truncate(new Date(), Calendar.DATE));
		given(metadataParameter.getExpirationTime()).willReturn(Optional.of(DateUtils.addDays(new Date(), -100)));

		quoteCommerceCartMetadataUpdateValidationHook.beforeMetadataUpdate(metadataParameter);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldValidateNameForSeller()
	{
		given(quoteUserTypeIdentificationStrategy.getCurrentQuoteUserType(quoteUser)).willReturn(Optional.of(QuoteUserType.SELLER));
		given(timeService.getCurrentDateWithTimeNormalized()).willReturn(DateUtils.truncate(new Date(), Calendar.DATE));
		given(metadataParameter.getExpirationTime()).willReturn(Optional.of(DateUtils.addDays(new Date(), 1000)));
		given(metadataParameter.getName()).willReturn(Optional.of("myQuoteName"));

		quoteCommerceCartMetadataUpdateValidationHook.beforeMetadataUpdate(metadataParameter);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldValidateDescriptionForSeller()
	{
		given(quoteUserTypeIdentificationStrategy.getCurrentQuoteUserType(quoteUser)).willReturn(Optional.of(QuoteUserType.SELLER));
		given(timeService.getCurrentDateWithTimeNormalized()).willReturn(DateUtils.truncate(new Date(), Calendar.DATE));
		given(metadataParameter.getExpirationTime()).willReturn(Optional.of(DateUtils.addDays(new Date(), 1000)));
		given(metadataParameter.getName()).willReturn(Optional.empty());
		given(metadataParameter.getDescription()).willReturn(Optional.of("myQuoteName"));

		quoteCommerceCartMetadataUpdateValidationHook.beforeMetadataUpdate(metadataParameter);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldValidateSellerApprover()
	{
		given(quoteUserTypeIdentificationStrategy.getCurrentQuoteUserType(quoteUser))
				.willReturn(Optional.of(QuoteUserType.SELLERAPPROVER));

		quoteCommerceCartMetadataUpdateValidationHook.beforeMetadataUpdate(metadataParameter);
	}

	@Test
	public void shouldValidateBuyer()
	{
		given(quoteUserTypeIdentificationStrategy.getCurrentQuoteUserType(quoteUser)).willReturn(Optional.of(QuoteUserType.BUYER));

		given(metadataParameter.getExpirationTime()).willReturn(Optional.empty());
		given(Boolean.valueOf(metadataParameter.isRemoveExpirationTime())).willReturn(Boolean.FALSE);

		quoteCommerceCartMetadataUpdateValidationHook.beforeMetadataUpdate(metadataParameter);
	}

	@Test
	public void shouldValidateSeller()
	{
		given(quoteUserTypeIdentificationStrategy.getCurrentQuoteUserType(quoteUser)).willReturn(Optional.of(QuoteUserType.SELLER));
		given(timeService.getCurrentDateWithTimeNormalized()).willReturn(DateUtils.truncate(new Date(), Calendar.DATE));
		given(metadataParameter.getExpirationTime()).willReturn(Optional.of(DateUtils.addDays(new Date(), 1000)));
		given(metadataParameter.getName()).willReturn(Optional.empty());
		given(metadataParameter.getDescription()).willReturn(Optional.empty());

		quoteCommerceCartMetadataUpdateValidationHook.beforeMetadataUpdate(metadataParameter);
	}
}
