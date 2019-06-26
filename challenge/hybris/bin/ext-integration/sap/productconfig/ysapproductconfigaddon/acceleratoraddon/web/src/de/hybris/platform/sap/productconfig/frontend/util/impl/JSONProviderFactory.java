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

import javax.json.spi.JsonProvider;


/**
 * Factory to instantiate a JSONProvider. As the JSONProvider is considered Thread Safe and instantiating it is quite
 * expensive in terms of memory consumption, this factory will ensure that there is only instance created.
 */
public final class JSONProviderFactory
{
	private static JsonProvider jsonProvider;

	private JSONProviderFactory()
	{
		//make implicit public constructor private
	}

	/**
	 * @return JSONProvider instance
	 */
	public static synchronized JsonProvider getJSONProvider()
	{
		if (null == jsonProvider)
		{
			// expensive call, due to reflection and class loading, however the provider itself is considered Thread Safe.
			// so it is sufficient to intstantiate only one instance and share it.
			jsonProvider = JsonProvider.provider();
		}
		return jsonProvider;
	}
}
