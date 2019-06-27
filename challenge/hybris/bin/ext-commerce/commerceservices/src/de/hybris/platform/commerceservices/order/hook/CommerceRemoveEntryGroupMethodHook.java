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
package de.hybris.platform.commerceservices.order.hook;

import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.service.data.RemoveEntryGroupParameter;

import javax.annotation.Nonnull;

public interface CommerceRemoveEntryGroupMethodHook
{
	/**
	 *  Executed after commerce remove entry group
	 *
	 * @param parameter
	 * @param result
	 */
	void afterRemoveEntryGroup(@Nonnull final RemoveEntryGroupParameter parameter, CommerceCartModification result);

	/**
	 *
	 * Executed before commerce remove entry gtoup
	 *
	 * @param parameter
	 */
	void beforeRemoveEntryGroup(@Nonnull final RemoveEntryGroupParameter parameter) throws CommerceCartModificationException;

}
