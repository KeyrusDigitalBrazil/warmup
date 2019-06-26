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
package de.hybris.platform.cmsfacades.common.function;

/**
 * Validator is a Functional Interface that consumes an object to be validated.
 * @param <T> the type of the input to be validated. 
 */
@FunctionalInterface
public interface Validator<T>
{
	/**
	 * Method to perform validation on a given object. 
	 * @param validatee the inpected object being validated. 
	 */
	void validate(T validatee);

}
