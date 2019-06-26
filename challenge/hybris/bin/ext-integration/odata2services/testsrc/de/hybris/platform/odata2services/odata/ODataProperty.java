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
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.olingo.odata2.api.edm.EdmAnnotationAttribute;
import org.apache.olingo.odata2.api.edm.provider.AnnotationAttribute;
import org.apache.olingo.odata2.api.edm.provider.Property;

/**
 *
 */
public class ODataProperty implements ODataAnnotatable
{
	private final Property property;

	ODataProperty(final Property prop)
	{
		property = prop;
	}

	@Override
	public Collection<String> getAnnotationNames()
	{
		return getAnnotations().stream()
				.map(EdmAnnotationAttribute::getName)
				.collect(Collectors.toList());
	}

	@Override
	public Optional<AnnotationAttribute> getAnnotation(final String name)
	{
		return getAnnotations().stream().filter(annotation -> annotation.getName() == name).findFirst();
	}

	public List<AnnotationAttribute> getAnnotations()
	{
		final List<AnnotationAttribute> attributes = property.getAnnotationAttributes();
		return attributes != null
				? attributes
				: Collections.emptyList();
	}
}
