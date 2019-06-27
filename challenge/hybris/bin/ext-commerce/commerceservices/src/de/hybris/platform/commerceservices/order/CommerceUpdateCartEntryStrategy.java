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
package de.hybris.platform.commerceservices.order;

import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;


public interface CommerceUpdateCartEntryStrategy
{

	/**
	 * Update quantity for cart entry.
	 *
	 * @param parameters
	 *           the parameters
	 * @return the commerce cart modification
	 * @throws CommerceCartModificationException
	 *            the commerce cart modification exception
	 */
	CommerceCartModification updateQuantityForCartEntry(final CommerceCartParameter parameters)
			throws CommerceCartModificationException;

	/**
	 * Update point of service for cart entry.
	 *
	 * @param parameters
	 *           the parameters
	 * @return the commerce cart modification
	 * @throws CommerceCartModificationException
	 *            the commerce cart modification exception
	 */
	CommerceCartModification updatePointOfServiceForCartEntry(final CommerceCartParameter parameters)
			throws CommerceCartModificationException;

	/**
	 * Update to shipping mode for cart entry.
	 *
	 * @param parameters
	 *           the parameters
	 * @return the commerce cart modification
	 * @throws CommerceCartModificationException
	 *            the commerce cart modification exception
	 */
	CommerceCartModification updateToShippingModeForCartEntry(final CommerceCartParameter parameters)
			throws CommerceCartModificationException;
}
