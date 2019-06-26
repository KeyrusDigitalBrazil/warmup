/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 *
 */
package de.hybris.platform.warehousingwebservices.validators;

import java.util.Date;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import static org.springframework.util.Assert.notNull;


/**
 * Validator checking if the obtained date is not null and not from the past
 */
public class FieldNotPastDateValidator implements Validator
{
	private static final String FIELD_REQUIRED_AND_NOT_FROM_PAST_ID = "field.requiredAndNotFromPast";
	private static final String FIELD_REQUIRED_AND_NOT_FROM_PAST_MESSAGE = "Date field cannot be null and from the past.";

	private String fieldPath;
	private final Date date = new Date();

	@Override
	public boolean supports(final Class clazz)
	{
		return true;
	}

	@Override
	public void validate(final Object target, final Errors errors)
	{
		notNull(errors, "Errors object must not be null");
		final Object fieldValue = getFieldPath() == null ? target : errors.getFieldValue(getFieldPath());

		if (!(fieldValue instanceof Date && ((Date)fieldValue).getTime() > date.getTime()))
		{
			errors.rejectValue(getFieldPath(), FIELD_REQUIRED_AND_NOT_FROM_PAST_ID, new String[]
					{ getFieldPath() }, FIELD_REQUIRED_AND_NOT_FROM_PAST_MESSAGE);
		}
	}

	protected String getFieldPath()
	{
		return fieldPath;
	}

	public void setFieldPath(final String fieldPath)
	{
		this.fieldPath = fieldPath;
	}

}
