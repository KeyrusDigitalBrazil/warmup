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

import de.hybris.platform.commerceservices.service.data.CommerceCartMetadataParameter;


/**
 * A hook interface into before and after cart metadata (i.e. name, description) update lifecycle
 */
public interface CommerceCartMetadataUpdateMethodHook
{
	/**
	 * Executed before commerce cart metadata update.
	 *
	 * @param parameter
	 *           a bean holding any number of additional attributes a client may want to pass to the method
	 * @throws IllegalArgumentException
	 *            if any attributes fail validation
	 */
	void beforeMetadataUpdate(CommerceCartMetadataParameter parameter);

	/**
	 * Executed after commerce cart metadata update.
	 *
	 * @param parameter
	 *           a bean holding any number of additional attributes a client may want to pass to the method
	 */
	void afterMetadataUpdate(CommerceCartMetadataParameter parameter);
}
