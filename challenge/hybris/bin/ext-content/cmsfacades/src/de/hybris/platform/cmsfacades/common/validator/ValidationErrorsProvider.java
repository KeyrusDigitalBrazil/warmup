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


import de.hybris.platform.cmsfacades.exception.ValidationException;

import java.util.Optional;


/**
 * Provider Interface for {@link ValidationErrors}. 
 * This interface provides a single instance of {@link ValidationErrors} per transaction, e.g. Hybris Session. 
 * Use this provider to get the {@link ValidationErrors} at any time during execution time. 
 */
public interface ValidationErrorsProvider
{

	/**
	 * Initializes a new {@link ValidationErrors} instance for this transaction.
	 * @return the current {@link ValidationErrors}
	 */
	ValidationErrors initializeValidationErrors();

	/**
	 * Provides the current {@link ValidationErrors} instance for this transaction. 
	 * @return the current {@link ValidationErrors}
	 */
	ValidationErrors getCurrentValidationErrors();

	/**
	 * Finalizes the latest {@link ValidationErrors} instance for this transaction.
	 */
	void finalizeValidationErrors();

	/**
	 * Collects the errors in the validation exception and adds to the global validation context.
	 * @param e the exception
	 * @param language optional; the validated language
	 * @param position optional; the position in which the object value in the collection
	 */
	void collectValidationErrors(ValidationException e, Optional<String> language, Optional<Integer> position);
}
