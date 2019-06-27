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
package de.hybris.platform.b2b.strategies.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.commerceservices.model.OrgUnitModel;
import de.hybris.platform.commerceservices.organization.services.impl.DefaultOrgUnitHierarchyService;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class B2BCustomerListSearchStrategyIntegrationTest extends ServicelayerTest
{
	private PageableData pageableData;

	@Resource
	private B2BCustomerListSearchStrategy b2bCustomerListSearchStrategy;

	@Resource(name = "defaultOrgUnitHierarchyService")
	private DefaultOrgUnitHierarchyService defaultOrgUnitHierarchyService;

	@Before
	public void setup() throws Exception
	{
		pageableData = new PageableData();
		pageableData.setPageSize(5);

		createCoreData();
		importCsv("/b2bcommerce/test/b2bCustomerListSearchStrategyData.impex", "UTF-8");
		defaultOrgUnitHierarchyService.generateUnitPaths(OrgUnitModel.class);
		defaultOrgUnitHierarchyService.generateUnitPaths(B2BUnitModel.class);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldRejectNullArgumentEmployeeUid()
	{
		b2bCustomerListSearchStrategy.getPagedCustomers("B2B", null, pageableData, new HashMap<>());
	}

	@Test(expected = UnknownIdentifierException.class)
	public void shouldRejectInvalidEmployeeUid()
	{
		b2bCustomerListSearchStrategy.getPagedCustomers("B2B", "invalid.employeeUid", pageableData, new HashMap<>());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldRejectNullArgumentPageableData()
	{
		b2bCustomerListSearchStrategy.getPagedCustomers("B2B", "darrin.hesser@test.com", null, new HashMap<>());
	}

	@Test
	public void shouldReturnEmptyResultsWhenEmployeeIsInNoSalesOrgUnit()
	{
		final SearchPageData<CustomerModel> customerModelSearchPageData = b2bCustomerListSearchStrategy.getPagedCustomers("B2B",
				"nosalesunit@test.com", pageableData, null);

		assertNotNull("Result should not be null", customerModelSearchPageData);
		assertEquals("Should have returned 0 result", 0, customerModelSearchPageData.getPagination().getTotalNumberOfResults());
	}

	@Test
	public void shouldReturnEmptyResultsWhenSalesOrgUnitHasNoB2BUnit()
	{
		final SearchPageData<CustomerModel> customerModelSearchPageData = b2bCustomerListSearchStrategy.getPagedCustomers("B2B",
				"nob2bunit@test.com", pageableData, null);

		assertNotNull("Result should not be null", customerModelSearchPageData);
		assertEquals("Should have returned 0 result", 0, customerModelSearchPageData.getPagination().getTotalNumberOfResults());
	}

	@Test
	public void shouldReturnExpectedResultsForValidCall()
	{
		final SearchPageData<CustomerModel> customerModelSearchPageData = b2bCustomerListSearchStrategy.getPagedCustomers(null,
				"darrin.hesser@test.com", pageableData, null);

		assertNotNull("Result should not be null", customerModelSearchPageData);
		assertEquals("Should return 11 results", 11, customerModelSearchPageData.getPagination().getTotalNumberOfResults());
	}

	@Test
	public void shouldReturnExpectedResultsForValidCallWithSearchTerm()
	{
		final Map<String, Object> parameterMap = new HashMap<String, Object>(1);
		parameterMap.put("query", "omer P");

		final SearchPageData<CustomerModel> customerModelSearchPageData = b2bCustomerListSearchStrategy.getPagedCustomers(null,
				"darrin.hesser@test.com", pageableData, parameterMap);

		assertNotNull("Result should not be null", customerModelSearchPageData);
		assertEquals("Should return 1 results", 1, customerModelSearchPageData.getPagination().getTotalNumberOfResults());
	}
}
