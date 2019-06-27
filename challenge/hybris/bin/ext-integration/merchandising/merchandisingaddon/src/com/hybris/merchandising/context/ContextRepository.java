/**
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.hybris.merchandising.context;
import java.util.Enumeration;

import com.hybris.merchandising.model.ContextMap;



/**
 * Interface for ContextRepository to manage ContextMap.
 *
 */
public interface ContextRepository
{

	/**
	 * Return a single ContextMap object for the given name
	 *
	 * @param name
	 * @return
	 */
	ContextMap get(String name);

	/**
	 * Add a single ContextMap object to the context store
	 *
	 * @param name
	 * @param context
	 */
	void put(String name, ContextMap context);

	/**
	 * Clear out the entire context store
	 */
	void clear();

	/**
	 * return the size of the current context store
	 *
	 * @return
	 */
	int size();

	/**
	 * Returns an enumeration of all keys.
	 *
	 * @return
	 */
	Enumeration<String> keys();
}
