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
package de.hybris.platform.sap.productconfig.frontend.util.impl;

import de.hybris.platform.sap.productconfig.frontend.validator.ConflictError;
import de.hybris.platform.sap.productconfig.frontend.validator.MandatoryFieldError;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;


/**
 * Utility class to parse specific UI-Errors types from the UI-Error-Binding.<br>
 * There are 3 Error types:
 * <ul>
 * <li>ValidationErrors: Indicating that the user input is considered invalid. They have no specific type, just the
 * default type {@link FieldError}</li>
 * <li>Conflicts: Indicating that the configuration engine has determined a conflict for one or many cstic(-values).
 * They are of specific type {@link ConflictError}</li>
 * <li>Missing Mandatory Fields: Indicating that a cstic has no value assigned, although it is considered mandatory by
 * configuration engine. They are of specific type {@link MandatoryFieldError}</li>
 * </ul>
 * Additionally this class can be used to create UI error messages from the UI errors.
 */
public class ErrorResolver
{
	private ErrorResolver()
	{
		// private constructor - Utility class should hide public constructor
	}


	/**
	 * Get for each error of type {@link ConflictError} an error message.
	 *
	 * @param bindResult
	 *           all UI errors
	 * @return list of error messages
	 */
	public static List<ErrorMessage> getConflictErrors(final BindingResult bindResult)
	{
		return getErrorMessages(bindResult.getFieldErrors(), ErrorResolver::isConfigError,
				ErrorResolver::createErrorMessageForConflictType);
	}

	/**
	 * Get for each error of type {@link MandatoryFieldError} an error message.
	 *
	 * @param bindResult
	 *           all UI errors
	 * @return list of error messages
	 */
	public static List<ErrorMessage> getMandatoryFieldErrors(final BindingResult bindResult)
	{
		return getErrorMessages(bindResult.getFieldErrors(), ErrorResolver::isMandatoryFieldError,
				ErrorResolver::createErrorMessageMandatoryFieldType);
	}

	/**
	 * Get for each error that is either of type {@link MandatoryFieldError} or of type {@link ConflictError} an error
	 * message.
	 *
	 * @param bindResult
	 *           all UI errors
	 * @return list of error messages
	 */
	public static List<ErrorMessage> getWarnings(final BindingResult bindResult)
	{
		final List<ErrorMessage> messages = new ArrayList<>();
		messages.addAll(getConflictErrors(bindResult));
		messages.addAll(getMandatoryFieldErrors(bindResult));
		return messages;
	}

	/**
	 * Get for each error that is neither of type {@link MandatoryFieldError} nor of type {@link ConflictError} an error
	 * message. Those errors are the classical validation errors.
	 *
	 * @param bindResult
	 *           all UI errors
	 * @return list of error messages
	 */
	public static List<ErrorMessage> getValidationErrors(final BindingResult bindResult)
	{
		return getErrorMessages(bindResult.getFieldErrors(), ErrorResolver::isErrorMessage,
				ErrorResolver::createErrorMessageForErrorType);
	}

	/**
	 * Get for each error that is neither of type {@link MandatoryFieldError} nor of type {@link ConflictError} an error
	 * message. Those errors are the classical validation errors.<br>
	 * The Result is filtered for the given cstic path.
	 *
	 * @param bindResult
	 *           all UI errors
	 * @param path
	 *           UI path identifying a cstic
	 * @return list of error messages
	 */
	public static List<ErrorMessage> getValidationErrorsForCstic(final BindingResult bindResult, final String path)
	{
		return getErrorMessages(bindResult.getFieldErrors(path), ErrorResolver::isErrorMessage,
				ErrorResolver::createErrorMessageForErrorType);
	}

	/**
	 * Checks whether the configuration has at lest one classical ValidationError, so an error that is neither of type
	 * {@link MandatoryFieldError} nor of type {@link ConflictError}.
	 *
	 * @param bindResult
	 *           all UI errors
	 * @return <code>true</code> only if at least one validation error exists.
	 */
	public static boolean hasErrorMessages(final BindingResult bindResult)
	{
		return !bindResult.getFieldErrors().stream().noneMatch(ErrorResolver::isErrorMessage);
	}

	/**
	 * Get for each error that is either of type {@link MandatoryFieldError} or of type {@link ConflictError} an error
	 * message.<br>
	 * The Result is filtered for the given cstic path.
	 *
	 * @param bindResult
	 *           all UI errors
	 * @param path
	 *           UI path identifying a cstic
	 * @return list of error messages
	 */
	public static List<ErrorMessage> getWarningsForCstic(final BindingResult bindResult, final String path)
	{
		String modPath = path;
		if (path.endsWith("formattedValue"))
		{
			modPath = path.substring(0, path.length() - 14) + "value";
		}

		return getErrorMessages(bindResult.getFieldErrors(modPath), ErrorResolver::isWarningMessage,
				ErrorResolver::createErrorMessageBasedOnType);
	}

	protected static boolean isWarningMessage(final FieldError objError)
	{
		return objError instanceof ConflictError || objError instanceof MandatoryFieldError;
	}

	protected static boolean isErrorMessage(final FieldError objError)
	{
		return !isWarningMessage(objError);
	}

	protected static ErrorMessage createErrorMessageBasedOnType(final FieldError objError)
	{
		ErrorMessage errorMessage;
		if (objError instanceof MandatoryFieldError)
		{
			errorMessage = createErrorMessageMandatoryFieldType(objError);
		}
		else
		{
			errorMessage = createErrorMessageForConflictType(objError);

		}
		return errorMessage;
	}

	protected static ErrorMessage createErrorMessageMandatoryFieldType(final FieldError objError)
	{
		return createErrorMessageForType(objError, ErrorType.MANDATORY_FIELD);
	}

	protected static ErrorMessage createErrorMessageForConflictType(final FieldError objError)
	{
		return createErrorMessageForType(objError, ErrorType.CONFLICT);
	}

	protected static ErrorMessage createErrorMessageForErrorType(final FieldError objError)
	{
		return createErrorMessageForType(objError, ErrorType.ERROR);
	}

	protected static ErrorMessage createErrorMessageForType(final FieldError objError, final ErrorType type)
	{
		final String path = objError.getField();
		final String defaultErrorMessage = objError.getDefaultMessage();
		final String code;
		final Object[] args;
		if (objError instanceof MandatoryFieldError)
		{
			code = objError.getCodes()[0];
			args = new Object[]
			{ ((MandatoryFieldError) objError).getCstic().getLangdepname() };
		}
		else
		{
			code = objError.getCode();
			args = objError.getArguments();
		}

		final ErrorMessage errorMessage = new ErrorMessage();

		errorMessage.setPath(path);
		errorMessage.setMessage(defaultErrorMessage);
		errorMessage.setCode(code);
		errorMessage.setArgs(args);
		errorMessage.setType(type);

		return errorMessage;
	}

	protected static List<ErrorMessage> getErrorMessages(final List<FieldError> errorList, final Predicate<FieldError> filter,
			final Function<FieldError, ErrorMessage> mapper)
	{
		return errorList.stream().filter(filter).map(mapper).collect(Collectors.toList());
	}

	protected static boolean isMandatoryFieldError(final FieldError error)
	{
		return error instanceof MandatoryFieldError;
	}

	protected static boolean isConfigError(final FieldError error)
	{
		return error instanceof ConflictError;
	}
}
