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
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.CPSMasterDataCharacteristicSpecific;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.CPSMasterDataClass;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataCharacteristicSpecificContainer;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataClassContainer;
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
public class ClassCacheContainerPopulatorTest
{
	private ClassCacheContainerPopulator classUnderTest;
	private CPSMasterDataClass source;
	private CPSMasterDataClassContainer target;
	@Mock
	private Converter<CPSMasterDataCharacteristicSpecific, CPSMasterDataCharacteristicSpecificContainer> characteristicSpecificConverter;

	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = new ClassCacheContainerPopulator();
		classUnderTest.setCharacteristicSpecificConverter(characteristicSpecificConverter);
		source = new CPSMasterDataClass();
		source.setId("classId");
		source.setName("className");
		target = new CPSMasterDataClassContainer();
	}

	@Test
	public void testPopulateCoreAttributes()
	{
		classUnderTest.populateCoreAttributes(source, target);
		assertNotNull(target.getId());
		assertEquals(source.getId(), target.getId());
		assertNotNull(target.getName());
		assertEquals(source.getName(), target.getName());
	}

	@Test
	public void testPopulateCharacteristicEmpty()
	{
		source.setCharacteristicSpecifics(new ArrayList<>());
		classUnderTest.populateCharacteristicsSpecific(source, target);
		assertTrue(target.getCharacteristicSpecifics().isEmpty());
	}

	@Test
	public void testPopulateCharacteristicNull()
	{
		source.setCharacteristicSpecifics(null);
		classUnderTest.populateCharacteristicsSpecific(source, target);
		assertTrue(target.getCharacteristicSpecifics().isEmpty());
	}

	@Test
	public void testPopulateCharacteristics()
	{
		final List<CPSMasterDataCharacteristicSpecific> characteristics = new ArrayList<>();
		final CPSMasterDataCharacteristicSpecific characteristic = new CPSMasterDataCharacteristicSpecific();
		characteristics.add(characteristic);
		source.setCharacteristicSpecifics(characteristics);

		final CPSMasterDataCharacteristicSpecificContainer characteristicContainer = new CPSMasterDataCharacteristicSpecificContainer();
		characteristicContainer.setId("id");
		Mockito.when(characteristicSpecificConverter.convert(characteristic)).thenReturn(characteristicContainer);
		classUnderTest.populateCharacteristicsSpecific(source, target);
		assertFalse(target.getCharacteristicSpecifics().isEmpty());
		assertEquals(1, target.getCharacteristicSpecifics().size());
		final CPSMasterDataCharacteristicSpecificContainer result = target.getCharacteristicSpecifics().get("id");
		assertNotNull(result);
	}
}
