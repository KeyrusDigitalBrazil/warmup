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

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import static de.hybris.platform.cmsfacades.common.validator.ValidationErrorBuilder.newValidationErrorBuilder;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.DATE_TIME_FORMAT;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.FIELD_FORMAT_INVALID;
import static java.time.ZoneOffset.UTC;
import static java.time.ZonedDateTime.parse;
import static java.util.Date.from;
import static java.util.Objects.isNull;


/**
 * Date Time Format validator adds validation errors when the value does not respect the expected date time format.
 */
public class DateFormatAttributeContentValidator extends AbstractAttributeContentValidator<String>
{
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);

	/*
	 * Suppress sonar warning (squid:S1166 | Exception handlers should preserve the original exceptions) : It is
	 * perfectly acceptable not to handle "e" here
	 */
	@SuppressWarnings("squid:S1166")
	@Override
	public List<ValidationError> validate(final String value, final AttributeDescriptorModel attribute)
	{
		final List<ValidationError> errors = new ArrayList<>();
		if (isNull(value))
		{
			return errors;
		}
		try
		{
			from(parse(value, DATE_FORMATTER.withZone(UTC)).toInstant());
		}
		catch (final DateTimeParseException e)
		{
			errors.add(
					newValidationErrorBuilder() //
							.field(attribute.getQualifier()) //
							.errorCode(FIELD_FORMAT_INVALID) //
							.exceptionMessage(e.getMessage()) //
							.build()
					);
		}
		return errors;
	}

}
