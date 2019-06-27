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

package de.hybris.platform.odata2services.odata;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.olingo.odata2.api.edm.provider.EntityType;
import org.apache.olingo.odata2.api.edm.provider.NavigationProperty;
import org.apache.olingo.odata2.api.edm.provider.Property;
import org.apache.olingo.odata2.api.edm.provider.PropertyRef;

/**
 * A helper class for exploring and evaluating content of an OData {@code EntityType}
 */
public class ODataEntityType
{
	private final EntityType contextEntityType;

	ODataEntityType(final EntityType type)
	{
		contextEntityType = type;
	}

	public List<String> getKeyProperties()
	{
		return contextEntityType.getKey().getKeys().stream()
				.map(PropertyRef::getName)
				.collect(Collectors.toList());
	}

	public ODataAnnotatable getAnnotatableProperty(final String name)
	{
		ODataAnnotatable property = lookupProperty(name);
		if (property == null)
		{
			property = lookupNavigationProperty(name);
			if (property == null)
			{
				throw new IllegalArgumentException("Property '" + name + "' does not exist in EntityType '" + contextEntityType.getName() + "'");
			}
		}
		return property;
	}

	private ODataAnnotatable lookupProperty(final String name)
	{
		return contextEntityType.getProperties().stream()
				.filter(p -> Objects.equals(p.getName(), name))
				.findAny()
				.map(ODataProperty::new)
				.orElse(null);
	}

	private ODataAnnotatable lookupNavigationProperty(final String name)
	{
		return contextEntityType.getNavigationProperties().stream()
				.filter(p -> Objects.equals(p.getName(), name))
				.findAny()
				.map(ODataNavigationProperty::new)
				.orElse(null);
	}

	public Collection<String> getPropertyNames()
	{
		return contextEntityType.getProperties().stream().map(Property::getName).collect(Collectors.toList());
	}

	public Collection<String> getNavigationPropertyNames()
	{
		return contextEntityType.getNavigationProperties().stream().map(NavigationProperty::getName).collect(Collectors.toList());
	}
}
