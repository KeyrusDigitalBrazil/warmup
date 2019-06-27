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

import de.hybris.platform.commerceservices.enums.QuoteAction;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.user.UserModel;


/**
 * Strategy for updating expiration time for a quote
 */
public interface QuoteUpdateExpirationTimeStrategy
{
	/**
	 * Updates the expiration time for a quote depending on the action that is being performed and type of the quote
	 * user. Edit & Submit actions are being handled. When a buyer performs such actions, the expiration time is set to
	 * null. When a seller Submits the quote to the seller approver and the expiration time for it is set to a date
	 * before the current date plus the minimum offer validity period in days or not set at all, the expiration time is
	 * set to the current date plus the default offer validity period in days and with the time being end of day. Edit
	 * action is not handled for seller. Please see commerceservices project.properties file for the quote offer validity
	 * period parameters.
	 *
	 * @param quoteAction
	 *           the action that is being performed
	 * @param quoteModel
	 *           the quote for which to update the expiration time
	 * @param userModel
	 *           the user performing the action on the quote
	 * @return {@link QuoteModel} updated quote model
	 * @throws IllegalArgumentException
	 *            if the quote user type is unknown
	 */
	QuoteModel updateExpirationTime(QuoteAction quoteAction, QuoteModel quoteModel, UserModel userModel);
}
