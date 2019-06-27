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

import static java.util.stream.Collectors.toMap;

import de.hybris.platform.core.HybrisEnumValue;
import de.hybris.platform.core.model.enumeration.EnumerationMetaTypeModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemAttributeModel;

import java.util.Map;


/**
 * Populate the enumerate meta type of item model's attribute to Map.
 */
public class DefaultEnumerationMetaType2MapPopulator<S extends ItemToMapConversionContext, T extends Map<String, Object>>
		extends AbstractItem2MapPopulator<S, T>
{
	private static final String HYBRIS_ENUM_CODE = "code";

	@Override
	protected void populateToMap(final IntegrationObjectItemAttributeModel attributeModel, final String qualifier,
			final S context, final T target)
	{
		if (attributeModel.getReturnIntegrationObjectItem() != null)
		{
			final HybrisEnumValue value = getModelService().getAttributeValue(context.getItemModel(), qualifier);
			final Map<String, Object> map =
					attributeModel.getReturnIntegrationObjectItem().getAttributes().stream()
					.filter(attr -> HYBRIS_ENUM_CODE.equals(attr.getAttributeDescriptor().getQualifier()))
					.collect(toMap(IntegrationObjectItemAttributeModel::getAttributeName, v -> value.getCode()));

			target.put(attributeModel.getAttributeName(), map);
		}
	}

	@Override
	protected boolean isApplicable(final AttributeDescriptorModel attributeDescriptor)
	{
		return attributeDescriptor.getAttributeType() instanceof EnumerationMetaTypeModel;
	}
}
