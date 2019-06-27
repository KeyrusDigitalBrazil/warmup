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

import static de.hybris.platform.integrationservices.constants.IntegrationservicesConstants.INTEGRATION_KEY_PROPERTY_NAME;
import static de.hybris.platform.integrationservices.constants.IntegrationservicesConstants.INTEGRATION_KEY_PROP_DIV;
import static de.hybris.platform.integrationservices.constants.IntegrationservicesConstants.INTEGRATION_KEY_TYPE_DIV;
import static de.hybris.platform.odata2services.odata.EdmAnnotationUtils.getAliasTextIfPresent;
import static de.hybris.platform.odata2services.odata.EdmAnnotationUtils.isKeyProperty;

import de.hybris.platform.odata2services.odata.integrationkey.IntegrationKeyToODataEntryGenerator;
import de.hybris.platform.odata2services.odata.persistence.exception.InvalidIntegrationKeyException;
import de.hybris.platform.odata2services.odata.persistence.exception.MissingKeyException;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayDeque;
import java.util.Date;
import java.util.Deque;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.regex.Pattern;

import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.olingo.odata2.api.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.edm.EdmEntityType;
import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.edm.EdmNavigationProperty;
import org.apache.olingo.odata2.api.edm.EdmProperty;
import org.apache.olingo.odata2.api.edm.EdmType;
import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.apache.olingo.odata2.core.edm.EdmBoolean;
import org.apache.olingo.odata2.core.edm.EdmByte;
import org.apache.olingo.odata2.core.edm.EdmDateTime;
import org.apache.olingo.odata2.core.edm.EdmDateTimeOffset;
import org.apache.olingo.odata2.core.edm.EdmDecimal;
import org.apache.olingo.odata2.core.edm.EdmDouble;
import org.apache.olingo.odata2.core.edm.EdmGuid;
import org.apache.olingo.odata2.core.edm.EdmInt16;
import org.apache.olingo.odata2.core.edm.EdmInt32;
import org.apache.olingo.odata2.core.edm.EdmInt64;
import org.apache.olingo.odata2.core.edm.EdmString;
import org.apache.olingo.odata2.core.edm.EdmTime;
import org.apache.olingo.odata2.core.ep.entry.ODataEntryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

public class DefaultIntegrationKeyToODataEntryGenerator implements IntegrationKeyToODataEntryGenerator
{
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultIntegrationKeyToODataEntryGenerator.class);

	private String encoding;

	private final Map<Class<? extends EdmType>, Function<String, Object>> propertyConverters = Maps.newHashMap();

	public DefaultIntegrationKeyToODataEntryGenerator()
	{
		propertyConverters.put(EdmDateTime.class, v -> DateUtils.toCalendar(new Date(Long.valueOf(v))));
		propertyConverters.put(EdmDateTimeOffset.class, v -> DateUtils.toCalendar(new Date(Long.valueOf(v))));
		propertyConverters.put(EdmTime.class, v -> DateUtils.toCalendar(new Date(Long.valueOf(v))));
		propertyConverters.put(EdmBoolean.class, Boolean::valueOf);
		propertyConverters.put(EdmByte.class, Byte::valueOf);
		propertyConverters.put(EdmGuid.class, UUID::fromString);
		propertyConverters.put(EdmDouble.class, Double::valueOf);
		propertyConverters.put(EdmDecimal.class, Double::valueOf);
		propertyConverters.put(EdmInt16.class, Long::valueOf);
		propertyConverters.put(EdmInt32.class, Long::valueOf);
		propertyConverters.put(EdmInt64.class, Long::valueOf);
		propertyConverters.put(EdmString.class, v -> v);
	}

	@Override
	public ODataEntry generate(final EdmEntitySet entitySet, final String integrationKey) throws EdmException
	{
		Preconditions.checkArgument(entitySet != null, "Cannot calculate ODataEntry for null edm entity set");
		Preconditions.checkArgument(integrationKey != null, "Cannot calculate ODataEntry for null integrationKey");

		final String aliasAttributeText = getAliasTextIfPresent(entitySet.getEntityType().getKeyProperties());

		if (aliasAttributeText.isEmpty())
		{
			throw new MissingKeyException(entitySet.getEntityType().getName());
		}

		final Map<String, Pair<String, String>> keyValuePerType =
				getKeyValuePerType(entitySet.getEntityType(), aliasAttributeText, integrationKey);

		final ODataEntry entry = buildODataEntryFrom(entitySet, keyValuePerType);
		entry.getProperties().put(INTEGRATION_KEY_PROPERTY_NAME, integrationKey);
		return entry;
	}

	protected Map<String, Pair<String, String>> getKeyValuePerType(final EdmEntityType entityType,
			final String keyMetadata, final String integrationKey) throws EdmException
	{
		final String[] integrationKeyValues = integrationKey.split(Pattern.quote(INTEGRATION_KEY_PROP_DIV));
		final String[] typeAndPropertyNames = keyMetadata.split(Pattern.quote(INTEGRATION_KEY_PROP_DIV));

		if (integrationKeyValues.length != typeAndPropertyNames.length)
		{
			throw new InvalidIntegrationKeyException(integrationKey, entityType.getName());
		}

		final Map<String, Pair<String, String>> keyValuePerType = Maps.newHashMap();

		for (int i = 0; i < integrationKeyValues.length; i++)
		{
			final String[] typeAndPropertyName = typeAndPropertyNames[i].split(INTEGRATION_KEY_TYPE_DIV);

			// example type and propertyName will be [Product,code]
			final String propertyName = typeAndPropertyName[1];

			keyValuePerType.put(typeAndPropertyNames[i], Pair.of(propertyName, decodeValue(integrationKeyValues[i])));
		}

		return keyValuePerType;
	}

	protected String decodeValue(final String value)
	{
		try
		{
			return URLDecoder.decode(value, getEncoding());
		}
		catch (final UnsupportedEncodingException e)
		{
			LOGGER.warn("Value [{}] was not able to be decoded.", value, e);
			return value;
		}
	}

	protected ODataEntry buildODataEntryFrom(final EdmEntitySet edmEntitySet,
			final Map<String, Pair<String, String>> keyValuePerType) throws EdmException
	{
		final ODataEntry outerEntry = new ODataEntryImpl(Maps.newHashMap(), null, null, null);
		final Deque<Pair<ODataEntry, EdmEntitySet>> stack = new ArrayDeque<>();
		stack.push(Pair.of(outerEntry, edmEntitySet));

		while ( !stack.isEmpty() )
		{
			final Pair<ODataEntry, EdmEntitySet> pair = stack.pop();
			final ODataEntry entry = pair.getLeft();
			final EdmEntitySet entitySet = pair.getRight();
			final EdmEntityType entityType = entitySet.getEntityType();

			for (final String property : entityType.getPropertyNames() )
			{
				final String typeAndPropertyName = entityType.getName() + INTEGRATION_KEY_TYPE_DIV + property;
				final Pair<String, String> keyValue = keyValuePerType.get(typeAndPropertyName);
				if(keyValue != null)
				{
					entry.getProperties().put(keyValue.getKey(),
							getODataEntryProperty((EdmProperty) entityType.getProperty(property), keyValue.getValue()));
				}
			}

			for (final String property : entityType.getNavigationPropertyNames())
			{
				final EdmNavigationProperty navigationProperty = (EdmNavigationProperty)entityType.getProperty(property);
				if (isKeyProperty(navigationProperty))
				{
					final ODataEntry newEntry = new ODataEntryImpl(Maps.newHashMap(), null, null, null);
					entry.getProperties().put(property, newEntry);

					final EdmEntitySet navigationPropertyEntitySet = entitySet.getRelatedEntitySet(navigationProperty);
					stack.push(Pair.of(newEntry, navigationPropertyEntitySet));
				}
			}
		}
		return outerEntry;
	}

	protected Object getODataEntryProperty(final EdmProperty property, final String value) throws EdmException
	{
		final Function<String, Object> function = getPropertyConverters().getOrDefault(property.getType().getClass(), v -> v);
		return function.apply(value);
	}

	protected Map<Class<? extends EdmType>, Function<String, Object>> getPropertyConverters()
	{
		return propertyConverters;
	}

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
