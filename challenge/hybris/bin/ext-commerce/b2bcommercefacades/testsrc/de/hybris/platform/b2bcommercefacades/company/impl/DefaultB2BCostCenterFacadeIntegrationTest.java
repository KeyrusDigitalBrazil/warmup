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
import de.hybris.platform.b2b.dao.B2BCostCenterDao;
import de.hybris.platform.b2b.model.B2BCostCenterModel;
import de.hybris.platform.b2b.util.B2BCommerceTestUtils;
import de.hybris.platform.b2bcommercefacades.company.data.B2BCostCenterData;
import de.hybris.platform.b2bcommercefacades.company.data.B2BSelectionData;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import de.hybris.platform.b2bcommercefacades.testframework.AbstractCommerceOrgIntegrationTest;
import de.hybris.platform.commercefacades.storesession.data.CurrencyData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Test;


@IntegrationTest
public class DefaultB2BCostCenterFacadeIntegrationTest extends AbstractCommerceOrgIntegrationTest
{
	private static final String ORIGINAL_CODE = "DC 2.5";
	private static final String NEW_CODE = "sampleB2BCostCenter";
	private static final String NEW_NAME = "sample B2B Cost Center";
	private static final String NEW_ISO_CODE = "EUR";
	private static final String NEW_UNIT_UID = "DC Sales Hamburg";
	private static final String NEW_BUDGET = "DC BUDGET EUR 5K";
	private static final String ORIGINAL_BUDGET = "DC BUDGET USD 1M";

	@Resource
	private DefaultB2BCostCenterFacade defaultB2BCostCenterFacade;

	@Resource
	private B2BCostCenterDao b2bCostCenterDao;

	@Test
	public void shouldGetCostCenters()
	{
		final List<? extends B2BCostCenterData> costCenters = defaultB2BCostCenterFacade.getCostCenters();
		Assert.assertNotNull("costCenters should not be null!", costCenters);
		// some cost centers are imported by system initialization
		Assert.assertTrue("size of costCenters should be greater or equal to 8!", costCenters.size() >= 8);
	}

	@Test
	public void shouldGetActiveCostCenters()
	{
		final List<? extends B2BCostCenterData> costCenters = defaultB2BCostCenterFacade.getActiveCostCenters();
		Assert.assertNotNull("costCenters should not be null!", costCenters);

		// some cost centers are imported by system initialization
		Assert.assertTrue("size of costCenters should be greater or equal to 7!", costCenters.size() >= 7);
	}

	@Test
	public void shouldGetCostCenterDataForCode()
	{
		final B2BCostCenterData costCenter = defaultB2BCostCenterFacade.getCostCenterDataForCode(ORIGINAL_CODE);
		Assert.assertNotNull("costCenter should not be null!", costCenter);
		Assert.assertNotNull("costCenter.getB2bBudgetData() should not be null!", costCenter.getB2bBudgetData());
		Assert.assertEquals("size of costCenter.getB2bBudgetData() should be 1!", 1, costCenter.getB2bBudgetData().size());
		Assert.assertEquals(ORIGINAL_BUDGET, costCenter.getB2bBudgetData().get(0).getCode());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotGetCostCenterDataForCode()
	{
		defaultB2BCostCenterFacade.getCostCenterDataForCode("Not exist");
	}

	@Test
	public void shouldSearch()
	{
		final SearchPageData<B2BCostCenterData> searchResult = defaultB2BCostCenterFacade.search(null,
				B2BCommerceTestUtils.createPageableData(0, 20, "byUnitName"));
		Assert.assertNotNull("searchResult should not be null!", searchResult);
		Assert.assertNotNull("searchResult.getResults() should not be null!", searchResult.getResults());
		// some cost centers are imported by system initialization
		Assert.assertTrue("size of searchResult.getResults() should be greater or equal to 8!",
				searchResult.getResults().size() >= 8);
	}

	@Test
	public void shouldUpdateCostCenter()
	{
		// check property values before update
		B2BCostCenterData costCenter = defaultB2BCostCenterFacade.getCostCenterDataForCode(ORIGINAL_CODE);
		Assert.assertNotNull("costCenter should not be null!", costCenter);
		Assert.assertEquals("costCenter.getCode() should be DC 2.5!", "DC 2.5", costCenter.getCode());
		Assert.assertEquals("costCenter.getName() should be DC 2.5!", "DC 2.5", costCenter.getName());
		Assert.assertNotNull("costCenter.getCurrency() should not be null!", costCenter.getCurrency());
		Assert.assertEquals("costCenter.getCurrency().getIsocode() should be USD", "USD", costCenter.getCurrency().getIsocode());
		Assert.assertNotNull("costCenter.getUnit() should not be null!", costCenter.getUnit());
		Assert.assertEquals("costCenter.getUnit().getUid() should be DC Sales Detroit", "DC Sales Detroit",
				costCenter.getUnit().getUid());

		// update
		defaultB2BCostCenterFacade.updateCostCenter(
				createB2BCostCenterData(ORIGINAL_CODE, ORIGINAL_CODE, NEW_NAME, NEW_ISO_CODE, createUnit(NEW_UNIT_UID)));

		// check property values after update
		costCenter = defaultB2BCostCenterFacade.getCostCenterDataForCode(ORIGINAL_CODE);
		Assert.assertNotNull("costCenter should not be null!", costCenter);
		Assert.assertEquals("costCenter.getName() should be " + NEW_NAME, NEW_NAME, costCenter.getName());
		Assert.assertNotNull("costCenter.getCurrency() should not be null!", costCenter.getCurrency());
		Assert.assertEquals("costCenter.getCurrency().getIsocode() should be " + NEW_ISO_CODE, NEW_ISO_CODE,
				costCenter.getCurrency().getIsocode());
		Assert.assertNotNull("costCenter.getUnit() should not be null!", costCenter.getUnit());
		Assert.assertEquals("costCenter.getUnit().getUid() should be " + NEW_UNIT_UID, NEW_UNIT_UID, costCenter.getUnit().getUid());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotUpdateCostCenter()
	{
		defaultB2BCostCenterFacade.updateCostCenter(null);
	}

	@Test
	public void shouldAddCostCenter()
	{
		// check before adding
		B2BCostCenterModel costCenter = b2bCostCenterDao.findByCode(NEW_CODE);
		Assert.assertNull(costCenter);

		// add
		defaultB2BCostCenterFacade
				.addCostCenter(createB2BCostCenterData(NEW_CODE, NEW_CODE, NEW_NAME, NEW_ISO_CODE, createUnit(NEW_UNIT_UID)));

		// check after adding
		costCenter = b2bCostCenterDao.findByCode(NEW_CODE);
		Assert.assertNotNull("costCenter should not be null!", costCenter);
		Assert.assertEquals("costCenter.getName() should be " + NEW_NAME, NEW_NAME, costCenter.getName());
		Assert.assertNotNull("costCenter.getCurrency() should not be null!", costCenter.getCurrency());
		Assert.assertEquals("costCenter.getCurrency().getIsocode() should be " + NEW_ISO_CODE, NEW_ISO_CODE,
				costCenter.getCurrency().getIsocode());
		Assert.assertNotNull("costCenter.getUnit() should not be null!", costCenter.getUnit());
		Assert.assertEquals("costCenter.getUnit().getUid() should be " + NEW_UNIT_UID, NEW_UNIT_UID, costCenter.getUnit().getUid());
	}

	private B2BUnitData createUnit(final String newUnitUid)
	{
		final B2BUnitData unit = new B2BUnitData();
		unit.setUid(newUnitUid);
		return unit;
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotAddCostCenter()
	{
		defaultB2BCostCenterFacade.addCostCenter(null);
	}

	@Test
	public void shouldEnableDisableCostCenter()
	{
		B2BCostCenterData costCenter = defaultB2BCostCenterFacade.getCostCenterDataForCode(ORIGINAL_CODE);
		Assert.assertNotNull("costCenter should not be null!", costCenter);
		Assert.assertTrue("costCenter should be active", costCenter.isActive());

		// disable
		defaultB2BCostCenterFacade.enableDisableCostCenter(ORIGINAL_CODE, false);
		costCenter = defaultB2BCostCenterFacade.getCostCenterDataForCode(ORIGINAL_CODE);
		Assert.assertNotNull("costCenter should not be null!", costCenter);
		Assert.assertFalse("costCenter should not be active", costCenter.isActive());

		// enable
		defaultB2BCostCenterFacade.enableDisableCostCenter(ORIGINAL_CODE, true);
		costCenter = defaultB2BCostCenterFacade.getCostCenterDataForCode(ORIGINAL_CODE);
		Assert.assertNotNull("costCenter should not be null!", costCenter);
		Assert.assertTrue("costCenter should be active", costCenter.isActive());
	}

	@Test
	public void shouldSelectBudgetForCostCenter()
	{
		B2BCostCenterData costCenter = defaultB2BCostCenterFacade.getCostCenterDataForCode(ORIGINAL_CODE);
		Assert.assertNotNull("costCenter should not be null!", costCenter);
		Assert.assertNotNull("costCenter.getB2bBudgetData() should not be null!", costCenter.getB2bBudgetData());
		Assert.assertEquals("size of costCenter.getB2bBudgetData() should be 1!", 1, costCenter.getB2bBudgetData().size());
		Assert.assertEquals("costCenter.getB2bBudgetData().get(0).getCode() should be " + ORIGINAL_BUDGET, ORIGINAL_BUDGET,
				costCenter.getB2bBudgetData().get(0).getCode());

		final B2BSelectionData selectionData = defaultB2BCostCenterFacade.selectBudgetForCostCenter(ORIGINAL_CODE, NEW_BUDGET);
		Assert.assertNotNull("selectionData should not be null!", selectionData);
		Assert.assertEquals("selectionData.getId() should be " + NEW_BUDGET, NEW_BUDGET, selectionData.getId());
		Assert.assertTrue("selectionData should be selected", selectionData.isSelected());

		costCenter = defaultB2BCostCenterFacade.getCostCenterDataForCode(ORIGINAL_CODE);
		Assert.assertNotNull("costCenter should not be null!", costCenter);
		Assert.assertNotNull("costCenter.getB2bBudgetData() should not be null!", costCenter.getB2bBudgetData());
		Assert.assertEquals("size of costCenter.getB2bBudgetData() should be 2!", 2, costCenter.getB2bBudgetData().size());
	}

	@Test
	public void shouldDeSelectBudgetForCostCenter()
	{
		B2BCostCenterData costCenter = defaultB2BCostCenterFacade.getCostCenterDataForCode(ORIGINAL_CODE);
		Assert.assertNotNull("costCenter should not be null!", costCenter);
		Assert.assertNotNull("costCenter.getB2bBudgetData() should not be null!", costCenter.getB2bBudgetData());
		Assert.assertEquals("size of costCenter.getB2bBudgetData() should be 1!", 1, costCenter.getB2bBudgetData().size());
		Assert.assertEquals("costCenter.getB2bBudgetData().get(0).getCode() should be " + ORIGINAL_BUDGET, ORIGINAL_BUDGET,
				costCenter.getB2bBudgetData().get(0).getCode());

		final B2BSelectionData selectionData = defaultB2BCostCenterFacade.deSelectBudgetForCostCenter(ORIGINAL_CODE,
				ORIGINAL_BUDGET);
		Assert.assertNotNull("selectionData should not be null!", selectionData);
		Assert.assertEquals("selectionData.getId() should be " + ORIGINAL_BUDGET, ORIGINAL_BUDGET, selectionData.getId());
		Assert.assertFalse("selectionData should be selected", selectionData.isSelected());

		costCenter = defaultB2BCostCenterFacade.getCostCenterDataForCode(ORIGINAL_CODE);
		Assert.assertNotNull("costCenter should not be null!", costCenter);
		Assert.assertNotNull("costCenter.getB2bBudgetData() should not be null!", costCenter.getB2bBudgetData());
		Assert.assertEquals("size of costCenter.getB2bBudgetData() should be 0!", 0, costCenter.getB2bBudgetData().size());
	}

	protected B2BCostCenterData createB2BCostCenterData(final String originalCode, final String code, final String name,
			final String isoCode, final B2BUnitData unit)
	{
		final B2BCostCenterData b2BCostCenterData = new B2BCostCenterData();
		b2BCostCenterData.setOriginalCode(originalCode);
		b2BCostCenterData.setCode(code);
		b2BCostCenterData.setName(name);
		final CurrencyData currencyData = new CurrencyData();
		currencyData.setIsocode(isoCode);
		b2BCostCenterData.setCurrency(currencyData);
		b2BCostCenterData.setUnit(unit);

		return b2BCostCenterData;
	}

	@Override
	protected String getTestDataPath()
	{
		return "/b2bcommercefacades/test/testOrganizations.csv";
	}
}
