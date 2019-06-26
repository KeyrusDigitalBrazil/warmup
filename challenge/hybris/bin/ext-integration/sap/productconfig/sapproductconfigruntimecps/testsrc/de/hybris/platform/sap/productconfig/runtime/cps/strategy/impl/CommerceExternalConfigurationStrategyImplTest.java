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
package de.hybris.platform.sap.productconfig.runtime.cps.strategy.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.product.UnitService;
import de.hybris.platform.sap.productconfig.runtime.cps.model.external.CPSCommerceExternalConfiguration;
import de.hybris.platform.sap.productconfig.runtime.cps.model.external.CPSExternalConfiguration;
import de.hybris.platform.sap.productconfig.runtime.cps.model.external.CPSExternalItem;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSQuantity;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
@SuppressWarnings("javadoc")
public class CommerceExternalConfigurationStrategyImplTest
{

	private final CommerceExternalConfigurationStrategyImpl classUnderTest = new CommerceExternalConfigurationStrategyImpl();
	private CPSCommerceExternalConfiguration commerceExternalConfiguration;
	private CPSExternalConfiguration externalConfiguration;
	private final CPSExternalItem rootItem = new CPSExternalItem();
	private final CPSExternalItem subItem = new CPSExternalItem();
	private final CPSQuantity quantity = new CPSQuantity();
	private final CPSQuantity quantitySubItem = new CPSQuantity();
	private static final String isoUnitKey = "unitISO";
	private static final String isoUnitKeyNotKnownToHybris = "unitISONotKnown";
	private static final String sapUnitKey = "unitSAP";
	private static final String isoUnitKeySubItem = "unitISO2";
	private static final String sapUnitKeySubItem = "unitSAP2";


	@Mock
	private UnitService unitService;
	@Mock
	private UnitModel unitModel;
	@Mock
	private UnitModel unitModel2;

	protected Map<String, String> getUnitList()
	{
		final Map<String, String> unitCodesAsMap = new HashMap<>();
		classUnderTest.collectUnitCodes(rootItem, unitCodesAsMap);
		assertNotNull(unitCodesAsMap);
		assertFalse(unitCodesAsMap.isEmpty());
		return unitCodesAsMap;
	}

	@SuppressWarnings("unchecked")
	@Before
	public void initialize()
	{
		commerceExternalConfiguration = new CPSCommerceExternalConfiguration();
		externalConfiguration = new CPSExternalConfiguration();
		externalConfiguration.setRootItem(rootItem);
		rootItem.setQuantity(quantity);
		quantity.setUnit(isoUnitKey);
		rootItem.setSubItems(new ArrayList<CPSExternalItem>());
		rootItem.getSubItems().add(subItem);
		subItem.setQuantity(quantitySubItem);
		quantitySubItem.setUnit(isoUnitKeySubItem);
		commerceExternalConfiguration.setExternalConfiguration(externalConfiguration);

		MockitoAnnotations.initMocks(this);
		Mockito.when(unitService.getUnitForCode(isoUnitKey)).thenReturn(unitModel);
		Mockito.when(unitService.getUnitForCode(isoUnitKeySubItem)).thenReturn(unitModel2);
		Mockito.when(unitService.getUnitForCode(isoUnitKeyNotKnownToHybris)).thenThrow(UnknownIdentifierException.class);
		Mockito.when(unitModel.getSapCode()).thenReturn(sapUnitKey);
		Mockito.when(unitModel2.getSapCode()).thenReturn(sapUnitKeySubItem);
		classUnderTest.setUnitService(unitService);
	}

	@Test
	public void testExtractCPSFormatFromCommerceRepresentation()
	{
		final CPSExternalConfiguration cpsExternalConfiguration = classUnderTest
				.extractCPSFormatFromCommerceRepresentation(commerceExternalConfiguration);

		assertEquals(externalConfiguration, cpsExternalConfiguration);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testExtractCPSFormatFromCommerceRepresentationErrorHandling()
	{
		classUnderTest.extractCPSFormatFromCommerceRepresentation(null);

	}

	@Test
	public void testCreateCommerceFormatFromCPSRepresentation()
	{
		final CPSCommerceExternalConfiguration cpsCommerceExternalConfiguration = classUnderTest
				.createCommerceFormatFromCPSRepresentation(externalConfiguration);
		assertEquals(cpsCommerceExternalConfiguration.getExternalConfiguration(), externalConfiguration);
	}

	@Test
	public void testCreateCommerceFormatFromCPSRepresentationUnitList()
	{
		final CPSCommerceExternalConfiguration cpsCommerceExternalConfiguration = classUnderTest
				.createCommerceFormatFromCPSRepresentation(externalConfiguration);
		final Map<String, String> unitCodes = cpsCommerceExternalConfiguration.getUnitCodes();
		assertNotNull(unitCodes);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCompileUnitCodesErrorHandling()
	{
		classUnderTest.compileUnitCodes(null);
	}

	@Test
	public void testCompileUnitCodes()
	{
		final Map<String, String> unitCodes = classUnderTest.compileUnitCodes(rootItem);
		assertNotNull(unitCodes);
		assertEquals(2, unitCodes.size());
	}

	@Test
	public void testCollectUnitCodes()
	{
		final Map<String, String> unitCodesAsMap = getUnitList();
		assertEquals(sapUnitKey, unitCodesAsMap.get(isoUnitKey));
	}

	@Test
	public void testCollectUnitCodesCanCopeWithEmptyQuantity()
	{
		rootItem.setQuantity(null);
		final Map<String, String> unitCodesAsMap = getUnitList();
		assertEquals(1, unitCodesAsMap.size());
	}

	@Test
	public void testCollectUnitCodesCanCopeWithEmptyIsoCode()
	{
		rootItem.getQuantity().setUnit(null);
		final Map<String, String> unitCodesAsMap = getUnitList();
		assertEquals(1, unitCodesAsMap.size());
	}


	@Test
	public void testCollectUnitCodesSubItem()
	{
		final Map<String, String> unitCodesAsMap = getUnitList();
		assertEquals(sapUnitKeySubItem, unitCodesAsMap.get(isoUnitKeySubItem));
	}

	@Test(expected = UnknownIdentifierException.class)
	public void testCollectUnitCodesUnitNotKnownToHybris()
	{
		rootItem.getQuantity().setUnit(isoUnitKeyNotKnownToHybris);
		final Map<String, String> unitCodesAsMap = new HashMap<>();
		classUnderTest.collectUnitCodes(rootItem, unitCodesAsMap);
	}

	@Test
	public void testUnitService()
	{
		assertEquals(unitService, classUnderTest.getUnitService());
	}
}
