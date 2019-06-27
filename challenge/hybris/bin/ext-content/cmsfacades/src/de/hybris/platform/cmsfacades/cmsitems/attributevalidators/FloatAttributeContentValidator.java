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

import de.hybris.platform.cmsfacades.validator.data.ValidationError;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;

import java.util.ArrayList;
import java.util.List;

import static de.hybris.platform.cmsfacades.common.validator.ValidationErrorBuilder.newValidationErrorBuilder;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.FIELD_NOT_FLOAT;
import static java.lang.Float.parseFloat;


/**
 * Float validator adds validation errors when the value is not parsable into a Float
 */
public class FloatAttributeContentValidator extends NumberAttributeContentValidator
{
	/*
	 * Suppress sonar warning (squid:S1166 | Exception handlers should preserve the original exceptions) : It is
	 * perfectly acceptable not to handle "e" here
	 */
	@SuppressWarnings("squid:S1166")
	@Override
	public List<ValidationError> validate(final Object value, final AttributeDescriptorModel attribute)
	{
		final List<ValidationError> errors = new ArrayList<>();

		if (value == null)
		{
			return errors;
		}

		try
		{
			parseFloat(value.toString());
			errors.addAll(super.validate(value, attribute));
		}
		catch (NumberFormatException e)
		{
			errors.add(
					newValidationErrorBuilder() //
							.field(attribute.getQualifier()) //
							.errorCode(FIELD_NOT_FLOAT) //
							.build());
		}

		return errors;
	}

}
