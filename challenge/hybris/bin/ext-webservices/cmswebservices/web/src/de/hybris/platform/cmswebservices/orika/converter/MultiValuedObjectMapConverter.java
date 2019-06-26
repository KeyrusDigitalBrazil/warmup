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
package de.hybris.platform.cmswebservices.orika.converter;

import de.hybris.platform.webservicescommons.mapping.WsDTOMapping;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import ma.glasnost.orika.MappingContext;


/**
 * Custom converter to convert a field of type {@code Map<String, Map<String, String>>} to a new
 * {@code Map<String, Map<String, String>>}.
 */
@WsDTOMapping
public class MultiValuedObjectMapConverter
extends BidirectionalConverter<Map<String, Map<String, String>>, Map<String, Map<String, String>>>
{

	@Override
	public Map<String, Map<String, String>> convertFrom(final Map<String, Map<String, String>> target,
			final Type<Map<String, Map<String, String>>> sourceType, final MappingContext mappingContext)
	{
		return cloneMap(target);
	}

	@Override
	public Map<String, Map<String, String>> convertTo(final Map<String, Map<String, String>> source,
			final Type<Map<String, Map<String, String>>> targetType, final MappingContext mappingContext)
	{
		return cloneMap(source);
	}

	protected Map<String, Map<String, String>> cloneMap(final Map<String, Map<String, String>> source)
	{
		return source.entrySet().stream()
				.collect(Collectors.toMap(entry -> entry.getKey(), entry -> new HashMap<>(entry.getValue())));
	}

}
