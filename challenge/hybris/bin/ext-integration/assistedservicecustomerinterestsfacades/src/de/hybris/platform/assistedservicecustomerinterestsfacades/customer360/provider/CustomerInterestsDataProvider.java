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
package de.hybris.platform.assistedservicecustomerinterestsfacades.customer360.provider;

import de.hybris.platform.assistedservicefacades.customer360.FragmentModelProvider;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.customerinterestsfacades.data.ProductInterestRelationData;
import de.hybris.platform.customerinterestsfacades.productinterest.ProductInterestFacade;

import java.util.List;
import java.util.Map;

import org.apache.solr.common.StringUtils;
import org.springframework.beans.factory.annotation.Required;


public class CustomerInterestsDataProvider implements FragmentModelProvider<List<ProductInterestRelationData>>
{
	private static final String BY_NAME_ASC = "byNameAsc";
	private ProductInterestFacade productInterestFacade;

	@Override
	public List<ProductInterestRelationData> getModel(final Map<String, String> parameters)
	{

		final String listSize = parameters.get("listSize");

		if (StringUtils.isEmpty(listSize))
		{
			throw new IllegalArgumentException(
					"Fragment arguments are not provided for provider [" + CustomerInterestsDataProvider.class.getName() + "] !");
		}

		int limit = 3;
		try
		{
			limit = Integer.parseInt(listSize);
		}
		catch (final NumberFormatException formatException)
		{
			throw new IllegalArgumentException("Provided value [" + listSize + "] is not in a valid integer range!",
					formatException);
		}

		return getProductInterestFacade().getProductsByCustomerInterests(createPageableData(limit));
	}

	/**
	 * @return PageableData
	 */
	protected PageableData createPageableData(final int limit)
	{
		final PageableData pageableData = new PageableData();
		pageableData.setCurrentPage(0);
		pageableData.setPageSize(limit);
		pageableData.setSort(BY_NAME_ASC);
		return pageableData;
	}

	protected ProductInterestFacade getProductInterestFacade()
	{
		return productInterestFacade;
	}

	@Required
	public void setProductInterestFacade(final ProductInterestFacade productInterestFacade)
	{
		this.productInterestFacade = productInterestFacade;
	}
}
