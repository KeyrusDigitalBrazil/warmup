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
package de.hybris.platform.cmsfacades.types.service.impl;

import de.hybris.platform.cmsfacades.types.service.CMSAttributeTypeService;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.CollectionTypeModel;
import de.hybris.platform.core.model.type.MapTypeModel;
import de.hybris.platform.core.model.type.TypeModel;
import de.hybris.platform.servicelayer.type.TypeService;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link CMSAttributeTypeService}
 */
public class DefaultCMSAttributeTypeService implements CMSAttributeTypeService
{
	private TypeService typeService;

	@Override
	public TypeModel getAttributeContainedType(final AttributeDescriptorModel attribute)
	{
		TypeModel attributeType = attribute.getAttributeType();
		if (isCollection(attributeType))
		{
			attributeType = ((CollectionTypeModel) attributeType).getElementType();
		}
		else if (attribute.getLocalized())
		{
			final TypeModel returnType = ((MapTypeModel) attributeType).getReturntype();
			attributeType = isCollection(returnType) ? ((CollectionTypeModel) returnType).getElementType() : returnType;
		}
		return attributeType;
	}

	protected boolean isCollection(final TypeModel type)
	{
		return type.getItemtype().contains(CollectionTypeModel._TYPECODE);
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
