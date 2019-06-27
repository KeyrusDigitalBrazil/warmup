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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCostCenterModel;
import de.hybris.platform.b2b.services.impl.DefaultB2BCostCenterService;
import de.hybris.platform.commerceservices.search.dao.PagedGenericDao;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultB2BCommerceCostCenterServiceTest
{
	private DefaultB2BCommerceCostCenterService defaultB2BCommerceCostCenterService;

	@Mock
	private DefaultB2BCostCenterService defaultB2BCostCenterService;

	@Mock
	private PagedGenericDao<B2BCostCenterModel> pagedB2BCostCenterDao;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		defaultB2BCommerceCostCenterService = new DefaultB2BCommerceCostCenterService();
		defaultB2BCommerceCostCenterService.setB2BCostCenterService(defaultB2BCostCenterService);
		defaultB2BCommerceCostCenterService.setPagedB2BCostCenterDao(pagedB2BCostCenterDao);
	}

	@Test
	public void shoulGetB2BCostCenter()
	{
		final String COST_CENTER_CODE = "costCenterCode";
		final B2BCostCenterModel b2BCostCenter = mock(B2BCostCenterModel.class);
		when(defaultB2BCostCenterService.getCostCenterForCode(COST_CENTER_CODE)).thenReturn(b2BCostCenter);
		Assert.assertEquals(b2BCostCenter, defaultB2BCommerceCostCenterService.getCostCenterForCode(COST_CENTER_CODE));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void shoulGetPagedCostCenters()
	{
		final SearchPageData<B2BCostCenterModel> b2BCostCenters = mock(SearchPageData.class);
		final PageableData pageableData = mock(PageableData.class);
		when(pagedB2BCostCenterDao.find(pageableData)).thenReturn(b2BCostCenters);
		Assert.assertEquals(b2BCostCenters, defaultB2BCommerceCostCenterService.getPagedCostCenters(pageableData));
	}

}
