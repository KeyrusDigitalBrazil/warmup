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
package de.hybris.platform.integrationservices.integrationkey.impl;

import de.hybris.platform.integrationservices.integrationkey.IntegrationKeyCalculationException;
import de.hybris.platform.integrationservices.integrationkey.IntegrationKeyMetadataGenerator;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemAttributeModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Required;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

public class DefaultMapToIntegrationKeyGenerator<T extends IntegrationObjectItemModel, E extends Map<String, Object>>
		extends AbstractIntegrationKeyGenerator<T, E>
{
	private IntegrationKeyMetadataGenerator integrationKeyMetadataGenerator;

	final Pattern pattern = Pattern.compile("/Date\\((\\d+)\\)/");

	@Override
	public String generate(final T integrationObjectItemModel, final E entry)
	{
		Preconditions.checkArgument(integrationObjectItemModel != null, "Cannot calculate integration key value for null integrationObjectItemModel");
		Preconditions.checkArgument(entry != null, "Cannot calculate integration key value for null entry");

		final String integrationKeyMetadata = getIntegrationKeyMetadataGenerator().generateKeyMetadata(integrationObjectItemModel);
		final Map<String, List<String>> sortedAliasEntityTypeKeys = extractKeyPropertyReferencesFromAlias(integrationKeyMetadata);
		final Map<String, List<String>> integrationKeyValues = Maps.newLinkedHashMap();

		calcKeyInternal(integrationObjectItemModel, entry, sortedAliasEntityTypeKeys, integrationKeyValues);

		return setKeyValuesToString(integrationKeyValues, sortedAliasEntityTypeKeys);
	}

	@Override
	protected Optional<String> findMatchingNavigationPropertyIn(final T type, final String entityName)
	{
		return type.getAttributes()
				.stream()
				.filter(attributeModel -> attributeModel.getReturnIntegrationObjectItem() != null)
				.filter(attributeModel -> attributeModel.getReturnIntegrationObjectItem().getCode().equals(entityName))
				.map(IntegrationObjectItemAttributeModel::getAttributeName)
				.findFirst();
	}

	@Override
	protected void populateKeyValueFromNavigationProperty(final T type,
			final E entry,
			final Map<String, List<String>> aliasComponents,
			final Map<String, List<String>> integrationKeyValues,
			final String propertyName)
	{
		final Object value = entry.get(propertyName);
		final IntegrationObjectItemModel relatedType =
				type.getAttributes()
						.stream()
						.filter(attributeModel -> attributeModel.getReturnIntegrationObjectItem() != null)
						.filter(attributeModel -> attributeModel.getAttributeName().equals(propertyName))
						.map(IntegrationObjectItemAttributeModel::getReturnIntegrationObjectItem)
						.findFirst()
						.orElseThrow(() -> new IntegrationKeyCalculationException("No Related Type found"));

		calcKeyInternal((T) relatedType, (E) value, aliasComponents, integrationKeyValues);
	}

	@Override
	protected void addToKeyValueMap(final Map<String, List<String>> integrationKeyValues, final T type, final E entry, final String propertyName)
	{
		if (entry.containsKey(propertyName))
		{
			final Object attributeValue = getProperty(entry, propertyName);
			addToKeyValues(integrationKeyValues, getTypeCode(type), transformValueToString(attributeValue));
		}
	}

	@Override
	protected String getTypeCode(final T type)
	{
		return type.getCode();
	}

	@Override
	protected Object getProperty(final E entry, final String propertyName)
	{
		return entry.get(propertyName);
	}

	@Override
	protected String transformValueToString(final Object attributeValue)
	{
		if (attributeValue != null && attributeValue instanceof String && pattern.matcher((String) attributeValue).matches())
		{
			final String date = attributeValue.toString();
			return date.substring(6, date.length() - 2);
		}
		return String.valueOf(attributeValue);
	}

	@Required
	public void setIntegrationKeyMetadataGenerator(final IntegrationKeyMetadataGenerator integrationKeyMetadataGenerator)
	{
		this.integrationKeyMetadataGenerator = integrationKeyMetadataGenerator;
	}

	protected IntegrationKeyMetadataGenerator getIntegrationKeyMetadataGenerator()
	{
		return this.integrationKeyMetadataGenerator;
	}
}
