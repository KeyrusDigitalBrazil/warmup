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
package de.hybris.platform.cmsfacades.rendering.attributeconverters;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cms2.common.functions.Converter;

import java.util.Objects;

/**
 * Rendering Attribute Converter for {@link de.hybris.platform.category.model.CategoryModel}.
 * Converts the category into its category code (string).
 */
public class CategoryToDataContentConverter implements Converter<CategoryModel, String>
{
	// --------------------------------------------------------------------------
	// Public API
	// --------------------------------------------------------------------------
	@Override
	public String convert(CategoryModel source)
	{
		if(Objects.isNull(source))
		{
			return null;
		}

		return source.getCode();
	}
}
