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

import de.hybris.platform.cms2.common.annotations.HybrisDeprecation;
import de.hybris.platform.core.model.enumeration.EnumerationMetaTypeModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.TypeModel;

import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Required;


/**
 * Predicate to identify if an attribute type is of a given enumeration type.
 * @deprecated since 1808. Reason: not used anywhere.
 */
@Deprecated
@HybrisDeprecation(sinceVersion = "1808")
public class DefaultEnumerationClassTypeAttributePredicate implements Predicate<AttributeDescriptorModel>
{

	private Class typeClass;

	@Override
	public boolean test(final AttributeDescriptorModel attributeDescriptor)
	{
		return isEnum().and(equalsClass()).test(attributeDescriptor.getAttributeType());
	}

	/**
	 * Tests if a type model is of the Enumeration type
	 * @return
	 */
	protected Predicate<TypeModel> isEnum()
	{
		return typeModel -> typeModel.getItemtype().equals(EnumerationMetaTypeModel._TYPECODE);
	}

	/**
	 * Tests if a type code is the same as this predicate's {@code typeClass} 
	 * @return
	 */
	@SuppressWarnings("squid:S1872")
	protected Predicate<TypeModel> equalsClass()
	{
		return typeModel -> typeModel.getCode().equals(getTypeClass().getSimpleName());
	}

	public Class getTypeClass()
	{
		return typeClass;
	}

	@Required
	public void setTypeClass(final Class typeClass)
	{
		this.typeClass = typeClass;
	}
}
