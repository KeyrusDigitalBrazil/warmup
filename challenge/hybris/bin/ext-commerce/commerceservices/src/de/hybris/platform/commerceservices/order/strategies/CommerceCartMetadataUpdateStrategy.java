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

import de.hybris.platform.commerceservices.service.data.CommerceCartMetadataParameter;

import java.util.Optional;


/**
 * Strategy to update cart metadata fields (i.e. name)
 */
public interface CommerceCartMetadataUpdateStrategy
{
	/**
	 * Updates cart metadata fields (i.e. name). Most of the attributes from the input bean are of {@link Optional} type
	 * and they will only be used when a value is present for them. An empty string for an {@link Optional<String>} will
	 * trigger the field to be stored as null.
	 *
	 * @param parameter
	 *           a bean holding any number of additional attributes a client may want to pass to the method
	 */
	void updateCartMetadata(CommerceCartMetadataParameter parameter);
}
