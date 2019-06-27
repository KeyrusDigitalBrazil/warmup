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
import de.hybris.platform.core.model.type.MapTypeModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemAttributeModel;
import de.hybris.platform.servicelayer.i18n.I18NService;

import java.util.Map;

import org.springframework.beans.factory.annotation.Required;

/**
 * Populate the map type of item model's attribute to Map.
 */
public class DefaultMapType2MapPopulator<S extends ItemToMapConversionContext, T extends Map<String, Object>>
		extends AbstractItem2MapPopulator<S, T>
{
	private I18NService i18NService;

	@Override
	protected void populateToMap(final IntegrationObjectItemAttributeModel attr, final String qualifier,
			final S context, final T target)
	{
		if (attr.getAttributeDescriptor().getLocalized())
		{
			final ItemModel itemModel = context.getItemModel();

			final Object value = getModelService().getAttributeValue(itemModel, qualifier, getI18NService().getCurrentLocale());

			if (value != null)
			{
				target.put(attr.getAttributeName(), value);
			}
		}
	}

	@Override
	protected boolean isApplicable(final AttributeDescriptorModel attributeDescriptor)
	{
		return attributeDescriptor.getAttributeType() instanceof MapTypeModel;
	}

	protected I18NService getI18NService()
	{
		return i18NService;
	}

	@Required
	public void setI18NService(final I18NService i18NService)
	{
		this.i18NService = i18NService;
	}
}
