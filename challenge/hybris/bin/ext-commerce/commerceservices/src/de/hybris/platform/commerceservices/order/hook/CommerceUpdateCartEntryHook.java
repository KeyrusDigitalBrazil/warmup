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
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;

public interface CommerceUpdateCartEntryHook
{
	/**
	 *  Executed after commerce update cart entry
	 *
	 * @param parameter
	 * @param result
	 */
	void afterUpdateCartEntry(CommerceCartParameter parameter, CommerceCartModification result);

	/**
	 *
	 * Executed before commerce update cart entry
	 *
	 * @param parameter
	 */
	void beforeUpdateCartEntry(CommerceCartParameter parameter);

}
