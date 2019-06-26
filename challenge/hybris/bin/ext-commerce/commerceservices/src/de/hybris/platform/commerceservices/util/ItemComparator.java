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
package de.hybris.platform.commerceservices.util;

import de.hybris.platform.core.model.ItemModel;

/**
 * hybris item comparator that orders results by PK.
 * This comparator provides a stable order over any type of hybris item.
 * The ordering is unknown but it won't change.
 */
public class ItemComparator extends AbstractComparator<ItemModel>
{
	public static final ItemComparator INSTANCE = new ItemComparator();

	@Override
	protected int compareInstances(final ItemModel item1, final ItemModel item2)
	{
		return item1.getPk().compareTo(item2.getPk());
	}
}
