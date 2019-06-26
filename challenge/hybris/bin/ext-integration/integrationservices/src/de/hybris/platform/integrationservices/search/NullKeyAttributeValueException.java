/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */

package de.hybris.platform.integrationservices.search;

import de.hybris.platform.integrationservices.model.IntegrationObjectItemAttributeModel;

import java.util.Map;

/**
 * Indicates that an operation involving item key cannot be performed because it does have a value for the required key attribute.
 */
public class NullKeyAttributeValueException extends RuntimeException
{
	private final IntegrationObjectItemAttributeModel attribute;

	public NullKeyAttributeValueException(final IntegrationObjectItemAttributeModel attr, final Map<String, Object> item)
	{
		super("Item " + item + " does not contain value for required attribute " + describe(attr));
		attribute = attr;
	}

	private static String describe(final IntegrationObjectItemAttributeModel attr)
	{
		return attr.getIntegrationObjectItem().getCode() + "." + attr.getAttributeName();
	}

	public String getItemType()
	{
		return attribute.getIntegrationObjectItem().getCode();
	}

	public String getAttributeName()
	{
		return attribute.getAttributeName();
	}
}
