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
package de.hybris.platform.cmsfacades.cmsitems.attributeconverters;

import de.hybris.platform.cms2.cmsitems.converter.AttributeContentConverter;
import de.hybris.platform.cms2.servicelayer.services.AttributeDescriptorModelHelperService;
import de.hybris.platform.cmsfacades.common.predicate.attributes.EnumTypeAttributePredicate;
import de.hybris.platform.core.HybrisEnumValue;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Required;

import static java.lang.Enum.valueOf;


/**
 * Implementation of {@link AttributeContentConverter} that converts properties of type {@link Enum}
 */
public class EnumAttributeContentConverter implements AttributeContentConverter<AttributeDescriptorModel>
{

	private AttributeDescriptorModelHelperService attributeDescriptorModelHelperService;
	private EnumTypeAttributePredicate isEnumPredicate;
	private EnumerationService enumerationService;

	@Override
	public Predicate<AttributeDescriptorModel> getConstrainedBy()
	{
		return getIsEnumPredicate();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Object convertModelToData(final AttributeDescriptorModel attribute, final Object source)
	{
		if (source != null)
		{
			Class enumClass = getAttributeClass(attribute);
			if (enumClass.isEnum())
			{
				return ((Enum) source).name();
			}
			else if (isDynamicEnum(enumClass))
			{
				return ((HybrisEnumValue) source).getCode();
			}
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object convertDataToModel(final AttributeDescriptorModel attributeDescriptor, final Object source)
	{

		if (source != null)
		{
			Class enumClass = getAttributeClass(attributeDescriptor);

			if (enumClass.isEnum() && source instanceof String)
			{
				return valueOf(enumClass, (String) source);
			}
			if (isDynamicEnum(enumClass) && source instanceof String)
			{
				return getEnumerationService().getEnumerationValue(enumClass, (String) source);
			}
			else
			{
				throw new ConversionException("could not convert to enum");
			}
		}
		else
		{
			return null;
		}
	}

	/**
	 * This method gets the class of the attribute described by the given descriptor.
	 *
	 * @param attributeDescriptor
	 * 		- The descriptor of the attribute whose class to retrieve.
	 * @return the class associated to the given attribute descriptor.
	 */
	protected Class getAttributeClass(final AttributeDescriptorModel attributeDescriptor)
	{
		return getAttributeDescriptorModelHelperService().getAttributeClass(attributeDescriptor);
	}

	/**
	 * This method is used to check if the given class is a dynamic enumeration.
	 * @param enumClass
	 * 		- The class to check
	 * @return <code>true</code> if the class represents a dynamic enum. <code>false</code>, otherwise.
	 */
	protected boolean isDynamicEnum(final Class enumClass)
	{
		return HybrisEnumValue.class.isAssignableFrom(enumClass);
	}

	@Required
	public void setAttributeDescriptorModelHelperService(
			AttributeDescriptorModelHelperService attributeDescriptorModelHelperService)
	{
		this.attributeDescriptorModelHelperService = attributeDescriptorModelHelperService;
	}

	protected AttributeDescriptorModelHelperService getAttributeDescriptorModelHelperService()
	{
		return attributeDescriptorModelHelperService;
	}

	@Required
	public void setIsEnumPredicate(EnumTypeAttributePredicate isEnumPredicate)
	{
		this.isEnumPredicate = isEnumPredicate;
	}

	protected EnumTypeAttributePredicate getIsEnumPredicate()
	{
		return isEnumPredicate;
	}

	protected EnumerationService getEnumerationService()
	{
		return enumerationService;
	}

	@Required
	public void setEnumerationService(final EnumerationService enumerationService)
	{
		this.enumerationService = enumerationService;
	}
}
