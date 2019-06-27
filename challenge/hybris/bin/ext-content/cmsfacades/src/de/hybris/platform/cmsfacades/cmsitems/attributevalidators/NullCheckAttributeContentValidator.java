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

import static de.hybris.platform.cmsfacades.common.validator.ValidationErrorBuilder.newValidationErrorBuilder;

import de.hybris.platform.cmsfacades.constants.CmsfacadesConstants;
import de.hybris.platform.cmsfacades.validator.data.ValidationError;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.validation.model.constraints.NotEmptyConstraintModel;
import de.hybris.platform.validation.model.constraints.jsr303.NotNullConstraintModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.util.Strings;

/**
 * Null check attribute content validator adds validation errors when value is null and attribute has {@link NotNullConstraintModel} and {@link NotEmptyConstraintModel} constraints. 
 * @param <T> type of the object being validated
 */
public class NullCheckAttributeContentValidator<T> extends AbstractAttributeContentValidator<T>
{
	@Override
	public List<ValidationError> validate(final T value, final AttributeDescriptorModel attribute)
	{
		final List<ValidationError> errors = new ArrayList<>();

		if (isEmpty(value))
		{
			errors.add(
				newValidationErrorBuilder() //
						.field(attribute.getQualifier()) //
						.errorCode(CmsfacadesConstants.FIELD_REQUIRED) //
						.build()
			);
		}
		return errors;
	}

	/**
	 * Verifies if an object is empty. 
	 * Possible value types are: String, Collection and Map. 
	 * @param value if the value is empty
	 * @return {@code true} if the value is empty; {@code false} otherwise. 
	 */
	protected boolean isEmpty(final T value)
	{
		final boolean isEmpty;
		if (value == null)
		{
			isEmpty = true;
		}
		else if (value instanceof String)
		{
			isEmpty = Strings.isEmpty((String) value);
		}
		else if (value instanceof Collection)
		{
			isEmpty = ((Collection) value).isEmpty(); 
		}
		else if (value instanceof Map)
		{
			isEmpty = ((Map) value).isEmpty();
		}
		else
		{
			isEmpty = false;
		}
		return isEmpty;
	}
}
