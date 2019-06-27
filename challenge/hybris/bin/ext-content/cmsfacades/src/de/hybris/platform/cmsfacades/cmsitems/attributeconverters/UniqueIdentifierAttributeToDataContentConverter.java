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
import de.hybris.platform.cmsfacades.data.ItemData;
import de.hybris.platform.cmsfacades.uniqueidentifier.UniqueItemIdentifierService;
import de.hybris.platform.core.model.ItemModel;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Required;


/**
 * Abstract Unique Identifier Attribute Converter for {@link ItemModel} types.
 * It converts the model using the {@link UniqueItemIdentifierService}
 * @param <T> the type parameter which extends the {@link ItemModel} type
 */
public class UniqueIdentifierAttributeToDataContentConverter<T extends ItemModel> implements Converter<T, String>
{

	private UniqueItemIdentifierService uniqueItemIdentifierService;

	@Override
	public String convert(final T source)
	{
		if (Objects.isNull(source))
		{
			return null;
		}
		return getUniqueItemIdentifierService() //
				.getItemData(source) //
				.map(ItemData::getItemId) //
				.orElseGet(() -> null);
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
}

