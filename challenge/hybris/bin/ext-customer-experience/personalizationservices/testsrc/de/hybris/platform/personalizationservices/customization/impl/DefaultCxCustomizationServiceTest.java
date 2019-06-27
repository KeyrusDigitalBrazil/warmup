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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.PK;
import de.hybris.platform.personalizationservices.model.CxCustomizationModel;
import de.hybris.platform.personalizationservices.model.CxCustomizationsGroupModel;
import de.hybris.platform.personalizationservices.stub.CxCustomizationModelStub;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;



@UnitTest
public class DefaultCxCustomizationServiceTest
{
	private final DefaultCxCustomizationService service = new DefaultCxCustomizationService();
	private CxCustomizationsGroupModel custGroup;
	@Mock
	private CatalogVersionModel catalogVersion;
	@Mock
	private ModelService modelService;

	@Before
	public void initMocks()
	{
		MockitoAnnotations.initMocks(this);
		service.setModelService(modelService);
		custGroup = new CxCustomizationsGroupModel();
		custGroup.setCatalogVersion(catalogVersion);
		custGroup.setCustomizations(Collections.emptyList());
		Mockito.when(catalogVersion.getPk()).thenReturn(PK.fromLong(1l));
	}

	//Tests for createCustomization


	@Test
	public void testCreateCustomization()
	{
		//given
		final String custCode = "newCust";
		final String custName = "newCustName";
		CxCustomizationModel cust = new CxCustomizationModelStub();
		cust.setCode(custCode);
		cust.setName(custName);
		final int expectedRank = custGroup.getCustomizations().size();

		//when
		cust = service.createCustomization(cust, custGroup, null);

		//then
		assertEquals(custCode, cust.getCode());
		assertEquals(custName, cust.getName());
		assertEquals(custGroup.getCatalogVersion(), cust.getCatalogVersion());
		assertEquals(custGroup, cust.getGroup());
		assertEquals(expectedRank, cust.getRank().intValue());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateCustomizationWithNulParam()
	{
		//when
		service.createCustomization(null, custGroup, null);
	}


	@Test(expected = IllegalArgumentException.class)
	public void testCreateCustomizationWithNullCode()
	{
		//given
		final CxCustomizationModel cust = new CxCustomizationModel();

		//when
		service.createCustomization(cust, custGroup, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateCustomizationWithNullGroup()
	{
		//given
		final String custCode = "newCust";
		final CxCustomizationModel cust = new CxCustomizationModel();
		cust.setCode(custCode);

		//when
		service.createCustomization(cust, null, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateCustomizationWithNullCatalogVersion()
	{
		//given
		final String custCode = "newCust";
		final CxCustomizationModel cust = new CxCustomizationModel();
		cust.setCode(custCode);
		custGroup.setCatalogVersion(null);

		//when
		service.createCustomization(cust, custGroup, null);
	}
}
