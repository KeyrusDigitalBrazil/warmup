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
package de.hybris.platform.customerinterestsfacades.strategies.impl;

import de.hybris.platform.customerinterestsfacades.data.ProductInterestRelationData;
import de.hybris.platform.customerinterestsfacades.strategies.CollectionSortStrategy;

import java.util.List;
import static java.util.Objects.nonNull;


/**
 * Default implementation of {@link CollectionSortStrategy}
 */
public class SortByProductNameStrategy implements CollectionSortStrategy<List<ProductInterestRelationData>>
{

	@Override
	public void ascendingSort(final List<ProductInterestRelationData> list)
	{
		list.sort((a, b) -> (nonNull(a.getProduct().getName()) ? a.getProduct().getName() : "").compareTo(nonNull(b.getProduct()
				.getName()) ? b.getProduct().getName() : ""));
	}
}
