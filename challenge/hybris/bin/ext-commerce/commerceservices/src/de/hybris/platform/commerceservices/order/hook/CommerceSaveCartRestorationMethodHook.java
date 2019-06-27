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

import de.hybris.platform.commerceservices.order.CommerceCartRestorationException;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;


/**
 * A hook strategy to run custom code before restoring a cart
 */
public interface CommerceSaveCartRestorationMethodHook
{

	/**
	 * Execute custom logic before restoring a cart
	 *
	 * @param parameters
	 *           a {@link CommerceCartParameter} parameter object
	 * @throws CommerceCartRestorationException
	 *
	 */
	void beforeRestoringCart(CommerceCartParameter parameters) throws CommerceCartRestorationException;


	/**
	 * Execute custom logic after restoring a cart
	 *
	 * @param parameters
	 *           a {@link CommerceCartParameter} parameter object
	 * @throws CommerceCartRestorationException
	 *
	 */
	void afterRestoringCart(CommerceCartParameter parameters) throws CommerceCartRestorationException;
}
