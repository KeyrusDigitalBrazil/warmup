/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.customerinterestsfacades.strategies;

import java.util.Collection;
import java.util.Collections;
import java.util.List;


/**
 * Strategy to sort a given collection
 */
public interface CollectionSortStrategy<T extends Collection>
{
	/**
	 * ascending order of a given attribute
	 *
	 * @param list
	 */
	void ascendingSort(T list);

	/**
	 * descending order of a given attribute
	 *
	 * @param list
	 */
	default void descendingSort(final T list)
	{
		ascendingSort(list);
		Collections.reverse((List<?>) list);
	}
}
