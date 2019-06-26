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
package de.hybris.platform.cmsfacades.media.validator;

import de.hybris.platform.cmsfacades.constants.CmsfacadesConstants;
import de.hybris.platform.cmsfacades.data.MediaData;
import de.hybris.platform.cmsfacades.media.validator.predicate.MediaCodeExistsPredicate;

import java.util.Objects;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;


/**
 * Validates DTO for creating a new media.
 *
 * <p>
 * Rules:</br>
 * <ul>
 * <li>code not null</li>
 * <li>code length not exceeded</li>
 * <li>code does not exist: {@link MediaCodeExistsPredicate}</li>
 * <li>description not null</li>
 * <li>description length not exceeded</li>
 * <li>altText not null</li>
 * <li>altText length not exceeded</li>
 * </ul>
 * </p>
 */
public class CreateMediaValidator implements Validator
{
	protected static final String DESCRIPTION = "description";
	protected static final String CODE = "code";
	protected static final String ALT_TEXT = "altText";

	private Predicate<String> validStringLengthPredicate;
	private Predicate<String> mediaCodeExistsPredicate;

	@Override
	public boolean supports(final Class<?> clazz)
	{
		return MediaData.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(final Object obj, final Errors errors)
	{
		final MediaData target = (MediaData) obj;

		ValidationUtils.rejectIfEmpty(errors, CODE, CmsfacadesConstants.FIELD_REQUIRED);
		ValidationUtils.rejectIfEmpty(errors, DESCRIPTION, CmsfacadesConstants.FIELD_REQUIRED);
		ValidationUtils.rejectIfEmpty(errors, ALT_TEXT, CmsfacadesConstants.FIELD_REQUIRED);

		if (!Objects.isNull(target.getCode()) && !getValidStringLengthPredicate().test(target.getCode()))
		{
			errors.rejectValue(CODE, CmsfacadesConstants.FIELD_LENGTH_EXCEEDED);
		}
		else if (!Objects.isNull(target.getCode()) && getMediaCodeExistsPredicate().test(target.getCode()))
		{
			errors.rejectValue(CODE, CmsfacadesConstants.FIELD_ALREADY_EXIST);
		}

		if (!Objects.isNull(target.getDescription()) && !getValidStringLengthPredicate().test(target.getDescription()))
		{
			errors.rejectValue(DESCRIPTION, CmsfacadesConstants.FIELD_LENGTH_EXCEEDED);
		}

		if (!Objects.isNull(target.getAltText()) && !getValidStringLengthPredicate().test(target.getAltText()))
		{
			errors.rejectValue(ALT_TEXT, CmsfacadesConstants.FIELD_LENGTH_EXCEEDED);
		}

	}

	protected Predicate<String> getValidStringLengthPredicate()
	{
		return validStringLengthPredicate;
	}

	@Required
	public void setValidStringLengthPredicate(final Predicate<String> validStringLengthPredicate)
	{
		this.validStringLengthPredicate = validStringLengthPredicate;
	}

	protected Predicate<String> getMediaCodeExistsPredicate()
	{
		return mediaCodeExistsPredicate;
	}

	@Required
	public void setMediaCodeExistsPredicate(final Predicate<String> mediaCodeExistsPredicate)
	{
		this.mediaCodeExistsPredicate = mediaCodeExistsPredicate;
	}

}
