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
import de.hybris.platform.b2b.dao.B2BCostCenterDao;
import de.hybris.platform.b2b.model.B2BCostCenterModel;
import de.hybris.platform.b2b.util.B2BCommerceTestUtils;
import de.hybris.platform.b2bacceleratorfacades.company.CompanyB2BCommerceFacade;
import de.hybris.platform.b2bcommercefacades.company.data.B2BCostCenterData;
import de.hybris.platform.b2bcommercefacades.company.data.B2BSelectionData;
import de.hybris.platform.b2bcommercefacades.util.B2BCommercefacadesTestUtils;
import de.hybris.platform.basecommerce.util.BaseCommerceBaseTest;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;


@SuppressWarnings("deprecation")
@IntegrationTest
@ContextConfiguration(locations =
{ "classpath:b2bacceleratorfacades/test/b2bacceleratorfacades-test-spring.xml" })
public class DefaultB2BCostCenterFacadeIntegrationTest extends BaseCommerceBaseTest
{
	private static final String ORIGINAL_CODE = "DC 2.5";
	private static final String NEW_CODE = "sampleB2BCostCenter";
	private static final String NEW_NAME = "sample B2B Cost Center";
	private static final String NEW_ISO_CODE = "EUR";
	private static final String NEW_UNIT_UID = "DC Sales Hamburg";
	private static final String NEW_BUDGET = "DC BUDGET EUR 5K";
	private static final String ORIGINAL_BUDGET = "DC BUDGET USD 1M";

	@Resource
	private DefaultB2BCostCenterFacade legacyB2BCostCenterFacade;

	@Resource
	private CompanyB2BCommerceFacade b2bCommerceFacade;

	@Resource
	private B2BCostCenterDao b2bCostCenterDao;

	@Resource
	private UserService userService;

	@Before
	public void beforeTest() throws Exception
	{
		createCoreData();
		createDefaultCatalog();
		importCsv("/b2bacceleratorfacades/test/testOrganizations.csv", "utf-8");

		userService.setCurrentUser(userService.getUserForUID("DC Admin"));
	}

	@Test
	public void shouldGetCostCenters()
	{
		final List<? extends B2BCostCenterData> costCenters = legacyB2BCostCenterFacade.getCostCenters();
		Assert.assertNotNull("costCenters should not be null!", costCenters);
		Assert.assertTrue("size of costCenters should be greater or equal to 8!", costCenters.size() >= 8); // some are loaded during system initialization
	}

	@Test
	public void shouldGetActiveCostCenters()
	{
		final List<? extends B2BCostCenterData> costCenters = legacyB2BCostCenterFacade.getActiveCostCenters();
		Assert.assertNotNull("costCenters should not be null!", costCenters);
		Assert.assertTrue("size of costCenters should be greater or equal to 7!", costCenters.size() >= 7); // some are loaded during system initialization
	}

	@Test
	public void shouldGetCostCenterDataForCode()
	{
		final B2BCostCenterData costCenter = legacyB2BCostCenterFacade.getCostCenterDataForCode(ORIGINAL_CODE);
		Assert.assertNotNull("costCenter should not be null!", costCenter);
		Assert.assertNotNull("costCenter.getB2bBudgetData() should not be null!", costCenter.getB2bBudgetData());
		Assert.assertEquals(1, costCenter.getB2bBudgetData().size());
		Assert.assertEquals(ORIGINAL_BUDGET, costCenter.getB2bBudgetData().get(0).getCode());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotGetCostCenterDataForCode()
	{
		legacyB2BCostCenterFacade.getCostCenterDataForCode("Not exist");
	}

	@Test
	public void shouldSearch()
	{
		final SearchPageData<B2BCostCenterData> searchResult = legacyB2BCostCenterFacade.search(null,
				B2BCommerceTestUtils.createPageableData(0, 20, "byUnitName"));
		Assert.assertNotNull("searchResult should not be null!", searchResult);
		Assert.assertNotNull("searchResult.getResults() should not be null!", searchResult.getResults());
		Assert.assertTrue(searchResult.getResults().size() >= 8); // some cost centers are imported by system initialization
	}

	@Test
	public void shouldUpdateCostCenter()
	{
		// check property values before update
		B2BCostCenterData costCenter = legacyB2BCostCenterFacade.getCostCenterDataForCode(ORIGINAL_CODE);
		Assert.assertNotNull("costCenter should not be null!", costCenter);
		Assert.assertEquals("DC 2.5", costCenter.getCode());
		Assert.assertEquals("DC 2.5", costCenter.getName());
		Assert.assertNotNull("costCenter.getCurrency() should not be null!", costCenter.getCurrency());
		Assert.assertEquals("USD", costCenter.getCurrency().getIsocode());
		Assert.assertNotNull("costCenter.getUnit() should not be null!", costCenter.getUnit());
		Assert.assertEquals("DC Sales Detroit", costCenter.getUnit().getUid());

		// update
		legacyB2BCostCenterFacade.updateCostCenter(B2BCommercefacadesTestUtils.createB2BCostCenterData(ORIGINAL_CODE,
				ORIGINAL_CODE, NEW_NAME, NEW_ISO_CODE, b2bCommerceFacade.getUnitForUid(NEW_UNIT_UID)));

		// check property values after update
		costCenter = legacyB2BCostCenterFacade.getCostCenterDataForCode(ORIGINAL_CODE);
		Assert.assertNotNull("costCenter should not be null!", costCenter);
		Assert.assertEquals(NEW_NAME, costCenter.getName());
		Assert.assertNotNull("costCenter.getCurrency() should not be null!", costCenter.getCurrency());
		Assert.assertEquals(NEW_ISO_CODE, costCenter.getCurrency().getIsocode());
		Assert.assertNotNull("costCenter.getUnit() should not be null!", costCenter.getUnit());
		Assert.assertEquals(NEW_UNIT_UID, costCenter.getUnit().getUid());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotUpdateCostCenter()
	{
		legacyB2BCostCenterFacade.updateCostCenter(null);
	}

	@Test
	public void shouldAddCostCenter()
	{
		// check before adding
		B2BCostCenterModel costCenter = b2bCostCenterDao.findByCode(NEW_CODE);
		Assert.assertNull(costCenter);

		// add
		legacyB2BCostCenterFacade.addCostCenter(B2BCommercefacadesTestUtils.createB2BCostCenterData(NEW_CODE, NEW_CODE, NEW_NAME,
				NEW_ISO_CODE, b2bCommerceFacade.getUnitForUid(NEW_UNIT_UID)));

		// check after adding
		costCenter = b2bCostCenterDao.findByCode(NEW_CODE);
		Assert.assertNotNull("costCenter should not be null!", costCenter);
		Assert.assertEquals(NEW_NAME, costCenter.getName());
		Assert.assertNotNull("costCenter.getCurrency() should not be null!", costCenter.getCurrency());
		Assert.assertEquals(NEW_ISO_CODE, costCenter.getCurrency().getIsocode());
		Assert.assertNotNull("costCenter.getUnit() should not be null!", costCenter.getUnit());
		Assert.assertEquals(NEW_UNIT_UID, costCenter.getUnit().getUid());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotAddCostCenter()
	{
		legacyB2BCostCenterFacade.addCostCenter(null);
	}

	@Test
	public void shouldEnableDisableCostCenter()
	{
		B2BCostCenterData costCenter = legacyB2BCostCenterFacade.getCostCenterDataForCode(ORIGINAL_CODE);
		Assert.assertNotNull(costCenter);
		Assert.assertTrue(costCenter.isActive());

		// disable
		legacyB2BCostCenterFacade.enableDisableCostCenter(ORIGINAL_CODE, false);
		costCenter = legacyB2BCostCenterFacade.getCostCenterDataForCode(ORIGINAL_CODE);
		Assert.assertNotNull("costCenter should not be null!", costCenter);
		Assert.assertFalse(costCenter.isActive());

		// enable
		legacyB2BCostCenterFacade.enableDisableCostCenter(ORIGINAL_CODE, true);
		costCenter = legacyB2BCostCenterFacade.getCostCenterDataForCode(ORIGINAL_CODE);
		Assert.assertNotNull("costCenter should not be null!", costCenter);
		Assert.assertTrue(costCenter.isActive());
	}

	@Test
	public void shouldSelectBudgetForCostCenter()
	{
		B2BCostCenterData costCenter = legacyB2BCostCenterFacade.getCostCenterDataForCode(ORIGINAL_CODE);
		Assert.assertNotNull("costCenter should not be null!", costCenter);
		Assert.assertNotNull("costCenter.getB2bBudgetData() should not be null!", costCenter.getB2bBudgetData());
		Assert.assertEquals(1, costCenter.getB2bBudgetData().size());
		Assert.assertEquals(costCenter.getB2bBudgetData().get(0).getCode(), ORIGINAL_BUDGET);

		final B2BSelectionData selectionData = legacyB2BCostCenterFacade.selectBudgetForCostCenter(ORIGINAL_CODE, NEW_BUDGET);
		Assert.assertNotNull("selectionData should not be null!", selectionData);
		Assert.assertEquals(NEW_BUDGET, selectionData.getId());
		Assert.assertTrue(selectionData.isSelected());

		costCenter = legacyB2BCostCenterFacade.getCostCenterDataForCode(ORIGINAL_CODE);
		Assert.assertNotNull("costCenter should not be null!", costCenter);
		Assert.assertNotNull("costCenter.getB2bBudgetData() should not be null!", costCenter.getB2bBudgetData());
		Assert.assertEquals(2, costCenter.getB2bBudgetData().size());
	}

	@Test
	public void shouldDeSelectBudgetForCostCenter()
	{
		B2BCostCenterData costCenter = legacyB2BCostCenterFacade.getCostCenterDataForCode(ORIGINAL_CODE);
		Assert.assertNotNull("costCenter.getB2bBudgetData() should not be null!", costCenter.getB2bBudgetData());
		Assert.assertEquals(1, costCenter.getB2bBudgetData().size());
		Assert.assertEquals(costCenter.getB2bBudgetData().get(0).getCode(), ORIGINAL_BUDGET);

		final B2BSelectionData selectionData = legacyB2BCostCenterFacade
				.deSelectBudgetForCostCenter(ORIGINAL_CODE, ORIGINAL_BUDGET);
		Assert.assertNotNull("selectionData should not be null!", selectionData);
		Assert.assertEquals(ORIGINAL_BUDGET, selectionData.getId());
		Assert.assertFalse(selectionData.isSelected());

		costCenter = legacyB2BCostCenterFacade.getCostCenterDataForCode(ORIGINAL_CODE);
		Assert.assertNotNull("selectionData should not be null!", selectionData);
		Assert.assertEquals(0, costCenter.getB2bBudgetData().size());
	}

}
