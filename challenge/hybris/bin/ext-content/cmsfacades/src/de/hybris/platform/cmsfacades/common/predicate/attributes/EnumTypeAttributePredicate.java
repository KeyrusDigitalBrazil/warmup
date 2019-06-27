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
package de.hybris.platform.cmsfacades.common.predicate.attributes;

import de.hybris.platform.cms2.servicelayer.services.AttributeDescriptorModelHelperService;
import de.hybris.platform.core.HybrisEnumValue;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;

import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Required;


/**
 * Predicate that returns true if the provided {@link AttributeDescriptorModel} is a {@link HybrisEnumValue}
 */
public class EnumTypeAttributePredicate implements Predicate<AttributeDescriptorModel>
{

	private AttributeDescriptorModelHelperService attributeDescriptorModelHelperService;

	@Override
	public boolean test(final AttributeDescriptorModel attributeDescriptor)
	{
		Class<?> type = getAttributeDescriptorModelHelperService().getAttributeClass(attributeDescriptor);
		return type.isEnum() || HybrisEnumValue.class.isAssignableFrom(type);
	}

	protected AttributeDescriptorModelHelperService getAttributeDescriptorModelHelperService()
	{
		return attributeDescriptorModelHelperService;
	}

	@Required
	public void setAttributeDescriptorModelHelperService(
			final AttributeDescriptorModelHelperService attributeDescriptorModelHelperService)
	{
		this.attributeDescriptorModelHelperService = attributeDescriptorModelHelperService;
	}
}
