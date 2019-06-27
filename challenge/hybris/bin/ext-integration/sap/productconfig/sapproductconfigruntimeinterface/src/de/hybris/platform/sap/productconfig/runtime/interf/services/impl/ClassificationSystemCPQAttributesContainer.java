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
package de.hybris.platform.sap.productconfig.runtime.interf.services.impl;

import de.hybris.platform.core.model.media.MediaModel;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * <b>Immutable Object.</b><br>
 * Container object for all CPQ related attributes of a single cstic within the hybris classification system.<br>
 * Can be uniquely identified by the specified code.
 */
public class ClassificationSystemCPQAttributesContainer
{
	private final String code;
	private final String name;
	private final String description;
	private final Map<String, String> valueNames;
	private final Map<String, String> valueDescriptions;
	private final Collection<MediaModel> csticMedia;
	private final Map<String, Collection<MediaModel>> csticValueMedia;
	private static final String NULL_CODE = "";

	/**
	 * <code>null</code> object, to model the case when there exists no data for the specific cstci whithin the hybris
	 * classification system.
	 */
	public static final ClassificationSystemCPQAttributesContainer NULL_OBJ = new ClassificationSystemCPQAttributesContainer(
			NULL_CODE, null, null, Collections.emptyMap(), Collections.emptyMap(), Collections.emptyList(), Collections.emptyMap());

	/**
	 * Default Constructor.<br>
	 * Because this object is immutable, all data has to be provided within this constructor call.
	 *
	 * @param code
	 *           unique identifier for this object, typically the cstic key
	 * @param name
	 *           display name for the cstic
	 * @param description
	 *           long text of the cstic
	 * @param valueNames
	 *           display names of all values belonging to this cstic, identified by the value name
	 * @param csticMedia
	 *           all medias assigned to this cstic
	 * @param csticValueMedia
	 *           all medias assigned to cstic values
	 */
	public ClassificationSystemCPQAttributesContainer(final String code, final String name, final String description,
			final Map<String, String> valueNames, final Map<String, String> valueDescriptions,
			final Collection<MediaModel> csticMedia, final Map<String, Collection<MediaModel>> csticValueMedia)
	{
		this.code = code;
		this.name = name;
		this.description = description;
		this.valueNames = Collections.unmodifiableMap(valueNames);
		this.valueDescriptions = Collections.unmodifiableMap(valueDescriptions);
		this.csticMedia = Collections.unmodifiableCollection(csticMedia);
		this.csticValueMedia = Collections.unmodifiableMap(csticValueMedia);
	}

	/**
	 * @return display name of the cstic
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @return long text of the cstic
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * @return cstic value display name map, value name is key
	 */
	public Map<String, String> getValueNames()
	{
		return valueNames;
	}

	/**
	 * @return collection of cstic media assigned to the cstic represented by this container
	 */
	public Collection<MediaModel> getCsticMedia()
	{
		return Optional.ofNullable(csticMedia).map(Collection::stream).orElseGet(Stream::empty).collect(Collectors.toList());
	}

	/**
	 * @return map with medias for each cstic value belonging to the cstic represented by this container
	 */
	public Map<String, Collection<MediaModel>> getCsticValueMedia()
	{
		return csticValueMedia;
	}

	/**
	 * @return the valueDescriptions
	 */
	public Map<String, String> getValueDescriptions()
	{
		return valueDescriptions;
	}

	@Override
	public int hashCode()
	{
		return code.hashCode();
	}

	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null)
		{
			return false;
		}
		if (getClass() != obj.getClass())
		{
			return false;
		}
		final ClassificationSystemCPQAttributesContainer other = (ClassificationSystemCPQAttributesContainer) obj;
		if (code == null)
		{
			if (other.code != null)
			{
				return false;
			}
		}
		else if (!code.equals(other.code))
		{
			return false;
		}
		return true;
	}

}
