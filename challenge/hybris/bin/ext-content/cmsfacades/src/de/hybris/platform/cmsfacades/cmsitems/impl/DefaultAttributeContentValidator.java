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
package de.hybris.platform.cmsfacades.cmsitems.impl;

import static java.util.stream.Collectors.toList;

import de.hybris.platform.cmsfacades.cmsitems.AttributeContentValidator;
import de.hybris.platform.cmsfacades.validator.data.ValidationError;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Required;

/**
 * Default and generic implementation of the {@link AttributeContentValidator}. 
 * Works as a dispatcher to other AttributeValidators, by processing all validations stored in 
 * injected in this Bean if a {@code Predicate<AttributeDesciptorModel>} holds true. 
 * @param <T> the Object type being validated. 
 */
public class DefaultAttributeContentValidator<T> implements AttributeContentValidator<T>
{
	protected static final String COLLECTION_TYPE = "CollectionType";
	
	private Map<Predicate<AttributeDescriptorModel>, AttributeContentValidator> validatorMap;
	
	@Override
	public List<ValidationError> validate(final T value, final AttributeDescriptorModel attributeDescriptor)
		{
		final Stream<List<ValidationError>> listStream = getValidatorMap().entrySet() //
				.stream() //
				.filter(entry -> entry.getKey().test(attributeDescriptor)) //
				.map(entry -> entry.getValue()) //
				.map(validator -> validator.validate(value, attributeDescriptor));
		
		return listStream.filter(validationErrors -> validationErrors != null).flatMap(Collection::stream).collect(toList());
	}

	/**
	 * will return true if this property is a collection (localized or not) as per our platform type system
	 *
	 * @param attribute the {@link AttributeDescriptorModel} describing the metadata of the property of a class
	 * @return a boolean
	 */
	protected boolean isCollection(AttributeDescriptorModel attribute)
	{
		return attribute.getAttributeType().getItemtype().contains(COLLECTION_TYPE);

	}

	protected Map<Predicate<AttributeDescriptorModel>, AttributeContentValidator> getValidatorMap()
	{
		return validatorMap;
	}

	@Required
	public void setValidatorMap(final Map<Predicate<AttributeDescriptorModel>, AttributeContentValidator> validatorMap)
	{
		this.validatorMap = validatorMap;
	}
}
