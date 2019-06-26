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

package de.hybris.platform.odata2services.odata.schema;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.olingo.odata2.api.edm.provider.Key;
import org.apache.olingo.odata2.api.edm.provider.Property;
import org.apache.olingo.odata2.api.edm.provider.SimpleProperty;

import com.google.common.base.Preconditions;

/**
 * Any generator responsible for entity type key generation. This marker interface is useful for encapsulating the
 * generics and for easy finding of all implementors.
 */
public abstract class KeyGenerator implements SchemaElementGenerator<Optional<Key>, List<Property>>
{
	/**
	 * Generates a key object from a set of entity attributes received as a parameter
	 *
	 * @param entityProperties used to generate the key
	 * @return Key generated containing the property refs of the valid entity attributes received as a parameter
	 */
	@Override
	public Optional<Key> generate(final List<Property> entityProperties)
	{
		final List<SimpleProperty> simpleProperties = getSimpleProperties(entityProperties);
		return simpleProperties.stream().anyMatch(this::isKey) ? Optional.of(createKey()) : Optional.empty();
	}

	protected abstract boolean isKey(final SimpleProperty simpleProperty);

	protected abstract Key createKey();

	protected static List<SimpleProperty> getSimpleProperties(final List<Property> entityProperties)
	{
		Preconditions.checkArgument(entityProperties != null, "Cannot generate a key out of null entityProperties");
		return entityProperties.stream().filter(p -> p instanceof SimpleProperty).map(p -> (SimpleProperty) p ).collect(Collectors.toList());
	}
}
