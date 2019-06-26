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
package de.hybris.platform.cmsfacades.common.itemcollector.impl;

import de.hybris.platform.cmsfacades.common.itemcollector.ItemCollector;
import de.hybris.platform.cmsfacades.common.itemcollector.ItemCollectorRegistry;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Required;

import com.google.common.base.Preconditions;

/**
 * Default implementation of {@link ItemCollectorRegistry}
 */
public class DefaultItemCollectorRegistry implements ItemCollectorRegistry
{

	private TypeService typeService;
	
	/*
	 * Maps the ItemType to a ItemCollector.   
	 */
	private Map<String, ItemCollector> itemCollectorMap; 
	
	@Override
	public Optional<ItemCollector> getItemCollector(final ItemModel itemModel)
	{
		Preconditions.checkNotNull(itemModel, "Item Model should not be null");
		
		final Optional<ItemCollector> result = Optional.ofNullable(getItemCollectorMap().get(itemModel.getItemtype()));
		
		if (result.isPresent())
		{
			return result;
		}
		
		// if it cannot find using the direct item type, check if it can be found using one of its super types
		final String itemType = itemModel.getItemtype();
		try
		{
			getTypeService().getComposedTypeForCode(itemType);
		}
		catch (final UnknownIdentifierException e)
		{
			return Optional.empty();
		}
		// if there is a collector for any of its super types, then return the collector in the optional object. 
		return getTypeService() //
				.getComposedTypeForCode(itemType) //
				.getAllSuperTypes().stream() //
				.filter(composedType -> getItemCollectorMap().containsKey(composedType.getCode())) //
				.map(composedType -> getItemCollectorMap().get(composedType.getCode())) //
				.findFirst();
	}

	protected Map<String, ItemCollector> getItemCollectorMap()
	{
		return itemCollectorMap;
	}

	@Required
	public void setItemCollectorMap(final Map<String, ItemCollector> itemCollectorMap)
	{
		this.itemCollectorMap = itemCollectorMap;
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
