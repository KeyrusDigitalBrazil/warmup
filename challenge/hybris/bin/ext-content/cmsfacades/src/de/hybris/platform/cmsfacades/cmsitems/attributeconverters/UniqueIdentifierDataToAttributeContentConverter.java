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

import de.hybris.platform.cms2.common.functions.Converter;
import de.hybris.platform.cmsfacades.uniqueidentifier.UniqueItemIdentifierService;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Required;


/**
 * Abstract Unique Identifier Attribute Converter for {@link ItemModel} child types.
 * It converts the model using the {@link UniqueItemIdentifierService}
 * @param <T> the type parameter which extends the {@link ItemModel} type
 */
public class UniqueIdentifierDataToAttributeContentConverter<T extends ItemModel> implements Converter<String, T>
{
	private UniqueItemIdentifierService uniqueItemIdentifierService;
	private Class<? extends ItemModel> modelClass;

	@SuppressWarnings("unchecked")
	@Override
	public T convert(final String source)
	{
		if (Objects.isNull(source))
		{
			return null;
		}
		return (T) getUniqueItemIdentifierService().getItemModel(source, getModelClass()).orElseThrow(
				() -> new ConversionException("could not convert source [" + source + "] for [" + getModelClass() + "]"));
	}

	protected UniqueItemIdentifierService getUniqueItemIdentifierService()
	{
		return uniqueItemIdentifierService;
	}

	@Required
	public void setUniqueItemIdentifierService(final UniqueItemIdentifierService uniqueItemIdentifierService)
	{
		this.uniqueItemIdentifierService = uniqueItemIdentifierService;
	}

	protected Class<? extends ItemModel> getModelClass()
	{
		return modelClass;
	}

	@Required
	public void setModelClass(final Class<? extends ItemModel> modelClass)
	{
		this.modelClass = modelClass;
	}
}
