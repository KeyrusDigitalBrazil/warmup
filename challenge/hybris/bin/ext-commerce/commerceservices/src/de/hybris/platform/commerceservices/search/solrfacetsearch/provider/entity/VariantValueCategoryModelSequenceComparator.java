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
package de.hybris.platform.commerceservices.search.solrfacetsearch.provider.entity;

import de.hybris.platform.variants.model.VariantValueCategoryModel;

import java.util.Comparator;


/**
 * Comparator for type {@link VariantValueCategoryModel}.
 */
public class VariantValueCategoryModelSequenceComparator implements Comparator<VariantValueCategoryModel>
{

	@Override
	public int compare(final VariantValueCategoryModel variantValueCategory1, final VariantValueCategoryModel variantValueCategory2)
	{
		if (variantValueCategory1 != null && variantValueCategory1.getSequence() != null && variantValueCategory2 != null
				&& variantValueCategory2.getSequence() != null)
		{
			return Integer.compare(variantValueCategory1.getSequence().intValue(), variantValueCategory2.getSequence().intValue());
		}
		else
		{
			throw new IllegalArgumentException("Cannot compare variants that are null or without sequence.");
		}
	}
}
