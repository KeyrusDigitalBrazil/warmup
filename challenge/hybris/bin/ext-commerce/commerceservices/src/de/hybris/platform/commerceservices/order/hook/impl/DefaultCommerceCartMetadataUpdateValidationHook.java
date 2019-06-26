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
package de.hybris.platform.commerceservices.order.hook.impl;

import de.hybris.platform.commerceservices.order.hook.CommerceCartMetadataUpdateMethodHook;
import de.hybris.platform.commerceservices.service.data.CommerceCartMetadataParameter;

import java.util.Optional;

import org.apache.commons.lang.StringUtils;


/**
 * Validates cart metadata attributes (i.e. name, description).
 */
public class DefaultCommerceCartMetadataUpdateValidationHook implements CommerceCartMetadataUpdateMethodHook
{
	protected static final int MAX_CHARS_LIMIT = 255;

	/**
	 * Validates name and description cart metadata attributes.
	 *
	 * @param parameter
	 *           a bean holding any number of additional attributes a client may want to pass to the method
	 * @throws IllegalArgumentException
	 *            if either name or description attributes have a length greater than 255
	 */
	@Override
	public void beforeMetadataUpdate(final CommerceCartMetadataParameter parameter)
	{
		validateAttribute(parameter.getName(), "Name");
		validateAttribute(parameter.getDescription(), "Description");
	}

	protected void validateAttribute(final Optional<String> attribute, final String attributeName)
	{
		attribute.ifPresent(value -> {
			if (StringUtils.length(value) > MAX_CHARS_LIMIT)
			{
				throw new IllegalArgumentException(
						String.format("%s cannot exceed %s characters", attributeName, Integer.valueOf(MAX_CHARS_LIMIT)));
			}
		});
	}

	@Override
	public void afterMetadataUpdate(final CommerceCartMetadataParameter parameter)
	{
		//empty
	}
}
