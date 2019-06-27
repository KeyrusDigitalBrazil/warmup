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
package de.hybris.platform.cmsfacades.uniqueidentifier.impl;

import static com.google.common.base.Preconditions.checkArgument;
import static java.lang.String.format;
import static org.apache.commons.lang.StringUtils.isNotBlank;

import de.hybris.platform.cmsfacades.common.service.ClassFieldFinder;
import de.hybris.platform.cmsfacades.data.ItemData;
import de.hybris.platform.cmsfacades.uniqueidentifier.UniqueIdentifierConverter;
import de.hybris.platform.cmsfacades.uniqueidentifier.UniqueItemIdentifierService;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import com.google.common.base.Preconditions;


/**
 * Default implementation for {@link UniqueItemIdentifierService}
 */
public class DefaultUniqueItemIdentifierService implements UniqueItemIdentifierService, InitializingBean
{
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultUniqueItemIdentifierService.class);

	@Autowired
	private Set<UniqueIdentifierConverter> uniqueIdentifierConverters;

	private TypeService typeService;
	private ObjectFactory<ItemData> cmsItemDataDataFactory;

	private Map<String, UniqueIdentifierConverter> converterMap = new HashMap<>();

	@Override
	public Optional<ItemData> getItemData(final ItemModel itemModel)
	{
		Preconditions.checkNotNull(itemModel, "Item Model cannot be null.");

		final String itemType = itemModel.getItemtype();

		if (StringUtils.isEmpty(itemType))
		{
			return Optional.empty();
		}

		final Optional<ItemData> uniqueIdentifier = Optional.ofNullable(getConverterMap().get(itemType)).map(
				uniqueIdentifierConverter -> uniqueIdentifierConverter.convert(itemModel));

		if (uniqueIdentifier.isPresent())
		{
			return uniqueIdentifier;
		}
		else
		{
			final Set<String> supportedItemTypes = getConverterMap().keySet();
			try
			{
				getTypeService().getComposedTypeForCode(itemType);
			}
			catch (final UnknownIdentifierException e)
			{
				return Optional.empty();
			}

			return getTypeService() //
					.getComposedTypeForCode(itemType) //
					.getAllSuperTypes().stream() //
					.filter(composedType -> supportedItemTypes.contains(composedType.getCode())) //
					.map(composedType -> getConverterMap().get(composedType.getCode())) //
					.map(converter -> converter.convert(itemModel)) //
					.findFirst();
		}
	}

	@Override
	public Optional<ItemModel> getItemModel(final ItemData itemData)
	{
		checkArgument(itemData != null, "Item Data cannot be null.");
		checkArgument(isNotBlank(itemData.getItemId()), "itemId of Item can neither be null nor empty");
		checkArgument(isNotBlank(itemData.getItemType()), "itemType of Item can neither be null nor empty");

		final String itemType = itemData.getItemType();

		final Optional<ItemModel> uniqueItem = Optional.ofNullable(getConverterMap().get(itemType)).map(
				uniqueIdentifierConverter -> uniqueIdentifierConverter.convert(itemData));

		if (uniqueItem.isPresent())
		{
			return uniqueItem;
		}
		else
		{
			final Set<String> supportedItemTypes = getConverterMap().keySet();
			try
			{
				getTypeService().getComposedTypeForCode(itemType);
			}
			catch (final UnknownIdentifierException e)
			{
				return Optional.empty();
			}

			try
			{
				return getTypeService() //
						.getComposedTypeForCode(itemType) //
						.getAllSuperTypes().stream() //
						.filter(composedType -> supportedItemTypes.contains(composedType.getCode())) //
						.map(composedType -> getConverterMap().get(composedType.getCode())) //
						.map(converter -> converter.convert(itemData)) //
						.findFirst();	
			} 
			catch (UnknownIdentifierException | ConversionException e)
			{
				LOGGER.debug(format("Cannot get the item model for [%s]", itemData.getItemId()), e);
				return Optional.empty();
			}
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> Optional<T> getItemModel(final String key, final Class<T> clazz) throws UnknownIdentifierException
	{
		final ItemData itemData = getCmsItemDataDataFactory().getObject();
		itemData.setItemId(key);
		itemData.setItemType(ClassFieldFinder.getTypeCode(clazz));
		return (Optional<T>) getItemModel(itemData);
	}

	@Override
	public void afterPropertiesSet() throws Exception
	{
		getUniqueIdentifierConverters()
				.forEach(entry -> getConverterMap().put(entry.getItemType(), entry));
	}

	protected Set<UniqueIdentifierConverter> getUniqueIdentifierConverters()
	{
		return uniqueIdentifierConverters;
	}

	public void setUniqueIdentifierConverters(Set<UniqueIdentifierConverter> uniqueIdentifierConverters)
	{
		this.uniqueIdentifierConverters = uniqueIdentifierConverters;
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

	protected ObjectFactory<ItemData> getCmsItemDataDataFactory()
	{
		return cmsItemDataDataFactory;
	}

	@Required
	public void setCmsItemDataDataFactory(ObjectFactory<ItemData> cmsItemDataDataFactory)
	{
		this.cmsItemDataDataFactory = cmsItemDataDataFactory;
	}

	protected Map<String, UniqueIdentifierConverter> getConverterMap()
	{
		return converterMap;
	}
}
