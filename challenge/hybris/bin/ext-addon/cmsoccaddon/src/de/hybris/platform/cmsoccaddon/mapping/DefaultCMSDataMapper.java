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
package de.hybris.platform.cmsoccaddon.mapping;

import static java.util.stream.Collectors.toMap;

import de.hybris.platform.cmsfacades.data.AbstractCMSComponentData;
import de.hybris.platform.cmsfacades.data.AbstractPageData;
import de.hybris.platform.cmsfacades.data.MediaData;
import de.hybris.platform.cmsfacades.data.NavigationNodeData;
import de.hybris.platform.cmsoccaddon.data.CMSPageWsDTO;
import de.hybris.platform.cmsoccaddon.data.ComponentWsDTO;
import de.hybris.platform.cmsoccaddon.data.MediaWsDTO;
import de.hybris.platform.cmsoccaddon.data.NavigationNodeWsDTO;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.webservicescommons.mapping.impl.DefaultDataMapper;

import java.util.Map;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;


/**
 * Extends the {@link de.hybris.platform.webservicescommons.mapping.impl.DefaultDataMapper} used for mapping
 * cmsfacades's Data objects into WsDTOs.
 */
public class DefaultCMSDataMapper extends DefaultDataMapper
{
	protected static final String LOCALIZED_TITLE = "localizedTitle";
	protected static final String TITLE = "title";

	private String fields;

	public DefaultCMSDataMapper()
	{
		super();
	}

	@Override
	public <S, D> void map(final S sourceObject, final D destinationObject, final String fields)
	{
		this.fields = fields;
		super.map(sourceObject, destinationObject, fields);
	}

	@Override
	public <S, D> D map(final S sourceObject, final Class<D> destinationClass, final String fields)
	{
		this.fields = fields;
		return super.map(sourceObject, destinationClass, createMappingContext(destinationClass, fields));
	}

	/**
	 * Configure MapperFactory to: <br/>
	 * 1. rename "localizedTitle" to "title" for NavigationNodeData and AbstractPageData<br/>
	 * 2. explicitly call convertMapData function for Map<String, Object> attribute in ComponentWsDTO object.
	 */
	@Override
	protected void configure(final MapperFactory factory)
	{
		super.configure(factory);
		factory.classMap(AbstractCMSComponentData.class, ComponentWsDTO.class).byDefault()
				.customize(new CustomMapper<AbstractCMSComponentData, ComponentWsDTO>()
				{
					@Override
					public void mapAtoB(final AbstractCMSComponentData a, final ComponentWsDTO b, final MappingContext mappingContext)
					{
						if (b.getUid() == null) // nested component
						{
							b.setModifiedtime(a.getModifiedtime());
							b.setName(a.getName());
							b.setTypeCode(a.getTypeCode());
							b.setUid(a.getUid());
							b.setOtherProperties(convertMapData(a.getOtherProperties()));
						}
						else if (b.getOtherProperties() != null)
						{
							b.getOtherProperties().putAll(convertMapData(a.getOtherProperties()));
						}
					}
				}).register();


		factory.classMap(NavigationNodeData.class, NavigationNodeWsDTO.class).field(LOCALIZED_TITLE, TITLE).byDefault().register();
		factory.classMap(AbstractPageData.class, CMSPageWsDTO.class).field(LOCALIZED_TITLE, TITLE).byDefault().register();
	}

	/**
	 * Convert Map value from Data to WsDTO for MediaData and NavigationNodeData, which are not CMS components
	 */
	protected Map<String, Object> convertMapData(final Map<String, Object> compSpecificProps)
	{
		return compSpecificProps.entrySet().stream() //
				.filter(entry -> !(entry.getValue() instanceof AbstractCMSComponentData))
				.collect(toMap(Map.Entry::getKey, entry -> mapDataToWsDTO(entry.getKey(), entry.getValue())));
	}

	/**
	 * For the Map<String, Object> which holds the component-specific attributes, if value is MediaData,
	 * NavigationNodeData or another map which entries contains MediaData, the
	 * {@link de.hybris.platform.webservicescommons.mapping.impl.DefaultDataMapper#map(Object, Class, String)}' functions
	 * will be explicitly called.
	 */
	protected Object mapDataToWsDTO(final String key, final Object obj)
	{
		if (obj instanceof MediaData)
		{
			final MediaWsDTO mediaWsDTO = new MediaWsDTO();
			map((MediaData) obj, mediaWsDTO, fields);
			return mediaWsDTO;
		}
		else if (obj instanceof NavigationNodeData)
		{
			final NavigationNodeWsDTO navigationNodeWsDTO = new NavigationNodeWsDTO();
			map(obj, navigationNodeWsDTO, fields);
			return navigationNodeWsDTO;
		}
		else if (obj instanceof Map<?, ?>)
		{
			final Map<String, Object> innerMap = (Map<String, Object>) obj;
			// if property name is "Media" and value is a map, we will convert the MediaData in the map value into MediaWsDTO
			if (key.equalsIgnoreCase(MediaModel._TYPECODE))
			{
				final Map<String, MediaWsDTO> newInnerMap = innerMap.entrySet().stream()
						.collect(toMap(entry -> entry.getKey(), entry -> {
							final MediaWsDTO mediaWsDTO = new MediaWsDTO();
							map((MediaData) ((Map.Entry) entry).getValue(), mediaWsDTO, fields);
							return mediaWsDTO;
						}));

				return newInnerMap;
			}
			else
			{
				convertMapData(innerMap);
			}
		}
		return obj;
	}
}
