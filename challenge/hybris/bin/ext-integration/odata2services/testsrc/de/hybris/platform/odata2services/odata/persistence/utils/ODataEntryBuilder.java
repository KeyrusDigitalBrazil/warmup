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
package de.hybris.platform.odata2services.odata.persistence.utils;

import java.util.HashMap;
import java.util.Map;

import org.apache.olingo.odata2.api.ep.entry.EntryMetadata;
import org.apache.olingo.odata2.api.ep.entry.MediaMetadata;
import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.apache.olingo.odata2.api.uri.ExpandSelectTreeNode;

import com.google.common.collect.ImmutableMap;

/**
 * Implementation of ODataEntry with builder like syntax to create an ODataEntry
 */
public class ODataEntryBuilder implements ODataEntry
{
	private Map<String, Object> properties = new HashMap<>();

	public static ODataEntryBuilder oDataEntryBuilder()
	{
		return new ODataEntryBuilder();
	}

	@Override
	public Map<String, Object> getProperties()
	{
		return properties;
	}

	@Override
	public MediaMetadata getMediaMetadata()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public EntryMetadata getMetadata()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsInlineEntry()
	{
		return false;
	}

	@Override
	public ExpandSelectTreeNode getExpandSelectTree()
	{
		throw new UnsupportedOperationException();
	}

	public ODataEntryBuilder withProperties(final ImmutableMap<String, Object> propertyMap)
	{
		properties = propertyMap;
		return this;
	}

	public ODataEntryBuilder withProperty(final String name, final Object value)
	{
		properties.put(name, value);
		return this;
	}

	public ODataEntry build()
	{
		return this;
	}
}
