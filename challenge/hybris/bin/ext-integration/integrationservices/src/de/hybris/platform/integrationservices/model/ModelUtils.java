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

package de.hybris.platform.integrationservices.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utilities used in integration object models.
 */
public class ModelUtils
{
	private static final Logger LOGGER = LoggerFactory.getLogger(ModelUtils.class);

	private ModelUtils()
	{}

	/**
	 * Indicates if an attribute is unique. If either the IntegrationObjectionAttributeDefinitionModel OR
	 * the model's attribute descriptor is unique, the attribute is unique
	 *
	 * @param attributeDefinitionModel Model to test for uniqueness
	 * @return true if unique, else false
	 */
	public static boolean isUnique(final IntegrationObjectItemAttributeModel attributeDefinitionModel)
	{
		final boolean modelUnique = falseIfNull(attributeDefinitionModel.getUnique());
		final boolean descriptorUnique = attributeDefinitionModel.getAttributeDescriptor() != null &&
				falseIfNull(attributeDefinitionModel.getAttributeDescriptor().getUnique());

		logWarningIfUniqueValuesConflict(attributeDefinitionModel, modelUnique, descriptorUnique);

		return modelUnique || descriptorUnique;
	}

	private static boolean falseIfNull(final Boolean value)
	{
		if( value == null )
		{
			return false;
		}
		return value;
	}

	/**
	 * Log a warning if the descriptor's unique flag is true, but the model's unique flag is false
	 *
	 * @param modelUnique Indicates if the attribute definition model is unique
	 * @param descriptorUnique Indicates if the attribute descriptor for this model is unique
	 */
	private static void logWarningIfUniqueValuesConflict(final IntegrationObjectItemAttributeModel attributeDefinitionModel, final boolean modelUnique, final boolean descriptorUnique)
	{
		if(descriptorUnique && !modelUnique && LOGGER.isDebugEnabled())
		{
			LOGGER.debug("The property '{}' has a value of unique='true' in the Type System which cannot be overridden.", attributeName(attributeDefinitionModel));
		}
	}

	private static String attributeName(final IntegrationObjectItemAttributeModel attributeDefinitionModel)
	{
		return attributeDefinitionModel.getIntegrationObjectItem() != null ?
				String.format("%s:%s", attributeDefinitionModel.getIntegrationObjectItem().getCode(), attributeDefinitionModel.getAttributeName()) :
				attributeDefinitionModel.getAttributeName();
	}
}
