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
package de.hybris.platform.assistedservicefacades.customer360;

import java.util.Map;


/**
 * Customer360 fragments model provider interface
 */
public interface FragmentModelProvider<T>
{

	/**
	 * Return restricted results based on parameters
	 *
	 * @param parameters
	 *           to restrict results
	 * @return results depends on the provided type
	 */
	T getModel(final Map<String, String> parameters);
}
