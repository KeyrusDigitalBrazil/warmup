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
package de.hybris.platform.cmsfacades.types.populator;

import de.hybris.platform.cmsfacades.data.ComponentTypeData;
import de.hybris.platform.cmsfacades.data.StructureTypeCategory;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import org.springframework.beans.factory.annotation.Required;


/**
 * This populator will populate {@link de.hybris.platform.cmsfacades.data.ComponentTypeData#setCategory(String)}
 */
public class CategoryComponentTypePopulator implements Populator<ComposedTypeModel, ComponentTypeData>
{
	private StructureTypeCategory category = StructureTypeCategory.COMPONENT;

	@Override
	public void populate(final ComposedTypeModel source, final ComponentTypeData target) throws ConversionException
	{
		target.setCategory(getCategory().name());
	}

	protected StructureTypeCategory getCategory()
	{
		return category;
	}

	@Required
	public void setCategory(final StructureTypeCategory category)
	{
		this.category = category;
	}

}
