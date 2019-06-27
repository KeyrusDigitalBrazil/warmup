/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.cmswebservices.jaxb.adapters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.eclipse.persistence.oxm.annotations.XmlVariableNode;


/**
 * This Adapter is used to convert {@code Map<String, String>} into XML/JSON objects, and vice-versa,
 * where the entries are represented as
 * Key/Value pairs, as opposed to an array representation.
 *
 * Example:
 *
 * <pre>
 *   <code>
 *   public class Container
 *   {
 *     private Map<String, String> value;
 *   }
 *   </code>
 * </pre>
 *
 * would have the following JSON representation:
 *
 * <pre>
 *   <code>
 *   {
 *     "value": {
 *       "key1": "value1",
 *       "key2": "value2"
 *     }
 *   }
 *   </code>
 * </pre>
 */
public class KeyValueMapAdapter extends XmlAdapter<KeyValueMapAdapter.KeyValueAdaptedMap, Map<String, String>>
{
	/**
	 * This class represents a key value map. It contains {@link KeyValueAdaptedEntry} entries.
	 */
	public static class KeyValueAdaptedMap
	{
		@XmlVariableNode("key")
		List<KeyValueAdaptedEntry> entries = new ArrayList<KeyValueAdaptedEntry>();
	}

	/**
	 * This class represents a simple key-value entry in a map. It holds a key and a value, both
	 * as strings.
	 */
	public static class KeyValueAdaptedEntry
	{
		@XmlTransient
		public String key;

		@XmlValue
		public String value;
	}

	@Override
	public KeyValueAdaptedMap marshal(final Map<String, String> map) throws Exception
	{
		if (map == null)
		{
			return null;
		}
		final KeyValueAdaptedMap adaptedMap = new KeyValueAdaptedMap();
		map.entrySet().stream().filter(entry -> entry.getValue() != null).forEach(entry ->
		{
			final KeyValueAdaptedEntry adaptedEntry = new KeyValueAdaptedEntry();
			adaptedEntry.key = entry.getKey();
			adaptedEntry.value = entry.getValue();
			adaptedMap.entries.add(adaptedEntry);
		});
		return adaptedMap;
	}

	@Override
	public Map<String, String> unmarshal(final KeyValueAdaptedMap adaptedMap) throws Exception
	{
		if (adaptedMap == null)
		{
			return null;
		}
		final List<KeyValueAdaptedEntry> adaptedEntries = adaptedMap.entries;
		final Map<String, String> map = new HashMap<String, String>(adaptedEntries.size());
		for (final KeyValueAdaptedEntry adaptedEntry : adaptedEntries)
		{
			map.put(adaptedEntry.key, adaptedEntry.value);
		}
		return map;
	}
}
