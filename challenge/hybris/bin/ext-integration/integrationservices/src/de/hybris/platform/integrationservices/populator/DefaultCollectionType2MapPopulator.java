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
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.CollectionTypeModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemAttributeModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;

/**
 * This class is responsible to populate the value from the source as collection of ItemModel/primitive
 *
 * @param <S> ItemToMapConversionContext which contains information about the IntegrationObject and the ItemModel
 * @param <T> it is a target to populate the values as a Map from given ItemModel
 */
public class DefaultCollectionType2MapPopulator<S extends ItemToMapConversionContext, T extends Map<String, Object>>
		extends AbstractItem2MapPopulator<S, T>
{
	private Converter<ItemToMapConversionContext, Map<String, Object>> itemToIntegrationObjectMapConverter;

   @Override
   protected void populateToMap(final IntegrationObjectItemAttributeModel attributeModel, final String qualifier,
   		final S context, final T target)
   {
	   final Collection<Object> itemModels = getModelService().getAttributeValue(context.getItemModel(), qualifier);

		if (CollectionUtils.isNotEmpty(itemModels) && populateDeeper(context, attributeModel.getReturnIntegrationObjectItem()))
		{
			final Object value = itemModels.stream().map(itemModel ->
			{
				if (itemModel instanceof ItemModel)
				{
					final ItemToMapConversionContext conversionContext = getConversionContext((ItemModel) itemModel,
							attributeModel.getReturnIntegrationObjectItem());

					conversionContext.setParentContext(context);

					return getItemToIntegrationObjectMapConverter().convert(conversionContext);
				}

				return itemModel;

			}).collect(Collectors.toList());

			target.put(attributeModel.getAttributeName(), value);
		}
	}

	@Override
	protected boolean isApplicable(final AttributeDescriptorModel attributeDescriptor)
	{
		return attributeDescriptor.getAttributeType() instanceof CollectionTypeModel;
	}

	protected boolean populateDeeper(final ItemToMapConversionContext conversionContext,
			final IntegrationObjectItemModel attributeTypeModel)
	{
		final ItemToMapConversionContext parent = conversionContext.getParentContext();

		if (parent != null)
		{
			if (attributeTypeModel.getType().getCode().equals(parent.getItemModel().getItemtype()))
			{
				return false;
			}

			populateDeeper(parent, attributeTypeModel);
		}

		return true;
	}

	protected ItemToMapConversionContext getConversionContext(final ItemModel item,
			final IntegrationObjectItemModel integrationObjectItem)
	{
		final ItemToMapConversionContext conversionContext = new ItemToMapConversionContext();
		conversionContext.setItemModel(item);
		conversionContext.setIntegrationObjectItemModel(integrationObjectItem);
		return conversionContext;
	}

	public Converter<ItemToMapConversionContext, Map<String, Object>> getItemToIntegrationObjectMapConverter()
	{
		return itemToIntegrationObjectMapConverter;
	}

	@Required
	public void setItemToIntegrationObjectMapConverter(
			final Converter<ItemToMapConversionContext, Map<String, Object>> itemToIntegrationObjectMapConverter)
	{
		this.itemToIntegrationObjectMapConverter = itemToIntegrationObjectMapConverter;
	}
}
