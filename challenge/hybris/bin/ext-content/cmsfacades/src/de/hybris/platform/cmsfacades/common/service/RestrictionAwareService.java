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
package de.hybris.platform.cmsfacades.common.service;

import de.hybris.platform.cms2.servicelayer.data.RestrictionData;
import java.util.function.Supplier;


/**
 * Sets restriction information in place during the execution of a {@link Supplier}.
 */
public interface RestrictionAwareService
{
	/**
	 * Makes restriction data available during the execution of the provided supplier.
	 * @param data The restriction data to make available during the execution of the supplier.
	 * @param supplier The supplier to execute.
	 * @param <T> The type returned by the supplier.
	 * @return The value calculated by the supplier.
	 */
	<T> T execute(RestrictionData data, Supplier<T> supplier);
}
