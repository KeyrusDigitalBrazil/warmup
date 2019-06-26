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
package de.hybris.platform.odata2services.odata.integrationkey.impl;

import static de.hybris.platform.odata2services.odata.EdmAnnotationUtils.getAliasTextIfPresent;

import de.hybris.platform.integrationservices.integrationkey.IntegrationKeyCalculationException;
import de.hybris.platform.integrationservices.integrationkey.impl.AbstractIntegrationKeyGenerator;

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.odata2.api.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.edm.EdmEntityType;
import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.edm.EdmNavigationProperty;
import org.apache.olingo.odata2.api.edm.EdmTyped;
import org.apache.olingo.odata2.api.ep.entry.ODataEntry;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

public class DefaultODataEntryToIntegrationKeyGenerator extends AbstractIntegrationKeyGenerator<EdmEntitySet, ODataEntry>
{
	@Override
	public String generate(final EdmEntitySet entitySet, final ODataEntry oDataEntry)
	{
		try
		{
			Preconditions.checkArgument(entitySet != null, "Cannot calculate integration key value for null edm entity set");
			Preconditions.checkArgument(oDataEntry != null, "Cannot calculate integration key value for null oDataEntry");
			Preconditions.checkArgument(entitySet.getEntityType() != null, "Cannot calculate integration key value for null entity type");

			final String aliasAttributeText = getAliasTextIfPresent(entitySet.getEntityType().getKeyProperties());

			if (!aliasAttributeText.isEmpty())
			{
				// Using LinkedHashMap and LinkedHashSet to maintain the original order defined in the alias string for each entityType's integrationKey property
				final Map<String, List<String>> sortedAliasEntityTypeKeys = extractKeyPropertyReferencesFromAlias(aliasAttributeText);
				final Map<String, List<String>> integrationKeyValues = Maps.newLinkedHashMap();

				calcKeyInternal(entitySet, oDataEntry, sortedAliasEntityTypeKeys, integrationKeyValues);
				return setKeyValuesToString(integrationKeyValues, sortedAliasEntityTypeKeys);
			}
			return StringUtils.EMPTY;
		}
		catch (final EdmException e)
		{
			throw new IntegrationKeyCalculationException(e);
		}
	}

	@Override
	protected Optional<String> findMatchingNavigationPropertyIn(final EdmEntitySet entitySet, final String entityName)
	{
		try
		{
			final EdmEntityType entityType = entitySet.getEntityType();
			return entityType.getNavigationPropertyNames()
					.stream()
					.map(name -> getNavigationProperty(entityType, name))
					.filter(prop -> getNavigationPropertyToRole(prop).equalsIgnoreCase(entityName))
					.map(this::getNavigationPropertyName)
					.findFirst();
		}
		catch (final EdmException e)
		{
			throw new IntegrationKeyCalculationException(e);
		}
	}

	protected EdmNavigationProperty getNavigationProperty(final EdmEntityType entityType, final String name)
	{
		try
		{
			return (EdmNavigationProperty) entityType.getProperty(name);
		}
		catch (final EdmException e)
		{
			throw new IntegrationKeyCalculationException(e);
		}
	}

	protected String getNavigationPropertyToRole(final EdmNavigationProperty property)
	{
		try
		{
			return property.getToRole();
		}
		catch (final EdmException e)
		{
			throw new IntegrationKeyCalculationException(e);
		}
	}

	protected String getNavigationPropertyName(final EdmNavigationProperty property)
	{
		try
		{
			return property.getName();
		}
		catch (final EdmException e)
		{
			throw new IntegrationKeyCalculationException(e);
		}
	}

	@Override
	protected void populateKeyValueFromNavigationProperty(final EdmEntitySet entitySet, final ODataEntry entry,
			final Map<String, List<String>> aliasComponents, final Map<String, List<String>> integrationKeyValues,
			final String propertyName)
	{
		try
		{
			final EdmEntityType entityType = entitySet.getEntityType();
			final Object value = entry.getProperties().get(propertyName);
			final EdmTyped type = entityType.getProperty(propertyName);
			if (value instanceof ODataEntry && type instanceof EdmNavigationProperty)
			{
				calcKeyInternal(
						entitySet.getRelatedEntitySet((EdmNavigationProperty) type),
						(ODataEntry) value,
						aliasComponents,
						integrationKeyValues
				);
			}
		}
		catch (final EdmException e)
		{
			throw new IntegrationKeyCalculationException(e);
		}
	}

	@Override
	protected void addToKeyValueMap(final Map<String, List<String>> integrationKeyValues, final EdmEntitySet type, final ODataEntry entry, final String propertyName)
	{
		if (entry.getProperties().containsKey(propertyName))
		{
			final Object attributeValue = getProperty(entry, propertyName);
			addToKeyValues(integrationKeyValues, getTypeCode(type), transformValueToString(attributeValue));
		}
	}

	@Override
	protected String getTypeCode(final EdmEntitySet entitySet)
	{
		try
		{
			return entitySet.getEntityType().getName();
		}
		catch (final EdmException e)
		{
			throw new IntegrationKeyCalculationException(e);
		}
	}

	@Override
	protected Object getProperty(final ODataEntry entry, final String propertyName)
	{
		return entry.getProperties().get(propertyName);
	}

	@Override
	protected String transformValueToString(final Object attributeValue)
	{
		return attributeValue instanceof Calendar
				? String.valueOf(((Calendar) attributeValue).getTimeInMillis())
				: String.valueOf(attributeValue);
	}
}   
