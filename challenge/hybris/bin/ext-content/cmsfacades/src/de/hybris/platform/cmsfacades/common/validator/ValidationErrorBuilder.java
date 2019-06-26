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
package de.hybris.platform.cmsfacades.common.validator;

import de.hybris.platform.cmsfacades.validator.data.ValidationError;

/**
 * Builder class for {@link ValidationError}
 */
public class ValidationErrorBuilder
{
	private String field;
	private Object rejectedValue;
	private String language;
	private String errorCode;
	private Object[] errorArgs;
	private String exceptionMessage;
	private Integer position;

	private ValidationErrorBuilder()
	{
	}

	public ValidationError build()
	{
		final ValidationError validationError = new ValidationError();
		validationError.setField(field);
		validationError.setErrorArgs(errorArgs);
		validationError.setErrorCode(errorCode);
		validationError.setExceptionMessage(exceptionMessage);
		validationError.setLanguage(language);
		validationError.setPosition(position);
		validationError.setRejectedValue(rejectedValue);
		return validationError;
	}

	public static ValidationErrorBuilder newValidationErrorBuilder()
	{
		return new ValidationErrorBuilder();
	}


	public ValidationErrorBuilder field(String field)
	{
		this.field = field;
		return this;
	}

	public ValidationErrorBuilder rejectedValue(Object rejectedValue)
	{
		this.rejectedValue = rejectedValue;
		return this;
	}

	public ValidationErrorBuilder language(String language)
	{
		this.language = language;
		return this;
	}

	public ValidationErrorBuilder errorCode(String errorCode)
	{
		this.errorCode = errorCode;
		return this;
	}

	public ValidationErrorBuilder errorArgs(Object[] errorArgs)
	{
		this.errorArgs = errorArgs;
		return this;
	}

	public ValidationErrorBuilder exceptionMessage(String exceptionMessage)
	{
		this.exceptionMessage = exceptionMessage;
		return this;
	}

	public ValidationErrorBuilder position(Integer position)
	{
		this.position = position;
		return this;
	}


}
