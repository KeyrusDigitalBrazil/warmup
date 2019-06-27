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
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;


/**
 * This populator will convert the {@link ComposedTypeModel#getCode()} and {@link ComposedTypeModel#getName()} only.
 */
public class BasicComponentTypePopulator implements Populator<ComposedTypeModel, ComponentTypeData>
{

	@Override
	public void populate(final ComposedTypeModel source, final ComponentTypeData target) throws ConversionException
	{
		target.setCode(source.getCode());
		target.setName(source.getName());
	}

}
