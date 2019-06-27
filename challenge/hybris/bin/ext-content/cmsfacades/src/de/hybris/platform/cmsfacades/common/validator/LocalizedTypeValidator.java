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

import org.springframework.validation.Errors;


/**
 * Validator to validate localized attribute value of a given type.
 */
public interface LocalizedTypeValidator
{
	/**
	 * Validate the attribute value.
	 *
	 * @param language
	 *           - the language of the error message
	 * @param locale
	 *           - the Locale under validation
	 * @param fieldName
	 *           - the name of the field under validation
	 * @param value
	 *           - the attribute value value in the given locale
	 * @param errors
	 *           - the current errors context
	 */
	void validate(final String language, final String fieldName, final String value, final Errors errors);

	/**
	 * Create a localized error message.
	 *
	 * @param language
	 *           - the language of the error message
	 * @param locale
	 *           - the Locale under validation
	 * @param fieldName
	 *           - the name of the field under validation
	 * @param value
	 *           - the attribute value value in the given locale
	 * @param errorCode
	 *           - the error code
	 * @param errors
	 *           - the current errors context
	 */
	void reject(final String language, final String fieldName, final String errorCode, final Errors errors);
}
