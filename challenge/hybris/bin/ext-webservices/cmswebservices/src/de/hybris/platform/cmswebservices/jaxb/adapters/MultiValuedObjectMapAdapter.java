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
 * This Adapter is used to convert multivalued maps {@code Map<String, Map<String, String>>} into XML/JSON objects, and
 * vice-versa,
 * where the entries are represented as
 * Key/Value pairs, as opposed to an array representation.
 *
 * Example:
 *
 * <pre>
 *   <code>
 *   public class Container
 *   {
 *     private Map<String, Map<String, String>> value;
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
 *       "key1": {"subkey1": "value1", "subkey2": "value2"},
 *       "key2": {"subkey3": "value3", "subkey4": "value4"}
 *     }
 *   }
 *   </code>
 * </pre>
 */
public class MultiValuedObjectMapAdapter
		extends XmlAdapter<MultiValuedObjectMapAdapter.KeyValueListAdaptedMap, Map<String, Map<String, String>>>
{

	/**
	 * This class represents a multivalued map. It contains {@link MultiValuedParentAdaptedEntry}
	 * entries.
	 */
	public static class KeyValueListAdaptedMap
	{
		@XmlVariableNode("key")
		List<MultiValuedParentAdaptedEntry> entries = new ArrayList<>();
	}

	/**
	 * This class represents a parent entry in a multivalued map. It has a key and is associated
	 * to a list of {@link MultiValuedAdaptedEntry}.
	 */
	public static class MultiValuedParentAdaptedEntry
	{
		@XmlTransient
		String key;

		@XmlVariableNode("key")
		List<MultiValuedAdaptedEntry> entries = new ArrayList<>();
	}

	/**
	 * This class represents a simple key-value entry in a multivalued map. It holds a key and a
	 * value, both as strings.
	 */
	public static class MultiValuedAdaptedEntry
	{
		@XmlTransient
		String key;

		@XmlValue
		String value;
	}

	@Override
	public KeyValueListAdaptedMap marshal(final Map<String, Map<String, String>> map) throws Exception
	{
		if (map == null)
		{
			return null;
		}
		final KeyValueListAdaptedMap adaptedMap = new KeyValueListAdaptedMap();
		map.entrySet().stream()
				.filter(entry -> entry.getValue() != null) //
				.forEach(entry ->
				{ //
					final MultiValuedParentAdaptedEntry adaptedEntry = new MultiValuedParentAdaptedEntry();
					adaptedEntry.key = entry.getKey();

					entry.getValue().entrySet().stream()
							.forEach(o ->
							{
								final MultiValuedAdaptedEntry e = new MultiValuedAdaptedEntry();
								e.key = o.getKey();
								e.value = o.getValue();
								adaptedEntry.entries.add(e);
							});
					adaptedMap.entries.add(adaptedEntry);
				});
		return adaptedMap;
	}

	@Override
	public Map<String, Map<String, String>> unmarshal(final KeyValueListAdaptedMap adaptedMap) throws Exception
	{
		if (adaptedMap == null)
		{
			return null;
		}
		final List<MultiValuedParentAdaptedEntry> adaptedEntries = adaptedMap.entries;
		final Map<String, Map<String, String>> map = new HashMap<>(adaptedEntries.size());
		for (final MultiValuedParentAdaptedEntry adaptedEntry : adaptedEntries)
		{
			final Map<String, String> entryMap = new HashMap<>();
			if (adaptedEntry.entries != null)
			{
				adaptedEntry.entries.stream().forEach(e -> entryMap.put(e.key, e.value));
			}
			map.put(adaptedEntry.key, entryMap);
		}
		return map;
	}
}
