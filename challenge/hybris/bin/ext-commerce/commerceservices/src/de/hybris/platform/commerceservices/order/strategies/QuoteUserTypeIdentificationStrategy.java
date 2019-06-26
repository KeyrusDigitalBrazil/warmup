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
package de.hybris.platform.commerceservices.order.strategies;

import de.hybris.platform.commerceservices.enums.QuoteUserType;
import de.hybris.platform.core.model.user.UserModel;

import java.util.Optional;


/**
 * Strategy to help return the current user type of user that is acting on a quote
 */
public interface QuoteUserTypeIdentificationStrategy
{
	/**
	 * Returns the {@link QuoteUserType} of the current user.
	 *
	 * @param userModel
	 *           user for which to get the quote user type
	 * @return the {@link QuoteUserType} of the given user
	 * @throws IllegalArgumentException
	 *            if the user model is null
	 */
	Optional<QuoteUserType> getCurrentQuoteUserType(UserModel userModel);
}