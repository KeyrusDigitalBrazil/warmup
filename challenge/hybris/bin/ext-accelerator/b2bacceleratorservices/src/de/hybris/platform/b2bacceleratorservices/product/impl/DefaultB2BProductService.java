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
package de.hybris.platform.b2bacceleratorservices.product.impl;

import de.hybris.platform.b2bacceleratorservices.dao.PagedB2BProductDao;
import de.hybris.platform.b2bacceleratorservices.product.B2BProductService;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.product.ProductModel;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of the {@link B2BProductService} interface.
 */
public class DefaultB2BProductService implements B2BProductService
{
	private PagedB2BProductDao pagedB2BProductDao;

	/**
	 * @deprecated Since 6.0. Use {@link #getProductsForSkus(Collection, PageableData)} instead.
	 */
	@Deprecated
	@Override
	public SearchPageData<ProductModel> findProductsForSkus(final Collection<String> skus, final PageableData pageableData)
	{
		return getPagedB2BProductDao().findPagedProductsForSkus(skus, pageableData);
	}

	@Override
	public SearchPageData<ProductModel> getProductsForSkus(final Collection<String> skus, final PageableData pageableData)
	{
		return getPagedB2BProductDao().findPagedProductsForSkus(skus, pageableData);
	}

	@Required
	public void setPagedB2BProductDao(final PagedB2BProductDao<ProductModel> pagedB2BProductDao)
	{
		this.pagedB2BProductDao = pagedB2BProductDao;
	}

	protected PagedB2BProductDao<ProductModel> getPagedB2BProductDao()
	{
		return pagedB2BProductDao;
	}
}
