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
package de.hybris.platform.subscriptionservices.model;

import de.hybris.platform.core.model.ItemModel;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;


/**
 * Service interface for sorting collections of model instances.
 *
 * @param <T> item type
 */
public interface ModelSortService<T extends ItemModel>
{
	/**
	 * Returns a sorted {@link List} of the elements in the given {@link Collection}.
	 * 
	 * @param collection
	 *           a Collection of elements to be sorted
	 * @return the sorted List of elements
	 */
	@Nullable
	List<T> sort(@Nullable Collection<T> collection);
}
