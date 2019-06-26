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

import de.hybris.platform.core.model.user.UserModel;


/**
 * Strategy to help return the current user that is acting on a quote
 */
public interface QuoteUserIdentificationStrategy
{

	/**
	 * Gets the current quote user. By default this will be the current session user.
	 *
	 * @return the current quote user
	 */
	UserModel getCurrentQuoteUser();
}
