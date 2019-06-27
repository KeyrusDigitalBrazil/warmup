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

import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemAttributeModel;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Map;

import org.springframework.beans.factory.annotation.Required;

import com.google.common.base.Preconditions;


/**
 * Abstract class to populate given item model to a Map which is representation of the Integration Object.
 *
 * @param <S> ItemToMapConversionContext which contains information about the IntegrationObject and the ItemModel
 * @param <T> it is a target to populate the values as a Map from given ItemModel
 */
public abstract class AbstractItem2MapPopulator<S extends ItemToMapConversionContext, T extends Map<String, Object>>
		implements Populator<S, T>
{
	private ModelService modelService;

	@Override
	public void populate(final S source, final T target)
	{
		Preconditions.checkArgument(source.getIntegrationObjectItemModel() != null, "Integration object item cannot be null.");
		Preconditions.checkArgument(
				source.getItemModel().getItemtype().equals(source.getIntegrationObjectItemModel().getType().getCode()),
				"item model type must match Integration Object Item type");

		for (final IntegrationObjectItemAttributeModel attributeModel : source.getIntegrationObjectItemModel().getAttributes())
		{
			final AttributeDescriptorModel attributeDescriptor = attributeModel.getAttributeDescriptor();
			final String qualifier = attributeDescriptor.getQualifier();

			if (isApplicable(attributeDescriptor))
			{
				populateToMap(attributeModel, qualifier, source, target);
			}
		}
	}

	/**
	 * Implements the logic to populate the target
	 *
	 * @param attr {@link IntegrationObjectItemAttributeModel} used in the implementation to populate the target
	 * @param qualifier Attribute's qualifier used in the implementation to populate the target
	 * @param source Source used in the implementation to populate the target
	 * @param target Populate the target with the result
	 */
	protected abstract void populateToMap(final IntegrationObjectItemAttributeModel attr, final String qualifier,
			final S source, final T target);

	/**
	 * Indicates whether this Populator is applicable to process the attribute
	 *
	 * @param attributeDescriptor {@link AttributeDescriptorModel} used to test whether the Populator can operate on the attribute
	 * @return {@code true}, if the Populator is applicable, otherwise {@code false}
	 */
	protected abstract boolean isApplicable(final AttributeDescriptorModel attributeDescriptor);

	/**
	 * Gets a reference to the {@link ModelService}
	 *
	 * @return The ModelService
	 */
	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}
}
