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
package de.hybris.platform.integrationservices.service;

public class AttributeDescriptorNotFoundException extends RuntimeException
{
	public AttributeDescriptorNotFoundException(final String integrationObjectCode, final String integrationObjectItemCode,
			final String integrationObjectItemAttributeName)
	{
		super(String.format("Property [%s] is required for EntityType [%s] in IntegrationObject [%s].",
				integrationObjectItemAttributeName, integrationObjectItemCode, integrationObjectCode));
	}
}
