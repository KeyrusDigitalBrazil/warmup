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
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.CPSMasterDataPossibleValue;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataCharacteristicContainer;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;


@SuppressWarnings("javadoc")
@UnitTest
public class CharacteristicCacheContainerPopulatorTest
{
	private CharacteristicCacheContainerPopulator classUnderTest;
	private CPSMasterDataCharacteristic source;
	private CPSMasterDataCharacteristicContainer target;

	@Before
	public void setup()
	{
		classUnderTest = new CharacteristicCacheContainerPopulator();
		source = new CPSMasterDataCharacteristic();
		source.setId("csticId");
		source.setLength(Integer.valueOf(2));
		source.setMultiValued(false);
		source.setName("csticName");
		source.setDescription("description");
		source.setNumberDecimals(Integer.valueOf(3));
		source.setType("type");
		source.setAdditionalValues(true);
		source.setCaseSensitive(true);
		source.setEntryFieldMask("entryFieldMask");
		source.setUnitOfMeasure("uom");
		target = new CPSMasterDataCharacteristicContainer();
	}

	@Test
	public void testPopulateCoreAttributes()
	{
		classUnderTest.populate(source, target);
		assertNotNull(target.getId());
		assertEquals(source.getId(), target.getId());
		assertNotNull(target.getLength());
		assertEquals(source.getLength(), target.getLength());
		assertEquals(Boolean.valueOf(source.isMultiValued()), Boolean.valueOf(target.isMultiValued()));
		assertNotNull(target.getName());
		assertEquals(source.getName(), target.getName());
		assertEquals(source.getDescription(), target.getDescription());
		assertNotNull(target.getNumberDecimals());
		assertEquals(source.getNumberDecimals(), target.getNumberDecimals());
		assertNotNull(target.getType());
		assertEquals(source.getType(), target.getType());
		assertNotNull(Boolean.valueOf(target.isAdditionalValues()));
		assertEquals(Boolean.valueOf(source.isAdditionalValues()), Boolean.valueOf(target.isAdditionalValues()));
		assertNotNull(Boolean.valueOf(target.isCaseSensitive()));
		assertEquals(Boolean.valueOf(source.isCaseSensitive()), Boolean.valueOf(target.isCaseSensitive()));
		assertNotNull(target.getEntryFieldMask());
		assertEquals(source.getEntryFieldMask(), target.getEntryFieldMask());
		assertNotNull(target.getUnitOfMeasure());
		assertEquals(source.getUnitOfMeasure(), target.getUnitOfMeasure());
	}

	@Test
	public void testPopulatePossibleValuesEmpty()
	{
		source.setPossibleValues(new ArrayList<>());
		classUnderTest.populatePossibleValuesGlobals(source, target);
		assertTrue(target.getPossibleValueGlobals().isEmpty());
	}

	@Test
	public void testPopulatePossibleValuesNull()
	{
		source.setPossibleValues(null);
		classUnderTest.populatePossibleValuesGlobals(source, target);
		assertTrue(target.getPossibleValueGlobals().isEmpty());
	}

	@Test
	public void testPopulatePossibleValues()
	{
		final List<CPSMasterDataPossibleValue> possibleValues = new ArrayList<>();
		final CPSMasterDataPossibleValue possibleValue = new CPSMasterDataPossibleValue();
		possibleValue.setId("valueId");
		possibleValues.add(possibleValue);
		source.setPossibleValues(possibleValues);

		classUnderTest.populate(source, target);
		assertNotNull(target.getPossibleValueGlobals());
		assertFalse(target.getPossibleValueGlobals().isEmpty());
		assertEquals(1, target.getPossibleValueGlobals().size());
		final CPSMasterDataPossibleValue result = target.getPossibleValueGlobals().get("valueId");
		assertNotNull(result);
		assertEquals(possibleValue, result);
	}
}
