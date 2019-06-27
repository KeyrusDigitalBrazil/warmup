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
package de.hybris.platform.cmsfacades.cmsitems.predicates;

import de.hybris.platform.core.model.type.AttributeDescriptorModel;

import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Required;


/**
 * Predicate to identify if an attribute is a content of an enclosing type.  
 */
public class DefaultEnclosingTypeAttributePredicate implements Predicate<AttributeDescriptorModel>
{

	private String typeCode;

	@Override
	public boolean test(final AttributeDescriptorModel attributeDescriptor)
	{
		return attributeDescriptor.getEnclosingType().getCode().equals(typeCode);
	}

	protected String getTypeCode()
	{
		return typeCode;
	}

	@Required
	public void setTypeCode(final String typeCode)
	{
		this.typeCode = typeCode;
	}
}
