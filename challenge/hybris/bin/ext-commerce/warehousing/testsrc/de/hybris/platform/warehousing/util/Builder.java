/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 *
 */
package de.hybris.platform.warehousing.util;


/**
 * @param <T>
 * 		the type of the instance that is going to be built
 */
public interface Builder<T>
{

	/**
	 * Builds an instance of type T (e.g., a SourcingLocation, a FitnessContext, ...)
	 *
	 * @return the instance
	 */
	T build();
}
