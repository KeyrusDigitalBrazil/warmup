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
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.CPSMasterDataCharacteristicGroup;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.CPSMasterDataCharacteristicSpecific;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.CPSMasterDataProduct;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataCharacteristicSpecificContainer;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataProductContainer;
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
public class ProductCacheContainerPopulatorTest
{
	private ProductCacheContainerPopulator classUnderTest;
	private CPSMasterDataProduct source;
	private CPSMasterDataProductContainer target;
	@Mock
	private Converter<CPSMasterDataCharacteristicSpecific, CPSMasterDataCharacteristicSpecificContainer> characteristicSpecificConverter;

	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = new ProductCacheContainerPopulator();
		classUnderTest.setCharacteristicSpecificConverter(characteristicSpecificConverter);
		source = new CPSMasterDataProduct();
		source.setId("productId");
		source.setName("productName");
		source.setMultilevel(true);
		source.setCharacteristicGroups(new ArrayList<>());
		source.setCharacteristicSpecifics(new ArrayList<>());

		target = new CPSMasterDataProductContainer();
	}

	@Test
	public void testPopulateCoreAttributes()
	{
		classUnderTest.populateCoreAttributes(source, target);
		assertNotNull(target.getId());
		assertEquals(source.getId(), target.getId());
		assertNotNull(target.getName());
		assertEquals(source.getName(), target.getName());
		assertEquals(Boolean.valueOf(source.isMultilevel()), Boolean.valueOf(target.isMultilevel()));
	}

	@Test
	public void testPopulateCharacteristicEmpty()
	{
		classUnderTest.populateCharacteristicsSpecific(source, target);
		assertTrue(target.getCstics().isEmpty());
	}

	@Test
	public void testPopulateCharacteristicNull()
	{
		source.setCharacteristicSpecifics(null);
		classUnderTest.populateCharacteristicsSpecific(source, target);
		assertTrue(target.getCstics().isEmpty());
	}

	@Test
	public void testPopulateCharacteristics()
	{
		final List<CPSMasterDataCharacteristicSpecific> characteristics = new ArrayList<>();
		final CPSMasterDataCharacteristicSpecific characteristic = new CPSMasterDataCharacteristicSpecific();
		characteristics.add(characteristic);

		final CPSMasterDataCharacteristicSpecificContainer characteristicContainer = new CPSMasterDataCharacteristicSpecificContainer();
		characteristicContainer.setId("id");
		Mockito.when(characteristicSpecificConverter.convert(characteristic)).thenReturn(characteristicContainer);
		source.setCharacteristicSpecifics(characteristics);
		classUnderTest.populateCharacteristicsSpecific(source, target);
		assertFalse(target.getCstics().isEmpty());
		assertEquals(1, target.getCstics().size());
		final CPSMasterDataCharacteristicSpecificContainer result = target.getCstics().get("id");
		assertNotNull(result);
	}

	@Test
	public void testPopulateGroupsEmpty()
	{
		classUnderTest.populateCharacteristicGroups(source, target);
		assertTrue(target.getGroups().isEmpty());
	}

	@Test
	public void testPopulateGroupsNull()
	{
		source.setCharacteristicGroups(null);
		classUnderTest.populateCharacteristicGroups(source, target);
		assertTrue(target.getGroups().isEmpty());
	}

	@Test
	public void testPopulateGroups()
	{
		final List<CPSMasterDataCharacteristicGroup> groups = new ArrayList<>();
		final CPSMasterDataCharacteristicGroup group = new CPSMasterDataCharacteristicGroup();
		group.setId("groupId");
		groups.add(group);
		source.setCharacteristicGroups(groups);

		classUnderTest.populateCharacteristicGroups(source, target);
		assertNotNull(target.getGroups());
		assertFalse(target.getGroups().isEmpty());
		assertEquals(1, target.getGroups().size());
		final CPSMasterDataCharacteristicGroup result = target.getGroups().get("groupId");
		assertNotNull(result);
		assertEquals(group.getId(), result.getId());
		assertEquals(group.getName(), result.getName());
	}

}
