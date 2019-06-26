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
package de.hybris.platform.personalizationservices.customization.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.personalizationservices.AbstractCxServiceTest;
import de.hybris.platform.personalizationservices.customization.CxCustomizationService;
import de.hybris.platform.personalizationservices.model.CxCustomizationModel;
import de.hybris.platform.personalizationservices.model.CxCustomizationsGroupModel;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import com.google.common.collect.Sets;


@IntegrationTest
public class DefaultCxCustomizationServiceIntegrationTest extends AbstractCxServiceTest
{

	@Resource
	private CxCustomizationService cxCustomizationService;

	@Resource
	private CatalogVersionService catalogVersionService;

	@Test
	public void findCustomizationByCodeTest()
	{
		final Optional<CxCustomizationModel> customization = cxCustomizationService.getCustomization(CUSTOMIZATION_CODE,
				catalogVersionService.getCatalogVersion("testCatalog", "Online"));

		assertTrue(customization.isPresent());
		assertTrue(CUSTOMIZATION_CODE.equals(customization.get().getCode()));
		assertNotNull(customization.get().getName());
	}

	@Test
	public void findNoCustomizationByCodeTest()
	{
		final Optional<CxCustomizationModel> customization = cxCustomizationService.getCustomization(CUSTOMIZATION_CODE + ".....",
				catalogVersionService.getCatalogVersion("testCatalog", "Online"));

		assertFalse(customization.isPresent());
	}

	@Test
	public void findCustomizationsTest()
	{
		//given
		final Set<String> expectedCodes = Sets.newHashSet("customization1", "customization2", "otherC");

		final CatalogVersionModel catalogVersion = catalogVersionService.getCatalogVersion("testCatalog", "Online");

		//when
		final List<CxCustomizationModel> customizations = cxCustomizationService.getCustomizations(catalogVersion);

		//then
		assertNotNull(customizations);
		assertEquals(expectedCodes, customizations.stream().map(CxCustomizationModel::getCode).collect(Collectors.toSet()));

		assertFalse(customizations.stream().filter(c -> StringUtils.isEmpty(c.getName())).findAny().isPresent());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidInputForCustomizations()
	{
		cxCustomizationService.getCustomizations(null);
	}

	@Test
	public void testCreateCustomization()
	{
		//given
		final String custCode = "newCust";
		final String custName = "newCustName";
		CxCustomizationModel cust = new CxCustomizationModel();
		cust.setCode(custCode);
		cust.setName(custName);
		final CxCustomizationsGroupModel group = cxCustomizationService
				.getDefaultGroup(catalogVersionService.getCatalogVersion("testCatalog", "Online"));
		final int expectedRank = group.getCustomizations().size();

		//when
		cust = cxCustomizationService.createCustomization(cust, group, null);

		//then
		assertEquals(custCode, cust.getCode());
		assertEquals(group.getCatalogVersion().getPk(), cust.getCatalogVersion().getPk());
		assertEquals(group.getPk(), cust.getGroup().getPk());
		assertEquals(expectedRank, cust.getRank().intValue());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateCustomizationWithoutCode()
	{
		//given
		final String custName = "newCustName";
		final CxCustomizationModel cust = new CxCustomizationModel();
		cust.setName(custName);
		final CxCustomizationsGroupModel group = cxCustomizationService
				.getDefaultGroup(catalogVersionService.getCatalogVersion("testCatalog", "Online"));

		//when
		cxCustomizationService.createCustomization(cust, group, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateCustomizationWithoutName()
	{
		//given
		final String custCode = "newCust";
		final CxCustomizationModel cust = new CxCustomizationModel();
		cust.setCode(custCode);
		final CxCustomizationsGroupModel group = cxCustomizationService
				.getDefaultGroup(catalogVersionService.getCatalogVersion("testCatalog", "Online"));

		//when
		cxCustomizationService.createCustomization(cust, group, null);
	}

	@Test
	public void testCreateCustomizationWithRank()
	{
		//given
		final String custCode = "newCust";
		final String custName = "newCustName";
		final Integer rank = Integer.valueOf(0);
		CxCustomizationModel cust = new CxCustomizationModel();
		cust.setCode(custCode);
		cust.setName(custName);
		final CxCustomizationsGroupModel group = cxCustomizationService
				.getDefaultGroup(catalogVersionService.getCatalogVersion("testCatalog", "Online"));
		final int custSize = group.getCustomizations().size();

		//when
		cust = cxCustomizationService.createCustomization(cust, group, rank);

		//then
		assertEquals(custCode, cust.getCode());
		assertEquals(custName, cust.getName());
		assertEquals(group.getCatalogVersion().getPk(), cust.getCatalogVersion().getPk());
		assertEquals(group.getPk(), cust.getGroup().getPk());
		assertEquals(rank.intValue(), cust.getRank().intValue());
		assertEquals(custSize + 1, group.getCustomizations().size());
	}

}
