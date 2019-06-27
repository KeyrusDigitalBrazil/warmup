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
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Required;

/**
 * Composite Validator to perform validation with multiple {@link AttributeContentValidator}
 * @param <T> the Object type being validated. 
 */
public class CompositeAttributeContentValidator<T> implements AttributeContentValidator<T>
{
	
	private List<AttributeContentValidator> validators;
	
	@Override
	public List<ValidationError> validate(final T value, final AttributeDescriptorModel attributeDescriptor)
	{
		final Stream<List<ValidationError>> listStream = getValidators() //
				.stream() //
				.map(validator -> validator.validate(value, attributeDescriptor));
				
		return listStream.filter(errors -> errors != null).flatMap(Collection::stream).collect(toList());
	}

	protected List<AttributeContentValidator> getValidators()
	{
		return validators;
	}

	@Required
	public void setValidators(final List<AttributeContentValidator> validators)
	{
		this.validators = validators;
	}
}
