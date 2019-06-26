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
package de.hybris.platform.cmsfacades.rendering.predicates.attributes;

import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cmsfacades.types.service.CMSAttributeTypeService;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.TypeModel;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Required;


/**
 * This predicate is used to test whether an attribute is or has elements of type {@link AbstractCMSComponentModel}.
 */
public class AttributeContainsCMSComponentsPredicate implements Predicate<AttributeDescriptorModel>
{
	private CMSAttributeTypeService cmsAttributeTypeService;
	private TypeService typeService;

	@Override
	public boolean test(final AttributeDescriptorModel attributeDescriptorModel)
	{
		return !attributeDescriptorModel.getPrimitive()
				&& isCmsComponentType(getCmsAttributeTypeService().getAttributeContainedType(attributeDescriptorModel));
	}

	protected boolean isCmsComponentType(final TypeModel attributeType)
	{
		return getTypeService().isAssignableFrom(AbstractCMSComponentModel._TYPECODE, attributeType.getCode());
	}

	protected CMSAttributeTypeService getCmsAttributeTypeService()
	{
		return cmsAttributeTypeService;
	}

	@Required
	public void setCmsAttributeTypeService(final CMSAttributeTypeService typeService)
	{
		this.cmsAttributeTypeService = typeService;
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
