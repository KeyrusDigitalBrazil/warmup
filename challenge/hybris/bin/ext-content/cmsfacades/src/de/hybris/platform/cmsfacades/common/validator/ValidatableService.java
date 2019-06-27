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

import java.util.function.Supplier;

/**
 * Interface to process the validatable supplier and throws a {@link ValidationException} with the ValidationErrors when errors > 0. 
 */
public interface ValidatableService
{
	/**
	 * Executes the validatable supplier to collect any validation error after execution
	 * @param validatable the validatable supplier. 
	 * @return the expected result after successful validation
	 * @throws ValidationException when there are validation errors after completion of the given supplier. 
	 */
	<T> T execute(Supplier<T> validatable);
	
}
