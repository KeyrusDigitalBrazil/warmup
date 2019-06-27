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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.cps.cache.MasterDataCacheAccessService;
import de.hybris.platform.sap.productconfig.runtime.cps.constants.SapproductconfigruntimecpsConstants;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.CPSMasterDataCharacteristicGroup;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.CPSMasterDataPossibleValue;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.CPSMasterDataPossibleValueSpecific;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataCharacteristicContainer;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataCharacteristicSpecificContainer;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataClassContainer;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataKnowledgeBaseContainer;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataProductContainer;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.common.CPSMasterDataKBHeaderInfo;
import de.hybris.platform.servicelayer.i18n.I18NService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@SuppressWarnings("javadoc")
@UnitTest
public class ConfigurationMasterDataServiceImplTest
{
	private static final String UNKNOWN = "non-existing";
	private static final String POSSIBLE_VALUE_2 = "possible value 2";
	private static final String POSSIBLE_VALUE_1 = "possible value 1";
	private static final String KB_ID = "kbId";
	private static final String CSTIC_ID = "csticId";
	private static final String NUMERIC_CSTIC_ID = "numericCsticId";
	private static final String NUMERIC_VALUE_ID = "2.0";
	private static final String PRODUCT_ID = "productId";
	private static final String PRODUCT_NAME = "productName";
	private static final String GROUP_ID = "groupId";
	private static final String GROUP_NAME = "The Group Name";
	private static final String CLASS_ID = "classId";
	private static final String CLASS_NAME = "The Class Name";
	private static final String TYPE_MARA = "MARA";
	private static final String TYPE_KLAH = "KLAH";
	private static final String VALUE_ID = "valueId";
	private static final String VALUE_NAME = "Value Name";
	private static final String CSTIC_TYPE_STRING = "string";
	private static final String ITEM_TYPE_PRODUCT = SapproductconfigruntimecpsConstants.ITEM_TYPE_MARA;
	private static final String ITEM_TYPE_CLASS = SapproductconfigruntimecpsConstants.ITEM_TYPE_KLAH;
	private static final Integer KB_BULD_NUMBER = Integer.valueOf(10);


	private ConfigurationMasterDataServiceImpl classUnderTest;
	private CPSMasterDataKnowledgeBaseContainer kbContainer;
	private CPSMasterDataKBHeaderInfo headerInfo;
	private Map<String, CPSMasterDataCharacteristicContainer> characteristics;
	private Map<String, CPSMasterDataProductContainer> products;
	private CPSMasterDataCharacteristicContainer csticContainer;
	private CPSMasterDataCharacteristicContainer csticContainer2;
	private CPSMasterDataProductContainer productContainer;
	private Map<String, CPSMasterDataCharacteristicGroup> groups;
	private CPSMasterDataCharacteristicGroup group;
	private Map<String, CPSMasterDataClassContainer> classes;
	private CPSMasterDataClassContainer classContainer;
	private Map<String, CPSMasterDataPossibleValue> possibleValues;
	private CPSMasterDataPossibleValue possibleValue;
	private Map<String, CPSMasterDataPossibleValue> possibleValues2;
	private CPSMasterDataPossibleValue possibleValue2;
	@Mock
	private MasterDataCacheAccessService accessService;
	@Mock
	private I18NService i18nService;


	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
		kbContainer = new CPSMasterDataKnowledgeBaseContainer();
		fillCstics();
		fillProducts();
		fillGroups();
		fillClasses();
		fillHeader();
		classUnderTest = new ConfigurationMasterDataServiceImpl();
		classUnderTest.setMasterDataResolver(new MasterDataContainerResolverImpl());
		classUnderTest.setCacheAccessService(accessService);
		Mockito.when(accessService.getKbContainer(KB_ID, Locale.ENGLISH.getLanguage())).thenReturn(kbContainer);
		classUnderTest.setI18NService(i18nService);
		Mockito.when(i18nService.getCurrentLocale()).thenReturn(Locale.ENGLISH);
	}

	protected void fillHeader()
	{
		headerInfo = new CPSMasterDataKBHeaderInfo();
		headerInfo.setBuild(KB_BULD_NUMBER);
		kbContainer.setHeaderInfo(headerInfo);
	}


	protected void fillProducts()
	{
		productContainer = new CPSMasterDataProductContainer();
		productContainer.setId(PRODUCT_ID);
		productContainer.setName(PRODUCT_NAME);
		productContainer.setMultilevel(true);
		productContainer.setCstics(new HashMap<>());
		productContainer.getCstics().put(CSTIC_ID, new CPSMasterDataCharacteristicSpecificContainer());
		productContainer.getCstics().get(CSTIC_ID).setPossibleValueSpecifics(new HashMap<>());


		final CPSMasterDataPossibleValueSpecific possibleValueSpecific1 = new CPSMasterDataPossibleValueSpecific();
		possibleValueSpecific1.setVariantConditionKey("varCond1");
		final CPSMasterDataPossibleValueSpecific possibleValueSpecific2 = new CPSMasterDataPossibleValueSpecific();
		possibleValueSpecific2.setVariantConditionKey("varCond2");
		productContainer.getCstics().get(CSTIC_ID).getPossibleValueSpecifics().put(POSSIBLE_VALUE_1, possibleValueSpecific1);
		productContainer.getCstics().get(CSTIC_ID).getPossibleValueSpecifics().put(POSSIBLE_VALUE_2, possibleValueSpecific2);
		products = new HashMap<>();
		products.put(PRODUCT_ID, productContainer);
		kbContainer.setProducts(products);
	}

	protected void fillClasses()
	{
		classContainer = new CPSMasterDataClassContainer();
		classContainer.setId(CLASS_ID);
		classContainer.setName(CLASS_NAME);
		final Map<String, CPSMasterDataCharacteristicSpecificContainer> characteristicSpecifics = new HashMap<>();
		final CPSMasterDataCharacteristicSpecificContainer value = new CPSMasterDataCharacteristicSpecificContainer();
		value.setId(CSTIC_ID);
		characteristicSpecifics.put(CSTIC_ID, value);
		classContainer.setCharacteristicSpecifics(characteristicSpecifics);
		classes = new HashMap<>();
		classes.put(CLASS_ID, classContainer);
		kbContainer.setClasses(classes);
	}


	protected void fillGroups()
	{
		group = new CPSMasterDataCharacteristicGroup();
		group.setId(GROUP_ID);
		group.setName(GROUP_NAME);
		group.setCharacteristicIDs(new ArrayList<>());
		groups = new HashMap<>();
		groups.put(GROUP_ID, group);
		productContainer.setGroups(groups);
	}


	protected void fillCstics()
	{
		csticContainer = new CPSMasterDataCharacteristicContainer();
		csticContainer.setId(CSTIC_ID);
		csticContainer.setType(CSTIC_TYPE_STRING);
		characteristics = new HashMap<>();
		characteristics.put(CSTIC_ID, csticContainer);
		fillPossibleValues();
		csticContainer2 = new CPSMasterDataCharacteristicContainer();
		csticContainer2.setId(NUMERIC_CSTIC_ID);
		csticContainer2.setType(MasterDataContainerResolverImpl.CSTIC_TYPE_FLOAT);
		characteristics.put(NUMERIC_CSTIC_ID, csticContainer2);
		fillPossibleNumericValues();
		kbContainer.setCharacteristics(characteristics);
	}


	private void fillPossibleValues()
	{
		possibleValue = new CPSMasterDataPossibleValue();
		possibleValue.setId(VALUE_ID);
		possibleValue.setName(VALUE_NAME);
		possibleValues = new HashMap<>();
		possibleValues.put(VALUE_ID, possibleValue);
		csticContainer.setPossibleValueGlobals(possibleValues);
	}


	private void fillPossibleNumericValues()
	{
		possibleValue2 = new CPSMasterDataPossibleValue();
		possibleValue2.setId(NUMERIC_VALUE_ID);
		possibleValue2.setName(NUMERIC_VALUE_ID);
		possibleValues2 = new HashMap<>();
		possibleValues2.put(NUMERIC_VALUE_ID, possibleValue2);
		csticContainer2.setPossibleValueGlobals(possibleValues2);
	}


	@Test(expected = IllegalArgumentException.class)
	public void testGetMasterData_NoKbId()
	{
		classUnderTest.getMasterData(null);
	}

	@Test
	public void testGetMasterData()
	{
		final CPSMasterDataKnowledgeBaseContainer result = classUnderTest.getMasterData(KB_ID);
		assertNotNull(result);
		assertEquals(kbContainer, result);
		Mockito.verify(accessService).getKbContainer(KB_ID, Locale.ENGLISH.getLanguage());
	}

	@Test
	public void testGetCharacteristic()
	{
		final CPSMasterDataCharacteristicContainer result = classUnderTest.getCharacteristic(KB_ID, CSTIC_ID);
		assertNotNull(result);
		Mockito.verify(accessService).getKbContainer(KB_ID, Locale.ENGLISH.getLanguage());
		assertEquals(csticContainer, result);
	}

	@Test(expected = IllegalStateException.class)
	public void testGetCharacteristic_NoValidCsticId()
	{
		classUnderTest.getCharacteristic(KB_ID, "invalidCsticId");
	}

	@Test
	public void testGetGroupName()
	{
		final String result = classUnderTest.getGroupName(KB_ID, PRODUCT_ID, ITEM_TYPE_PRODUCT, GROUP_ID);
		assertNotNull(result);
		Mockito.verify(accessService).getKbContainer(KB_ID, Locale.ENGLISH.getLanguage());
		assertEquals(GROUP_NAME, result);
	}

	@Test
	public void testGetGroupNameClassNode()
	{
		final String result = classUnderTest.getGroupName(KB_ID, CLASS_ID, ITEM_TYPE_CLASS, GROUP_ID);
		assertNotNull(result);
		Mockito.verify(accessService).getKbContainer(KB_ID, Locale.ENGLISH.getLanguage());
		assertEquals(CLASS_NAME, result);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetGroupNameWrongType()
	{
		classUnderTest.getGroupName(KB_ID, CLASS_ID, null, GROUP_ID);
	}

	@Test(expected = IllegalStateException.class)
	public void testGetGroupName_NoValidGroupId()
	{
		classUnderTest.getGroupName(KB_ID, PRODUCT_ID, ITEM_TYPE_PRODUCT, "invalidGroupId");
	}


	@Test(expected = IllegalStateException.class)
	public void testGetItemName_NoValidId()
	{
		classUnderTest.getItemName(KB_ID, "invaliId", TYPE_MARA);
	}

	@Test(expected = IllegalStateException.class)
	public void testGetItemName_TypeInvalid()
	{
		final String result = classUnderTest.getItemName(KB_ID, PRODUCT_ID, "invalidType");
		assertNotNull(result);
	}

	@Test(expected = IllegalStateException.class)
	public void testGetItemName_TypeNull()
	{
		final String result = classUnderTest.getItemName(KB_ID, PRODUCT_ID, null);
		assertNotNull(result);
	}


	@Test
	public void testGetItemName_TypeKlah()
	{
		final String result = classUnderTest.getItemName(KB_ID, CLASS_ID, TYPE_KLAH);
		assertNotNull(result);
		Mockito.verify(accessService).getKbContainer(KB_ID, Locale.ENGLISH.getLanguage());
		assertEquals(CLASS_NAME, result);
	}

	@Test(expected = IllegalStateException.class)
	public void testGetValueName_NoValidCsticId()
	{
		classUnderTest.getValueName(KB_ID, "invalidCsticId", VALUE_ID);
	}

	@Test
	public void testGetValueName_NoValidValueId()
	{
		assertEquals("invalidValueId", classUnderTest.getValueName(KB_ID, CSTIC_ID, "invalidValueId"));
	}

	@Test
	public void testGetValueName()
	{
		final String result = classUnderTest.getValueName(KB_ID, CSTIC_ID, VALUE_ID);
		assertNotNull(result);
		Mockito.verify(accessService).getKbContainer(KB_ID, Locale.ENGLISH.getLanguage());
		assertEquals(VALUE_NAME, result);
	}

	@Test
	public void testGetValueNameForNumeric()
	{
		final String result = classUnderTest.getValueName(KB_ID, NUMERIC_CSTIC_ID, NUMERIC_VALUE_ID);
		assertNull(result);
	}

	@Test
	public void testGetGroupCharacteristicIDs()
	{
		final List<String> result = classUnderTest.getGroupCharacteristicIDs(KB_ID, PRODUCT_ID, ITEM_TYPE_PRODUCT, GROUP_ID);
		assertNotNull(result);
		Mockito.verify(accessService).getKbContainer(KB_ID, Locale.ENGLISH.getLanguage());
		assertEquals(group.getCharacteristicIDs(), result);
	}

	@Test
	public void testGetGroupCharacteristicIDsClassNode()
	{
		final List<String> result = classUnderTest.getGroupCharacteristicIDs(KB_ID, CLASS_ID, ITEM_TYPE_CLASS, GROUP_ID);
		assertNotNull(result);
		Mockito.verify(accessService).getKbContainer(KB_ID, Locale.ENGLISH.getLanguage());
		assertEquals(Arrays.asList(CSTIC_ID), result);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetGroupCharacteristicIDsWrongType()
	{
		classUnderTest.getGroupCharacteristicIDs(KB_ID, CLASS_ID, null, GROUP_ID);
	}

	@Test
	public void testGetSpecificPossibleValueIds()
	{
		final Set<String> result = classUnderTest.getSpecificPossibleValueIds(KB_ID, PRODUCT_ID,
				SapproductconfigruntimecpsConstants.ITEM_TYPE_MARA, CSTIC_ID);
		assertNotNull(result);
		assertEquals(2, result.size());
		assertTrue(result.contains(POSSIBLE_VALUE_1));
		assertTrue(result.contains(POSSIBLE_VALUE_2));
	}

	@Test
	public void testGetSpecificPossibleValueIdsEmpty()
	{
		final Set<String> result = classUnderTest.getSpecificPossibleValueIds(KB_ID, PRODUCT_ID,
				SapproductconfigruntimecpsConstants.ITEM_TYPE_MARA, UNKNOWN);
		assertNotNull(result);
		assertEquals(Collections.emptySet(), result);
	}

	@Test
	public void testGetSpecificPossibleValueProductIdNotKnown()
	{
		final Set<String> result = classUnderTest.getSpecificPossibleValueIds(KB_ID, CLASS_ID,
				SapproductconfigruntimecpsConstants.ITEM_TYPE_KLAH, UNKNOWN);
		assertNotNull(result);
		assertEquals(Collections.emptySet(), result);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetSpecificPossibleValueTypeNotKnown()
	{
		classUnderTest.getSpecificPossibleValueIds(KB_ID, CLASS_ID, null, null);
	}

	@Test
	public void testGetPossibleValueIds()
	{
		final Set<String> result = classUnderTest.getPossibleValueIds(KB_ID, CSTIC_ID);
		assertNotNull(result);
		assertEquals(1, result.size());
		assertTrue(result.contains(VALUE_ID));
	}

	@Test
	public void testGetPossibleValueIdsEmpty()
	{
		final Set<String> result = classUnderTest.getPossibleValueIds(KB_ID, UNKNOWN);
		assertNotNull(result);
		assertEquals(Collections.emptySet(), result);
	}


	@Test
	public void testIsCsticNumericNotNum()
	{
		assertFalse(classUnderTest.isCharacteristicNumeric(KB_ID, CSTIC_ID));
	}

	@Test
	public void testIsCsticNumeric()
	{
		assertTrue(classUnderTest.isCharacteristicNumeric(KB_ID, NUMERIC_CSTIC_ID));
	}

	@Test
	public void testGetKBBuildNumber()
	{
		final Integer result = classUnderTest.getKbBuildNumber(KB_ID);
		assertNotNull(result);
		assertEquals(KB_BULD_NUMBER, result);
	}

	@Test
	public void testRemoveKbContainer()
	{
		classUnderTest.removeCachedKb(KB_ID);
		Mockito.verify(accessService).removeKbContainer(KB_ID, Locale.ENGLISH.getLanguage());
	}
}
