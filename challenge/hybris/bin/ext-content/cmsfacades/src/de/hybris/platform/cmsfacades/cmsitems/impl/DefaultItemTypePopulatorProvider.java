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
package de.hybris.platform.cmsfacades.cmsitems.impl;

import de.hybris.platform.cmsfacades.cmsitems.ItemTypePopulatorProvider;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.type.TypeService;
import org.springframework.beans.factory.annotation.Required;

import java.util.Map;
import java.util.Optional;


/**
 * Default implementation for {@link ItemTypePopulatorProvider}.
 * This class defines a Map that is used to lookup for a Populator given a typeCode.
 * If no populator was found for the type code, then it looks for the populator for its super class.
 */
public class DefaultItemTypePopulatorProvider implements ItemTypePopulatorProvider
{

	private Map<String, Populator<Map<String, Object>, ItemModel>> populatorsMap;

	private TypeService typeService;

	@Override
	public Optional<Populator<Map<String, Object>, ItemModel>> getItemTypePopulator(final String typeCode)
	{
		try
		{
			final ComposedTypeModel composedType = getTypeService().getComposedTypeForCode(typeCode);
			// checks if it exists a populator for this typeCode
			if (getPopulatorsMap().containsKey(typeCode))
			{
				return Optional.of(getPopulatorsMap().get(typeCode));
			}
			// if it does not find it, then tries sto get it from the super type.
			if (composedType.getSuperType() != null)
			{
				return getItemTypePopulator(composedType.getSuperType().getCode());
			}
			// otherwise, returns empty. 
			return Optional.empty();
		}
		catch (final UnknownIdentifierException e)
		{
			return Optional.empty();
		}
	}

	protected Map<String, Populator<Map<String, Object>, ItemModel>> getPopulatorsMap()
	{
		return populatorsMap;
	}

	@Required
	public void setPopulatorsMap(final Map<String, Populator<Map<String, Object>, ItemModel>> populatorsMap)
	{
		this.populatorsMap = populatorsMap;
	}

	protected TypeService getTypeService()
	{
		return typeService;
	}

	@Required
	public void setTypeService(final TypeService typeService)
	{
		this.typeService = typeService;
	}
}
