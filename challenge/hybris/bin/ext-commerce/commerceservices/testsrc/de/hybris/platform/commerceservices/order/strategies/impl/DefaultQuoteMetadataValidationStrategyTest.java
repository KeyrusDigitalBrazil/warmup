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
package de.hybris.platform.commerceservices.order.strategies.impl;

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.enums.QuoteAction;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.user.UserModel;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit test for DefaultQuoteMetadataValidationStrategy
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultQuoteMetadataValidationStrategyTest
{
	private final DefaultQuoteMetadataValidationStrategy defaultQuoteMetadataValidationStrategy = new DefaultQuoteMetadataValidationStrategy();

	@Mock
	private QuoteModel quoteModel;
	@Mock
	private UserModel userModel;

	@Test(expected = IllegalArgumentException.class)
	public void shouldValidateNullQuoteName()
	{
		given(quoteModel.getName()).willReturn(null);

		defaultQuoteMetadataValidationStrategy.validate(QuoteAction.SUBMIT, quoteModel, userModel);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldValidateEmptyQuoteName()
	{
		given(quoteModel.getName()).willReturn("");

		defaultQuoteMetadataValidationStrategy.validate(QuoteAction.SUBMIT, quoteModel, userModel);
	}

	@Test
	public void shouldValidate()
	{
		given(quoteModel.getName()).willReturn("myQuoteName");

		defaultQuoteMetadataValidationStrategy.validate(QuoteAction.SUBMIT, quoteModel, userModel);
	}
}
