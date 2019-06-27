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
package de.hybris.platform.b2b.company.impl;

import de.hybris.platform.b2b.company.B2BCommerceCostCenterService;
import de.hybris.platform.b2b.model.B2BCostCenterModel;
import de.hybris.platform.b2b.services.B2BCostCenterService;
import de.hybris.platform.commerceservices.search.dao.PagedGenericDao;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;

import org.springframework.util.Assert;


/**
 * Default implementation of {@link B2BCommerceCostCenterService}
 */
public class DefaultB2BCommerceCostCenterService implements B2BCommerceCostCenterService
{
	private B2BCostCenterService b2BCostCenterService;
	private PagedGenericDao<B2BCostCenterModel> pagedB2BCostCenterDao;

	@Override
	public <T extends B2BCostCenterModel> T getCostCenterForCode(final String costCenterCode)
	{
		return (T) getB2BCostCenterService().getCostCenterForCode(costCenterCode);
	}

	@Override
	public SearchPageData<B2BCostCenterModel> getPagedCostCenters(final PageableData pageableData)
	{
		Assert.notNull(pageableData, "PageableData can not be null!");
		return getPagedB2BCostCenterDao().find(pageableData);
	}

	public B2BCostCenterService getB2BCostCenterService()
	{
		return b2BCostCenterService;
	}

	public void setB2BCostCenterService(final B2BCostCenterService b2bCostCenterService)
	{
		b2BCostCenterService = b2bCostCenterService;
	}

	public PagedGenericDao<B2BCostCenterModel> getPagedB2BCostCenterDao()
	{
		return pagedB2BCostCenterDao;
	}

	public void setPagedB2BCostCenterDao(final PagedGenericDao<B2BCostCenterModel> pagedB2BCostCenterDao)
	{
		this.pagedB2BCostCenterDao = pagedB2BCostCenterDao;
	}
}
