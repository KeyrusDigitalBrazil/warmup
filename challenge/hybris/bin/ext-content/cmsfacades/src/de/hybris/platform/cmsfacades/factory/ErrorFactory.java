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
package de.hybris.platform.cmsfacades.factory;

import org.springframework.validation.Errors;


/**
 * Factory to creates instance of {@link Errors}
 */
public interface ErrorFactory
{

	/**
	 * Creates instance of {@link Errors}
	 * 
	 * @param object
	 *           the object target to be wrapped
	 * @return a {@link Errors} instance
	 */
	Errors createInstance(final Object object);
}
