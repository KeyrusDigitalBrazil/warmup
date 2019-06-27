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
package de.hybris.platform.cmsfacades.cmsitems.attributeconverters;

import de.hybris.platform.cms2.cmsitems.converter.AttributeContentConverter;
import de.hybris.platform.cms2.common.functions.Converter;

/**
 * Default Attribute Converter.
 * This converter should only be used when all of the {@link AttributeContentConverter} instances fail to convert a given object.
 * Returns the {@link Object#toString()} from the Object.
 */
public class DefaultAttributeToDataContentConverter implements Converter<Object, Object>
{
	@Override
	public Object convert(final Object source)
	{
		return source;
	}
}
