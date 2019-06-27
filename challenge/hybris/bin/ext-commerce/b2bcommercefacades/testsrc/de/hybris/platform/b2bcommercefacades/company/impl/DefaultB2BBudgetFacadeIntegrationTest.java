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
package de.hybris.platform.b2bcommercefacades.company.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.b2b.model.B2BBudgetModel;
import de.hybris.platform.b2b.services.B2BBudgetService;
import de.hybris.platform.b2b.util.B2BCommerceTestUtils;
import de.hybris.platform.b2bcommercefacades.company.data.B2BBudgetData;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import de.hybris.platform.b2bcommercefacades.search.data.BudgetSearchStateData;
import de.hybris.platform.b2bcommercefacades.testframework.AbstractCommerceOrgIntegrationTest;
import de.hybris.platform.commercefacades.storesession.data.CurrencyData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;

import java.math.BigDecimal;
import java.util.Date;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Test;


@IntegrationTest
public class DefaultB2BBudgetFacadeIntegrationTest extends AbstractCommerceOrgIntegrationTest
{
	private static final String BUDGET_CODE = "TEST BUDGET";
	private static final String NEW_BUDGET_CODE = "NEW TEST BUDGET";
	private static final String DISABLED_BUDGET_CODE = "DISABLED TEST BUDGET";
	private static final String TEST_COST_CENTER = "DC 2.6";
	private static final String TEST_UNIT_CODE = "DC Test Center";

	@Resource
	private DefaultB2BBudgetFacade defaultB2BBudgetFacade;

	@Resource
	private B2BBudgetService b2BBudgetService;

	@Test
	public void testShouldGetBudgetDataForCode()
	{
		final B2BBudgetData budgetData = defaultB2BBudgetFacade.getBudgetDataForCode(BUDGET_CODE);
		Assert.assertNotNull("Unexpected returned null budgetData", budgetData);
		Assert.assertNotNull("Unexpected returned null for budgetData's cost centers", budgetData.getCostCenterNames());
		Assert.assertEquals("Unexpected size of cost centers, should be 1", 1, budgetData.getCostCenterNames().size());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNotShouldGetBudgetDataForCodeNull()
	{
		final B2BBudgetData budgetData = defaultB2BBudgetFacade.getBudgetDataForCode(null);
		Assert.assertNull(budgetData);
	}

	@Test
	public void testShouldUpdateBudget()
	{
		final B2BBudgetData b2bBudgetData = createBudgetData(BUDGET_CODE, BUDGET_CODE);

		defaultB2BBudgetFacade.updateBudget(b2bBudgetData);

		final B2BBudgetModel b2BBudgetModel = b2BBudgetService.getB2BBudgetForCode(BUDGET_CODE);
		validateBudgetModel(b2bBudgetData, b2BBudgetModel);
	}

	@Test
	public void testShouldAddBudget()
	{
		final B2BBudgetData b2bBudgetData = createBudgetData(NEW_BUDGET_CODE, NEW_BUDGET_CODE);

		defaultB2BBudgetFacade.addBudget(b2bBudgetData);

		final B2BBudgetModel b2BBudgetModel = b2BBudgetService.getB2BBudgetForCode(NEW_BUDGET_CODE);
		validateBudgetModel(b2bBudgetData, b2BBudgetModel);
	}

	@Test
	public void testShouldEnableBudget()
	{
		defaultB2BBudgetFacade.enableDisableBudget(DISABLED_BUDGET_CODE, true);

		final B2BBudgetModel b2BBudgetModel = b2BBudgetService.getB2BBudgetForCode(DISABLED_BUDGET_CODE);
		Assert.assertNotNull(b2BBudgetModel);
		Assert.assertTrue("b2bBudgetData should be enabled", b2BBudgetModel.getActive().booleanValue());
	}

	@Test
	public void testShouldDisableBudget()
	{
		defaultB2BBudgetFacade.enableDisableBudget(BUDGET_CODE, false);

		final B2BBudgetModel b2BBudgetModel = b2BBudgetService.getB2BBudgetForCode(BUDGET_CODE);
		Assert.assertNotNull(b2BBudgetModel);
		Assert.assertFalse("b2bBudgetData should not be enabled", b2BBudgetModel.getActive().booleanValue());
	}

	@Test
	public void testShouldSearch()
	{
		final PageableData pageableData = B2BCommerceTestUtils.createPageableData(1, 3, "byName");
		assertSearchPageData(3, defaultB2BBudgetFacade.search(null, pageableData));
	}

	@Test
	public void testShouldSearchCostCenter()
	{
		final BudgetSearchStateData searchState = new BudgetSearchStateData();
		searchState.setCostCenterCode(TEST_COST_CENTER);
		final PageableData pageableData = B2BCommerceTestUtils.createPageableData(1, 2, "byName");
		assertSearchPageData(2, defaultB2BBudgetFacade.search(searchState, pageableData));
	}

	protected B2BBudgetData createBudgetData(final String code, final String name)
	{
		final B2BBudgetData b2bBudgetData = new B2BBudgetData();
		b2bBudgetData.setOriginalCode(code);
		b2bBudgetData.setCode(code);
		b2bBudgetData.setName(name);
		b2bBudgetData.setBudget(BigDecimal.TEN);
		final B2BUnitData b2bUnitData = new B2BUnitData();
		b2bUnitData.setUid(TEST_UNIT_CODE);
		b2bUnitData.setActive(true);
		b2bBudgetData.setUnit(b2bUnitData);
		final CurrencyData currencyData = new CurrencyData();
		currencyData.setIsocode("USD");
		b2bBudgetData.setCurrency(currencyData);
		final Date date = new Date();
		b2bBudgetData.setStartDate(date);
		b2bBudgetData.setEndDate(date);
		b2bBudgetData.setActive(true);

		return b2bBudgetData;
	}

	protected void validateBudgetModel(final B2BBudgetData b2bBudgetData, final B2BBudgetModel b2bBudgetModel)
	{
		Assert.assertNotNull(b2bBudgetModel);
		Assert.assertEquals("Code of b2bBudgetData and updated b2bBudgetModel should be equal", b2bBudgetData.getCode(),
				b2bBudgetModel.getCode());
		Assert.assertEquals("Name of b2bBudgetData and updated b2bBudgetModel should be equal", b2bBudgetData.getName(),
				b2bBudgetModel.getName());
		Assert.assertEquals("Budget of b2bBudgetData and updated b2bBudgetModel should be equal",
				b2bBudgetData.getBudget().stripTrailingZeros(), b2bBudgetModel.getBudget().stripTrailingZeros());
		Assert.assertEquals("Currency of b2bBudgetData and updated b2bBudgetModel should be equal",
				b2bBudgetData.getCurrency().getIsocode(), b2bBudgetModel.getCurrency().getIsocode());
		Assert.assertEquals("StartDate of b2bBudgetData and updated b2bBudgetModel should be equal", b2bBudgetData.getStartDate(),
				b2bBudgetModel.getDateRange().getStart());
		Assert.assertEquals("EndDate of b2bBudgetData and updated b2bBudgetModel should be equal", b2bBudgetData.getEndDate(),
				b2bBudgetModel.getDateRange().getEnd());
	}

	@Override
	protected String getTestDataPath()
	{
		return "/b2bcommercefacades/test/testOrganizations.csv";
	}
}
