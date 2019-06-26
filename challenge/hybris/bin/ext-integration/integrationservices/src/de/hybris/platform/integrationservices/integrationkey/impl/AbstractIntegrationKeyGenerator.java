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

import static de.hybris.platform.integrationservices.constants.IntegrationservicesConstants.INTEGRATION_KEY_PROP_DIV;
import static de.hybris.platform.integrationservices.constants.IntegrationservicesConstants.INTEGRATION_KEY_TYPE_DIV;

import de.hybris.platform.integrationservices.integrationkey.IntegrationKeyGenerator;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

public abstract class AbstractIntegrationKeyGenerator<T, E> implements IntegrationKeyGenerator<T, E>
{
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractIntegrationKeyGenerator.class);

	private String encoding;

	protected String encodeValue(final String value)
	{
		try
		{
			return URLEncoder.encode(value, getEncoding());
		}
		catch (final UnsupportedEncodingException e)
		{
			LOGGER.warn("Value [{}] was not able to be encoded.", value, e);
			return value;
		}
	}

	protected Map<String, List<String>> extractKeyPropertyReferencesFromAlias(final String aliasString)
	{
		final Map<String, List<String>> integrationKeyComponents = new LinkedHashMap<>();

		Pattern.compile("\\" + INTEGRATION_KEY_PROP_DIV).splitAsStream(aliasString).forEach(
				typeAndPropertyName -> addToKeyReferenceMap(integrationKeyComponents, typeAndPropertyName.split(INTEGRATION_KEY_TYPE_DIV))
		);

		return integrationKeyComponents;
	}

	protected void addToKeyReferenceMap(final Map<String, List<String>> integrationKeyComponents, final String[] aliasTypeAndPropertyName)
	{
		addToKeyValues(integrationKeyComponents, aliasTypeAndPropertyName[0], aliasTypeAndPropertyName[1]);
	}

	protected void addToKeyValues(final Map<String, List<String>> integrationKeyComponents, final String entityType, final String simplePropertyName)
	{
		if (integrationKeyComponents.containsKey(entityType))
		{
			integrationKeyComponents.get(entityType).add(simplePropertyName);
		}
		else
		{
			integrationKeyComponents.put(entityType, Lists.newArrayList(simplePropertyName));
		}
	}

	protected void calcKeyInternal(final T type, final E entry,
			final Map<String, List<String>> aliasComponents, final Map<String, List<String>> integrationKeyValues)
	{
		final Map<String, List<String>> aliasComponentsCopy = new HashMap<>(aliasComponents);
		final Iterator<Map.Entry<String, List<String>>> it = aliasComponentsCopy.entrySet().iterator();

		while (it.hasNext())
		{
			final Map.Entry<String, List<String>> aliasComponent = it.next();
			final Optional<String> navPropNameOptional = findMatchingNavigationPropertyIn(type, aliasComponent.getKey());
			if (navPropNameOptional.isPresent())
			{
				populateKeyValueFromNavigationProperty(type, entry, aliasComponentsCopy, integrationKeyValues, navPropNameOptional.get());
			}
			else
			{
				populateKeyValueFromSimpleProperty(type, entry, integrationKeyValues, aliasComponent);
				it.remove();
			}
		}
	}

	/**
	 * Looks for a certain attribute of typeCode in type.
	 *
	 * @param type The type to be used.
	 * @param typeCode The typeCode to look for.
	 * @return The attributeName of the Property that was found. Empty otherwise.
	 */
	protected abstract Optional<String> findMatchingNavigationPropertyIn(final T type, final String typeCode);

	protected void populateKeyValueFromSimpleProperty(final T type,
			final E entry,
			final Map<String, List<String>> integrationKeyValues,
			final Map.Entry<String, List<String>> aliasComponent)
	{
		aliasComponent.getValue().forEach(propertyName -> addToKeyValueMap(integrationKeyValues, type, entry, propertyName));
	}

	protected abstract void populateKeyValueFromNavigationProperty(final T type,
			final E entry,
			final Map<String, List<String>> aliasComponents,
			final Map<String, List<String>> integrationKeyValues,
			final String propertyName);



	protected abstract void addToKeyValueMap(final Map<String, List<String>> integrationKeyValues, final T type, final E entry, final String propertyName);

	/**
	 * Given a type, it returns its code representation.
	 *
	 * @param type The type to be used.
	 * @return The type code.
	 */
	protected abstract String getTypeCode(final T type);

	/**
	 * Given an entry, it returns a property value.
	 *
	 * @param entry The entry to be used.
	 * @param propertyName The propertyName to look for.
	 * @return The value related to propertyName.
	 */
	protected abstract Object getProperty(final E entry, final String propertyName);

	protected String setKeyValuesToString(final Map<String, List<String>> integrationKeyValues, final Map<String, List<String>> sortedAliasEntityTypeKeys)
	{
		final StringBuilder integrationKeyValue = new StringBuilder();
		sortedAliasEntityTypeKeys.keySet().stream()
				.filter(key -> integrationKeyValues.get(key) != null)
				.forEach(key ->
						integrationKeyValue
								.append(entitySimpleKeys(integrationKeyValues.get(key)))
								.append(INTEGRATION_KEY_PROP_DIV));
		return StringUtils.chop(integrationKeyValue.toString());
	}

	protected String entitySimpleKeys(final Collection<String> entityKeyValues)
	{
		final StringBuilder currentValue = new StringBuilder();
		entityKeyValues.forEach(value -> currentValue
				.append(encodeValue(value))
				.append(INTEGRATION_KEY_PROP_DIV));
		return StringUtils.chop(currentValue.toString());
	}

	/**
	 * Implementations should transform a value into string representation. For instance Date.
	 *
	 * @param attributeValue the value to be converted.
	 * @return The string representation
	 */
	protected abstract String transformValueToString(final Object attributeValue);

	protected String getEncoding()
	{
		return encoding;
	}

	@Required
	public void setEncoding(final String encoding)
	{
		this.encoding = encoding;
	}
}
