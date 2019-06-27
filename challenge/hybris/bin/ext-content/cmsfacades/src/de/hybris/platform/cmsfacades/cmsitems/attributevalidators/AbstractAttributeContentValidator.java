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
package de.hybris.platform.cmsfacades.cmsitems.attributevalidators;

import static java.util.stream.Collectors.toMap;

import de.hybris.platform.cmsfacades.cmsitems.AttributeContentValidator;
import de.hybris.platform.cmsfacades.common.validator.ValidationErrorsProvider;
import de.hybris.platform.cmsfacades.validator.data.ValidationError;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.validation.model.constraints.AttributeConstraintModel;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Required;

/**
 * Abstract implementation of the {@link AttributeContentValidator} that holds a reference to {@link ValidationErrorsProvider}. 
 * @param <T> type of the object being validated
 */
public abstract class AbstractAttributeContentValidator<T> implements AttributeContentValidator<T>
{
	private ValidationErrorsProvider validationErrorsProvider;
	
	protected ValidationErrorsProvider getValidationErrorsProvider()
	{
		return validationErrorsProvider;
	}

	@Required
	public void setValidationErrorsProvider(final ValidationErrorsProvider validationErrorsProvider)
	{
		this.validationErrorsProvider = validationErrorsProvider;
	}

	/**
	 * Returns the Constraint Map for this Attribute filtered by the given filter. 
	 * @param attribute the attribute descriptor in question
	 * @param filter the filter to accept the {@link AttributeConstraintModel}
	 * @return Constraint Map
	 */
	protected Map<String, AttributeConstraintModel> getConstraintMap(final AttributeDescriptorModel attribute, 
			final Predicate<AttributeConstraintModel> filter)
	{
		return attribute //
				.getConstraints() //
				.stream() //
				.filter(filter) //
				.collect(toMap(constraint -> constraint.getItemtype(), Function.identity(), (c1, c2) -> c1));
	}

	/**
	 * Adds validation error to the current validation context. 
	 * @param validationError the validation to be added. 
	 */
	protected void addValidationError(final ValidationError validationError)
	{
		getValidationErrorsProvider().getCurrentValidationErrors().add(validationError);
	}
	
}
