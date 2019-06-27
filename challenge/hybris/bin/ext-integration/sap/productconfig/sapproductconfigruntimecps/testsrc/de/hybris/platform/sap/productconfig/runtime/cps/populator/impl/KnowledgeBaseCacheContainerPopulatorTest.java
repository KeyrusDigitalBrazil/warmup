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
package de.hybris.platform.sap.productconfig.runtime.cps.populator.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.CPSMasterDataCharacteristic;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.CPSMasterDataClass;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.CPSMasterDataKnowledgeBase;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.CPSMasterDataProduct;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataCharacteristicContainer;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataClassContainer;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataKnowledgeBaseContainer;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataProductContainer;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.common.CPSMasterDataKBHeaderInfo;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@SuppressWarnings("javadoc")
@UnitTest
public class KnowledgeBaseCacheContainerPopulatorTest
{
	private KnowledgeBaseCacheContainerPopulator classUnderTest;
	private CPSMasterDataKnowledgeBase source;
	private CPSMasterDataKnowledgeBaseContainer target;
	@Mock
	private Converter<CPSMasterDataProduct, CPSMasterDataProductContainer> productConverter;
	@Mock
	private Converter<CPSMasterDataClass, CPSMasterDataClassContainer> classConverter;
	@Mock
	private Converter<CPSMasterDataCharacteristic, CPSMasterDataCharacteristicContainer> characteristicConverter;

	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = new KnowledgeBaseCacheContainerPopulator();
		classUnderTest.setProductConverter(productConverter);
		classUnderTest.setClassConverter(classConverter);
		classUnderTest.setCharacteristicConverter(characteristicConverter);
		source = new CPSMasterDataKnowledgeBase();
		source.setLanguage("lang");
		source.setHeaderInfo(new CPSMasterDataKBHeaderInfo());
		target = new CPSMasterDataKnowledgeBaseContainer();
	}

	@Test
	public void testPopulateCoreAttributes()
	{
		classUnderTest.populateCoreAttributes(source, target);
		assertEquals(source.getLanguage(), target.getLanguage());
		assertEquals(source.getHeaderInfo(), target.getHeaderInfo());
	}

	@Test
	public void testPopulateProductsEmpty()
	{
		source.setProducts(new ArrayList<>());
		classUnderTest.populateProducts(source, target);
		assertTrue(target.getProducts().isEmpty());
	}

	@Test
	public void testPopulateProductsNull()
	{
		source.setProducts(null);
		classUnderTest.populateProducts(source, target);
		assertTrue(target.getProducts().isEmpty());
	}

	@Test
	public void testPopulateProducts()
	{
		final List<CPSMasterDataProduct> products = new ArrayList<>();
		final CPSMasterDataProduct product = new CPSMasterDataProduct();
		products.add(product);
		source.setProducts(products);

		final CPSMasterDataProductContainer productContainer = new CPSMasterDataProductContainer();
		productContainer.setId("id");
		Mockito.when(productConverter.convert(product)).thenReturn(productContainer);
		classUnderTest.populateProducts(source, target);
		assertFalse(target.getProducts().isEmpty());
		assertEquals(1, target.getProducts().size());
		final CPSMasterDataProductContainer result = target.getProducts().get("id");
		assertNotNull(result);
	}

	@Test
	public void testPopulateClassesEmpty()
	{
		source.setClasses(new ArrayList<>());
		classUnderTest.populateClasses(source, target);
		assertTrue(target.getClasses().isEmpty());
	}

	@Test
	public void testPopulateClassesNull()
	{
		source.setClasses(null);
		classUnderTest.populateClasses(source, target);
		assertTrue(target.getClasses().isEmpty());
	}

	@Test
	public void testPopulateClasses()
	{
		final List<CPSMasterDataClass> classes = new ArrayList<>();
		final CPSMasterDataClass clazz = new CPSMasterDataClass();
		classes.add(clazz);
		source.setClasses(classes);

		final CPSMasterDataClassContainer classContainer = new CPSMasterDataClassContainer();
		classContainer.setId("id");
		Mockito.when(classConverter.convert(clazz)).thenReturn(classContainer);
		classUnderTest.populateClasses(source, target);
		assertFalse(target.getClasses().isEmpty());
		assertEquals(1, target.getClasses().size());
		final CPSMasterDataClassContainer result = target.getClasses().get("id");
		assertNotNull(result);
	}

	@Test
	public void testPopulateCharacteristicEmpty()
	{
		source.setCharacteristics(new ArrayList<>());
		classUnderTest.populateCharacteristics(source, target);
		assertTrue(target.getCharacteristics().isEmpty());
	}

	@Test
	public void testPopulateCharacteristicNull()
	{
		source.setCharacteristics(null);
		classUnderTest.populateCharacteristics(source, target);
		assertTrue(target.getCharacteristics().isEmpty());
	}

	@Test
	public void testPopulateCharacteristics()
	{
		final List<CPSMasterDataCharacteristic> characteristics = new ArrayList<>();
		final CPSMasterDataCharacteristic characteristic = new CPSMasterDataCharacteristic();
		characteristics.add(characteristic);

		final CPSMasterDataCharacteristicContainer characteristicContainer = new CPSMasterDataCharacteristicContainer();
		characteristicContainer.setId("id");
		Mockito.when(characteristicConverter.convert(characteristic)).thenReturn(characteristicContainer);
		source.setCharacteristics(characteristics);
		classUnderTest.populateCharacteristics(source, target);
		assertFalse(target.getCharacteristics().isEmpty());
		assertEquals(1, target.getCharacteristics().size());
		final CPSMasterDataCharacteristicContainer result = target.getCharacteristics().get("id");
		assertNotNull(result);
	}



}
