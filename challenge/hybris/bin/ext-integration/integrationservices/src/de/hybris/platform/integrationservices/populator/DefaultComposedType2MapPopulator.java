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
package de.hybris.platform.integrationservices.populator;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.enumeration.EnumerationMetaTypeModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.core.model.type.TypeModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemAttributeModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.Map;


/**
 * Populate the composed type of item model's attribute to Map.
 * Note, the enumerate meta type dose not include.
 */
public class DefaultComposedType2MapPopulator<S extends ItemToMapConversionContext, T extends Map<String, Object>>
		extends AbstractItem2MapPopulator<S, T>
{
	private Converter<ItemToMapConversionContext, Map<String, Object>> itemToIntegrationObjectMapConverter;

	@Override
	protected void populateToMap(final IntegrationObjectItemAttributeModel attributeModel, final String qualifier,
			final S context, final T target)
	{
		final ItemModel itemModel = context.getItemModel();

		final ItemModel valueItem = getModelService().getAttributeValue(itemModel, qualifier);

		if (valueItem != null && attributeModel.getReturnIntegrationObjectItem() != null)
		{
			final ItemToMapConversionContext conversionContext =
					getConversionContext(valueItem, attributeModel.getReturnIntegrationObjectItem());
			target.put(attributeModel.getAttributeName(), getItemToIntegrationObjectMapConverter().convert(conversionContext));
		}
	}

	protected ItemToMapConversionContext getConversionContext(final ItemModel item,
			final IntegrationObjectItemModel integrationObjectItem)
	{
		final ItemToMapConversionContext conversionContext = new ItemToMapConversionContext();
		conversionContext.setItemModel(item);
		conversionContext.setIntegrationObjectItemModel(integrationObjectItem);
		return conversionContext;
	}

	@Override
	protected boolean isApplicable(final AttributeDescriptorModel attributeDescriptor)
	{
		final TypeModel attributeTypeModel = attributeDescriptor.getAttributeType();
		return attributeTypeModel instanceof ComposedTypeModel && !(attributeTypeModel instanceof EnumerationMetaTypeModel);
	}

	public Converter<ItemToMapConversionContext, Map<String, Object>> getItemToIntegrationObjectMapConverter()
	{
		return itemToIntegrationObjectMapConverter;
	}

	public void setItemToIntegrationObjectMapConverter(
			final Converter<ItemToMapConversionContext, Map<String, Object>> itemToIntegrationObjectMapConverter)
	{
		this.itemToIntegrationObjectMapConverter = itemToIntegrationObjectMapConverter;
	}
}
