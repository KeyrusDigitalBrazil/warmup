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
package de.hybris.platform.cmsoccaddon.jaxb.adapters;

import static java.util.stream.Collectors.toList;

import de.hybris.platform.cmsoccaddon.data.ComponentWsDTO;
import de.hybris.platform.cmsoccaddon.data.NavigationNodeWsDTO;
import de.hybris.platform.cmsoccaddon.jaxb.adapters.KeyMapAdaptedEntryAdapter.KeyMapAdaptedEntry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.commons.lang3.ClassUtils;
import org.eclipse.persistence.jaxb.JAXBMarshaller;

import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * This class is used by adapters to convert {@link de.hybris.platform.cmsoccaddon.data.ComponentWsDTO} into XML/JSON
 * objects. Map<String, Object> are represented as a list of
 * {@link de.hybris.platform.cmsoccaddon.jaxb.adapters.KeyMapAdaptedEntryAdapter.KeyMapAdaptedEntry} objects.
 */
public class ComponentAdapterUtil
{
	private static final String UID = "uid";
	private static final String TYPE_CODE = "typeCode";
	private static final String NAME = "name";

	/**
	 * This class represents the converted ComponentWsDTO data. Except the component common attributes, it contains a
	 * list of {@link de.hybris.platform.cmsoccaddon.jaxb.adapters.KeyMapAdaptedEntryAdapter.KeyMapAdaptedEntry} entries
	 * which hold the component-specific attributes.
	 */
	@XmlRootElement(name = "component")
	public static class ComponentAdaptedData
	{
		@XmlElement
		String uid;

		@XmlElement
		String typeCode;

		@XmlElement
		Date modifiedTime;

		@XmlElement
		String name;

		@XmlAnyElement
		@XmlJavaTypeAdapter(KeyMapAdaptedEntryAdapter.class)
		List<KeyMapAdaptedEntry> entries = new ArrayList<KeyMapAdaptedEntry>();

		//move it out of entries
		@XmlElement
		NavigationNodeAdaptedData navigationNode;

		// When marshal xmlAnyCollection, JSON_REDUCE_ANY_ARRAYS is also considered.
		// We don't want any non-collection object in "List<KeyMapAdaptedEntry>" to be an array.
		void beforeMarshal(final Marshaller m)
		{
			((JAXBMarshaller) m).getXMLMarshaller().setReduceAnyArrays(true);
		}

		void afterMarshal(final Marshaller m)
		{
			((JAXBMarshaller) m).getXMLMarshaller().setReduceAnyArrays(false);
		}

	}

	@XmlRootElement
	public static class NavigationNodeAdaptedData extends NavigationNodeWsDTO
	{
		// for navigation node, JSON_REDUCE_ANY_ARRAYS should be false
		void beforeMarshal(final Marshaller m)
		{
			((JAXBMarshaller) m).getXMLMarshaller().setReduceAnyArrays(false);
		}

		void afterMarshal(final Marshaller m)
		{
			((JAXBMarshaller) m).getXMLMarshaller().setReduceAnyArrays(true);
		}
	}


	private ComponentAdapterUtil()
	{
		// private constructor to avoid instantiation
	}

	/**
	 * convert ComponentWsDTO object into ComponentAdaptedData object
	 *
	 * @param componentDTO
	 * @return ComponentAdaptedData object
	 */
	public static ComponentAdaptedData convert(final ComponentWsDTO componentDTO)
	{
		final ComponentAdaptedData adaptedComponent = new ComponentAdaptedData();
		adaptedComponent.uid = componentDTO.getUid();
		adaptedComponent.typeCode = componentDTO.getTypeCode();
		adaptedComponent.modifiedTime = componentDTO.getModifiedtime();
		adaptedComponent.name = componentDTO.getName();

		if (componentDTO.getOtherProperties() != null)
		{
			componentDTO.getOtherProperties().entrySet().stream().forEach(entry -> {
				if (entry.getValue() instanceof NavigationNodeWsDTO)
				{
					final NavigationNodeWsDTO dto = (NavigationNodeWsDTO) entry.getValue();
					adaptedComponent.navigationNode = new NavigationNodeAdaptedData();
					adaptedComponent.navigationNode.setName(dto.getName());
					adaptedComponent.navigationNode.setUid(dto.getUid());
					adaptedComponent.navigationNode.setChildren(dto.getChildren());
					adaptedComponent.navigationNode.setEntries(dto.getEntries());
					adaptedComponent.navigationNode.setTitle(dto.getTitle());
					adaptedComponent.navigationNode.setLocalizedTitle(dto.getLocalizedTitle());
					adaptedComponent.navigationNode.setPosition(dto.getPosition());
				}
			});
			adaptedComponent.entries.addAll(marshalMap(componentDTO.getOtherProperties()));
		}
		return adaptedComponent;
	}

	public static Map<String, Object> convertNestedComponentToMap(final ComponentWsDTO componentDTO)
	{
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put(NAME, componentDTO.getName());
		map.put(UID, componentDTO.getUid());
		map.put(TYPE_CODE, componentDTO.getTypeCode());
		map.putAll(componentDTO.getOtherProperties());

		return map;
	}

	/**
	 * convert map with String/Object pairs into a list of KeyMapAdaptedEntry
	 *
	 * @param map
	 * @return List<KeyMapAdaptedEntry>
	 */
	public static List<KeyMapAdaptedEntry> marshalMap(final Map<String, Object> map)
	{
		return map.entrySet().stream()
				.filter(entry -> entry.getValue() != null && !(entry.getValue() instanceof NavigationNodeWsDTO)) //
				.map(entry -> convertToAdaptedEntry(entry)).collect(toList());
	}

	/**
	 * Convert Map entry to KeyMapAdaptedEntry object
	 *
	 * @param entry
	 * @return KeyMapAdaptedEntry object
	 */
	public static KeyMapAdaptedEntry convertToAdaptedEntry(final Map.Entry<String, Object> entry)
	{
		final KeyMapAdaptedEntry adaptedEntry = new KeyMapAdaptedEntry();
		adaptedEntry.key = entry.getKey();
		final Object valueObj = entry.getValue();
		if (valueObj instanceof Map<?, ?>)
		{
			adaptedEntry.mapValue = marshalMap((Map<String, Object>) valueObj);
		}
		else if (ClassUtils.isPrimitiveOrWrapper(valueObj.getClass()) || valueObj.getClass() == String.class)
		{
			adaptedEntry.strValue = valueObj.toString();
		}
		else if (valueObj instanceof Collection<?>)
		{
			adaptedEntry.arrayValue = (List<String>) ((Collection) valueObj).stream() //
					.filter(obj -> (ClassUtils.isPrimitiveOrWrapper(obj.getClass()) || obj.getClass() == String.class)) //
					.map(obj -> obj.toString()).collect(toList());
		}
		else if (valueObj instanceof ComponentWsDTO)
		{
			adaptedEntry.mapValue = marshalMap(convertNestedComponentToMap((ComponentWsDTO) valueObj));
		}
		else
		{
			final ObjectMapper objectMapper = new ObjectMapper();
			final Map<String, Object> props = objectMapper.convertValue(valueObj, Map.class);
			adaptedEntry.mapValue = marshalMap(props);
		}
		return adaptedEntry;
	}
}
