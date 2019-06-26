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
package de.hybris.platform.assistedserviceservices.strategy;

import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.commerceservices.customer.strategies.CustomerListSearchStrategy;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class DefaultInStoreCustomerListSearchStrategyTest extends ServicelayerTest
{
	private PageableData pageableData;

	@Resource
	private CustomerListSearchStrategy defaultInStoreCustomerListSearchStrategy;

	@Before
	public void setup() throws Exception
	{
		pageableData = new PageableData();
		pageableData.setPageSize(5);
		System.out.println(defaultInStoreCustomerListSearchStrategy);
		importCsv("/assistedserviceservices/test/instore_data.impex", "UTF-8");
	}

	@Test(expected = IllegalArgumentException.class)
	public void instoreCuystomerListSearchStrategyEmptyParamsTest()
	{
		defaultInStoreCustomerListSearchStrategy.getPagedCustomers(null, null, null, null);
	}


	@Test(expected = UnknownIdentifierException.class)
	public void instoreCuystomerListSearchStrategyInvalidEmployeeTest()
	{
		defaultInStoreCustomerListSearchStrategy.getPagedCustomers("InStore", "INVALID_EMPLOYEE", pageableData, null);
	}

	@Test
	public void instoreCuystomerListSearchStrategyNakanoTest()
	{
		final SearchPageData<CustomerModel> customers = defaultInStoreCustomerListSearchStrategy.getPagedCustomers("InStore",
				"customer.support@nakano.com", pageableData, null);

		assertEquals(1, customers.getResults().size());
	}

	@Test
	public void instoreCuystomerListSearchStrategyIchikawaTest()
	{
		final SearchPageData<CustomerModel> customers = defaultInStoreCustomerListSearchStrategy.getPagedCustomers("InStore",
				"customer.support@ichikawa.com", pageableData, null);

		assertEquals(2, customers.getResults().size());
	}

	@Test
	public void instoreCuystomerListSearchStrategyNakanoAndIchikawaTest()
	{
		final SearchPageData<CustomerModel> customers = defaultInStoreCustomerListSearchStrategy.getPagedCustomers("InStore",
				"customer.support@manager.com", pageableData, null);

		assertEquals(3, customers.getResults().size());
	}
}
