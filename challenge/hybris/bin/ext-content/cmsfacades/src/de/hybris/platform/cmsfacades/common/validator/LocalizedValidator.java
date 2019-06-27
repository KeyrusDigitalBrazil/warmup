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

import java.util.function.BiConsumer;
import java.util.function.Function;

import org.springframework.validation.Errors;


/**
 * Validator to validate localized attributes.
 */
public interface LocalizedValidator
{
	/**
	 * Validate localized attributes for all languages.
	 *
	 * @param consumer
	 *           - contains the logic to perform the actual validation
	 * @param function
	 *           - contains the method to use to extract the localized content to be validated
	 * @param errors
	 *           - contains the current error context
	 */
	<T> void validateAllLanguages(BiConsumer<String, T> consumer, Function<String, T> function, Errors errors);

	/**
	 * Validate localized attributes for required languages only.
	 *
	 * @param consumer
	 *           - contains the logic to perform the actual validation
	 * @param function
	 *           - contains the method to use to extract the localized content to be validated
	 * @param errors
	 *           - contains the current error context
	 */
	<T> void validateRequiredLanguages(BiConsumer<String, T> consumer, Function<String, T> function, Errors errors);
}
