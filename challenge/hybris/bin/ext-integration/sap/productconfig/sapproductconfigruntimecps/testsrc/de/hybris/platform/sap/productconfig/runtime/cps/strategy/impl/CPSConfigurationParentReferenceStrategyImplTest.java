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
import static org.junit.Assert.assertNull;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSCharacteristic;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSCharacteristicGroup;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSConfiguration;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSItem;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSPossibleValue;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;


@UnitTest
public class CPSConfigurationParentReferenceStrategyImplTest
{
	protected static final String SUB_ITEM_CSTIC_VALUE = "SubItemCsticValue";
	protected static final String SUB_ITEM_CSTIC = "SubItemCstic";
	protected static final String LANG = Locale.ENGLISH.getLanguage();
	protected static final String ITEM_ID = "1";
	protected static final String GROUP_ID = "Group";
	protected static final String CSTIC_ID = "Cstic";
	protected static final String CSTIC_ID2 = "Cstic2";

	private final CPSConfigurationParentReferenceStrategyImpl classUnderTest = new CPSConfigurationParentReferenceStrategyImpl();
	protected final CPSConfiguration configuration = new CPSConfiguration();
	protected final CPSItem rootItem = new CPSItem();
	protected final CPSCharacteristicGroup group = new CPSCharacteristicGroup();
	protected final CPSCharacteristic characteristic = new CPSCharacteristic();
	protected final CPSCharacteristic characteristic2 = new CPSCharacteristic();

	@Before
	public void setup()
	{
		configuration.setRootItem(rootItem);
		rootItem.setId(ITEM_ID);
		rootItem.setCharacteristicGroups(new ArrayList<>());
		rootItem.setSubItems(new ArrayList<>());
		rootItem.setCharacteristics(new ArrayList<CPSCharacteristic>());

		group.setId(GROUP_ID);
		addRuntimeCsticGroup(rootItem, group);
		characteristic.setId(CSTIC_ID);
		characteristic.setPossibleValues(new ArrayList<>());
		characteristic.setValues(new ArrayList<>());
		characteristic2.setId(CSTIC_ID2);
		characteristic2.setPossibleValues(new ArrayList<>());
		characteristic2.setValues(new ArrayList<>());
		addRuntimeCstic(rootItem, characteristic);
	}

	protected CPSItem createCPSItem(final String itemId)
	{
		final CPSItem subItem = new CPSItem();
		subItem.setId(itemId);
		subItem.setSubItems(new ArrayList<>());
		final List<CPSCharacteristic> characteristics = new ArrayList<>();
		characteristics.add(createCPSCharacteristic());
		subItem.setCharacteristics(characteristics);
		subItem.setCharacteristicGroups(new ArrayList<>());
		subItem.getCharacteristicGroups().add(new CPSCharacteristicGroup());
		return subItem;
	}

	protected CPSCharacteristic createCPSCharacteristic()
	{
		final CPSCharacteristic characteristic = new CPSCharacteristic();
		characteristic.setId(SUB_ITEM_CSTIC);
		characteristic.setValues(createListOfCPSValues(characteristic));
		characteristic.setPossibleValues(createListOfPossibleValues());
		return characteristic;
	}

	protected List<CPSPossibleValue> createListOfPossibleValues()
	{
		final List<CPSPossibleValue> possibleValues = new ArrayList<>();
		possibleValues.add(new CPSPossibleValue());
		return possibleValues;
	}

	protected List<CPSValue> createListOfCPSValues(final CPSCharacteristic characteristic)
	{
		final List<CPSValue> values = new ArrayList<>();
		values.add(createCPSValue(characteristic, SUB_ITEM_CSTIC_VALUE));
		return values;
	}

	protected CPSValue createCPSValue(final CPSCharacteristic characteristic, final String valueName)
	{
		final CPSValue value = new CPSValue();
		value.setValue(valueName);
		return value;
	}

	protected void addRuntimeCsticGroup(final CPSItem item, final CPSCharacteristicGroup characteristicGroup)
	{
		if (characteristicGroup == null)
		{
			throw new IllegalArgumentException(
					new StringBuilder().append("tried to add null CharacteristicGroup to Item ").append(item.getId()).toString());
		}
		if (isRuntimeCsticGroupPresent(item, characteristicGroup.getId()))
		{
			throw new IllegalArgumentException(
					new StringBuilder().append("tried to add CharacteristicGroup with already existing id ")
							.append(characteristicGroup.getId()).append(" to Item ").append(item.getId()).toString());
		}
		item.getCharacteristicGroups().add(characteristicGroup);
	}

	protected boolean isRuntimeCsticGroupPresent(final CPSItem item, final String id)
	{
		for (final CPSCharacteristicGroup group : item.getCharacteristicGroups())
		{
			if (group.getId().equals(id))
			{
				return true;
			}
		}
		return false;
	}

	protected boolean isRuntimeCsticPresent(final CPSItem item, final String id)
	{
		for (final CPSCharacteristic characteristic : item.getCharacteristics())
		{
			if (characteristic.getId().equals(id))
			{
				return true;
			}
		}
		return false;
	}

	protected void addRuntimeCstic(final CPSItem item, final CPSCharacteristic characteristic)
	{
		if (characteristic == null)
		{
			throw new IllegalArgumentException(
					new StringBuilder().append("tried to add null Characteristic to Item ").append(item.getId()).toString());
		}
		if (isRuntimeCsticPresent(item, characteristic.getId()))
		{
			throw new IllegalArgumentException(new StringBuilder().append("tried to add Characteristic with already existing id ")
					.append(characteristic.getId()).append(" to Item ").append(item.getId()).toString());
		}
		item.getCharacteristics().add(characteristic);
	}

	@Test
	public void testAddParentReferences()
	{
		assertNull("Root item should have no reference to parentconfiguration initially",
				configuration.getRootItem().getParentConfiguration());
		classUnderTest.addParentReferences(configuration);
		assertEquals(configuration, configuration.getRootItem().getParentConfiguration());
	}

	@Test
	public void testAddParentReferencesForSubItems()
	{
		rootItem.setParentConfiguration(configuration);
		final CPSItem subItem = createCPSItem("item id");
		rootItem.getSubItems().add(subItem);
		assertNull(subItem.getParentConfiguration());
		assertNull(subItem.getParentItem());

		final CPSItem subSubItem = createCPSItem("subItem id");
		subItem.getSubItems().add(subSubItem);
		assertNull(subSubItem.getParentConfiguration());
		assertNull(subSubItem.getParentItem());

		classUnderTest.addParentReferencesForSubItems(rootItem);
		assertEquals(configuration, subItem.getParentConfiguration());
		assertEquals(rootItem, subItem.getParentItem());

		assertEquals(configuration, subSubItem.getParentConfiguration());
		assertEquals(subItem, subSubItem.getParentItem());
	}

	@Test
	public void testAddParentReferencesForCharacteristics()
	{
		final CPSItem item = createCPSItem("item id");
		final CPSCharacteristic cstic = item.getCharacteristics().get(0);
		assertNull(cstic.getParentItem());

		classUnderTest.addParentReferencesForCharacteristics(item);
		assertEquals(item, cstic.getParentItem());
	}

	@Test
	public void testAddParentReferencesForCharacteristicGroups()
	{
		final CPSItem item = createCPSItem("item id");
		final CPSCharacteristicGroup csticGroup = item.getCharacteristicGroups().get(0);
		assertNull(csticGroup.getParentItem());

		classUnderTest.addParentReferencesForCharacteristicGroups(item);
		assertEquals(item, csticGroup.getParentItem());
	}

	@Test
	public void testAddParentReferencesForCharacteristicValues()
	{
		final CPSCharacteristic cstic = createCPSCharacteristic();
		final CPSPossibleValue possibleValue = cstic.getPossibleValues().get(0);
		assertNull(possibleValue.getParentCharacteristic());
		final CPSValue value = cstic.getValues().get(0);
		assertNull(value.getParentCharacteristic());

		classUnderTest.addParentReferencesForCharacteristicValues(cstic);
		assertEquals(cstic, possibleValue.getParentCharacteristic());
		assertEquals(cstic, value.getParentCharacteristic());
	}

	@Test
	public void testAddParentReferences_FullHierarchy()
	{
		final CPSItem rootItem = configuration.getRootItem();
		rootItem.getSubItems().add(createCPSItem("subItem id"));
		assertNull("Root item should have no reference to parentconfiguration initially", rootItem.getParentConfiguration());
		final CPSItem subItem = rootItem.getSubItems().get(0);
		classUnderTest.addParentReferences(configuration);
		assertEquals(configuration, rootItem.getParentConfiguration());
		assertEquals(rootItem, rootItem.getCharacteristicGroups().get(0).getParentItem());

		assertEquals(rootItem, subItem.getParentItem());
		assertEquals(configuration, subItem.getParentConfiguration());
		assertEquals(subItem, subItem.getCharacteristicGroups().get(0).getParentItem());
		assertEquals(subItem, subItem.getCharacteristics().get(0).getParentItem());
		final CPSCharacteristic subCharacteristic = subItem.getCharacteristics().get(0);
		assertEquals(subItem, subCharacteristic.getParentItem());
		assertEquals(subCharacteristic, subCharacteristic.getPossibleValues().get(0).getParentCharacteristic());
		assertEquals(subCharacteristic, subCharacteristic.getValues().get(0).getParentCharacteristic());
	}

}
