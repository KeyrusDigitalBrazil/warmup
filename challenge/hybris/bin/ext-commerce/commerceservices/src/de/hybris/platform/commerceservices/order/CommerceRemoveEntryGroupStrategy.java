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

import de.hybris.platform.commerceservices.service.data.RemoveEntryGroupParameter;

import javax.annotation.Nonnull;


/**
 * A strategy interface for adding products to cart cart.
 */
public interface CommerceRemoveEntryGroupStrategy
{
	/**
	 * Removes from the (existing) {@link de.hybris.platform.core.model.order.CartModel} the (existing) {@link de.hybris.platform.core.order.EntryGroup}
	 * If an entry with the given entry group already exists in the cart, it will be removed too.
	 *
	 * @param parameter - A parameter object containing all attributes needed for remove entry group
	 *	<P>
	 *      {@link RemoveEntryGroupParameter#cart} - The user's cart in session
	 *      {@link RemoveEntryGroupParameter#entryGroupNumber} - The number of the entry group to be removed from the cart
	 *      {@link RemoveEntryGroupParameter#enableHooks} - Are hooks enabled
	 *           </P>
	 * @return the cart modification data that includes a statusCode and the actual entry group number removed from the cart
	 * @throws CommerceCartModificationException
	 *            if related cart entry wasn't removed
	 */
	@Nonnull
	CommerceCartModification removeEntryGroup(@Nonnull final RemoveEntryGroupParameter parameter) throws CommerceCartModificationException;
}
