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
package de.hybris.platform.b2bacceleratorfacades.company.refactoring.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.b2b.model.B2BBudgetModel;
import de.hybris.platform.b2b.util.B2BCommerceTestUtils;
import de.hybris.platform.b2bacceleratorfacades.search.data.BudgetSearchStateData;
import de.hybris.platform.b2bacceleratorservices.company.B2BCommerceBudgetService;
import de.hybris.platform.b2bcommercefacades.company.data.B2BBudgetData;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import de.hybris.platform.basecommerce.util.BaseCommerceBaseTest;
import de.hybris.platform.commercefacades.storesession.data.CurrencyData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.testframework.Transactional;

import java.math.BigDecimal;
import java.util.Date;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;


@SuppressWarnings("deprecation")
@IntegrationTest
@Transactional
@ContextConfiguration(locations =
{ "classpath:b2bacceleratorfacades/test/b2bacceleratorfacades-test-spring.xml" })
public class DefaultB2BBudgetFacadeIntegrationTest extends BaseCommerceBaseTest
{
	private static final String BUDGET_CODE = "TEST BUDGET";
	private static final String NEW_BUDGET_CODE = "NEW TEST BUDGET";
	private static final String DISABLED_BUDGET_CODE = "DISABLED TEST BUDGET";
	private static final String TEST_COST_CENTER = "DC 2.6";
	private static final String TEST_UNIT_CODE = "DC Test Center";

	@Resource
	private DefaultB2BBudgetFacade legacy2BBudgetFacade;

	@Resource
	private B2BCommerceBudgetService b2bCommerceBudgetService;


	@Before
	public void setup() throws Exception
	{
		createCoreData();
		createDefaultCatalog();
		importCsv("/b2bacceleratorfacades/test/testOrganizations.csv", "utf-8");
	}

	@Test
	public void testShouldGetBudgetDataForCode()
	{
		final B2BBudgetData budgetData = legacy2BBudgetFacade.getBudgetDataForCode(BUDGET_CODE);
		Assert.assertNotNull("Unexpected returned null budgetData", budgetData);
		Assert.assertNotNull("Unexpected returned null for budgetData's cost centers", budgetData.getCostCenterNames());
		Assert.assertEquals("Unexpected size of cost centers, should be 1", 1, budgetData.getCostCenterNames().size());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNotShouldGetBudgetDataForCodeNull()
	{
		final B2BBudgetData budgetData = legacy2BBudgetFacade.getBudgetDataForCode(null);
		Assert.assertNull(budgetData);
	}

	@Test
	public void testShouldUpdateBudget()
	{
		final B2BBudgetData b2bBudgetData = createBudgetData(BUDGET_CODE, BUDGET_CODE);

		legacy2BBudgetFacade.updateBudget(b2bBudgetData);

		final B2BBudgetModel b2BBudgetModel = b2bCommerceBudgetService.getBudgetModelForCode(BUDGET_CODE);
		validateBudgetModel(b2bBudgetData, b2BBudgetModel);
	}

	@Test
	public void testShouldAddBudget()
	{
		final B2BBudgetData b2bBudgetData = createBudgetData(NEW_BUDGET_CODE, NEW_BUDGET_CODE);

		legacy2BBudgetFacade.addBudget(b2bBudgetData);

		final B2BBudgetModel b2BBudgetModel = b2bCommerceBudgetService.getBudgetModelForCode(NEW_BUDGET_CODE);
		validateBudgetModel(b2bBudgetData, b2BBudgetModel);
	}

	@Test
	public void testShouldEnableBudget()
	{
		legacy2BBudgetFacade.enableDisableBudget(DISABLED_BUDGET_CODE, true);

		final B2BBudgetModel b2BBudgetModel = b2bCommerceBudgetService.getBudgetModelForCode(DISABLED_BUDGET_CODE);
		Assert.assertNotNull(b2BBudgetModel);
		Assert.assertTrue("b2bBudgetData should be enabled", b2BBudgetModel.getActive().booleanValue());
	}

	@Test
	public void testShouldDisableBudget()
	{
		legacy2BBudgetFacade.enableDisableBudget(BUDGET_CODE, false);

		final B2BBudgetModel b2BBudgetModel = b2bCommerceBudgetService.getBudgetModelForCode(BUDGET_CODE);
		Assert.assertNotNull(b2BBudgetModel);
		Assert.assertFalse("b2bBudgetData should not be enabled", b2BBudgetModel.getActive().booleanValue());
	}

	@Test
	public void testShouldSearch()
	{
		final PageableData pageableData = B2BCommerceTestUtils.createPageableData(1, 3, "byName");

		final SearchPageData<B2BBudgetData> searchPageData = legacy2BBudgetFacade.search(null, pageableData);

		Assert.assertNotNull(searchPageData);
		Assert.assertNotNull("searchPageData should have results", searchPageData.getResults());
		Assert.assertEquals("Unexpected size of results, should be 3", 3, searchPageData.getResults().size());
	}

	@Test
	public void testShouldSearchCostCenter()
	{
		final BudgetSearchStateData searchState = new BudgetSearchStateData();

		searchState.setCostCenterCode(TEST_COST_CENTER);
		final PageableData pageableData = B2BCommerceTestUtils.createPageableData(1, 2, "byName");

		final SearchPageData<B2BBudgetData> searchPageData = legacy2BBudgetFacade.search(searchState, pageableData);

		Assert.assertNotNull(searchPageData);
		Assert.assertNotNull("searchPageData should have results", searchPageData.getResults());
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
		Assert.assertEquals("Budget of b2bBudgetData and updated b2bBudgetModel should be equal", b2bBudgetData.getBudget()
				.stripTrailingZeros(), b2bBudgetModel.getBudget().stripTrailingZeros());
		Assert.assertEquals("Currency of b2bBudgetData and updated b2bBudgetModel should be equal", b2bBudgetData.getCurrency()
				.getIsocode(), b2bBudgetModel.getCurrency().getIsocode());
		Assert.assertEquals("StartDate of b2bBudgetData and updated b2bBudgetModel should be equal", b2bBudgetData.getStartDate(),
				b2bBudgetModel.getDateRange().getStart());
		Assert.assertEquals("EndDate of b2bBudgetData and updated b2bBudgetModel should be equal", b2bBudgetData.getEndDate(),
				b2bBudgetModel.getDateRange().getEnd());
	}
}
