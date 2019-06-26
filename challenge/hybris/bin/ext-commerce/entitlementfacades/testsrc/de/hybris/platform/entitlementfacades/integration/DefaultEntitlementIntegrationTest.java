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
package de.hybris.platform.entitlementfacades.integration;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.entitlementfacades.data.EntitlementData;
import de.hybris.platform.entitlementfacades.integration.templates.TemplateProcessor;
import de.hybris.platform.entitlementfacades.integration.templates.TemplateProcessorFactory;
import de.hybris.platform.entitlementfacades.integration.templates.impl.VelocityTemplateProcessorFactory;
import de.hybris.platform.entitlementservices.enums.EntitlementTimeUnit;
import de.hybris.platform.impex.constants.ImpExConstants;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.util.Config;

import java.io.InputStream;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.annotation.Resource;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;



/**
 * Integration test for creating entitlements.
 */
@IntegrationTest
public class DefaultEntitlementIntegrationTest extends ServicelayerTest
{
	private static final Logger LOG = Logger.getLogger(DefaultEntitlementIntegrationTest.class);

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Resource
	private ProductFacade productFacade;

	private TemplateProcessorFactory templateProcessorFactory = new VelocityTemplateProcessorFactory();

	private TemplateProcessor impexTemplateProcessor;

	private static final String PRODUCT_CODE = "P1";
	private static final String PRODUCT_CODE_2 = "P2";
	private static final String NME1_CODE = "NME1";
	private static final String NME1_NAME = "Non-Metered Entitlement 1";
	private static final String NME2_CODE = "NME2";
	private static final String NME2_NAME = "Non-Metered Entitlement 2";
	private static final String NMETU1_CODE = "NMETU1";
	private static final String NMETU1_NAME = "Non-Metered Entitlement TimeUnits1";
	private static final String ME1_CODE = "ME1";
	private static final String ME1_NAME = "Metered Entitlement 1";
	private static final String ME2_CODE = "ME2";
	private static final String ME2_NAME = "Metered Entitlement 2";
	private static final String METU1_CODE = "METU1";
	private static final String METU1_NAME = "Metered Entitlement TimeUnits1";
	private static final String STRING_CONDITION = "stringCondition1";
	private static final String STRING_CONDITION_2 = "stringCondition2";
	private static final String GEO_CONDITION = "geoCondition1";
	private static final String GEO_CONDITION_2 = "geoCondition2";
	private static final String PATH_CONDITION = "pathCondition1";
	private static final String PATH_CONDITION_2 = "pathCondition2";
	private static final String TIME_UNIT_DAY = "day";
	private static final String TIME_UNIT_MONTH = "month";
	private static final Integer TIME_UNIT_0 = 0;
	private static final Integer TIME_UNIT_1 = 1;
	private static final Integer TIME_UNIT_30 = 30;
	private static final Integer TIME_UNIT_12 = 12;
	private static final Integer TIME_UNIT_365 = 365;
	private static final String DEFAULT_ENCODING = "UTF-8";


	@Before
	public void setUp() throws ImpExException
	{
		// importing test csv
		LOG.info("Creating data for DefaultEntitlementIntegrationTest ...");
		final long startTime = System.currentTimeMillis();
		final String legacyModeBackup = Config.getParameter(ImpExConstants.Params.LEGACY_MODE_KEY);
		Config.setParameter(ImpExConstants.Params.LEGACY_MODE_KEY, "true");
		importCsv("/entitlementfacades/test/testCommerceCart.csv", DEFAULT_ENCODING);
		Config.setParameter(ImpExConstants.Params.LEGACY_MODE_KEY, "false");
		importCsv("/entitlementfacades/test/testEntitlements.csv", DEFAULT_ENCODING);
		Config.setParameter(ImpExConstants.Params.LEGACY_MODE_KEY, legacyModeBackup);

		LOG.info("Finished data for DefaultEntitlementIntegrationTest " + (System.currentTimeMillis() - startTime) + "ms");
	}

	@Test
	public void test_Entitlement_CreateGeoCondition_ValidPath_No1_PASS() throws ImpExException
	{
		final String geoCondition = "A";
		createNonMeteredEntitlement(PRODUCT_CODE, NME1_CODE, "", "", geoCondition, "", "", "");

		final ProductData productData = productFacade.getProductForCodeAndOptions(PRODUCT_CODE,
				Arrays.asList(ProductOption.BASIC, ProductOption.PRICE, ProductOption.ENTITLEMENTS));
		Assert.assertNotNull(productData);
		Assert.assertNotNull(productData.getEntitlements());
		Assert.assertFalse(productData.getEntitlements().isEmpty());
		Assert.assertEquals(1, productData.getEntitlements().size());

		final EntitlementData entitlementData = productData.getEntitlements().get(0);
		Assert.assertEquals(NME1_NAME, entitlementData.getName());
		Assert.assertNotNull(entitlementData.getId());
		Assert.assertEquals(0, entitlementData.getQuantity());
		Assert.assertNull(entitlementData.getConditionPath());
		Assert.assertNull(entitlementData.getConditionString());
		Assert.assertFalse(entitlementData.getConditionGeo().isEmpty());
		Assert.assertEquals(1, entitlementData.getConditionGeo().size());
		Assert.assertTrue(entitlementData.getConditionGeo().contains(geoCondition));
		Assert.assertNull(entitlementData.getTimeUnit());
		Assert.assertNull(entitlementData.getTimeUnitStart());
		Assert.assertNull(entitlementData.getTimeUnitDuration());

	}

	@Test
	public void test_Entitlement_CreateAndVerifyXML_Metered_GeoCondition_x1() throws ImpExException
	{
		createEntitlement(PRODUCT_CODE, ME1_CODE, "2", "", "", GEO_CONDITION, "", "", "");

		final ProductData productData = productFacade.getProductForCodeAndOptions(PRODUCT_CODE,
				Arrays.asList(ProductOption.BASIC, ProductOption.PRICE, ProductOption.ENTITLEMENTS));
		Assert.assertNotNull(productData);
		Assert.assertNotNull(productData.getEntitlements());
		Assert.assertFalse(productData.getEntitlements().isEmpty());
		Assert.assertEquals(1, productData.getEntitlements().size());

		final EntitlementData entitlementData = productData.getEntitlements().get(0);
		Assert.assertEquals(ME1_NAME, entitlementData.getName());
		Assert.assertNotNull(entitlementData.getId());
		Assert.assertEquals(2, entitlementData.getQuantity());
		Assert.assertNull(entitlementData.getConditionPath());
		Assert.assertNull(entitlementData.getConditionString());
		Assert.assertFalse(entitlementData.getConditionGeo().isEmpty());
		Assert.assertEquals(1, entitlementData.getConditionGeo().size());
		Assert.assertTrue(entitlementData.getConditionGeo().contains(GEO_CONDITION));
		Assert.assertNull(entitlementData.getTimeUnit());
		Assert.assertNull(entitlementData.getTimeUnitStart());
		Assert.assertNull(entitlementData.getTimeUnitDuration());

	}

	@Test
	public void test_Entitlement_CreateAndVerifyXML_NonMetered_GeoCondition_x2() throws ImpExException
	{
		createNonMeteredEntitlement(PRODUCT_CODE, NME1_CODE,"", "", GEO_CONDITION, "", "", "");
		createNonMeteredEntitlement(PRODUCT_CODE, NME2_CODE,"", "", GEO_CONDITION_2, "", "", "");

		final ProductData productData = productFacade.getProductForCodeAndOptions(PRODUCT_CODE,
				Arrays.asList(ProductOption.BASIC, ProductOption.PRICE, ProductOption.ENTITLEMENTS));
		Assert.assertNotNull(productData);
		Assert.assertNotNull(productData.getEntitlements());
		Assert.assertFalse(productData.getEntitlements().isEmpty());
		Assert.assertEquals(2, productData.getEntitlements().size());

		final EntitlementData entitlementData = productData.getEntitlements().get(0);
		Assert.assertEquals(NME1_NAME, entitlementData.getName());
		Assert.assertNotNull(entitlementData.getId());
		Assert.assertEquals(0, entitlementData.getQuantity());
		Assert.assertNull(entitlementData.getConditionPath());
		Assert.assertNull(entitlementData.getConditionString());
		Assert.assertFalse(entitlementData.getConditionGeo().isEmpty());
		Assert.assertEquals(1, entitlementData.getConditionGeo().size());
		Assert.assertTrue(entitlementData.getConditionGeo().contains(GEO_CONDITION));
		Assert.assertNull(entitlementData.getTimeUnit());
		Assert.assertNull(entitlementData.getTimeUnitStart());
		Assert.assertNull(entitlementData.getTimeUnitDuration());

		final EntitlementData entitlementData2 = productData.getEntitlements().get(1);
		Assert.assertEquals(NME2_NAME, entitlementData2.getName());
		Assert.assertNotNull(entitlementData2.getId());
		Assert.assertEquals(0, entitlementData2.getQuantity());
		Assert.assertNull(entitlementData2.getConditionPath());
		Assert.assertNull(entitlementData2.getConditionString());
		Assert.assertFalse(entitlementData2.getConditionGeo().isEmpty());
		Assert.assertEquals(1, entitlementData2.getConditionGeo().size());
		Assert.assertTrue(entitlementData2.getConditionGeo().contains(GEO_CONDITION_2));
		Assert.assertNull(entitlementData2.getTimeUnit());
		Assert.assertNull(entitlementData2.getTimeUnitStart());
		Assert.assertNull(entitlementData2.getTimeUnitDuration());

	}

	@Test
	public void test_Entitlement_CreateAndVerifyXML_Metered_GeoCondition_x2() throws ImpExException
	{
		createEntitlement(PRODUCT_CODE, ME1_CODE, "1","", "", GEO_CONDITION, "", "", "");
		createEntitlement(PRODUCT_CODE, ME2_CODE, "2","", "", GEO_CONDITION_2, "", "", "");

		final ProductData productData = productFacade.getProductForCodeAndOptions(PRODUCT_CODE,
				Arrays.asList(ProductOption.BASIC, ProductOption.PRICE, ProductOption.ENTITLEMENTS));
		Assert.assertNotNull(productData);
		Assert.assertNotNull(productData.getEntitlements());
		Assert.assertFalse(productData.getEntitlements().isEmpty());
		Assert.assertEquals(2, productData.getEntitlements().size());

		final EntitlementData entitlementData = productData.getEntitlements().get(0);
		Assert.assertEquals(ME1_NAME, entitlementData.getName());
		Assert.assertNotNull(entitlementData.getId());
		Assert.assertEquals(1, entitlementData.getQuantity());
		Assert.assertNull(entitlementData.getConditionPath());
		Assert.assertNull(entitlementData.getConditionString());
		Assert.assertFalse(entitlementData.getConditionGeo().isEmpty());
		Assert.assertEquals(1, entitlementData.getConditionGeo().size());
		Assert.assertTrue(entitlementData.getConditionGeo().contains(GEO_CONDITION));
		Assert.assertNull(entitlementData.getTimeUnit());
		Assert.assertNull(entitlementData.getTimeUnitStart());
		Assert.assertNull(entitlementData.getTimeUnitDuration());

		final EntitlementData entitlementData2 = productData.getEntitlements().get(1);
		Assert.assertEquals(ME2_NAME, entitlementData2.getName());
		Assert.assertNotNull(entitlementData2.getId());
		Assert.assertEquals(2, entitlementData2.getQuantity());
		Assert.assertNull(entitlementData2.getConditionPath());
		Assert.assertNull(entitlementData2.getConditionString());
		Assert.assertFalse(entitlementData2.getConditionGeo().isEmpty());
		Assert.assertEquals(1, entitlementData2.getConditionGeo().size());
		Assert.assertTrue(entitlementData2.getConditionGeo().contains(GEO_CONDITION_2));
		Assert.assertNull(entitlementData2.getTimeUnit());
		Assert.assertNull(entitlementData2.getTimeUnitStart());
		Assert.assertNull(entitlementData2.getTimeUnitDuration());

	}

	@Test
	public void test_Entitlement_CreateGeoCondition_ValidPath_No2_PASS() throws ImpExException
	{
		final String geoCondition = "A/B";
		createNonMeteredEntitlement(PRODUCT_CODE, NME1_CODE,"", "", geoCondition, "", "", "");

		final ProductData productData = productFacade.getProductForCodeAndOptions(PRODUCT_CODE,
				Arrays.asList(ProductOption.BASIC, ProductOption.PRICE, ProductOption.ENTITLEMENTS));
		Assert.assertNotNull(productData);
		Assert.assertNotNull(productData.getEntitlements());
		Assert.assertFalse(productData.getEntitlements().isEmpty());
		Assert.assertEquals(1, productData.getEntitlements().size());

		final EntitlementData entitlementData = productData.getEntitlements().get(0);
		Assert.assertEquals(NME1_NAME, entitlementData.getName());
		Assert.assertNotNull(entitlementData.getId());
		Assert.assertEquals(0, entitlementData.getQuantity());
		Assert.assertNull(entitlementData.getConditionPath());
		Assert.assertNull(entitlementData.getConditionString());
		Assert.assertFalse(entitlementData.getConditionGeo().isEmpty());
		Assert.assertEquals(1, entitlementData.getConditionGeo().size());
		Assert.assertTrue(entitlementData.getConditionGeo().contains(geoCondition));
		Assert.assertNull(entitlementData.getTimeUnit());
		Assert.assertNull(entitlementData.getTimeUnitStart());
		Assert.assertNull(entitlementData.getTimeUnitDuration());
	}

	@Test
	public void test_Entitlement_CreateGeoCondition_ValidPath_No3_PASS() throws ImpExException
	{
		final String geoCondition = "A/B/Omsk";
		createNonMeteredEntitlement(PRODUCT_CODE, NME1_CODE,"", "", geoCondition, "", "", "");

		final ProductData productData = productFacade.getProductForCodeAndOptions(PRODUCT_CODE,
				Arrays.asList(ProductOption.BASIC, ProductOption.PRICE, ProductOption.ENTITLEMENTS));
		Assert.assertNotNull(productData);
		Assert.assertNotNull(productData.getEntitlements());
		Assert.assertFalse(productData.getEntitlements().isEmpty());
		Assert.assertEquals(1, productData.getEntitlements().size());

		final EntitlementData entitlementData = productData.getEntitlements().get(0);
		Assert.assertEquals(NME1_NAME, entitlementData.getName());
		Assert.assertNotNull(entitlementData.getId());
		Assert.assertEquals(0, entitlementData.getQuantity());
		Assert.assertNull(entitlementData.getConditionPath());
		Assert.assertNull(entitlementData.getConditionString());
		Assert.assertFalse(entitlementData.getConditionGeo().isEmpty());
		Assert.assertEquals(1, entitlementData.getConditionGeo().size());
		Assert.assertTrue(entitlementData.getConditionGeo().contains(geoCondition));
		Assert.assertNull(entitlementData.getTimeUnit());
		Assert.assertNull(entitlementData.getTimeUnitStart());
		Assert.assertNull(entitlementData.getTimeUnitDuration());
	}

	@Test
	public void test_Entitlement_CreateGeoCondition_MultipleConditionEntries() throws ImpExException
	{
		final String composedGeoCondition = "A/B/Omsk, Z, X/C, 1/2/3";
		final String geoCondition1 = "A/B/Omsk";
		final String geoCondition2 = "Z";
		final String geoCondition3 = "X/C";
		final String geoCondition4 = "1/2/3";
		createNonMeteredEntitlement(PRODUCT_CODE, NME1_CODE, "", "", composedGeoCondition, "", "", "");

		final ProductData productData = productFacade.getProductForCodeAndOptions(PRODUCT_CODE,
				Arrays.asList(ProductOption.BASIC, ProductOption.PRICE, ProductOption.ENTITLEMENTS));
		Assert.assertNotNull(productData);
		Assert.assertNotNull(productData.getEntitlements());
		Assert.assertFalse(productData.getEntitlements().isEmpty());
		Assert.assertEquals(1, productData.getEntitlements().size());

		final EntitlementData entitlementData = productData.getEntitlements().get(0);
		Assert.assertEquals(NME1_NAME, entitlementData.getName());
		Assert.assertNotNull(entitlementData.getId());
		Assert.assertEquals(0, entitlementData.getQuantity());
		Assert.assertNull(entitlementData.getConditionPath());
		Assert.assertNull(entitlementData.getConditionString());
		Assert.assertFalse(entitlementData.getConditionGeo().isEmpty());
		Assert.assertEquals(4, entitlementData.getConditionGeo().size());
		Assert.assertTrue(entitlementData.getConditionGeo().contains(geoCondition1));
		Assert.assertTrue(entitlementData.getConditionGeo().contains(geoCondition2));
		Assert.assertTrue(entitlementData.getConditionGeo().contains(geoCondition3));
		Assert.assertTrue(entitlementData.getConditionGeo().contains(geoCondition4));
		Assert.assertNull(entitlementData.getTimeUnit());
		Assert.assertNull(entitlementData.getTimeUnitStart());
		Assert.assertNull(entitlementData.getTimeUnitDuration());

	}

	@Test
	public void test_Entitlement_CreateAndVerifyXML_GeoCondition_x2() throws ImpExException
	{
		createNonMeteredEntitlement(PRODUCT_CODE, NME1_CODE,"", "", GEO_CONDITION, "", "", "");
		createNonMeteredEntitlement(PRODUCT_CODE, NME2_CODE,"", "", GEO_CONDITION_2, "", "", "");

		final ProductData productData = productFacade.getProductForCodeAndOptions(PRODUCT_CODE,
				Arrays.asList(ProductOption.BASIC, ProductOption.PRICE, ProductOption.ENTITLEMENTS));
		Assert.assertNotNull(productData);
		Assert.assertNotNull(productData.getEntitlements());
		Assert.assertFalse(productData.getEntitlements().isEmpty());
		Assert.assertEquals(2, productData.getEntitlements().size());

		final EntitlementData entitlementData = productData.getEntitlements().get(0);
		Assert.assertEquals(NME1_NAME, entitlementData.getName());
		Assert.assertNotNull(entitlementData.getId());
		Assert.assertEquals(0, entitlementData.getQuantity());
		Assert.assertNull(entitlementData.getConditionPath());
		Assert.assertNull(entitlementData.getConditionString());
		Assert.assertFalse(entitlementData.getConditionGeo().isEmpty());
		Assert.assertEquals(1, entitlementData.getConditionGeo().size());
		Assert.assertTrue(entitlementData.getConditionGeo().contains(GEO_CONDITION));
		Assert.assertNull(entitlementData.getTimeUnit());
		Assert.assertNull(entitlementData.getTimeUnitStart());
		Assert.assertNull(entitlementData.getTimeUnitDuration());

		final EntitlementData entitlementData2 = productData.getEntitlements().get(1);
		Assert.assertEquals(NME2_NAME, entitlementData2.getName());
		Assert.assertNotNull(entitlementData2.getId());
		Assert.assertEquals(0, entitlementData2.getQuantity());
		Assert.assertNull(entitlementData2.getConditionPath());
		Assert.assertNull(entitlementData2.getConditionString());
		Assert.assertFalse(entitlementData2.getConditionGeo().isEmpty());
		Assert.assertEquals(1, entitlementData2.getConditionGeo().size());
		Assert.assertTrue(entitlementData2.getConditionGeo().contains(GEO_CONDITION_2));
		Assert.assertNull(entitlementData2.getTimeUnit());
		Assert.assertNull(entitlementData2.getTimeUnitStart());
		Assert.assertNull(entitlementData2.getTimeUnitDuration());


	}

	@Test
	public void test_Entitlement_CreateGeoCondition_InvalidPath_No1_FAIL() throws ImpExException
	{
		thrown.expect(AssertionError.class);
		thrown.expectMessage("Import has 1+unresolved lines, first lines are:");

		createNonMeteredEntitlement(PRODUCT_CODE, NME1_CODE,"", "", "//Omsk", "", "", "");
	}

	@Test
	public void test_Entitlement_CreateGeoCondition_InvalidPath_No2_FAIL() throws ImpExException
	{
		thrown.expect(AssertionError.class);
		thrown.expectMessage("Import has 1+unresolved lines, first lines are:");

		createNonMeteredEntitlement(PRODUCT_CODE, NME1_CODE,"", "", "/B/Omsk", "", "", "");
	}

	@Test
	public void test_Entitlement_CreateGeoCondition_InvalidPath_No3_FAIL() throws ImpExException
	{
		thrown.expect(AssertionError.class);
		thrown.expectMessage("Import has 1+unresolved lines, first lines are:");

		createNonMeteredEntitlement(PRODUCT_CODE, NME1_CODE,"", "", "A//Omsk", "", "", "");

	}

	@Test
	public void test_Entitlement_CreateGeoCondition_InvalidPath_No4_FAIL() throws ImpExException
	{
		thrown.expect(AssertionError.class);
		thrown.expectMessage("Import has 1+unresolved lines, first lines are:");

		createNonMeteredEntitlement(PRODUCT_CODE, NME1_CODE,"", "", "/B/", "", "", "");
	}

	@Test
	public void test_Entitlement_CreateGeoCondition_InvalidPath_No5_FAIL() throws ImpExException
	{
		thrown.expect(AssertionError.class);
		thrown.expectMessage("Import has 1+unresolved lines, first lines are:");

		createNonMeteredEntitlement(PRODUCT_CODE, NME1_CODE,"", "", "//", "", "", "");
	}

	@Test
	public void test_Entitlement_CreateGeoCondition_InvalidPath_No6_FAIL() throws ImpExException
	{
		thrown.expect(AssertionError.class);
		thrown.expectMessage("Import has 1+unresolved lines, first lines are:");

		createNonMeteredEntitlement(PRODUCT_CODE, NME1_CODE,"", "", "A//", "", "", "");
	}

	@Test
	public void test_Entitlement_CreateGeoCondition_InvalidPath_No7_FAIL() throws ImpExException
	{
		thrown.expect(AssertionError.class);
		thrown.expectMessage("Import has 1+unresolved lines, first lines are:");

		createNonMeteredEntitlement(PRODUCT_CODE, NME1_CODE,"", "", "A/B/", "", "", "");
	}

@Test
	public void test_Entitlement_CreateGeoCondition_InvalidPath_No8_FAIL() throws ImpExException
	{
		thrown.expect(AssertionError.class);
		thrown.expectMessage("Import has 1+unresolved lines, first lines are:");

		createNonMeteredEntitlement(PRODUCT_CODE, NME1_CODE,"", "", "A/B/Omsk/", "", "", "");
	}

	@Test
	public void test_Entitlement_CreateGeoCondition_InvalidPath_No9_FAIL() throws ImpExException
	{
		thrown.expect(AssertionError.class);
		thrown.expectMessage("Import has 1+unresolved lines, first lines are:");

		createNonMeteredEntitlement(PRODUCT_CODE, NME1_CODE,"", "", "A/B/Omsk/C", "", "", "");
	}


	//Metered

	@Test
	public void test_Entitlement_CreateAndVerifyXML_MeteredCondition_x1() throws ImpExException
	{
		createEntitlement(PRODUCT_CODE, ME1_CODE, "11","", "", "", "", "", "");

		final ProductData productData = productFacade.getProductForCodeAndOptions(PRODUCT_CODE,
				Arrays.asList(ProductOption.BASIC, ProductOption.PRICE, ProductOption.ENTITLEMENTS));
		Assert.assertNotNull(productData);
		Assert.assertNotNull(productData.getEntitlements());
		Assert.assertFalse(productData.getEntitlements().isEmpty());
		Assert.assertEquals(1, productData.getEntitlements().size());

		final EntitlementData entitlementData = productData.getEntitlements().get(0);
		Assert.assertEquals(ME1_NAME, entitlementData.getName());
		Assert.assertNotNull(entitlementData.getId());
		Assert.assertEquals(11, entitlementData.getQuantity());
		Assert.assertNull(entitlementData.getConditionPath());
		Assert.assertNull(entitlementData.getConditionString());
		Assert.assertNull(entitlementData.getConditionGeo());
		Assert.assertNull(entitlementData.getTimeUnit());
		Assert.assertNull(entitlementData.getTimeUnitStart());
		Assert.assertNull(entitlementData.getTimeUnitDuration());
	}

	@Test
	public void test_Entitlement_CreateAndVerifyXML_MeteredCondition_x2() throws ImpExException
	{
		createEntitlement(PRODUCT_CODE, ME2_CODE, "11","", "", "", "", "", "");
		createEntitlement(PRODUCT_CODE, ME1_CODE, "2","", "", "", "", "", "");

		final ProductData productData = productFacade.getProductForCodeAndOptions(PRODUCT_CODE,
				Arrays.asList(ProductOption.BASIC, ProductOption.PRICE, ProductOption.ENTITLEMENTS));
		Assert.assertNotNull(productData);
		Assert.assertNotNull(productData.getEntitlements());
		Assert.assertFalse(productData.getEntitlements().isEmpty());
		Assert.assertEquals(2, productData.getEntitlements().size());

		final EntitlementData entitlementData = productData.getEntitlements().get(0);
		Assert.assertEquals(ME2_NAME, entitlementData.getName());
		Assert.assertNotNull(entitlementData.getId());
		Assert.assertEquals(11, entitlementData.getQuantity());
		Assert.assertNull(entitlementData.getConditionPath());
		Assert.assertNull(entitlementData.getConditionString());
		Assert.assertNull(entitlementData.getConditionGeo());
		Assert.assertNull(entitlementData.getTimeUnit());
		Assert.assertNull(entitlementData.getTimeUnitStart());
		Assert.assertNull(entitlementData.getTimeUnitDuration());

		final EntitlementData entitlementData2 = productData.getEntitlements().get(1);
		Assert.assertEquals(ME1_NAME, entitlementData2.getName());
		Assert.assertNotNull(entitlementData2.getId());
		Assert.assertEquals(2, entitlementData2.getQuantity());
		Assert.assertNull(entitlementData2.getConditionPath());
		Assert.assertNull(entitlementData2.getConditionString());
		Assert.assertNull(entitlementData2.getConditionGeo());
		Assert.assertNull(entitlementData2.getTimeUnit());
		Assert.assertNull(entitlementData2.getTimeUnitStart());
		Assert.assertNull(entitlementData2.getTimeUnitDuration());
	}

	@Test
	public void test_Entitlement_CreateAndVerifyXML_MeteredCondition_Quantity_NoValue() throws ImpExException
	{
		createEntitlement(PRODUCT_CODE, ME1_CODE, "","", "", "", "", "", "");

		final ProductData productData = productFacade.getProductForCodeAndOptions(PRODUCT_CODE,
				Arrays.asList(ProductOption.BASIC, ProductOption.PRICE, ProductOption.ENTITLEMENTS));
		Assert.assertNotNull(productData);
		Assert.assertNotNull(productData.getEntitlements());
		Assert.assertFalse(productData.getEntitlements().isEmpty());
		Assert.assertEquals(1, productData.getEntitlements().size());

		final EntitlementData entitlementData = productData.getEntitlements().get(0);
		Assert.assertEquals(ME1_NAME, entitlementData.getName());
		Assert.assertNotNull(entitlementData.getId());
		Assert.assertEquals(0, entitlementData.getQuantity());
		Assert.assertNull(entitlementData.getConditionPath());
		Assert.assertNull(entitlementData.getConditionString());
		Assert.assertNull(entitlementData.getConditionGeo());
		Assert.assertNull(entitlementData.getTimeUnit());
		Assert.assertNull(entitlementData.getTimeUnitStart());
		Assert.assertNull(entitlementData.getTimeUnitDuration());
	}

	@Test
	public void test_Entitlement_CreateAndVerifyXML_MeteredCondition_Quantity_NonNumericValue_FAIL() throws ImpExException
	{
		thrown.expect(AssertionError.class);
		thrown.expectMessage("Import has 1+unresolved lines, first lines are:");

		createEntitlement(PRODUCT_CODE, ME1_CODE, "a","", "", "", "", "", "");
	}

	@Test
	public void test_Entitlement_CreateAndVerifyXML_MeteredCondition_Quantity_ZeroValue_PASS() throws ImpExException
	{
		createEntitlement(PRODUCT_CODE, ME1_CODE, "0","", "", "", "", "", "");

		final ProductData productData = productFacade.getProductForCodeAndOptions(PRODUCT_CODE,
				Arrays.asList(ProductOption.BASIC, ProductOption.PRICE, ProductOption.ENTITLEMENTS));
		Assert.assertNotNull(productData);
		Assert.assertNotNull(productData.getEntitlements());
		Assert.assertFalse(productData.getEntitlements().isEmpty());
		Assert.assertEquals(1, productData.getEntitlements().size());

		final EntitlementData entitlementData = productData.getEntitlements().get(0);
		Assert.assertEquals(ME1_NAME, entitlementData.getName());
		Assert.assertNotNull(entitlementData.getId());
		Assert.assertEquals(0, entitlementData.getQuantity());
		Assert.assertNull(entitlementData.getConditionPath());
		Assert.assertNull(entitlementData.getConditionString());
		Assert.assertNull(entitlementData.getConditionGeo());
		Assert.assertNull(entitlementData.getTimeUnit());
		Assert.assertNull(entitlementData.getTimeUnitStart());
		Assert.assertNull(entitlementData.getTimeUnitDuration());
	}

	//PathCondition

	@Test
	public void test_Entitlement_CreateAndVerifyXML_PathCondition_InvalidPath_No1_FAIL() throws ImpExException
	{
		thrown.expect(AssertionError.class);
		thrown.expectMessage("Import has 1+unresolved lines, first lines are:");

		createNonMeteredEntitlement(PRODUCT_CODE, NME1_CODE,"/", "", "", "", "", "");
	}

	@Test
	public void test_Entitlement_CreateAndVerifyXML_PathCondition_InvalidPath_No2_FAIL() throws ImpExException
	{
		thrown.expect(AssertionError.class);
		thrown.expectMessage("Import has 1+unresolved lines, first lines are:");

		createNonMeteredEntitlement(PRODUCT_CODE, NME1_CODE,"a/", "", "", "", "", "");
	}

	@Test
	public void test_Entitlement_CreateAndVerifyXML_PathCondition_InvalidPath_No3_FAIL() throws ImpExException
	{
		thrown.expect(AssertionError.class);
		thrown.expectMessage("Import has 1+unresolved lines, first lines are:");

		createNonMeteredEntitlement(PRODUCT_CODE, NME1_CODE,"a/b/", "", "", "", "", "");
	}

	@Test
	public void test_Entitlement_CreateAndVerifyXML_PathCondition_ValidPath_No1_PASS() throws ImpExException
	{
		final String pathCondition = "a";
		createNonMeteredEntitlement(PRODUCT_CODE, NME1_CODE, pathCondition, "", "", "", "", "");

		final ProductData productData = productFacade.getProductForCodeAndOptions(PRODUCT_CODE,
				Arrays.asList(ProductOption.BASIC, ProductOption.PRICE, ProductOption.ENTITLEMENTS));
		Assert.assertNotNull(productData);
		Assert.assertNotNull(productData.getEntitlements());
		Assert.assertFalse(productData.getEntitlements().isEmpty());
		Assert.assertEquals(1, productData.getEntitlements().size());

		final EntitlementData entitlementData = productData.getEntitlements().get(0);
		Assert.assertEquals(NME1_NAME, entitlementData.getName());
		Assert.assertNotNull(entitlementData.getId());
		Assert.assertEquals(0, entitlementData.getQuantity());
		Assert.assertTrue(entitlementData.getConditionPath().contains(pathCondition));
		Assert.assertNull(entitlementData.getConditionString());
		Assert.assertNull(entitlementData.getConditionGeo());
		Assert.assertNull(entitlementData.getTimeUnit());
		Assert.assertNull(entitlementData.getTimeUnitStart());
		Assert.assertNull(entitlementData.getTimeUnitDuration());
	}

	@Test
	public void test_Entitlement_CreateAndVerifyXML_PathCondition_ValidPath_No2_PASS() throws ImpExException
	{
		final String pathCondition = "a/b";
		createNonMeteredEntitlement(PRODUCT_CODE, NME1_CODE, pathCondition, "", "", "", "", "");

		final ProductData productData = productFacade.getProductForCodeAndOptions(PRODUCT_CODE,
				Arrays.asList(ProductOption.BASIC, ProductOption.PRICE, ProductOption.ENTITLEMENTS));
		Assert.assertNotNull(productData);
		Assert.assertNotNull(productData.getEntitlements());
		Assert.assertFalse(productData.getEntitlements().isEmpty());
		Assert.assertEquals(1, productData.getEntitlements().size());

		final EntitlementData entitlementData = productData.getEntitlements().get(0);
		Assert.assertEquals(NME1_NAME, entitlementData.getName());
		Assert.assertNotNull(entitlementData.getId());
		Assert.assertEquals(0, entitlementData.getQuantity());
		Assert.assertTrue(entitlementData.getConditionPath().contains(pathCondition));
		Assert.assertNull(entitlementData.getConditionString());
		Assert.assertNull(entitlementData.getConditionGeo());
		Assert.assertNull(entitlementData.getTimeUnit());
		Assert.assertNull(entitlementData.getTimeUnitStart());
		Assert.assertNull(entitlementData.getTimeUnitDuration());
	}

	@Test
	public void test_Entitlement_CreateAndVerifyXML_PathCondition_ValidPath_Long_PASS() throws ImpExException
	{
		final String pathCondition = "a/b/1@/}{%*";
		createNonMeteredEntitlement(PRODUCT_CODE, NME1_CODE, pathCondition, "", "", "", "", "");

		final ProductData productData = productFacade.getProductForCodeAndOptions(PRODUCT_CODE,
				Arrays.asList(ProductOption.BASIC, ProductOption.PRICE, ProductOption.ENTITLEMENTS));
		Assert.assertNotNull(productData);
		Assert.assertNotNull(productData.getEntitlements());
		Assert.assertFalse(productData.getEntitlements().isEmpty());
		Assert.assertEquals(1, productData.getEntitlements().size());

		final EntitlementData entitlementData = productData.getEntitlements().get(0);
		Assert.assertEquals(NME1_NAME, entitlementData.getName());
		Assert.assertNotNull(entitlementData.getId());
		Assert.assertEquals(0, entitlementData.getQuantity());
		Assert.assertTrue(entitlementData.getConditionPath().contains(pathCondition));
		Assert.assertNull(entitlementData.getConditionString());
		Assert.assertNull(entitlementData.getConditionGeo());
		Assert.assertNull(entitlementData.getTimeUnit());
		Assert.assertNull(entitlementData.getTimeUnitStart());
		Assert.assertNull(entitlementData.getTimeUnitDuration());
	}

	@Test
	public void test_Entitlement_CreateAndVerifyXML_PathCondition_ValidPath_Short_PASS() throws ImpExException
	{
		final String pathCondition = "/a";
		createNonMeteredEntitlement(PRODUCT_CODE, NME1_CODE, pathCondition, "", "", "", "", "");

		final ProductData productData = productFacade.getProductForCodeAndOptions(PRODUCT_CODE,
				Arrays.asList(ProductOption.BASIC, ProductOption.PRICE, ProductOption.ENTITLEMENTS));
		Assert.assertNotNull(productData);
		Assert.assertNotNull(productData.getEntitlements());
		Assert.assertFalse(productData.getEntitlements().isEmpty());
		Assert.assertEquals(1, productData.getEntitlements().size());

		final EntitlementData entitlementData = productData.getEntitlements().get(0);
		Assert.assertEquals(NME1_NAME, entitlementData.getName());
		Assert.assertNotNull(entitlementData.getId());
		Assert.assertEquals(0, entitlementData.getQuantity());
		Assert.assertTrue(entitlementData.getConditionPath().contains(pathCondition));
		Assert.assertNull(entitlementData.getConditionString());
		Assert.assertNull(entitlementData.getConditionGeo());
		Assert.assertNull(entitlementData.getTimeUnit());
		Assert.assertNull(entitlementData.getTimeUnitStart());
		Assert.assertNull(entitlementData.getTimeUnitDuration());
	}

	@Test
	public void test_Entitlement_CreateAndVerifyXML_PathCondition_x2() throws ImpExException
	{
		createNonMeteredEntitlement(PRODUCT_CODE, NME1_CODE,PATH_CONDITION, "", "", "", "", "");
		createNonMeteredEntitlement(PRODUCT_CODE, NME2_CODE, PATH_CONDITION_2, "", "", "", "", "");

		final ProductData productData = productFacade.getProductForCodeAndOptions(PRODUCT_CODE,
				Arrays.asList(ProductOption.BASIC, ProductOption.PRICE, ProductOption.ENTITLEMENTS));
		Assert.assertNotNull(productData);
		Assert.assertNotNull(productData.getEntitlements());
		Assert.assertFalse(productData.getEntitlements().isEmpty());
		Assert.assertEquals(2, productData.getEntitlements().size());

		final EntitlementData entitlementData = productData.getEntitlements().get(0);
		Assert.assertEquals(NME1_NAME, entitlementData.getName());
		Assert.assertNotNull(entitlementData.getId());
		Assert.assertEquals(0, entitlementData.getQuantity());
		Assert.assertTrue(entitlementData.getConditionPath().contains(PATH_CONDITION));
		Assert.assertNull(entitlementData.getConditionString());
		Assert.assertNull(entitlementData.getConditionGeo());
		Assert.assertNull(entitlementData.getTimeUnit());
		Assert.assertNull(entitlementData.getTimeUnitStart());
		Assert.assertNull(entitlementData.getTimeUnitDuration());

		final EntitlementData entitlementData2 = productData.getEntitlements().get(1);
		Assert.assertEquals(NME2_NAME, entitlementData2.getName());
		Assert.assertNotNull(entitlementData2.getId());
		Assert.assertEquals(0, entitlementData2.getQuantity());
		Assert.assertTrue(entitlementData2.getConditionPath().contains(PATH_CONDITION_2));
		Assert.assertNull(entitlementData2.getConditionString());
		Assert.assertNull(entitlementData2.getConditionGeo());
		Assert.assertNull(entitlementData2.getTimeUnit());
		Assert.assertNull(entitlementData2.getTimeUnitStart());
		Assert.assertNull(entitlementData2.getTimeUnitDuration());
	}

	//string condition
	@Test
	public void test_Entitlement_CreateAndVerifyXML_StringCondition_x1() throws ImpExException
	{
		createNonMeteredEntitlement(PRODUCT_CODE, NME1_CODE,"", STRING_CONDITION, "", "", "", "");

		final ProductData productData = productFacade.getProductForCodeAndOptions(PRODUCT_CODE,
				Arrays.asList(ProductOption.BASIC, ProductOption.PRICE, ProductOption.ENTITLEMENTS));
		Assert.assertNotNull(productData);
		Assert.assertNotNull(productData.getEntitlements());
		Assert.assertFalse(productData.getEntitlements().isEmpty());
		Assert.assertEquals(1, productData.getEntitlements().size());

		final EntitlementData entitlementData = productData.getEntitlements().get(0);
		Assert.assertEquals(NME1_NAME, entitlementData.getName());
		Assert.assertNotNull(entitlementData.getId());
		Assert.assertEquals(0, entitlementData.getQuantity());
		Assert.assertNull(entitlementData.getConditionPath());
		Assert.assertTrue(entitlementData.getConditionString().contains(STRING_CONDITION));
		Assert.assertNull(entitlementData.getConditionGeo());
		Assert.assertNull(entitlementData.getTimeUnit());
		Assert.assertNull(entitlementData.getTimeUnitStart());
		Assert.assertNull(entitlementData.getTimeUnitDuration());
	}

	@Test
	public void test_Entitlement_CreateAndVerifyXML_StringCondition_x2() throws ImpExException
	{
		createNonMeteredEntitlement(PRODUCT_CODE, NME1_CODE,"", STRING_CONDITION, "", "", "", "");
		createNonMeteredEntitlement(PRODUCT_CODE, NME2_CODE,"", STRING_CONDITION_2, "", "", "", "");

		final ProductData productData = productFacade.getProductForCodeAndOptions(PRODUCT_CODE,
				Arrays.asList(ProductOption.BASIC, ProductOption.PRICE, ProductOption.ENTITLEMENTS));
		Assert.assertNotNull(productData);
		Assert.assertNotNull(productData.getEntitlements());
		Assert.assertFalse(productData.getEntitlements().isEmpty());
		Assert.assertEquals(2, productData.getEntitlements().size());

		final EntitlementData entitlementData = productData.getEntitlements().get(0);
		Assert.assertEquals(NME1_NAME, entitlementData.getName());
		Assert.assertNotNull(entitlementData.getId());
		Assert.assertEquals(0, entitlementData.getQuantity());
		Assert.assertNull(entitlementData.getConditionPath());
		Assert.assertTrue(entitlementData.getConditionString().contains(STRING_CONDITION));
		Assert.assertNull(entitlementData.getConditionGeo());
		Assert.assertNull(entitlementData.getTimeUnit());
		Assert.assertNull(entitlementData.getTimeUnitStart());
		Assert.assertNull(entitlementData.getTimeUnitDuration());

		final EntitlementData entitlementData2 = productData.getEntitlements().get(1);
		Assert.assertEquals(NME2_NAME, entitlementData2.getName());
		Assert.assertNotNull(entitlementData2.getId());
		Assert.assertEquals(0, entitlementData2.getQuantity());
		Assert.assertNull(entitlementData2.getConditionGeo());
		Assert.assertNull(entitlementData2.getConditionPath());
		Assert.assertTrue(entitlementData2.getConditionString().contains(STRING_CONDITION_2));
		Assert.assertNull(entitlementData2.getTimeUnit());
		Assert.assertNull(entitlementData2.getTimeUnitStart());
		Assert.assertNull(entitlementData2.getTimeUnitDuration());
	}

	//timeframe condition
	@Test
	public void test_Entitlement_CreateAndVerifyXML_TimeframeCondition_x1() throws ImpExException
	{
		createNonMeteredEntitlement(PRODUCT_CODE, NME1_CODE,"", "", "", TIME_UNIT_DAY, "1", "1");

		final ProductData productData = productFacade.getProductForCodeAndOptions(PRODUCT_CODE,
				Arrays.asList(ProductOption.BASIC, ProductOption.PRICE, ProductOption.ENTITLEMENTS));
		Assert.assertNotNull(productData);
		Assert.assertNotNull(productData.getEntitlements());
		Assert.assertFalse(productData.getEntitlements().isEmpty());
		Assert.assertEquals(1, productData.getEntitlements().size());

		final EntitlementData entitlementData = productData.getEntitlements().get(0);
		Assert.assertEquals(NME1_NAME, entitlementData.getName());
		Assert.assertNotNull(entitlementData.getId());
		Assert.assertEquals(0, entitlementData.getQuantity());
		Assert.assertNull(entitlementData.getConditionPath());
		Assert.assertNull(entitlementData.getConditionString());
		Assert.assertNull(entitlementData.getConditionGeo());
		Assert.assertEquals(EntitlementTimeUnit.DAY, entitlementData.getTimeUnit());
		Assert.assertEquals(TIME_UNIT_1, entitlementData.getTimeUnitStart());
		Assert.assertEquals(TIME_UNIT_1, entitlementData.getTimeUnitDuration());
	}

	@Test
	public void test_Entitlement_CreateAndVerifyXML_TimeframeCondition_x2() throws ImpExException
	{
		createNonMeteredEntitlement(PRODUCT_CODE, NME1_CODE,"", "", "", TIME_UNIT_DAY, "1", "1");
		createNonMeteredEntitlement(PRODUCT_CODE, NME2_CODE,"", "", "", TIME_UNIT_MONTH, "1", "0");

		final ProductData productData = productFacade.getProductForCodeAndOptions(PRODUCT_CODE,
				Arrays.asList(ProductOption.BASIC, ProductOption.PRICE, ProductOption.ENTITLEMENTS));
		Assert.assertNotNull(productData);
		Assert.assertNotNull(productData.getEntitlements());
		Assert.assertFalse(productData.getEntitlements().isEmpty());
		Assert.assertEquals(2, productData.getEntitlements().size());

		final EntitlementData entitlementData = productData.getEntitlements().get(0);
		Assert.assertEquals(NME1_NAME, entitlementData.getName());
		Assert.assertNotNull(entitlementData.getId());
		Assert.assertEquals(0, entitlementData.getQuantity());
		Assert.assertNull(entitlementData.getConditionPath());
		Assert.assertNull(entitlementData.getConditionString());
		Assert.assertNull(entitlementData.getConditionGeo());
		Assert.assertEquals(EntitlementTimeUnit.DAY, entitlementData.getTimeUnit());
		Assert.assertEquals(TIME_UNIT_1, entitlementData.getTimeUnitStart());
		Assert.assertEquals(TIME_UNIT_1, entitlementData.getTimeUnitDuration());

		final EntitlementData entitlementData2 = productData.getEntitlements().get(1);
		Assert.assertEquals(NME2_NAME, entitlementData2.getName());
		Assert.assertNotNull(entitlementData2.getId());
		Assert.assertEquals(0, entitlementData2.getQuantity());
		Assert.assertNull(entitlementData2.getConditionPath());
		Assert.assertNull(entitlementData2.getConditionString());
		Assert.assertNull(entitlementData2.getConditionGeo());
		Assert.assertEquals(EntitlementTimeUnit.MONTH, entitlementData2.getTimeUnit());
		Assert.assertEquals(TIME_UNIT_1, entitlementData2.getTimeUnitStart());
		Assert.assertEquals(TIME_UNIT_0, entitlementData2.getTimeUnitDuration());
	}

	@Test
	public void test_Entitlement_CreateAndVerifyXML_TimeframeCondition_TimeUnit_Without_StartTime_FAIL() throws ImpExException
	{
		thrown.expect(AssertionError.class);
		thrown.expectMessage("Import has 1+unresolved lines, first lines are:");

		createNonMeteredEntitlement(PRODUCT_CODE, NME1_CODE,"", "", "", TIME_UNIT_DAY, "", "1");
	}

	@Test
	public void test_Entitlement_CreateAndVerifyXML_TimeframeCondition_Duration_only_FAIL() throws ImpExException
	{
		thrown.expect(AssertionError.class);
		thrown.expectMessage("Import has 1+unresolved lines, first lines are:");

		createNonMeteredEntitlement(PRODUCT_CODE, NME1_CODE,"", "", "", "", "", "1");
	}

	@Test
	public void test_Entitlement_CreateAndVerifyXML_TimeframeCondition_Negative_StartTime_FAIL() throws ImpExException
	{
		thrown.expect(AssertionError.class);
		thrown.expectMessage("Import has 1+unresolved lines, first lines are:");

		createNonMeteredEntitlement(PRODUCT_CODE, NME1_CODE,"", "", "", TIME_UNIT_DAY, "-1", "1");
	}

	@Test
	public void test_Entitlement_CreateAndVerifyXML_TimeframeCondition_Negative_Duration_FAIL() throws ImpExException
	{
		thrown.expect(AssertionError.class);
		thrown.expectMessage("Import has 1+unresolved lines, first lines are:");

		createNonMeteredEntitlement(PRODUCT_CODE, NME1_CODE,"", "", "", TIME_UNIT_DAY, "10", "-10");
	}

	@Test
	public void test_Entitlement_CreateAndVerifyXML_TimeframeCondition_Duration_NoValue_PASS() throws ImpExException
	{
		createNonMeteredEntitlement(PRODUCT_CODE, NME1_CODE,"", "", "", TIME_UNIT_DAY, "1", "");

		final ProductData productData = productFacade.getProductForCodeAndOptions(PRODUCT_CODE,
				Arrays.asList(ProductOption.BASIC, ProductOption.PRICE, ProductOption.ENTITLEMENTS));
		Assert.assertNotNull(productData);
		Assert.assertNotNull(productData.getEntitlements());
		Assert.assertFalse(productData.getEntitlements().isEmpty());
		Assert.assertEquals(1, productData.getEntitlements().size());

		final EntitlementData entitlementData = productData.getEntitlements().get(0);
		Assert.assertEquals(NME1_NAME, entitlementData.getName());
		Assert.assertNotNull(entitlementData.getId());
		Assert.assertEquals(0, entitlementData.getQuantity());
		Assert.assertNull(entitlementData.getConditionPath());
		Assert.assertNull(entitlementData.getConditionString());
		Assert.assertNull(entitlementData.getConditionGeo());
		Assert.assertEquals(EntitlementTimeUnit.DAY, entitlementData.getTimeUnit());
		Assert.assertEquals(TIME_UNIT_1, entitlementData.getTimeUnitStart());
		Assert.assertNull(entitlementData.getTimeUnitDuration());
	}

	@Test
	public void test_Entitlement_CreateAndVerifyXML_TimeframeCondition_Duration_ZeroValue_PASS() throws ImpExException
	{
		createNonMeteredEntitlement(PRODUCT_CODE, NME1_CODE,"", "", "", TIME_UNIT_DAY, "1", "0");

		final ProductData productData = productFacade.getProductForCodeAndOptions(PRODUCT_CODE,
				Arrays.asList(ProductOption.BASIC, ProductOption.PRICE, ProductOption.ENTITLEMENTS));
		Assert.assertNotNull(productData);
		Assert.assertNotNull(productData.getEntitlements());
		Assert.assertFalse(productData.getEntitlements().isEmpty());
		Assert.assertEquals(1, productData.getEntitlements().size());

		final EntitlementData entitlementData = productData.getEntitlements().get(0);
		Assert.assertEquals(NME1_NAME, entitlementData.getName());
		Assert.assertNotNull(entitlementData.getId());
		Assert.assertEquals(0, entitlementData.getQuantity());
		Assert.assertNull(entitlementData.getConditionPath());
		Assert.assertNull(entitlementData.getConditionString());
		Assert.assertNull(entitlementData.getConditionGeo());
		Assert.assertEquals(EntitlementTimeUnit.DAY, entitlementData.getTimeUnit());
		Assert.assertEquals(TIME_UNIT_1, entitlementData.getTimeUnitStart());
		Assert.assertEquals(TIME_UNIT_0, entitlementData.getTimeUnitDuration());
	}

	@Test
	public void test_Entitlement_CreateAndVerifyXML_TimeframeCondition_Duration_UnlimitedValue_PASS() throws ImpExException
	{
		createNonMeteredEntitlement(PRODUCT_CODE, NME1_CODE,"", "", "", TIME_UNIT_DAY, "1", "0");

		final ProductData productData = productFacade.getProductForCodeAndOptions(PRODUCT_CODE,
				Arrays.asList(ProductOption.BASIC, ProductOption.PRICE, ProductOption.ENTITLEMENTS));
		Assert.assertNotNull(productData);
		Assert.assertNotNull(productData.getEntitlements());
		Assert.assertFalse(productData.getEntitlements().isEmpty());
		Assert.assertEquals(1, productData.getEntitlements().size());

		final EntitlementData entitlementData = productData.getEntitlements().get(0);
		Assert.assertEquals(NME1_NAME, entitlementData.getName());
		Assert.assertNotNull(entitlementData.getId());
		Assert.assertEquals(0, entitlementData.getQuantity());
		Assert.assertNull(entitlementData.getConditionPath());
		Assert.assertNull(entitlementData.getConditionString());
		Assert.assertNull(entitlementData.getConditionGeo());
		Assert.assertEquals(EntitlementTimeUnit.DAY, entitlementData.getTimeUnit());
		Assert.assertEquals(TIME_UNIT_1, entitlementData.getTimeUnitStart());
		Assert.assertEquals(TIME_UNIT_0, entitlementData.getTimeUnitDuration());
	}

	@Test
	public void test_Entitlement_CreateAndVerifyXML_TimeframeCondition_Start_NonNumericValue_FAIL() throws ImpExException
	{
		thrown.expect(AssertionError.class);
		thrown.expectMessage("Import has 1+unresolved lines, first lines are:");

		createNonMeteredEntitlement(PRODUCT_CODE, NME1_CODE,"", "", "", TIME_UNIT_DAY, "a", "1");
	}

	@Test
	public void test_Entitlement_CreateAndVerifyXML_TimeframeCondition_Duration_NonNumericValue_FAIL() throws ImpExException
	{
		thrown.expect(AssertionError.class);
		thrown.expectMessage("Import has 1+unresolved lines, first lines are:");

		createNonMeteredEntitlement(PRODUCT_CODE, NME1_CODE,"", "", "", TIME_UNIT_DAY, "1", "a");
	}

	//no condition
	@Test
	public void test_Entitlement_CreateAndVerifyXML_WithoutCondition_x1() throws ImpExException
	{
		createNonMeteredEntitlement(PRODUCT_CODE, NME2_CODE,"", "", "", "", "", "");

		final ProductData productData = productFacade.getProductForCodeAndOptions(PRODUCT_CODE,
				Arrays.asList(ProductOption.BASIC, ProductOption.PRICE, ProductOption.ENTITLEMENTS));
		Assert.assertNotNull(productData);
		Assert.assertNotNull(productData.getEntitlements());
		Assert.assertFalse(productData.getEntitlements().isEmpty());
		Assert.assertEquals(1, productData.getEntitlements().size());

		final EntitlementData entitlementData = productData.getEntitlements().get(0);
		Assert.assertEquals(NME2_NAME, entitlementData.getName());
		Assert.assertNotNull(entitlementData.getId());
		Assert.assertEquals(0, entitlementData.getQuantity());
		Assert.assertNull(entitlementData.getConditionPath());
		Assert.assertNull(entitlementData.getConditionString());
		Assert.assertNull(entitlementData.getConditionGeo());
		Assert.assertNull(entitlementData.getTimeUnit());
		Assert.assertNull(entitlementData.getTimeUnitStart());
		Assert.assertNull(entitlementData.getTimeUnitDuration());
	}

	@Test
	public void test_Entitlement_CreateAndVerifyXML_WithoutCondition_x2() throws ImpExException
	{
		createNonMeteredEntitlement(PRODUCT_CODE, NME1_CODE,"", "", "", "", "", "");
		createNonMeteredEntitlement(PRODUCT_CODE, NME2_CODE,"", "", "", "", "", "");

		final ProductData productData = productFacade.getProductForCodeAndOptions(PRODUCT_CODE,
				Arrays.asList(ProductOption.BASIC, ProductOption.PRICE, ProductOption.ENTITLEMENTS));
		Assert.assertNotNull(productData);
		Assert.assertNotNull(productData.getEntitlements());
		Assert.assertFalse(productData.getEntitlements().isEmpty());
		Assert.assertEquals(2, productData.getEntitlements().size());

		final EntitlementData entitlementData = productData.getEntitlements().get(0);
		Assert.assertEquals(NME1_NAME, entitlementData.getName());
		Assert.assertNotNull(entitlementData.getId());
		Assert.assertEquals(0, entitlementData.getQuantity());
		Assert.assertNull(entitlementData.getConditionPath());
		Assert.assertNull(entitlementData.getConditionString());
		Assert.assertNull(entitlementData.getConditionGeo());
		Assert.assertNull(entitlementData.getTimeUnit());
		Assert.assertNull(entitlementData.getTimeUnitStart());
		Assert.assertNull(entitlementData.getTimeUnitDuration());

		final EntitlementData entitlementData2 = productData.getEntitlements().get(1);
		Assert.assertEquals(NME2_NAME, entitlementData2.getName());
		Assert.assertNotNull(entitlementData2.getId());
		Assert.assertEquals(0, entitlementData2.getQuantity());
		Assert.assertNull(entitlementData2.getConditionPath());
		Assert.assertNull(entitlementData2.getConditionString());
		Assert.assertNull(entitlementData2.getConditionGeo());
		Assert.assertNull(entitlementData2.getTimeUnit());
		Assert.assertNull(entitlementData2.getTimeUnitStart());
		Assert.assertNull(entitlementData2.getTimeUnitDuration());
	}

	//mixed
	@Test
	public void test_Entitlement_CreateAndVerifyXML_MixedConditions_Simple_String() throws ImpExException
	{
		//###
		//# NONMETERED
		//# NONMETERED + STRING
		//###
		createNonMeteredEntitlement(PRODUCT_CODE, NME1_CODE,"", "", "", "", "", "");
		createNonMeteredEntitlement(PRODUCT_CODE, NME2_CODE,"", STRING_CONDITION, "", "", "", "");

		final ProductData productData = productFacade.getProductForCodeAndOptions(PRODUCT_CODE,
				Arrays.asList(ProductOption.BASIC, ProductOption.PRICE, ProductOption.ENTITLEMENTS));
		Assert.assertNotNull(productData);
		Assert.assertNotNull(productData.getEntitlements());
		Assert.assertFalse(productData.getEntitlements().isEmpty());
		Assert.assertEquals(2, productData.getEntitlements().size());

		final EntitlementData entitlementData = productData.getEntitlements().get(0);
		Assert.assertEquals(NME1_NAME, entitlementData.getName());
		Assert.assertNotNull(entitlementData.getId());
		Assert.assertEquals(0, entitlementData.getQuantity());
		Assert.assertNull(entitlementData.getConditionPath());
		Assert.assertNull(entitlementData.getConditionString());
		Assert.assertNull(entitlementData.getConditionGeo());
		Assert.assertNull(entitlementData.getTimeUnit());
		Assert.assertNull(entitlementData.getTimeUnitStart());
		Assert.assertNull(entitlementData.getTimeUnitDuration());

		final EntitlementData entitlementData2 = productData.getEntitlements().get(1);
		Assert.assertEquals(NME2_NAME, entitlementData2.getName());
		Assert.assertNotNull(entitlementData2.getId());
		Assert.assertEquals(0, entitlementData2.getQuantity());
		Assert.assertNull(entitlementData2.getConditionPath());
		Assert.assertEquals(STRING_CONDITION, entitlementData2.getConditionString());
		Assert.assertNull(entitlementData2.getConditionGeo());
		Assert.assertNull(entitlementData2.getTimeUnit());
		Assert.assertNull(entitlementData2.getTimeUnitStart());
		Assert.assertNull(entitlementData2.getTimeUnitDuration());
	}

	@Test
	public void test_Entitlement_CreateAndVerifyXML_MixedConditions_Simple_Path() throws ImpExException
	{
		//###
		//# NONMETERED
		//# NONMETERED + PATH
		//###
		createNonMeteredEntitlement(PRODUCT_CODE, NME1_CODE,"", "", "", "", "", "");
		createNonMeteredEntitlement(PRODUCT_CODE, NME2_CODE,PATH_CONDITION, "", "", "", "", "");

		final ProductData productData = productFacade.getProductForCodeAndOptions(PRODUCT_CODE,
				Arrays.asList(ProductOption.BASIC, ProductOption.PRICE, ProductOption.ENTITLEMENTS));
		Assert.assertNotNull(productData);
		Assert.assertNotNull(productData.getEntitlements());
		Assert.assertFalse(productData.getEntitlements().isEmpty());
		Assert.assertEquals(2, productData.getEntitlements().size());

		final EntitlementData entitlementData = productData.getEntitlements().get(0);
		Assert.assertEquals(NME1_NAME, entitlementData.getName());
		Assert.assertNotNull(entitlementData.getId());
		Assert.assertEquals(0, entitlementData.getQuantity());
		Assert.assertNull(entitlementData.getConditionPath());
		Assert.assertNull(entitlementData.getConditionString());
		Assert.assertNull(entitlementData.getConditionGeo());
		Assert.assertNull(entitlementData.getTimeUnit());
		Assert.assertNull(entitlementData.getTimeUnitStart());
		Assert.assertNull(entitlementData.getTimeUnitDuration());

		final EntitlementData entitlementData2 = productData.getEntitlements().get(1);
		Assert.assertEquals(NME2_NAME, entitlementData2.getName());
		Assert.assertNotNull(entitlementData2.getId());
		Assert.assertEquals(0, entitlementData2.getQuantity());
		Assert.assertEquals(PATH_CONDITION, entitlementData2.getConditionPath());
		Assert.assertNull(entitlementData2.getConditionString());
		Assert.assertNull(entitlementData2.getConditionGeo());
		Assert.assertNull(entitlementData2.getTimeUnit());
		Assert.assertNull(entitlementData2.getTimeUnitStart());
		Assert.assertNull(entitlementData2.getTimeUnitDuration());
	}

	@Test
	public void test_Entitlement_CreateAndVerifyXML_MixedConditions_Simple_Metered() throws ImpExException
	{
		//###
		//# NONMETERED
		//# METERED
		//###
		createNonMeteredEntitlement(PRODUCT_CODE, NME1_CODE,"", "", "", "", "", "");
		createEntitlement(PRODUCT_CODE, ME1_CODE,"9", "", "", "", "", "", "");

		final ProductData productData = productFacade.getProductForCodeAndOptions(PRODUCT_CODE,
				Arrays.asList(ProductOption.BASIC, ProductOption.PRICE, ProductOption.ENTITLEMENTS));
		Assert.assertNotNull(productData);
		Assert.assertNotNull(productData.getEntitlements());
		Assert.assertFalse(productData.getEntitlements().isEmpty());
		Assert.assertEquals(2, productData.getEntitlements().size());

		final EntitlementData entitlementData = productData.getEntitlements().get(0);
		Assert.assertEquals(NME1_NAME, entitlementData.getName());
		Assert.assertNotNull(entitlementData.getId());
		Assert.assertEquals(0, entitlementData.getQuantity());
		Assert.assertNull(entitlementData.getConditionPath());
		Assert.assertNull(entitlementData.getConditionString());
		Assert.assertNull(entitlementData.getConditionGeo());
		Assert.assertNull(entitlementData.getTimeUnit());
		Assert.assertNull(entitlementData.getTimeUnitStart());
		Assert.assertNull(entitlementData.getTimeUnitDuration());

		final EntitlementData entitlementData2 = productData.getEntitlements().get(1);
		Assert.assertEquals(ME1_NAME, entitlementData2.getName());
		Assert.assertNotNull(entitlementData2.getId());
		Assert.assertEquals(9, entitlementData2.getQuantity());
		Assert.assertNull(entitlementData2.getConditionPath());
		Assert.assertNull(entitlementData2.getConditionString());
		Assert.assertNull(entitlementData2.getConditionGeo());
		Assert.assertNull(entitlementData2.getTimeUnit());
		Assert.assertNull(entitlementData2.getTimeUnitStart());
		Assert.assertNull(entitlementData2.getTimeUnitDuration());
	}

	@Test
	public void test_Entitlement_CreateAndVerifyXML_MixedConditions_StringAndPath() throws ImpExException
	{
		//###
		//# NONMETERED + STRING + PATH
		//###
		createNonMeteredEntitlement(PRODUCT_CODE, NME1_CODE,PATH_CONDITION, STRING_CONDITION, "", "", "", "");

		final ProductData productData = productFacade.getProductForCodeAndOptions(PRODUCT_CODE,
				Arrays.asList(ProductOption.BASIC, ProductOption.PRICE, ProductOption.ENTITLEMENTS));
		Assert.assertNotNull(productData);
		Assert.assertNotNull(productData.getEntitlements());
		Assert.assertFalse(productData.getEntitlements().isEmpty());
		Assert.assertEquals(1, productData.getEntitlements().size());

		final EntitlementData entitlementData = productData.getEntitlements().get(0);
		Assert.assertEquals(NME1_NAME, entitlementData.getName());
		Assert.assertNotNull(entitlementData.getId());
		Assert.assertEquals(0, entitlementData.getQuantity());
		Assert.assertEquals(PATH_CONDITION, entitlementData.getConditionPath());
		Assert.assertEquals(STRING_CONDITION, entitlementData.getConditionString());
		Assert.assertNull(entitlementData.getConditionGeo());
		Assert.assertNull(entitlementData.getTimeUnit());
		Assert.assertNull(entitlementData.getTimeUnitStart());
		Assert.assertNull(entitlementData.getTimeUnitDuration());
	}

	@Test
	public void test_Entitlement_CreateAndVerifyXML_MixedConditions_String_Path() throws ImpExException
	{
		//###
		//# NONMETERED + STRING
		//# NONMETERED + PATH
		//###
		createNonMeteredEntitlement(PRODUCT_CODE, NME1_CODE,"", STRING_CONDITION, "", "", "", "");
		createNonMeteredEntitlement(PRODUCT_CODE, NME2_CODE,PATH_CONDITION, "", "", "", "", "");

		final ProductData productData = productFacade.getProductForCodeAndOptions(PRODUCT_CODE,
				Arrays.asList(ProductOption.BASIC, ProductOption.PRICE, ProductOption.ENTITLEMENTS));
		Assert.assertNotNull(productData);
		Assert.assertNotNull(productData.getEntitlements());
		Assert.assertFalse(productData.getEntitlements().isEmpty());
		Assert.assertEquals(2, productData.getEntitlements().size());

		final EntitlementData entitlementData = productData.getEntitlements().get(0);
		Assert.assertEquals(NME1_NAME, entitlementData.getName());
		Assert.assertNotNull(entitlementData.getId());
		Assert.assertEquals(0, entitlementData.getQuantity());
		Assert.assertNull(entitlementData.getConditionPath());
		Assert.assertEquals(STRING_CONDITION, entitlementData.getConditionString());
		Assert.assertNull(entitlementData.getConditionGeo());
		Assert.assertNull(entitlementData.getTimeUnit());
		Assert.assertNull(entitlementData.getTimeUnitStart());
		Assert.assertNull(entitlementData.getTimeUnitDuration());

		final EntitlementData entitlementData2 = productData.getEntitlements().get(1);
		Assert.assertEquals(NME2_NAME, entitlementData2.getName());
		Assert.assertNotNull(entitlementData2.getId());
		Assert.assertEquals(0, entitlementData2.getQuantity());
		Assert.assertEquals(PATH_CONDITION, entitlementData2.getConditionPath());
		Assert.assertNull(entitlementData2.getConditionString());
		Assert.assertNull(entitlementData2.getConditionGeo());
		Assert.assertNull(entitlementData2.getTimeUnit());
		Assert.assertNull(entitlementData2.getTimeUnitStart());
		Assert.assertNull(entitlementData2.getTimeUnitDuration());
	}

	@Test
	public void test_Entitlement_CreateAndVerifyXML_MixedConditions_StringAndMetered() throws ImpExException
	{
		//###
		//# METERED + STRING
		//###
		createEntitlement(PRODUCT_CODE, ME1_CODE, "9","", STRING_CONDITION, "", "", "", "");

		final ProductData productData = productFacade.getProductForCodeAndOptions(PRODUCT_CODE,
				Arrays.asList(ProductOption.BASIC, ProductOption.PRICE, ProductOption.ENTITLEMENTS));
		Assert.assertNotNull(productData);
		Assert.assertNotNull(productData.getEntitlements());
		Assert.assertFalse(productData.getEntitlements().isEmpty());
		Assert.assertEquals(1, productData.getEntitlements().size());

		final EntitlementData entitlementData = productData.getEntitlements().get(0);
		Assert.assertEquals(ME1_NAME, entitlementData.getName());
		Assert.assertNotNull(entitlementData.getId());
		Assert.assertEquals(9, entitlementData.getQuantity());
		Assert.assertNull(entitlementData.getConditionPath());
		Assert.assertEquals(STRING_CONDITION, entitlementData.getConditionString());
		Assert.assertNull(entitlementData.getConditionGeo());
		Assert.assertNull(entitlementData.getTimeUnit());
		Assert.assertNull(entitlementData.getTimeUnitStart());
		Assert.assertNull(entitlementData.getTimeUnitDuration());
	}

	@Test
	public void test_Entitlement_CreateAndVerifyXML_MixedConditions_PathAndMetered_x1() throws ImpExException
	{
		//###
		//# METERED + PATH
		//###
		createEntitlement(PRODUCT_CODE, ME1_CODE, "10",PATH_CONDITION, "", "", "", "", "");

		final ProductData productData = productFacade.getProductForCodeAndOptions(PRODUCT_CODE,
				Arrays.asList(ProductOption.BASIC, ProductOption.PRICE, ProductOption.ENTITLEMENTS));
		Assert.assertNotNull(productData);
		Assert.assertNotNull(productData.getEntitlements());
		Assert.assertFalse(productData.getEntitlements().isEmpty());
		Assert.assertEquals(1, productData.getEntitlements().size());

		final EntitlementData entitlementData = productData.getEntitlements().get(0);
		Assert.assertEquals(ME1_NAME, entitlementData.getName());
		Assert.assertNotNull(entitlementData.getId());
		Assert.assertEquals(10, entitlementData.getQuantity());
		Assert.assertEquals(PATH_CONDITION, entitlementData.getConditionPath());
		Assert.assertNull(entitlementData.getConditionString());
		Assert.assertNull(entitlementData.getConditionGeo());
		Assert.assertNull(entitlementData.getTimeUnit());
		Assert.assertNull(entitlementData.getTimeUnitStart());
		Assert.assertNull(entitlementData.getTimeUnitDuration());
	}

	@Test
	public void test_Entitlement_CreateAndVerifyXML_MixedConditions_TimeframeAndMetered_x1() throws ImpExException
	{
		//###
		//# METERED + TIMEFRAME
		//###
		createEntitlement(PRODUCT_CODE, ME1_CODE, "99","", "", "", TIME_UNIT_DAY, "1", "365");

		final ProductData productData = productFacade.getProductForCodeAndOptions(PRODUCT_CODE,
				Arrays.asList(ProductOption.BASIC, ProductOption.PRICE, ProductOption.ENTITLEMENTS));
		Assert.assertNotNull(productData);
		Assert.assertNotNull(productData.getEntitlements());
		Assert.assertFalse(productData.getEntitlements().isEmpty());
		Assert.assertEquals(1, productData.getEntitlements().size());

		final EntitlementData entitlementData = productData.getEntitlements().get(0);
		Assert.assertEquals(ME1_NAME, entitlementData.getName());
		Assert.assertNotNull(entitlementData.getId());
		Assert.assertEquals(99, entitlementData.getQuantity());
		Assert.assertNull(entitlementData.getConditionPath());
		Assert.assertNull(entitlementData.getConditionString());
		Assert.assertNull(entitlementData.getConditionGeo());
		Assert.assertEquals(EntitlementTimeUnit.DAY, entitlementData.getTimeUnit());
		Assert.assertEquals(TIME_UNIT_1, entitlementData.getTimeUnitStart());
		Assert.assertEquals(TIME_UNIT_365, entitlementData.getTimeUnitDuration());
	}

	@Test
	public void test_Entitlement_CreateAndVerifyXML_MixedConditions_GeoAndMetered_x1() throws ImpExException
	{
		//###
		//# METERED + GEO
		//###
		createEntitlement(PRODUCT_CODE, ME1_CODE, "2","", "", GEO_CONDITION, "", "", "");

		final ProductData productData = productFacade.getProductForCodeAndOptions(PRODUCT_CODE,
				Arrays.asList(ProductOption.BASIC, ProductOption.PRICE, ProductOption.ENTITLEMENTS));
		Assert.assertNotNull(productData);
		Assert.assertNotNull(productData.getEntitlements());
		Assert.assertFalse(productData.getEntitlements().isEmpty());
		Assert.assertEquals(1, productData.getEntitlements().size());

		final EntitlementData entitlementData = productData.getEntitlements().get(0);
		Assert.assertEquals(ME1_NAME, entitlementData.getName());
		Assert.assertNotNull(entitlementData.getId());
		Assert.assertEquals(2, entitlementData.getQuantity());
		Assert.assertFalse(entitlementData.getConditionGeo().isEmpty());
		Assert.assertEquals(1, entitlementData.getConditionGeo().size());
		Assert.assertNull(entitlementData.getConditionPath());
		Assert.assertNull(entitlementData.getConditionString());
		Assert.assertTrue(entitlementData.getConditionGeo().contains(GEO_CONDITION));
		Assert.assertNull(entitlementData.getTimeUnit());
		Assert.assertNull(entitlementData.getTimeUnitStart());
		Assert.assertNull(entitlementData.getTimeUnitDuration());
	}


	@Test
	public void test_Entitlement_CreateAndVerifyXML_MixedConditions_NonMetered_Full() throws ImpExException
	{
		//##
		//# NONMETERED + STRING + PATH + GEO + TIMEFRAME
		//###
		createNonMeteredEntitlement(PRODUCT_CODE, NME2_CODE,PATH_CONDITION, STRING_CONDITION, GEO_CONDITION, TIME_UNIT_DAY, "30", "0");

		final ProductData productData = productFacade.getProductForCodeAndOptions(PRODUCT_CODE,
				Arrays.asList(ProductOption.BASIC, ProductOption.PRICE, ProductOption.ENTITLEMENTS));
		Assert.assertNotNull(productData);
		Assert.assertNotNull(productData.getEntitlements());
		Assert.assertFalse(productData.getEntitlements().isEmpty());
		Assert.assertEquals(1, productData.getEntitlements().size());

		final EntitlementData entitlementData = productData.getEntitlements().get(0);
		Assert.assertEquals(NME2_NAME, entitlementData.getName());
		Assert.assertNotNull(entitlementData.getId());
		Assert.assertEquals(0, entitlementData.getQuantity());
		Assert.assertEquals(PATH_CONDITION, entitlementData.getConditionPath());
		Assert.assertEquals(STRING_CONDITION, entitlementData.getConditionString());
		Assert.assertFalse(entitlementData.getConditionGeo().isEmpty());
		Assert.assertEquals(1, entitlementData.getConditionGeo().size());
		Assert.assertTrue(entitlementData.getConditionGeo().contains(GEO_CONDITION));
		Assert.assertEquals(EntitlementTimeUnit.DAY, entitlementData.getTimeUnit());
		Assert.assertEquals(TIME_UNIT_30, entitlementData.getTimeUnitStart());
		Assert.assertEquals(TIME_UNIT_0, entitlementData.getTimeUnitDuration());
	}

	@Test
	public void test_Entitlement_CreateAndVerifyXML_MixedConditions_Metred_Full() throws ImpExException
	{
		//##
		//# METERED + STRING + PATH + GEO + TIMEFRAME
		//###
		createEntitlement(PRODUCT_CODE, ME1_CODE, "199",PATH_CONDITION, STRING_CONDITION, GEO_CONDITION, TIME_UNIT_MONTH, "12", "1");

		final ProductData productData = productFacade.getProductForCodeAndOptions(PRODUCT_CODE,
				Arrays.asList(ProductOption.BASIC, ProductOption.PRICE, ProductOption.ENTITLEMENTS));
		Assert.assertNotNull(productData);
		Assert.assertNotNull(productData.getEntitlements());
		Assert.assertFalse(productData.getEntitlements().isEmpty());
		Assert.assertEquals(1, productData.getEntitlements().size());

		final EntitlementData entitlementData = productData.getEntitlements().get(0);
		Assert.assertEquals(ME1_NAME, entitlementData.getName());
		Assert.assertNotNull(entitlementData.getId());
		Assert.assertEquals(199, entitlementData.getQuantity());
		Assert.assertEquals(PATH_CONDITION, entitlementData.getConditionPath());
		Assert.assertEquals(STRING_CONDITION, entitlementData.getConditionString());
		Assert.assertFalse(entitlementData.getConditionGeo().isEmpty());
		Assert.assertEquals(1, entitlementData.getConditionGeo().size());
		Assert.assertTrue(entitlementData.getConditionGeo().contains(GEO_CONDITION));
		Assert.assertEquals(EntitlementTimeUnit.MONTH, entitlementData.getTimeUnit());
		Assert.assertEquals(TIME_UNIT_12, entitlementData.getTimeUnitStart());
		Assert.assertEquals(TIME_UNIT_1, entitlementData.getTimeUnitDuration());
	}

	@Test
	public void test_Entitlement_CompareXML_MixedConditions_DifferentTypes() throws ImpExException
	{
		//##
		//# METERED + STRING + PATH + GEO + TIMEFRAME
		//# METERED + STRING + PATH + GEO + TIMEFRAME
		//# NONMETERED + STRING + PATH + GEO + TIMEFRAME
		//# NONMETERED + STRING + PATH + GEO + TIMEFRAME
		//###
		final String pathCondition3 = "pathCondition3";
		final String stringCondition3 = "stringCondition3";
		final String geoCondition3 = "geoCondition3";
		final String pathCondition4 = "pathCondition4";
		final String stringCondition4 = "stringCondition4";
		final String geoCondition4 = "geoCondition4";

		createEntitlement(PRODUCT_CODE, ME1_CODE, "1",PATH_CONDITION, STRING_CONDITION, GEO_CONDITION, TIME_UNIT_DAY, "30", "365");
		createEntitlement(PRODUCT_CODE, ME1_CODE, "2", PATH_CONDITION_2, STRING_CONDITION_2, GEO_CONDITION_2, TIME_UNIT_MONTH, "12", "1");
		createNonMeteredEntitlement(PRODUCT_CODE, NME1_CODE,pathCondition3, stringCondition3, geoCondition3, TIME_UNIT_DAY, "1", "30");
		createNonMeteredEntitlement(PRODUCT_CODE, NME2_CODE,pathCondition4, stringCondition4, geoCondition4, TIME_UNIT_DAY, "30", "30");

		final ProductData productData = productFacade.getProductForCodeAndOptions(PRODUCT_CODE,
				Arrays.asList(ProductOption.BASIC, ProductOption.PRICE, ProductOption.ENTITLEMENTS));
		Assert.assertNotNull(productData);
		Assert.assertNotNull(productData.getEntitlements());
		Assert.assertFalse(productData.getEntitlements().isEmpty());
		Assert.assertEquals(4, productData.getEntitlements().size());

		final EntitlementData entitlementData = productData.getEntitlements().get(0);
		Assert.assertEquals(ME1_NAME, entitlementData.getName());
		Assert.assertNotNull(entitlementData.getId());
		Assert.assertEquals(1, entitlementData.getQuantity());
		Assert.assertEquals(PATH_CONDITION, entitlementData.getConditionPath());
		Assert.assertEquals(STRING_CONDITION, entitlementData.getConditionString());
		Assert.assertFalse(entitlementData.getConditionGeo().isEmpty());
		Assert.assertEquals(1, entitlementData.getConditionGeo().size());
		Assert.assertTrue(entitlementData.getConditionGeo().contains(GEO_CONDITION));
		Assert.assertEquals(EntitlementTimeUnit.DAY, entitlementData.getTimeUnit());
		Assert.assertEquals(TIME_UNIT_30, entitlementData.getTimeUnitStart());
		Assert.assertEquals(TIME_UNIT_365, entitlementData.getTimeUnitDuration());

		final EntitlementData entitlementData2 = productData.getEntitlements().get(1);
		Assert.assertEquals(ME1_NAME, entitlementData2.getName());
		Assert.assertNotNull(entitlementData2.getId());
		Assert.assertEquals(2, entitlementData2.getQuantity());
		Assert.assertEquals(PATH_CONDITION_2, entitlementData2.getConditionPath());
		Assert.assertEquals(STRING_CONDITION_2, entitlementData2.getConditionString());
		Assert.assertFalse(entitlementData2.getConditionGeo().isEmpty());
		Assert.assertEquals(1, entitlementData2.getConditionGeo().size());
		Assert.assertTrue(entitlementData2.getConditionGeo().contains(GEO_CONDITION_2));
		Assert.assertEquals(EntitlementTimeUnit.MONTH, entitlementData2.getTimeUnit());
		Assert.assertEquals(TIME_UNIT_12, entitlementData2.getTimeUnitStart());
		Assert.assertEquals(TIME_UNIT_1, entitlementData2.getTimeUnitDuration());

		final EntitlementData entitlementData3 = productData.getEntitlements().get(2);
		Assert.assertEquals(NME1_NAME, entitlementData3.getName());
		Assert.assertNotNull(entitlementData3.getId());
		Assert.assertEquals(0, entitlementData3.getQuantity());
		Assert.assertEquals(pathCondition3, entitlementData3.getConditionPath());
		Assert.assertEquals(stringCondition3, entitlementData3.getConditionString());
		Assert.assertFalse(entitlementData3.getConditionGeo().isEmpty());
		Assert.assertEquals(1, entitlementData3.getConditionGeo().size());
		Assert.assertTrue(entitlementData3.getConditionGeo().contains(geoCondition3));
		Assert.assertEquals(EntitlementTimeUnit.DAY, entitlementData3.getTimeUnit());
		Assert.assertEquals(TIME_UNIT_1, entitlementData3.getTimeUnitStart());
		Assert.assertEquals(TIME_UNIT_30, entitlementData3.getTimeUnitDuration());

		final EntitlementData entitlementData4 = productData.getEntitlements().get(3);
		Assert.assertEquals(NME2_NAME, entitlementData4.getName());
		Assert.assertNotNull(entitlementData4.getId());
		Assert.assertEquals(0, entitlementData4.getQuantity());
		Assert.assertEquals(pathCondition4, entitlementData4.getConditionPath());
		Assert.assertEquals(stringCondition4, entitlementData4.getConditionString());
		Assert.assertFalse(entitlementData4.getConditionGeo().isEmpty());
		Assert.assertEquals(1, entitlementData4.getConditionGeo().size());
		Assert.assertTrue(entitlementData4.getConditionGeo().contains(geoCondition4));
		Assert.assertEquals(EntitlementTimeUnit.DAY, entitlementData4.getTimeUnit());
		Assert.assertEquals(TIME_UNIT_30, entitlementData4.getTimeUnitStart());
		Assert.assertEquals(TIME_UNIT_30, entitlementData4.getTimeUnitDuration());
	}

	@Test
	public void test_Entitlement_CompareXML_MixedConditions_DifferentProducts() throws ImpExException
	{
		//##
		//# METERED + STRING + PATH + GEO + TIMEFRAME
		//# METERED + STRING + PATH + GEO + TIMEFRAME
		//# NONMETERED + STRING + PATH + GEO + TIMEFRAME
		//# NONMETERED + STRING + PATH + GEO + TIMEFRAME
		//# (for every product)
		//###
		final String pathCondition3 = "pathCondition3";
		final String stringCondition3 = "stringCondition3";
		final String geoCondition3 = "geoCondition3";
		final String pathCondition4 = "pathCondition4";
		final String stringCondition4 = "stringCondition4";
		final String geoCondition4 = "geoCondition4";
		final String pathCondition1forP2 = "pathCondition1forP2";
		final String stringCondition1forP2 = "stringCondition1forP2";
		final String geoCondition1forP2 = "geoCondition1forP2";
		final String pathCondition2forP2 = "pathCondition2forP2";
		final String stringCondition2forP2 = "stringCondition2forP2";
		final String geoCondition2forP2 = "geoCondition2forP2";

		createEntitlement(PRODUCT_CODE, ME1_CODE, "1", PATH_CONDITION, STRING_CONDITION, GEO_CONDITION, TIME_UNIT_DAY, "30", "30");
		createEntitlement(PRODUCT_CODE_2, ME1_CODE, "1", PATH_CONDITION, STRING_CONDITION, GEO_CONDITION, TIME_UNIT_DAY, "30", "30");
		createEntitlement(PRODUCT_CODE, ME2_CODE, "2", PATH_CONDITION_2, STRING_CONDITION_2, GEO_CONDITION_2, TIME_UNIT_MONTH, "12", "1");
		createEntitlement(PRODUCT_CODE_2, METU1_CODE, "2", pathCondition1forP2, stringCondition1forP2, geoCondition1forP2, TIME_UNIT_MONTH, "1", "30");
		createNonMeteredEntitlement(PRODUCT_CODE, NME1_CODE, pathCondition3, stringCondition3, geoCondition3, TIME_UNIT_DAY, "1", "30");
		createNonMeteredEntitlement(PRODUCT_CODE_2, NME1_CODE, pathCondition3, stringCondition3, geoCondition3, TIME_UNIT_DAY, "1", "30");
		createNonMeteredEntitlement(PRODUCT_CODE, NME2_CODE, pathCondition4, stringCondition4, geoCondition4, TIME_UNIT_DAY, "30", "365");
		createNonMeteredEntitlement(PRODUCT_CODE_2, NMETU1_CODE, pathCondition2forP2, stringCondition2forP2, geoCondition2forP2, TIME_UNIT_DAY, "365", "365");

		final ProductData productData = productFacade.getProductForCodeAndOptions(PRODUCT_CODE,
				Arrays.asList(ProductOption.BASIC, ProductOption.PRICE, ProductOption.ENTITLEMENTS));
		Assert.assertNotNull(productData);
		Assert.assertNotNull(productData.getEntitlements());
		Assert.assertFalse(productData.getEntitlements().isEmpty());
		Assert.assertEquals(4, productData.getEntitlements().size());

		final EntitlementData entitlementData = productData.getEntitlements().get(0);
		Assert.assertEquals(ME1_NAME, entitlementData.getName());
		Assert.assertNotNull(entitlementData.getId());
		Assert.assertEquals(1, entitlementData.getQuantity());
		Assert.assertEquals(PATH_CONDITION, entitlementData.getConditionPath());
		Assert.assertEquals(STRING_CONDITION, entitlementData.getConditionString());
		Assert.assertFalse(entitlementData.getConditionGeo().isEmpty());
		Assert.assertEquals(1, entitlementData.getConditionGeo().size());
		Assert.assertTrue(entitlementData.getConditionGeo().contains(GEO_CONDITION));
		Assert.assertEquals(EntitlementTimeUnit.DAY, entitlementData.getTimeUnit());
		Assert.assertEquals(TIME_UNIT_30, entitlementData.getTimeUnitStart());
		Assert.assertEquals(TIME_UNIT_30, entitlementData.getTimeUnitDuration());

		final EntitlementData entitlementData2 = productData.getEntitlements().get(1);
		Assert.assertEquals(ME2_NAME, entitlementData2.getName());
		Assert.assertNotNull(entitlementData2.getId());
		Assert.assertEquals(2, entitlementData2.getQuantity());
		Assert.assertEquals(PATH_CONDITION_2, entitlementData2.getConditionPath());
		Assert.assertEquals(STRING_CONDITION_2, entitlementData2.getConditionString());
		Assert.assertFalse(entitlementData2.getConditionGeo().isEmpty());
		Assert.assertEquals(1, entitlementData2.getConditionGeo().size());
		Assert.assertTrue(entitlementData2.getConditionGeo().contains(GEO_CONDITION_2));
		Assert.assertEquals(EntitlementTimeUnit.MONTH, entitlementData2.getTimeUnit());
		Assert.assertEquals(TIME_UNIT_12, entitlementData2.getTimeUnitStart());
		Assert.assertEquals(TIME_UNIT_1, entitlementData2.getTimeUnitDuration());

		final EntitlementData entitlementData3 = productData.getEntitlements().get(2);
		Assert.assertEquals(NME1_NAME, entitlementData3.getName());
		Assert.assertNotNull(entitlementData3.getId());
		Assert.assertEquals(0, entitlementData3.getQuantity());
		Assert.assertEquals(pathCondition3, entitlementData3.getConditionPath());
		Assert.assertEquals(stringCondition3, entitlementData3.getConditionString());
		Assert.assertFalse(entitlementData3.getConditionGeo().isEmpty());
		Assert.assertEquals(1, entitlementData3.getConditionGeo().size());
		Assert.assertTrue(entitlementData3.getConditionGeo().contains(geoCondition3));
		Assert.assertEquals(EntitlementTimeUnit.DAY, entitlementData3.getTimeUnit());
		Assert.assertEquals(TIME_UNIT_1, entitlementData3.getTimeUnitStart());
		Assert.assertEquals(TIME_UNIT_30, entitlementData3.getTimeUnitDuration());

		final EntitlementData entitlementData4 = productData.getEntitlements().get(3);
		Assert.assertEquals(NME2_NAME, entitlementData4.getName());
		Assert.assertNotNull(entitlementData4.getId());
		Assert.assertEquals(0, entitlementData4.getQuantity());
		Assert.assertEquals(pathCondition4, entitlementData4.getConditionPath());
		Assert.assertEquals(stringCondition4, entitlementData4.getConditionString());
		Assert.assertFalse(entitlementData4.getConditionGeo().isEmpty());
		Assert.assertEquals(1, entitlementData4.getConditionGeo().size());
		Assert.assertTrue(entitlementData4.getConditionGeo().contains(geoCondition4));
		Assert.assertEquals(EntitlementTimeUnit.DAY, entitlementData4.getTimeUnit());
		Assert.assertEquals(TIME_UNIT_30, entitlementData4.getTimeUnitStart());
		Assert.assertEquals(TIME_UNIT_365, entitlementData4.getTimeUnitDuration());

		final ProductData productData2 = productFacade.getProductForCodeAndOptions(PRODUCT_CODE_2,
				Arrays.asList(ProductOption.BASIC, ProductOption.PRICE, ProductOption.ENTITLEMENTS));
		Assert.assertNotNull(productData2);
		Assert.assertNotNull(productData2.getEntitlements());
		Assert.assertFalse(productData2.getEntitlements().isEmpty());
		Assert.assertEquals(4, productData2.getEntitlements().size());

		final EntitlementData entitlementData2_1 = productData2.getEntitlements().get(0);
		Assert.assertEquals(ME1_NAME, entitlementData2_1.getName());
		Assert.assertNotNull(entitlementData2_1.getId());
		Assert.assertEquals(1, entitlementData2_1.getQuantity());
		Assert.assertEquals(PATH_CONDITION, entitlementData2_1.getConditionPath());
		Assert.assertEquals(STRING_CONDITION, entitlementData2_1.getConditionString());
		Assert.assertFalse(entitlementData2_1.getConditionGeo().isEmpty());
		Assert.assertEquals(1, entitlementData2_1.getConditionGeo().size());
		Assert.assertTrue(entitlementData2_1.getConditionGeo().contains(GEO_CONDITION));
		Assert.assertEquals(EntitlementTimeUnit.DAY, entitlementData2_1.getTimeUnit());
		Assert.assertEquals(TIME_UNIT_30, entitlementData2_1.getTimeUnitStart());
		Assert.assertEquals(TIME_UNIT_30, entitlementData2_1.getTimeUnitDuration());

		final EntitlementData entitlementData2_2 = productData2.getEntitlements().get(1);
		Assert.assertEquals(METU1_NAME, entitlementData2_2.getName());
		Assert.assertNotNull(entitlementData2_2.getId());
		Assert.assertEquals(2, entitlementData2_2.getQuantity());
		Assert.assertEquals(pathCondition1forP2, entitlementData2_2.getConditionPath());
		Assert.assertEquals(stringCondition1forP2, entitlementData2_2.getConditionString());
		Assert.assertFalse(entitlementData2_2.getConditionGeo().isEmpty());
		Assert.assertEquals(1, entitlementData2_2.getConditionGeo().size());
		Assert.assertTrue(entitlementData2_2.getConditionGeo().contains(geoCondition1forP2));
		Assert.assertEquals(EntitlementTimeUnit.MONTH, entitlementData2_2.getTimeUnit());
		Assert.assertEquals(TIME_UNIT_1, entitlementData2_2.getTimeUnitStart());
		Assert.assertEquals(TIME_UNIT_30, entitlementData2_2.getTimeUnitDuration());

		final EntitlementData entitlementData2_3 = productData2.getEntitlements().get(2);
		Assert.assertEquals(NME1_NAME, entitlementData2_3.getName());
		Assert.assertNotNull(entitlementData2_3.getId());
		Assert.assertEquals(0, entitlementData2_3.getQuantity());
		Assert.assertEquals(pathCondition3, entitlementData2_3.getConditionPath());
		Assert.assertEquals(stringCondition3, entitlementData2_3.getConditionString());
		Assert.assertFalse(entitlementData2_3.getConditionGeo().isEmpty());
		Assert.assertEquals(1, entitlementData2_3.getConditionGeo().size());
		Assert.assertTrue(entitlementData2_3.getConditionGeo().contains(geoCondition3));
		Assert.assertEquals(EntitlementTimeUnit.DAY, entitlementData2_3.getTimeUnit());
		Assert.assertEquals(TIME_UNIT_1, entitlementData2_3.getTimeUnitStart());
		Assert.assertEquals(TIME_UNIT_30, entitlementData2_3.getTimeUnitDuration());

		final EntitlementData entitlementData2_4 = productData2.getEntitlements().get(3);
		Assert.assertEquals(NMETU1_NAME, entitlementData2_4.getName());
		Assert.assertNotNull(entitlementData2_4.getId());
		Assert.assertEquals(0, entitlementData2_4.getQuantity());
		Assert.assertEquals(pathCondition2forP2, entitlementData2_4.getConditionPath());
		Assert.assertEquals(stringCondition2forP2, entitlementData2_4.getConditionString());
		Assert.assertFalse(entitlementData2_4.getConditionGeo().isEmpty());
		Assert.assertEquals(1, entitlementData2_4.getConditionGeo().size());
		Assert.assertTrue(entitlementData2_4.getConditionGeo().contains(geoCondition2forP2));
		Assert.assertEquals(EntitlementTimeUnit.DAY, entitlementData2_4.getTimeUnit());
		Assert.assertEquals(TIME_UNIT_365, entitlementData2_4.getTimeUnitStart());
		Assert.assertEquals(TIME_UNIT_365, entitlementData2_4.getTimeUnitDuration());
	}

	private void createNonMeteredEntitlement(final String productCode, final String entitlementType, final String pathCondition,
			final String stringCondition, final String geoCondition, final String timeUnit, final String startTime, final String duration)
			throws ImpExException
	{
		createEntitlement(productCode, entitlementType, "0", pathCondition, stringCondition, geoCondition, timeUnit, startTime, duration);
	}


	private void createEntitlement(final String productCode, final String entitlementType, final String qty, final String pathCondition,
			final String stringCondition, final String geoCondition, final String timeUnit, final String startTime, final String duration)
			throws ImpExException
	{

		//@{binding}           create list     productCode        ${productCode}        entitlementType    ${entitlementType}
		//	...                                  quantity           ${count}              id                 ${id}
		//	...                                  conditionPath      ${pathCondition}
		//	...                                  conditionString    ${stringCondition}
		//	...                                  conditionGeo       ${geoCondition}
		//	...                                  timeUnit           ${timeUnit}           timeUnitStart      ${startTime}          timeUnitDuration    ${duration}
		String[] bingings = new String[20];
		bingings[0] = "productCode";
		bingings[1] = productCode;
		bingings[2] = "entitlementType";
		bingings[3] = entitlementType;
		bingings[4] = "quantity";
		bingings[5] = qty;
		bingings[6] = "id";
		bingings[7] = String.valueOf(new Random().nextLong());
		bingings[8] = "conditionPath";
		bingings[9] = pathCondition;
		bingings[10] = "conditionString";
		bingings[11] = stringCondition;
		bingings[12] = "conditionGeo";
		bingings[13] = geoCondition;
		bingings[14] = "timeUnit";
		bingings[15] = timeUnit;
		bingings[16] = "timeUnitStart";
		bingings[17] = startTime;
		bingings[18] = "timeUnitDuration";
		bingings[19] = duration;
		String impex = generateImpExFromTemplate("impex-templates/entitlement-insert.impex.vm", bingings);
		System.out.println(impex);
		InputStream targetStream = IOUtils.toInputStream(impex);

		importStream(targetStream, DEFAULT_ENCODING, "entitlementsImport.impex");
	}

	public String generateImpExFromTemplate(final String templatePath, final String[] params)
	{
		final Map<String, Object> binding = new HashMap<>();

		if (params.length % 2 != 0)
		{
			throw new IllegalArgumentException("Given parameters must be a multiple of 2");
		}

		for (int i = 0; i < params.length; i += 2)
		{
			binding.put(params[i], params[i + 1]);
		}

		final StringWriter writer = new StringWriter();

		getImpexTemplateProcessor().processTemplate(writer, templatePath, binding);

		return writer.toString();
	}

	protected TemplateProcessor getImpexTemplateProcessor()
	{
		if (impexTemplateProcessor == null)
		{
			impexTemplateProcessor = templateProcessorFactory.createTemplateProcessor();
		}

		return impexTemplateProcessor;
	}

}
