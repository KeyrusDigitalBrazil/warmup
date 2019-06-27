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
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.FIELD_MAX_VIOLATED;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.FIELD_MIN_VIOLATED;

import de.hybris.platform.cmsfacades.validator.data.ValidationError;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.validation.model.constraints.AttributeConstraintModel;
import de.hybris.platform.validation.model.constraints.jsr303.MaxConstraintModel;
import de.hybris.platform.validation.model.constraints.jsr303.MinConstraintModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Sets;

/**
 * Integer validator adds validation errors when the value does not respects the attribute's constraints.  
 */
public class NumberAttributeContentValidator extends AbstractAttributeContentValidator<Object>
{
	private final Set<String> constraints = Sets.newHashSet(MinConstraintModel._TYPECODE, MaxConstraintModel._TYPECODE);
	
	@Override
	public List<ValidationError> validate(final Object value, final AttributeDescriptorModel attribute)
	{
		final List<ValidationError> errors = new ArrayList<>();
		
		if (value == null)
		{
			return errors;
		}
		final Map<String, AttributeConstraintModel> validConstraintsMap = getConstraintMap(attribute, constraint -> getConstraints().contains(constraint.getItemtype()));

		final MinConstraintModel minConstraint = (MinConstraintModel) validConstraintsMap.get(MinConstraintModel._TYPECODE);
		final MaxConstraintModel maxConstraint = (MaxConstraintModel) validConstraintsMap.get(MaxConstraintModel._TYPECODE);
		if (minConstraint != null && Long.valueOf(value.toString()) < minConstraint.getValue())
		{
			errors.add(
					newValidationErrorBuilder() //
							.field(attribute.getQualifier()) //
							.errorCode(FIELD_MIN_VIOLATED) //
							.build()
			);	
		}
		
		if (maxConstraint != null && Long.valueOf(value.toString()) > maxConstraint.getValue())
		{
			errors.add(
					newValidationErrorBuilder() //
							.field(attribute.getQualifier()) //
							.errorCode(FIELD_MAX_VIOLATED) //
							.build()
			);
		}
		return errors;
	}

	protected Set<String> getConstraints()
	{
		return constraints;
	}
}
