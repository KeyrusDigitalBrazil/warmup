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
package de.hybris.platform.sap.productconfig.runtime.cps.masterdata.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataCharacteristicContainer;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataClassContainer;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataKnowledgeBaseContainer;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataProductContainer;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.common.CPSMasterDataKBHeaderInfo;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;


@UnitTest
public class MasterDataContainerResolverTest
{
	private static final String CSTIC_ID = "csticId";
	private static final String NUMERIC_CSTIC_ID = "numericCsticId";
	private static final String CSTIC_TYPE_STRING = "string";
	private static final String CLASS_ID = "classId";
	private static final String INVALID_CLASS_ID = "invalidClassId";
	private static final String CLASS_NAME = "The Class Name";
	private static final String PRODUCT_ID = "productId";
	private static final String PRODUCT_NAME = "productName";
	private static final Integer KB_BULD_NUMBER = Integer.valueOf(10);

	public MasterDataContainerResolverImpl classUnderTest;

	private Map<String, CPSMasterDataCharacteristicContainer> characteristics;
	private CPSMasterDataCharacteristicContainer csticContainer;
	private CPSMasterDataKnowledgeBaseContainer kbContainer;
	private CPSMasterDataProductContainer productContainer;
	private CPSMasterDataClassContainer classContainer;
	private CPSMasterDataKBHeaderInfo headerInfo;

	@Before
	public void setUp()
	{
		classUnderTest = new MasterDataContainerResolverImpl();
		csticContainer = new CPSMasterDataCharacteristicContainer();
		csticContainer.setId(CSTIC_ID);
		csticContainer.setType(CSTIC_TYPE_STRING);
		characteristics = new HashMap<>();
		characteristics.put(CSTIC_ID, csticContainer);
		csticContainer = new CPSMasterDataCharacteristicContainer();
		csticContainer.setId(NUMERIC_CSTIC_ID);
		csticContainer.setType(MasterDataContainerResolverImpl.CSTIC_TYPE_FLOAT);
		characteristics.put(NUMERIC_CSTIC_ID, csticContainer);

		kbContainer = new CPSMasterDataKnowledgeBaseContainer();
		kbContainer.setCharacteristics(characteristics);

		headerInfo = new CPSMasterDataKBHeaderInfo();
		headerInfo.setBuild(KB_BULD_NUMBER);
		kbContainer.setHeaderInfo(headerInfo);

		classContainer = new CPSMasterDataClassContainer();
		classContainer.setId(CLASS_ID);
		classContainer.setName(CLASS_NAME);
		kbContainer.setClasses(Collections.singletonMap(CLASS_ID, classContainer));

		productContainer = new CPSMasterDataProductContainer();
		productContainer.setId(PRODUCT_ID);
		productContainer.setName(PRODUCT_NAME);
		productContainer.setMultilevel(true);
		kbContainer.setProducts(Collections.singletonMap(PRODUCT_ID, productContainer));


	}


	@Test
	public void testIsCsticStringType()
	{
		assertTrue(classUnderTest.isCsticStringType(characteristics.get(CSTIC_ID)));
		assertFalse(classUnderTest.isCsticStringType(characteristics.get(NUMERIC_CSTIC_ID)));
	}

	@Test
	public void testIsCsticTypeNumericFloat()
	{
		csticContainer.setType(MasterDataContainerResolverImpl.CSTIC_TYPE_FLOAT);
		assertTrue(classUnderTest.isCsticNumericType(csticContainer));
	}

	@Test
	public void testIsCsticTypeNumericInteger()
	{
		csticContainer.setType(MasterDataContainerResolverImpl.CSTIC_TYPE_INTEGER);
		assertTrue(classUnderTest.isCsticNumericType(csticContainer));
	}

	@Test
	public void testGetProductName()
	{
		final String result = classUnderTest.getProductName(kbContainer, PRODUCT_ID);
		assertNotNull(result);
		assertEquals(PRODUCT_NAME, result);
	}

	@Test
	public void testIsProductMultilevel()
	{
		assertTrue(classUnderTest.isProductMultilevel(kbContainer, PRODUCT_ID));
	}

	@Test(expected = IllegalStateException.class)
	public void testGetProductName_NoValidProductId()
	{
		classUnderTest.getProductName(kbContainer, "invalidProductId");
	}

	@Test
	public void testGetClassName()
	{
		final String result = classUnderTest.getClassName(kbContainer, CLASS_ID);
		assertNotNull(result);
		assertEquals(CLASS_NAME, result);
	}

	@Test
	public void testGetClass()
	{
		final CPSMasterDataClassContainer classContainer = classUnderTest.getClass(kbContainer, CLASS_ID);
		assertNotNull(classContainer);
	}

	@Test(expected = IllegalStateException.class)
	public void testGetClass_NoValidClassId()
	{
		classUnderTest.getClass(kbContainer, INVALID_CLASS_ID);
	}

	@Test(expected = IllegalStateException.class)
	public void testGetClassName_NoValidClassId()
	{
		classUnderTest.getClassName(kbContainer, INVALID_CLASS_ID);
	}

	@Test
	public void testGetKbBuildNumber()
	{
		final Integer result = classUnderTest.getKbBuildNumber(kbContainer);
		assertNotNull(result);
	}

}
