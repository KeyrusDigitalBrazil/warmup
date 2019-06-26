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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.b2b.model.B2BCostCenterModel;
import de.hybris.platform.b2b.util.B2BCommerceTestUtils;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.servicelayer.ServicelayerTest;

import java.util.Locale;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


@IntegrationTest
public class DefaultB2BCommerceCostCenterServiceIntegrationTest extends ServicelayerTest
{
	@Resource
	private DefaultB2BCommerceCostCenterService b2bCommerceCostCenterService;

	@Rule
	public final ExpectedException expectedEx = ExpectedException.none();

	@Before
	public void setup() throws Exception
	{
		createCoreData();
		importCsv("/b2bcommerce/test/b2bData.impex", "UTF-8");
	}

	@Test
	public void shouldGetCostCenterForCode() throws Exception
	{
		final String COST_CENTER_CODE = "CostCenterA";
		final B2BCostCenterModel costCenter = b2bCommerceCostCenterService.getCostCenterForCode(COST_CENTER_CODE);
		assertNotNull(costCenter);
		assertEquals("CostCenterA name", costCenter.getName(Locale.ENGLISH));
		assertNotNull(costCenter.getCurrency());
		assertEquals("USD", costCenter.getCurrency().getIsocode());
		assertNotNull(costCenter.getUnit());
		assertEquals("B2BUnitA", costCenter.getUnit().getUid());
	}

	@Test
	public void shouldNotGetCostCenterNotExistID() throws Exception
	{
		assertNull(b2bCommerceCostCenterService.getCostCenterForCode("notExist"));
	}

	@Test
	public void shouldNotGetCostCenterNullID() throws Exception
	{
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Value is required, null given for key: code");
		b2bCommerceCostCenterService.getCostCenterForCode(null);
	}

	@Test
	public void shoulGetPagedCostCenters() throws Exception
	{
		final PageableData pageableData = B2BCommerceTestUtils.createPageableData(0, 10, "byName");
		final SearchPageData<B2BCostCenterModel> b2BCostCenters = b2bCommerceCostCenterService.getPagedCostCenters(pageableData);
		assertNotNull(b2BCostCenters);
		assertFalse(b2BCostCenters.getResults().isEmpty());
		assertTrue(b2BCostCenters.getResults().size() >= 2); // the system may already have some cost center loaded
	}

	@Test
	public void shoulNotGetPagedCostCentersNullAsPageableData() throws Exception
	{
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("PageableData can not be null!");
		b2bCommerceCostCenterService.getPagedCostCenters(null);
	}
}
