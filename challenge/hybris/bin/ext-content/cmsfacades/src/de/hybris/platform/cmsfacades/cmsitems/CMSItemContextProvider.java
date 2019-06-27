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
package de.hybris.platform.cmsfacades.cmsitems;

/**
 * Interface responsible for storing (in a stack-like data structure) context information per transaction.
 */
public interface CMSItemContextProvider<T>
{
	/**
	 * Initializes and stores a new instance for this transaction.
	 *
	 * @param item
	 *           the value to store
	 */
	void initializeItem(final T item);

	/**
	 * Provides the current instance for this transaction.
	 *
	 * @return the current item
	 */
	T getCurrentItem();

	/**
	 * Finalizes the latest instance for this transaction.
	 */
	void finalizeItem();
}
